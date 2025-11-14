import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

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
            } catch (Throwable e) {
                System.out.printf("Error: %s%n", e.getMessage());
            }
        }
    }

    private String evaluate(String input) {
        String[] tokens = input.split(" ");
        String cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            return switch (cmd) {
                // PreLogin
                case "quit" -> quit();
                case "login" -> login(args);
                case "register" -> register(args);

                // PostLogin
                case "logout" -> logout();
                case "create" -> createGame(args);
                case "list" -> listGames(args);
                case "play" -> playGame(args);
                case "observe" -> observeGame(args);

                default -> help();
            };
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + "Error: " + e.getMessage() + RESET_TEXT_COLOR;
        }
    }

    private String help() {
        return switch (state) {
            case PreLogin -> SET_TEXT_COLOR_BLUE + "====Pre-Login Commands====\n" +
                    RESET_TEXT_COLOR; // TODO
            case PostLogin -> """
                    Post-Login commands go here!"""; // TODO
            case Gameplay -> """
                    Gameplay commands go here!"""; // TODO
        };
    }

    private String quit() {
        System.out.println("♕ Goodbye! ♕");
        return "quit";
    }

    private String login(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String register(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String logout() throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String createGame(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String listGames(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String playGame(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String observeGame(String[] args) throws ResponseException {
        // TODO
        throw new ResponseException("Not implemented yet!");
    }

    private String promptInput(Scanner scanner) {
        System.out.print(">>> ");
        return scanner.nextLine();
    }
}
