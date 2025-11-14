import server.ServerFacade;

import java.util.Scanner;

public class ChessClient {
    private final ServerFacade server;
    private ClientState state;

    public enum ClientState {
        PreLogin, PostLogin, Gameplay
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        state = ClientState.PreLogin;
    }

    public void run() {
        System.out.println("♕ Welcome to the Chess Client! Type `help` to get started ♕");
        var scanner = new Scanner(System.in);

        String result = "";
        while (!result.equals("quit")) {
            String input = promptInput(scanner);

            try {
                result = evaluate(input);
                System.out.println(result);
            } catch (Throwable ex) {
                System.out.printf("Error: %s%n", ex.getMessage());
            }
        }
    }

    private String evaluate(String input) {
        // TODO
        return "Not implemented yet!";
    }

    private String promptInput(Scanner scanner) {
        System.out.print(">>> ");
        return scanner.nextLine();
    }
}
