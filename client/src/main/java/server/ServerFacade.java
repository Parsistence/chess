package server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
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
     * @return The resultant auth token
     * @throws ResponseException If there is an issue communicating with the server.
     */
    public String register(String username, String password, String email) throws ResponseException {
        var userData = new UserData(username, password, email);
        BodyPublisher requestBody = BodyPublishers.ofString(new Gson().toJson(userData));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .method("POST", requestBody)
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ResponseException(e);
        }

        if (response.statusCode() / 100 != 2) {
            throw ResponseException.fromJson(response.body());
        }

        AuthData authData = new Gson().fromJson(response.body(), AuthData.class);
        if (authData.authToken().isBlank()) {
            throw new ResponseException("Server did not return an auth token in its response.");
        }

        return authData.authToken();
    }

    public void login() {
        // TODO
    }

    public void logout() {
        // TODO
    }

    public void listGames() {
        // TODO
    }

    public void createGame() {
        // TODO
    }

    public void joinGame() {
        // TODO
    }

}
