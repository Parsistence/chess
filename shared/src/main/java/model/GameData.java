package model;

import chess.ChessGame;
import chess.ChessGame.TeamColor;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData(int gameID, String gameName) {
        this(gameID, null, null, gameName, new ChessGame());
    }

    public GameData withPlayers(String whiteUsername, String blackUsername) {
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    /**
     * Get the team color of a user in the game.
     *
     * @param username The name of the user.
     * @return WHITE if the user is on the White team; BLACK if the user is on the Black team; null otherwise.
     */
    public TeamColor getTeamOfPlayer(String username) {
        TeamColor teamColor;

        if (whiteUsername.equals(username)) {
            teamColor = TeamColor.WHITE;
        } else if (blackUsername.equals(username)) {
            teamColor = TeamColor.BLACK;
        } else {
            teamColor = null;
        }

        return teamColor;
    }
}
