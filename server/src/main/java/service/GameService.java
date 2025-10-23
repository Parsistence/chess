package service;

import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import model.GameData;
import server.CreateGameResponse;

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

    /**
     * Create a new chess game.
     *
     * @param gameName The name to give the new game.
     * @return A CreateGameResponse with the game ID.
     */
    public CreateGameResponse createGame(String gameName) throws EntryAlreadyExistsException {
        GameData gameData = dataAccess.createGame(gameName);
        return new CreateGameResponse(gameData.gameID());
    }
}
