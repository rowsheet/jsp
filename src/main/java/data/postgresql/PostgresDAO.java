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

    public int QueryInt(String sql, String variable_name) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = null;
        Integer result_int = null;
        try {
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result_int = resultSet.getInt(variable_name);
            }
            return result_int;
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    public void Execute(String sql) throws Exception {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        try {
            statement.execute(sql);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}
