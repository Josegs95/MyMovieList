package database;

import exception.DatabaseException;
import file.ApplicationProperty;
import security.Security;

import java.sql.*;
import java.util.Map;
import java.util.Random;

public class Database {
    final private static String DB_NAME;
    final private static String DB_HOST;
    private static final String DB_PORT;
    private static final String DB_USER;
    private static final String DB_PASS;

    static {
        Map<String, String> sysProperties = ApplicationProperty.getProperties();

        DB_NAME = sysProperties.getOrDefault("DB_NAME", ApplicationProperty.getApplicationName());
        DB_HOST = sysProperties.getOrDefault("DB_HOST", "localhost");
        DB_PORT = sysProperties.getOrDefault("DB_PORT", "3306");

        DB_USER = sysProperties.get("DB_USER");
        DB_PASS = sysProperties.get("DB_PASS");
    }

    private Database(){}

    public static boolean registerUser(Map<String, Object> userData) throws DatabaseException {
        String sqlStatement = "INSERT INTO user (name, password, password_salt, email) VALUES (?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){
            String hashedPassword = userData.get("password").toString();
            int salt = new Random().nextInt();
            String resultPassword = Security.hashString(hashedPassword, salt);

            statement.setString(1, userData.get("username").toString());
            statement.setString(2, resultPassword);
            statement.setInt(3, salt);
            Object email = userData.get("email");
            if (email == null)
                statement.setString(4, null);
            else
                statement.setString(4, userData.get("email").toString());

            return statement.executeUpdate() != 0;

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public static boolean loginUser(Map<String, Object> userData) throws DatabaseException {
        String sqlStatement = "SELECT password_salt FROM user WHERE name = ? AND password = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            String username = userData.get("username").toString();
            Integer passwordSalt = getUserPasswordSalt(username);
            if (passwordSalt == null)
                return false;

            String hashedPassword = Security.hashString(userData.get("password").toString(), passwordSalt);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static Integer getUserPasswordSalt(String username) throws DatabaseException {
        String sqlStatement = "SELECT password_salt FROM user WHERE name = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())
                return null;
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private static Connection getConnection() throws SQLException{

        return DriverManager.getConnection(
                "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME,
                DB_USER,
                DB_PASS
        );
    }
}
