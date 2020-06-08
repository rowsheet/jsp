package data.postgresql;

import java.sql.*;

public class PostgresDAO {

    static final String JDBC_DRIVER = "org.postgresql.Driver";
    public final String DB_URL;
    public final String HOST;
    public final String NAME;
    public final String USER;
    public final String PASS;

    public PostgresDAO(String databaseHost, String databaseName, String databaseUser, String databasePassword) {
        HOST = databaseHost;
        NAME = databaseName;
        USER = databaseUser;
        PASS = databasePassword;
        DB_URL = "jdbc:postgresql://" + HOST + "/" + NAME;
    }

    public Connection getConnection() throws Exception {
        Connection conn = null;
        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        return conn;
    }
}
