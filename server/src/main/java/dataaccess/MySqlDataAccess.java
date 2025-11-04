package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user_data (
                `username` VARCHAR(256) NOT NULL,
                `password` VARCHAR(256) NOT NULL,
                `email` VARCHAR(256) NOT NULL,
                PRIMARY KEY (`username`),
                INDEX (`email`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth_data (
            `username` VARCHAR(256) NOT NULL,
            `auth_token` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`auth_token`),
            INDEX (`username`)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS game_data (
                `game_id` INT NOT NULL AUTO_INCREMENT,
                `white_username` VARCHAR(256) DEFAULT NULL,
                `black_username` VARCHAR(256) DEFAULT NULL,
                `game_name` VARCHAR(256) NOT NULL,
                `game` TEXT DEFAULT NULL,
                PRIMARY KEY (`game_id`),
                INDEX (`game_name`)
            )
            """
    };

    /**
     * Configures the database with the necessary tables.
     *
     * @throws DataAccessException If there was an issue configuring the database.
     */
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Clears all user data in the database.
     */
    @Override
    public void clearUsers() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE TABLE user_data")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Clears all auth data in the database.
     */
    @Override
    public void clearAuthData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE TABLE auth_data")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Clears all game data in the database.
     */
    @Override
    public void clearGameData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("TRUNCATE TABLE game_data")) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Insert data for a new user into the data store.
     * <br>
     * <b>Note</b>: The user password is encrypted before it is inserted into the data store.
     *
     * @param userData The user data to insert.
     * @throws EntryAlreadyExistsException If the user data was unable to be inserted into the data store.
     */
    @Override
    public void insertUser(UserData userData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO user_data VALUES (?, ?, ?)")) {
                statement.setString(1, userData.username());
                String encryptedPassword = encryptPassword(userData.password());
                statement.setString(2, encryptedPassword);
                statement.setString(3, userData.email());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate Entry code for MySQL
                throw new EntryAlreadyExistsException(e);
            }
            throw new DataAccessException(e);
        }
    }

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws EntryNotFoundException If the user data was unable to be retrieved from the data store.
     */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT (username, password, email) FROM user_data WHERE username=?")) {
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String password = rs.getString(2);
                    String email = rs.getString(3);
                    return new UserData(username, password, email);
                } else {
                    throw new EntryNotFoundException("No user " + username + " found in database.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Insert new auth data into the database
     *
     * @param authData The auth data to insert
     * @throws EntryAlreadyExistsException If auth data already exists in database
     */
    @Override
    public void insertAuthData(AuthData authData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO auth_data (username, auth_token) VALUES (?, ?)")) {
                statement.setString(1, authData.username());
                statement.setString(2, authData.authToken());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate Entry code for MySQL
                throw new EntryAlreadyExistsException(e);
            }
            throw new DataAccessException(e);
        }
    }

    /**
     * Get auth data from the database.
     *
     * @param authToken The auth token corresponding to the auth data.
     * @return The auth data.
     */
    @Override
    public AuthData getAuthData(String authToken) throws EntryNotFoundException {
        return null;
    }

    /**
     * Remove auth data from the database.
     *
     * @param authToken the auth token associated with the auth data.
     */
    @Override
    public void removeAuth(String authToken) {

    }

    /**
     * Gets all games in the database.
     *
     * @return A collection of all games in the database.
     */
    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    /**
     * Creates a new game and adds it to the database.
     *
     * @param gameName The name to give the new game.
     * @return The game data added to the database.
     */
    @Override
    public GameData createGame(String gameName) throws EntryAlreadyExistsException {
        return null;
    }

    /**
     * Gets a user in the database given the user's auth token.
     *
     * @param authToken The auth token associated with the user.
     * @return The user data.
     */
    @Override
    public UserData getUserFromAuth(String authToken) throws EntryNotFoundException {
        return null;
    }

    /**
     * Gets a game from the database given a game ID.
     *
     * @param gameID The game ID associated with the game.
     * @return The game.
     */
    @Override
    public GameData getGame(int gameID) throws EntryNotFoundException {
        return null;
    }

    /**
     * Updates game data in the database with the given data.
     *
     * @param gameID      The game ID.
     * @param updatedGame The data to update into the database.
     */
    @Override
    public void updateGame(int gameID, GameData updatedGame) throws EntryNotFoundException {

    }
}
