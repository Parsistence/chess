package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
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

    public void logout(String authToken) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        HttpRequest request = buildHttpRequest("DELETE", "/session", null, authHeader);
        sendHttpRequest(request); // No response body
    }

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

    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {
        HttpHeader authHeader = new HttpHeader("authorization", authToken);
        var requestBody = new JoinGameRequest(playerColor, gameID);
        HttpRequest request = buildHttpRequest("PUT", "/game", requestBody, authHeader);
        sendHttpRequest(request); // No response body
    }

    private HttpRequest buildHttpRequest(String method, String path, Object body) {
        return buildHttpRequest(method, path, body, null);
    }

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

    private HttpResponse<String> sendHttpRequest(HttpRequest request) throws ResponseException {
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ResponseException(e);
        }

        if (response.statusCode() / 100 != 2) {
            throw ResponseException.fromJson(response.body());
        }

        return response;
    }
}
