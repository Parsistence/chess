import chess.*;

public class Main {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";
        if (args.length > 0) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();
        } catch (Throwable ex) {
            System.out.printf("Error starting up client: %s%n", ex.getMessage());
        }
    }
}