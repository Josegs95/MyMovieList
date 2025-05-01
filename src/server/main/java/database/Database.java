package database;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import exception.DatabaseException;
import file.ApplicationProperty;
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

    public static void registerUser(String username, String password, String email)
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

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                String errorMessage = "Already exists a user with that username";
                throw new DatabaseException(23, errorMessage);
            }
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

    public static void createUserList(int idUser, String listName)
            throws SQLException, DatabaseException {
        String sqlStatement = "INSERT INTO list (name, user_id) VALUES (?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, listName);
            statement.setInt(2, idUser);

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                String errorMessage = "You already has a list with that name";
                throw new DatabaseException(23, errorMessage);
            }
            throw new SQLException(e);
        }
    }

    public static void renameUserList(int idUser, String oldListName, String newListName) throws DatabaseException, SQLException {
        String sqlStatement = "UPDATE list AS l " +
                "SET l.name = ? " +
                "WHERE l.user_id = ? AND l.name = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setString(1, newListName);
            statement.setInt(2, idUser);
            statement.setString(3, oldListName);

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                String errorMessage = "You already has a list with that name";
                throw new DatabaseException(23, errorMessage);
            }
            throw new SQLException(e);
        }
    }

    public static boolean deleteUserList(int idUser, String listName) throws SQLException {
        String sqlStatement = "DELETE FROM list AS l " +
                "WHERE l.user_id = ? AND l.name = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setInt(1, idUser);
            statement.setString(2, listName);

            return statement.executeUpdate() == 1;
        }
    }

    public static List<Map<String, Object>> getUserLists(int idUser){
        String sqlStatement = "SELECT l.id, l.name FROM list AS l " +
                "INNER JOIN user AS u ON u.id = l.user_id " +
                "WHERE u.id = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)){

            statement.setInt(1, idUser);

            ResultSet resultSet = statement.executeQuery();
            List<Map<String, Object>> userLists = new ArrayList<>();
            while(resultSet.next()){
                int idList = resultSet.getInt(1);
                String listName = resultSet.getString(2);
                userLists.add(getListData(idList, listName));
            }

            return userLists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int existMultimedia(int apiId, String type){
        String sqlStatement = "SELECT m.id FROM multimedia AS m " +
                "INNER JOIN multimedia_type AS mt ON m.multimedia_type_id = mt.id " +
                "WHERE m.api_id = ? AND mt.name = ?";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)) {

            statement.setInt(1, apiId);
            statement.setString(2, type);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }

            return -1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int createMultimedia(int apiId, String title, String type, int totalEpisodes)
            throws DatabaseException, SQLException {
        String sqlStatement = "INSERT INTO multimedia (title, api_id, total_episodes, " +
                "multimedia_type_id) " +
                "SELECT ?, ?, ?, mt.id FROM multimedia_type AS mt " +
                "WHERE mt.name = ?";

        Connection connection = getConnection();
        PreparedStatement statement =
                connection.prepareStatement(sqlStatement, Statement.RETURN_GENERATED_KEYS);
        try(connection; statement){

            statement.setString(1, title);
            statement.setInt(2, apiId);
            statement.setString(4, type);
            if (type.equals("MOVIE")) {
                statement.setNull(3, Types.NULL);
            } else {
                statement.setInt(3, totalEpisodes);
            }

            if (statement.executeUpdate() != 1) {
                return -1;
            }

            ResultSet generatedIds = statement.getGeneratedKeys();
            generatedIds.next();
            return generatedIds.getInt(1);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                String errorMessage = "Already exists a multimedia with that API_ID and type";
                throw new DatabaseException(23, errorMessage);
            }
            throw new SQLException(e);
        }
    }

    public static void addMultimediaToList(int idUser, int idMultimedia, String listName,
                                              String status, int currentEpisode) throws DatabaseException, SQLException {
        String sqlStatement = "CALL insert_multimedia_item_to_list(?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)) {

            statement.setInt(1, idUser);
            statement.setString(2, listName);
            statement.setInt(3, idMultimedia);
            statement.setInt(4, currentEpisode);
            statement.setString(5, status);

            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                String errorMessage = "You already has the multimedia in that list";
                throw new DatabaseException(23, errorMessage);
            }
            throw new SQLException(e);
        }
    }

    public static void removeMultimediaFromList(int idUser, int idMultimedia, String listName) throws SQLException {
        String sqlStatement = "DELETE lhm FROM list_has_multimedia AS lhm " +
                "WHERE lhm.multimedia_id = ? AND lhm.list_id = (" +
                "SELECT l.id FROM list AS l " +
                "WHERE l.name = ? AND l.user_id = ?)";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(sqlStatement)) {

            statement.setInt(1, idMultimedia);
            statement.setString(2, listName);
            statement.setInt(3, idUser);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    // Private methods

    private static Map<String, Object> getListData(int idList, String listName){
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

            Map<String, Object> listData = new HashMap<>();
            List<Map<String, Object>> multimediaDataList = new ArrayList<>();
            listData.put("listName", listName);
            listData.put("multimediaItems", multimediaDataList);

            while(resultSet.next()){
                Map<String, Object> multimediaData = new HashMap<>();
                multimediaData.put("type", resultSet.getString(1));
                multimediaData.put("title", resultSet.getString(2));
                multimediaData.put("apiId", resultSet.getInt(3));
                multimediaData.put("status", resultSet.getString(4));
                multimediaData.put("currentEpisode", resultSet.getInt(5));
                multimediaData.put("totalEpisodes", resultSet.getInt(6));

                multimediaDataList.add(multimediaData);
            }

            return listData;
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
                RuntimeException ex = new RuntimeException("MySQL service might be down");
                ex.setStackTrace(new StackTraceElement[]{});
                throw ex;
            } else {
                throw new RuntimeException(e);
            }
        }
    }
}
