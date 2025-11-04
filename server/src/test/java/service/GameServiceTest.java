package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import server.CreateGameResponse;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void clearAll() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        GameData game1 = dataAccess.createGame("Booyah");
        GameData game2 = dataAccess.createGame("Wowie Zowie");
        GameData game3 = dataAccess.createGame("Lock In 2025");

        assertDoesNotThrow(() -> dataAccess.getGame(game1.gameID()));
        assertDoesNotThrow(() -> dataAccess.getGame(game2.gameID()));
        assertDoesNotThrow(() -> dataAccess.getGame(game3.gameID()));

        gameService.clearAll();

        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game1.gameID()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game2.gameID()));
        assertThrows(EntryNotFoundException.class, () -> dataAccess.getGame(game3.gameID()));
    }

    @Test
    void listGames() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        GameData game1 = dataAccess.createGame("Booyah");
        GameData game2 = dataAccess.createGame("Wowie Zowie");
        GameData game3 = dataAccess.createGame("Lock In 2025");

        Collection<GameData> games = gameService.listGames();

        assertTrue(games.containsAll(List.of(
                game1,
                game2,
                game3
        )));
    }

    // No known negative cases for GameService#listGames (authentication occurs separately outside of method).

    @Test
    void createGame() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        CreateGameResponse res1 = gameService.createGame("Booyah");
        CreateGameResponse res2 = gameService.createGame("Wowie Zowie");
        CreateGameResponse res3 = gameService.createGame("Lock In 2025");

        assertDoesNotThrow(() -> {
            dataAccess.getGame(res1.gameID());
            dataAccess.getGame(res2.gameID());
            dataAccess.getGame(res3.gameID());
        });
    }

    // No known negative cases for GameService#createGame
    // (authentication and request validation occurs separately outside of method).

    @Test
    void joinGame() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        int gameID = dataAccess.createGame("Booyah").gameID();

        UserData user1 = new UserData(
                "team_white_guy",
                "whitegoesfirst",
                "twg@jmail.com"
        );
        assertDoesNotThrow(() -> gameService.joinGame(user1, ChessGame.TeamColor.WHITE, gameID));
        GameData game = dataAccess.getGame(gameID);
        assertEquals(game.whiteUsername(), user1.username());

        UserData user2 = new UserData(
                "team_black_guy",
                "blackalwayswins",
                "tbg@jmail.com"
        );
        assertDoesNotThrow(() -> gameService.joinGame(user2, ChessGame.TeamColor.BLACK, gameID));
        game = dataAccess.getGame(gameID);
        assertEquals(game.blackUsername(), user2.username());

    }

    @Test
    void joinTeamAlreadyTaken() throws DataAccessException {
        DataAccess dataAccess = new MemoryDataAccess();
        GameService gameService = new GameService(dataAccess);

        int gameID = dataAccess.createGame("Booyah").gameID();

        UserData user1 = new UserData(
                "i_like_white",
                "whiterules",
                "ilw@jmail.com"
        );
        assertDoesNotThrow(() -> gameService.joinGame(user1, ChessGame.TeamColor.WHITE, gameID));
        GameData game = dataAccess.getGame(gameID);
        assertEquals(game.whiteUsername(), user1.username());

        UserData user2 = new UserData(
                "i_also_like_white",
                "whiterocks",
                "ialw@jmail.com"
        );
        assertThrows(TeamAlreadyTakenException.class, () -> gameService.joinGame(user2, ChessGame.TeamColor.WHITE, gameID));
    }
}