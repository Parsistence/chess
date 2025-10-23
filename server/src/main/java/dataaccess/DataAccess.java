package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    /**
     * Clears all user data in the database.
     */
    void clearUsers();

    /**
     * Clears all auth data in the database.
     */
    void clearAuthData();

    /**
     * Clears all game data in the database.
     */
    void clearGameData();

    /**
     * Insert data for a new user into the data store.
     *
     * @param userData The user data to insert.
     * @throws EntryAlreadyExistsException If the user data was unable to be inserted into the data store.
     */
    void insertUser(UserData userData) throws EntryAlreadyExistsException;

    /**
     * Get data for an existing user from the data store.
     *
     * @param username The username of the user whose data is to be retrieved.
     * @return A UserData object corresponding to the given username.
     * @throws EntryNotFoundException If the user data was unable to be retrieved from the data store.
     */
    UserData getUser(String username) throws EntryNotFoundException;

    /**
     * Insert new auth data into the database
     *
     * @param authData The auth data to insert
     * @throws EntryAlreadyExistsException If auth data already exists in database
     */
    void insertAuthData(AuthData authData) throws EntryAlreadyExistsException;

    /**
     * Get auth data from the database.
     *
     * @param authToken The auth token corresponding to the auth data.
     * @return The auth data.
     */
    AuthData getAuthData(String authToken) throws EntryNotFoundException;

    /**
     * Remove auth data from the database.
     *
     * @param authToken the auth token associated with the auth data.
     */
    void removeAuth(String authToken);

    /**
     * Gets all games in the database.
     *
     * @return A collection of all games in the database.
     */
    Collection<GameData> listGames();

    /**
     * Creates a new game and adds it to the database.
     *
     * @param gameName The name to give the new game.
     * @return The game data added to the database.
     */
    GameData createGame(String gameName) throws EntryAlreadyExistsException;

    /**
     * Gets a user in the database given the user's auth token.
     *
     * @param authToken The auth token associated with the user.
     * @return The user data.
     */
    UserData getUserFromAuth(String authToken) throws EntryNotFoundException;

    /**
     * Gets a game from the database given a game ID.
     *
     * @param gameID The game ID associated with the game.
     * @return The game.
     */
    GameData getGame(int gameID) throws EntryNotFoundException;

    /**
     * Updates game data in the database with the given data.
     *
     * @param gameID      The game ID.
     * @param updatedGame The data to update into the database.
     */
    void updateGame(int gameID, GameData updatedGame) throws EntryNotFoundException;
}
