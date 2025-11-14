package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Collection;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String serverUrl;

    /**
     * Create a new ServerFacade, which handles HTTP requests to the server behind the scenes.
     *
     * @param serverUrl The URL of the server to communicate with.
     */
    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    /**
     * Register a new user and get the resultant auth token.
     *
     * @param username The username to be associated with the user.
     * @param password The password to be associated with the user.
     * @param email    The email to be associated with the user.
     * @return The resultant auth token.
     * @throws ResponseException If there is an issue communicating with the server.
     */
    public String register(String username, String password, String email) throws ResponseException {
        var userData = new UserData(username, password, email);

        HttpRequest request = buildHttpRequest("POST", "/user", userData);
        HttpResponse<String> response = sendHttpRequest(request);

        AuthData authData = new Gson().fromJson(response.body(), AuthData.class);
        if (authData.authToken().isBlank()) {
            throw new ResponseException("Server did not return an auth token in its response.");
        }

        return authData.authToken();
    }

    /**
     * Log in an existing user and get the resultant auth token.
     *
     * @param username The username associated with the user.
     * @param password The password associated with the user.
     * @return The resultant auth token.
     * @throws ResponseException If there is an issue communicating with the server.
     */
    public String login(String username, String password) throws ResponseException {
        var loginRequest = new LoginRequest(username, password);

        HttpRequest request = buildHttpRequest("POST", "/session", loginRequest);
        HttpResponse<String> response = sendHttpRequest(request);

        AuthData authData = new Gson().fromJson(response.body(), AuthData.class);
        if (authData.authToken().isBlank()) {
            throw new ResponseException("Server did not return an auth token in its response.");
        }

        return authData.authToken();
    }

    /**
     * Logout an existing user.
     *
     * @param authToken The auth token associated with the logged-in user.
     * @throws ResponseException If there is an issue communicating with the server,
     *                           such as the auth token not found in the database.
     */
    public void logout(String authToken) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        HttpRequest request = buildHttpRequest("DELETE", "/session", null, authHeader);
        sendHttpRequest(request); // No response body
    }

    /**
     * List all games in the database.
     *
     * @param authToken The auth token associated with the user.
     * @return A Collection of GameData objects.
     * @throws ResponseException If there is an issue communicating with the server.
     */
    public Collection<GameData> listGames(String authToken) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        HttpRequest request = buildHttpRequest("GET", "/game", null, authHeader);
        HttpResponse<String> response = sendHttpRequest(request);

        GameDataList gameDataList = gson.fromJson(response.body(), GameDataList.class);
        Collection<GameData> games = gameDataList.games();
        if (games == null) {
            throw new ResponseException("Server did not return a list of games in its response.");
        }

        return games;
    }

    /**
     * Create a new chess game.
     *
     * @param authToken The auth token associated with the user.
     * @param gameName  The name to give the new game.
     * @return The integer ID of the game.
     * @throws ResponseException If there was an issue communicating with the server,
     *                           such as trying to create a game with a name that already exists.
     */
    public int createGame(String authToken, String gameName) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        var requestBody = new CreateGameRequest(gameName);
        HttpRequest request = buildHttpRequest("POST", "/game", requestBody, authHeader);
        HttpResponse<String> response = sendHttpRequest(request);

        CreateGameResponse createGameResponse = gson.fromJson(response.body(), CreateGameResponse.class);
        int gameID = createGameResponse.gameID();
        if (gameID == 0) {
            throw new ResponseException("Server did not return a game ID in its response.");
        }

        return gameID;
    }

    /**
     * Join an existing game with the given team color.
     *
     * @param authToken   The auth token associated with the joining user.
     * @param playerColor The team color the joining user will join as.
     * @param gameID      The integer ID of the game to join.
     * @throws ResponseException If there is an issue communicating with the server,
     *                           such as the given player color already taken.
     */
    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        var requestBody = new JoinGameRequest(playerColor, gameID);
        HttpRequest request = buildHttpRequest("PUT", "/game", requestBody, authHeader);
        sendHttpRequest(request); // No response body
    }

    /**
     * Build an HttpRequest object given a method, path, and body.
     *
     * @param method The method of the request.
     * @param path   The path of the request.
     * @param body   The object representing the request body. This will be serialized into JSON format.
     * @return The resultant HttpRequest.
     */
    private HttpRequest buildHttpRequest(String method, String path, Object body) {
        return buildHttpRequest(method, path, body, null);
    }

    /**
     * Build an HttpRequest object given a method, path, body, and header.
     *
     * @param method The method of the request.
     * @param path   The path of the request.
     * @param body   The object representing the request body. This will be serialized into JSON format.
     * @param header (Optional) An HttpHeader record represent the name and value of the header.
     * @return The resultant HttpRequest.
     */
    private HttpRequest buildHttpRequest(String method, String path, Object body, HttpHeader header) {
        BodyPublisher requestBody = BodyPublishers.ofString(gson.toJson(body));
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, requestBody);
        if (header != null) {
            builder.header(header.name(), header.value());
        }
        return builder.build();
    }

    /**
     * Send an HttpRequest to the server.
     *
     * @param request The HttpRequest to send.
     * @return The HttpResponse received from the server.
     * @throws ResponseException If there was an issue communicating with the server,
     *                           including if any status code other than 2XX was received.
     */
    private HttpResponse<String> sendHttpRequest(HttpRequest request) throws ResponseException {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            throw new ResponseException("Unable to establish connection to the server.");
        } catch (IOException | InterruptedException e) {
            throw new ResponseException(e);
        }

        if (response.statusCode() / 100 != 2) {
            throw ResponseException.fromJson(response.body());
        }

        return response;
    }
}
