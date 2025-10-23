package service;

import dataaccess.DataAccess;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /**
     * Clear all game data from the server.
     */
    public void clearAll() {
        dataAccess.clearGameData();
    }

    /**
     * Gets all games in the server.
     *
     * @return A collection of all games in the server.
     */
    public Collection<GameData> listGames() {
        return dataAccess.listGames();
    }
}
