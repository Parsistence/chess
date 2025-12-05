package websocket;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Integer> connections = new ConcurrentHashMap<>();

    public void add(String authToken, int gameID) {
        connections.put(authToken, gameID);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }
}
