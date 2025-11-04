package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

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
                `game_name` VARCHAR(256) NOT NULL UNIQUE,
                `game` TEXT NOT NULL,
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
            try (var statement = conn.prepareStatement("SELECT * FROM user_data WHERE username = ?")) {
                statement.setString(1, username);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String password = rs.getString("password");
                    String email = rs.getString("email");
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
     * Verifies a user's password matches the given password.
     *
     * @param username The username.
     * @param password The password belonging to the user, unencrypted.
     * @return True if the password matches; false otherwise.
     */
    public boolean verifyPassword(String username, String password) throws DataAccessException {
        String hashedPassword = getUser(username).password();
        return BCrypt.checkpw(password, hashedPassword);
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
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM auth_data WHERE auth_token = ?")) {
                statement.setString(1, authToken);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    return new AuthData(username, authToken);
                } else {
                    throw new EntryNotFoundException("No user with auth token " + authToken + " found in database.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Remove auth data from the database.
     *
     * @param authToken the auth token associated with the auth data.
     */
    @Override
    public void removeAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("DELETE FROM auth_data WHERE auth_token=?")) {
                statement.setString(1, authToken);
                int rowCount = statement.executeUpdate();
                if (rowCount == 0) {
                    throw new EntryNotFoundException("Could not remove auth token " + authToken + " because it was not found.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Gets all games in the database.
     *
     * @return A collection of all games in the database.
     */
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM game_data")) {
                ResultSet rs = statement.executeQuery();
                Collection<GameData> gameList = new HashSet<>();
                while (rs.next()) {
                    int gameID = rs.getInt("game_id");
                    String whiteUsername = rs.getString("white_username");
                    String blackUsername = rs.getString("black_username");
                    String gameName = rs.getString("game_name");
                    String chessGameJson = rs.getString("game");
                    ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);
                    gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                }
                return gameList;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Creates a new game and adds it to the database.
     *
     * @param gameName The name to give the new game.
     * @return The game data added to the database.
     */
    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("INSERT INTO game_data (game_name, game) VALUES (?, ?)")) {
                statement.setString(1, gameName);
                var game = new ChessGame();
                String gameJson = new Gson().toJson(game);
                statement.setString(2, gameJson);
                statement.executeUpdate();

                try (var gameIDStatement = conn.prepareStatement("SELECT game_id FROM game_data WHERE game_name = ?")) {
                    gameIDStatement.setString(1, gameName);
                    ResultSet rs = gameIDStatement.executeQuery();
                    if (!rs.next()) {
                        throw new DataAccessException("unable to query game ID for game " + gameName);
                    }
                    int gameID = rs.getInt(1);
                    return new GameData(gameID, gameName);
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate Entry code for MySQL
                throw new EntryAlreadyExistsException(e);
            }
            throw new DataAccessException(e);
        }
    }

    /**
     * Gets a user in the database given the user's auth token.
     *
     * @param authToken The auth token associated with the user.
     * @return The user data.
     */
    @Override
    public UserData getUserFromAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM auth_data WHERE auth_token = ?")) {
                statement.setString(1, authToken);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    return getUser(username);
                } else {
                    throw new EntryNotFoundException("No user with auth token " + authToken + " found in database.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Gets a game from the database given a game ID.
     *
     * @param gameID The game ID associated with the game.
     * @return The game.
     */
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var statement = conn.prepareStatement("SELECT * FROM game_data WHERE game_id = ?")) {
                statement.setInt(1, gameID);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    String chessGameJson = rs.getString("game");
                    ChessGame chessGame = new Gson().fromJson(chessGameJson, ChessGame.class);
                    return new GameData(
                            rs.getInt("game_id"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            rs.getString("game_name"),
                            chessGame
                    );
                }
                throw new EntryNotFoundException("No game found with ID " + gameID);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /**
     * Updates game data in the database with the given data.
     *
     * @param gameID      The game ID.
     * @param updatedGame The data to update into the database.
     */
    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statementStr = """
                    UPDATE game_data
                        SET
                            white_username = ?,
                            black_username = ?,
                            game_name = ?,
                            game = ?
                        WHERE game_id = ?
                    """;
            try (var statement = conn.prepareStatement(statementStr)) {
                String gameJson = new Gson().toJson(updatedGame);

                statement.setString(1, updatedGame.whiteUsername());
                statement.setString(2, updatedGame.blackUsername());
                statement.setString(3, updatedGame.gameName());
                statement.setString(4, gameJson);
                statement.setInt(5, gameID);

                if (statement.executeUpdate() == 0) {
                    throw new EntryNotFoundException("No game found with ID " + gameID);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
