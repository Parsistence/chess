import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private ClientState state;
    private String username;
    private String authToken;

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
        while (!result.equals("quitting...")) {
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
            return SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR;
        }
    }

    private String help() {
        String stateCommands = switch (state) {
            case PreLogin -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Pre-Login Commands====" + RESET_TEXT_COLOR,
                    buildUsageMessage(
                            "login", "<username> <password>", "Log in with an existing account."
                    ),
                    buildUsageMessage(
                            "register", "<username> <password> <email>", "Register a new account and log in."
                    )
            )); // TODO
            case PostLogin -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Post-Login Commands====" + RESET_TEXT_COLOR
            )); // TODO
            case Gameplay -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Gameplay Commands====" + RESET_TEXT_COLOR
            )); // TODO
        };

        String statelessCommands = String.join("\n", List.of(
                buildUsageMessage("quit", null, "Quit the Chess client."),
                buildUsageMessage("help", null, "Display this message.")
        ));

        return String.join("\n", List.of(stateCommands, statelessCommands));
    }

    private String quit() {
        System.out.println("♕ Goodbye! ♕");
        return "quitting...";
    }

    private String login(String[] args) throws ResponseException {
        if (args.length < 2) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "login", "<username> <password>"
            ));
        }

        String username = args[0];
        String password = args[1];

        authToken = server.login(username, password);

        this.username = username;
        state = ClientState.PostLogin;

        return "login successful! Welcome, " + SET_TEXT_BOLD + username + RESET_TEXT_COLOR + "!";
    }

    private String register(String[] args) throws ResponseException {
        if (args.length < 3) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "register", "<username> <password> <email>"
            ));
        }

        String username = args[0];
        String password = args[1];
        String email = args[2];

        authToken = server.register(username, password, email);

        this.username = username;
        state = ClientState.PostLogin;

        return "Registration successful! You are now logged in as " + SET_TEXT_BOLD + username + RESET_TEXT_COLOR;
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
        if (state != ClientState.PreLogin) {
            System.out.print(username + " ");
        }
        System.out.print(">>> ");
        return scanner.nextLine();
    }

    private String buildUsageMessage(String cmd, String args) {
        return buildUsageMessage(cmd, args, null);
    }

    private String buildUsageMessage(String cmd, String args, String desc) {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(SET_TEXT_COLOR_YELLOW).append(cmd);

        if (args != null) {
            stringBuilder.append(SET_TEXT_COLOR_BLUE).append(" ").append(args);
        }

        if (desc != null) {
            stringBuilder.append(SET_TEXT_COLOR_MAGENTA).append(" - ").append(desc);
        }

        stringBuilder.append(RESET_TEXT_COLOR);

        return stringBuilder.toString();
    }
}
