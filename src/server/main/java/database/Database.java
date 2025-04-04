package database;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import exception.DatabaseException;
import file.ApplicationProperty;
import model.*;
import security.Security;

import javax.naming.AuthenticationException;
import java.sql.*;
import java.util.*;

public class Database {
    private static final String DB_NAME;
    private static final String DB_HOST;
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

    public static boolean registerUser(String username, String password, String email)
            throws DatabaseException, SQLException {
        String sqlStatement = "INSERT INTO user (name, password, password_salt, email) " +
                "VALUES (?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){
            int salt = new Random().nextInt();
            String resultPassword = Security.hashString(password, salt);

            statement.setString(1, username);
            statement.setString(2, resultPassword);
            statement.setInt(3, salt);
            statement.setString(4, email);

            return statement.executeUpdate() != 0;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000"))
                throw new DatabaseException(23);
            throw new SQLException(e);
        }
    }

    public static Integer loginUser(String username, String password){
        String sqlStatement = "SELECT password_salt FROM user WHERE name = ? AND password = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            Integer passwordSalt = getUserPasswordSalt(username);
            if (passwordSalt == null)
                return null;

            String hashedPassword = Security.hashString(password, passwordSalt);
            statement.setString(1, username);
            statement.setString(2, hashedPassword);

            return statement.executeQuery().next() ? passwordSalt : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int validateUser(String username, Integer token) throws AuthenticationException {
        String sqlStatement = "SELECT id FROM user WHERE name = ? AND password_salt = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, username);
            statement.setInt(2, token);

            ResultSet result = statement.executeQuery();
            if (!result.next())
                throw new AuthenticationException("Invalid user");

            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean createUserList(int idUser, String listName)
            throws SQLException, DatabaseException {
        String sqlStatement = "INSERT INTO list (name, user_id) VALUES (?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, listName);
            statement.setInt(2, idUser);

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000"))
                throw new DatabaseException(23);
            throw new SQLException(e);
        }
    }

    public static List<UserList> getUserLists(int idUser){
        String sqlStatement = "SELECT l.id, l.name FROM list AS l " +
                "INNER JOIN user AS u ON u.id = l.user_id " +
                "WHERE u.id = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setInt(1, idUser);

            ResultSet resultSet = statement.executeQuery();
            List<UserList> userLists = new ArrayList<>();
            while(resultSet.next()){
                userLists.add(getListItems(
                        resultSet.getInt(1), resultSet.getString(2)));
            }

            return userLists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Private methods

    private static UserList getListItems(int idList, String listName){
        String sqlStatement = "SELECT mt.name, m.title, m.api_id, s.name, " +
                "lhm.current_episode, m.total_episodes " +
                "FROM multimedia_type AS mt " +
                "INNER JOIN multimedia AS m ON m.multimedia_type_id = mt.id " +
                "INNER JOIN list_has_multimedia AS lhm ON lhm.multimedia_id = m.id " +
                "INNER JOIN status AS s ON s.id = lhm.status_id " +
                "WHERE lhm.list_id = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setInt(1, idList);

            ResultSet resultSet = statement.executeQuery();
            Set<MultimediaAtList> multimediaSet = new HashSet<>();
            while(resultSet.next()){
                Multimedia multi;
                String multimediaTypeString = resultSet.getString(1);
                String multimediaTitle = resultSet.getString(2);
                int apiID = resultSet.getInt(3);
                String statusName = resultSet.getString(4);
                int current_episode = resultSet.getInt(5);
                int total_episodes = resultSet.getInt(6);

                if (resultSet.wasNull())
                    current_episode = -1;

                MultimediaType multimediaType;
                if (multimediaTypeString.equalsIgnoreCase("movie")) {
                    multi = new Movie(apiID);
                    multimediaType = MultimediaType.MOVIE;
                } else if (multimediaTypeString.equalsIgnoreCase("tv_show")) {
                    multi = new TvShow(apiID);
                    ((TvShow) multi).setTotalEpisodes(total_episodes);
                    multimediaType = MultimediaType.TV_SHOW;
                } else
                    throw new SQLException("Unknown multimedia type: " + multimediaTypeString);

                multi.setTitle(multimediaTitle);

                multimediaSet.add(
                        new MultimediaAtList(multi, MultimediaStatus.valueOf(statusName),
                                current_episode, multimediaType));
            }

            return new UserList(listName, multimediaSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer getUserPasswordSalt(String username){
        String sqlStatement = "SELECT password_salt FROM user WHERE name = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())
                return null;
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnection(){
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME,
                    DB_USER,
                    DB_PASS
            );
        } catch (SQLException e) {
            if (e instanceof CommunicationsException){
                RuntimeException ex =new RuntimeException("MySQL service might be down");
                ex.setStackTrace(new StackTraceElement[]{});
                throw ex;
            }
            throw new RuntimeException(e);
        }
    }
}
