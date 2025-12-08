package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import server.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade {
    private final Session session;

    public WebSocketFacade(String serverUrl, ServerMessageHandler messageHandler) {
        try {
            String wsUrl = serverUrl.replace("http", "ws");
            URI socketUri = new URI(wsUrl + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(this, socketUri);

            session.addMessageHandler(messageHandler);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sends a CONNECT message to the server to join a chess game.
     *
     * @param authToken The auth token of the connecting user.
     * @param gameID    The ID of the chess game to connect to.
     */
    public void connect(String authToken, int gameID) throws IOException {
        var connectCommand = new UserGameCommand(CommandType.CONNECT, authToken, gameID);
        String connectJson = new Gson().toJson(connectCommand);
        sendJson(connectJson);
    }

    /**
     * Sends a MAKE_MOVE message to the server to make a move in a chess game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to make the move on.
     * @param move      The move to make.
     */
    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        var makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);
        String makeMoveJson = new Gson().toJson(makeMoveCommand);
        sendJson(makeMoveJson);
    }

    /**
     * Sends a LEAVE message to the server to leave a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void leave(String authToken, int gameID) throws IOException {
        var leaveCommand = new UserGameCommand(CommandType.LEAVE, authToken, gameID);
        String leaveJson = new Gson().toJson(leaveCommand);
        sendJson(leaveJson);
    }

    /**
     * Sends a RESIGN message to the server to resign (but not leave) a game.
     *
     * @param authToken The auth token of the user.
     * @param gameID    The ID of the chess game to leave.
     */
    public void resign(String authToken, int gameID) throws IOException {
        var resignCommand = new UserGameCommand(CommandType.RESIGN, authToken, gameID);
        String resignJson = new Gson().toJson(resignCommand);
        sendJson(resignJson);
    }

    /**
     * Sends JSON data to the server.
     *
     * @param json The Json data to send.
     * @throws IOException If there was an issue sending the data.
     */
    private void sendJson(String json) throws IOException {
        session.getBasicRemote().sendText(json);
    }
}
