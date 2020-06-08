package launch;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import data.postgresql.PostgresDAO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import launch.exceptions.AssertDatabaseInitializationException;
import launch.exceptions.LoadConfigException;
import launch.exceptions.LoadPostgresDAOException;
import launch.exceptions.RunDatabaseMigrationsException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class Bootstrap {

    public Bootstrap() throws Exception {
    }

    public Config LoadConfig() throws LoadConfigException {
        try {
            Config config = new Config();
            return config;
        } catch (Exception ex) {
           throw new LoadConfigException(ex.getMessage());
        }
    }

    public PostgresDAO LoadPostgresDAO(Config config) throws LoadPostgresDAOException {
        try {
            PostgresDAO postgresDAO = new PostgresDAO(
                    config.DATA_POSTGRES_HOST,
                    config.DATA_POSTGRES_NAME,
                    config.DATA_POSTGRES_USER,
                    config.DATA_POSTGRES_PASS);
            return postgresDAO;
        } catch (Exception ex) {
            throw new LoadPostgresDAOException(ex.getMessage());
        }
    }

    /**
     * Select how many migrations in migration tracking table.
     * If no migration tracking table exists, create it.
     * If created, insert a zero row for initialization.
     * @param postgresDAO
     * @throws AssertDatabaseInitializationException
     */
    public void AssertDatabaseInitialization(PostgresDAO postgresDAO) throws AssertDatabaseInitializationException {
        try {
            Connection connection = postgresDAO.getConnection();
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM internal_database_migrations");
            int total = 0;
            while (resultSet.next()) {
                total = resultSet.getInt("total");
            }
        } catch (Exception ex) {
            if (ex.getMessage().contains("relation \"internal_database_migrations\" does not exist")) {
                try {
                    String create_internal_database_migrations_sql = this.getMigrationTemplate(
                            "init.internal_database_migrations.sql");
                    Connection connection = postgresDAO.getConnection();
                    connection.createStatement().execute(create_internal_database_migrations_sql);
                    connection.close();
                    return;
                } catch (Exception create_table_exception) {
                    throw new AssertDatabaseInitializationException(
                            "Unable to create table\n" + create_table_exception.getMessage());
                }
            }
            throw new AssertDatabaseInitializationException(ex.getMessage());
        }
    }

    /**
     * 1) Select the max applied migration_id in the migration tracking table.
     * 2) Fetch a list of all migration files.
     * 3) Find all files where the migration_id is greater than the max applied migration_id.
     * 4) If applied, insert tracking row with description as migration file.
     * @param postgresDAO
     * @throws RunDatabaseMigrationsException
     */
    public void RunDatabaseMigrations(PostgresDAO postgresDAO) throws RunDatabaseMigrationsException {
        try {
            int max_migration = postgresDAO.QueryInt(
                    "SELECT MAX(id) FROM internal_database_migrations",
                    "max");
            HashMap<Integer, String> migration_file_names = getMigrationFilenames();
            for (Map.Entry<Integer, String> entry : migration_file_names.entrySet()) {
                Integer migration_id = entry.getKey();
                String filename = entry.getValue();
                if (migration_id > max_migration) {
                    String sql = getMigrationTemplate(filename);
                    try {
                        postgresDAO.Execute(sql);
                    } catch (Exception ex) {
                       throw new Exception(ex.getMessage());
                    }
                    postgresDAO.Execute("INSERT INTO internal_database_migrations (id, description) VALUES ("
                            + Integer.toString(migration_id) + ",'" + filename + "')");
                }
            }
        } catch (Exception ex) {
            throw new RunDatabaseMigrationsException(ex.getMessage());
        }
    }

    public void StartProductionServer() throws Exception {
        this.runWebapp("webapp/");
    }

    public void StartErrorServer() throws Exception {
        this.runWebapp("webapp_error");
    }

    /**
     * HELPER FUNCTIONS
     */

    private String getMigrationTemplate(String name) throws Exception {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setDirectoryForTemplateLoading(new File("."));
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate("src/main/java/data/postgresql/migrations/" + name);
        StringWriter stringWriter = new StringWriter();
        template.process(null, stringWriter);
        return stringWriter.toString();
    }

    private HashMap<Integer, String> getMigrationFilenames() {
        HashMap<Integer, String> migrations_files = new HashMap<Integer, String>();
        File folder = new File(getRootFolder() + "/src/main/java/data/postgresql/migrations");
        File[] files = folder.listFiles();
        for (File file : files) {
            String filename = file.getName();
            String type = filename.split("\\.")[0];
            String name = filename.split("\\.")[1];
            if (type.equalsIgnoreCase("mig")) {
                migrations_files.put(Integer.parseInt(name), filename);
            }
        }
        return migrations_files;
    }

    private void runWebapp(String dir) throws Exception {

        File root = getRootFolder();
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
        Tomcat tomcat = new Tomcat();
        Path tempPath = Files.createTempDirectory("tomcat-base-dir");
        tomcat.setBaseDir(tempPath.toString());

        //The port that we should run on can be set into an environment variable
        //Look for that variable and default to 8080 if it isn't there.
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        tomcat.setPort(Integer.valueOf(webPort));
        File webContentFolder = new File(root.getAbsolutePath(), "src/main/" + dir);
        if (!webContentFolder.exists()) {
            webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
        }
        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webContentFolder.getAbsolutePath());
        //Set execution independent of current thread context classloader (compatibility with exec:java mojo)
        ctx.setParentClassLoader(Main.class.getClassLoader());

        System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);

        WebResourceSet resourceSet;
        if (additionWebInfClassesFolder.exists()) {
            resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
            System.out.println("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
        } else {
            resourceSet = new EmptyResourceSet(resources);
        }
        resources.addPreResources(resourceSet);
        ctx.setResources(resources);

        tomcat.start();
        tomcat.getServer().await();
    }

    private static File getRootFolder() {
        try {
            File root;
            String runningJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            System.out.println("application resolved root folder: " + root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
