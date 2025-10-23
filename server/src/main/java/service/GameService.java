package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.EntryAlreadyExistsException;
import dataaccess.EntryNotFoundException;
import model.GameData;
import model.UserData;
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

    /**
     * Attemps to join a user to a game with the given team color and game ID.
     *
     * @param userData    The user data.
     * @param playerColor The desired player color.
     * @param gameID      The ID of the game to join.
     */
    public void joinGame(UserData userData, ChessGame.TeamColor playerColor, int gameID) throws TeamAlreadyTakenException, EntryNotFoundException {
        GameData gameData = dataAccess.getGame(gameID);

        if (
                (playerColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null) ||
                        (playerColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null)
        ) {
            throw new TeamAlreadyTakenException("The " + playerColor + " team is already taken.");
        }

        var updatedGame = (playerColor == ChessGame.TeamColor.WHITE) ?
                new GameData(
                        gameData.gameID(),
                        userData.username(), // White
                        gameData.blackUsername(),
                        gameData.gameName(),
                        gameData.game()
                ) :
                new GameData(
                        gameData.gameID(),
                        gameData.whiteUsername(),
                        userData.username(), // Black
                        gameData.gameName(),
                        gameData.game()
                );

        dataAccess.updateGame(gameID, updatedGame);
    }
}
