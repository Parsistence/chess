package websocket;

import dataaccess.DataAccessException;
import dataaccess.EntryNotFoundException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class ConnectionManager {
    public enum UserType {
        WHITE_PLAYER, BLACK_PLAYER, OBSERVER
    }

    private final ConcurrentHashMap<Integer, HashMap<String, UserType>> connections = new ConcurrentHashMap<>();
    private final MemoryDataAccess dataAccess = new MemoryDataAccess();

    private HashMap<String, UserType> getGameConnections(int gameID) {
        return connections.computeIfAbsent(gameID, k -> new HashMap<>());
    }


    /**
     * Adds an authenticated user to an existing game and calls the given callback function.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to join.
     * @param callback  The callback function to call, which accepts the username and user type
     *                  of the user that was added.
     * @throws EntryNotFoundException If the authenticated user is not found in the database.
     */
    public void add(String authToken, int gameID, BiConsumer<String, UserType> callback) throws EntryNotFoundException {
        String username = dataAccess.getUserFromAuth(authToken).username();
        GameData game = dataAccess.getGame(gameID);

        UserType userType;
        if (game.whiteUsername().equals(username)) {
            userType = UserType.WHITE_PLAYER;
        } else if (game.blackUsername().equals(username)) {
            userType = UserType.BLACK_PLAYER;
        } else {
            userType = UserType.OBSERVER;
        }

        getGameConnections(gameID).put(authToken, userType);

        if (callback != null) {
            callback.accept(username, userType);
        }
    }

    public void remove(String authToken, int gameID) {
        getGameConnections(gameID).remove(authToken);
    }
}
