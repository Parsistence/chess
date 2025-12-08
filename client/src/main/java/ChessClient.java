import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessGame.TeamColor;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import ui.ChessBoardStringRenderer;
import websocket.ServerMessageHandler;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final WebSocketFacade webSocket;
    private final ChessBoardStringRenderer boardStringRenderer = new ChessBoardStringRenderer();
    private ClientState state;
    private String username;
    private String authToken;
    private List<GameData> gameList;
    private ChessBoard board;
    private TeamColor playerColor = TeamColor.WHITE;

    public enum ClientState {
        PreLogin, PostLogin, Gameplay
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        var messageHandler = new ServerMessageHandler(this);
        webSocket = new WebSocketFacade(serverUrl, messageHandler);
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
            ));
            case PostLogin -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Post-Login Commands====" + RESET_TEXT_COLOR,
                    buildUsageMessage(
                            "logout", null, "Log out of the current account."
                    ),
                    buildUsageMessage(
                            "create", "<name>", "Create a new chess game with the given name."
                    ),
                    buildUsageMessage(
                            "list", null, "Retrieve and list all chess games on the server."
                    ),
                    buildUsageMessage(
                            "play",
                            "<ID> [WHITE|BLACK]",
                            "Join an existing game and play as the given color. Run `list` first to update the list of games."
                    ),
                    buildUsageMessage(
                            "observe",
                            "<ID>",
                            "Join an existing game as an observer. Run `list` first to update the list of games."
                    )
            ));
            case Gameplay -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Gameplay Commands====" + RESET_TEXT_COLOR,
                    buildUsageMessage(
                            "displayboard",
                            null,
                            "Display the chessboard in its current state."
                    ),
                    buildUsageMessage("leave", "Leave the current game."),
                    buildUsageMessage(
                            "move",
                            "<startpos> <endpos>",
                            "Move a piece from startpos to endpos. A position should be formatted like `B4` or `b4`."
                    ),
                    buildUsageMessage("resign", null, "Resign from the current game."),
                    buildUsageMessage(
                            "showmoves",
                            "<piecepos>",
                            "Highlight all legal moves a piece at piecepos can make. A position should be formatted like `B4` or `b4`."
                    )
            ));
        };

        String statelessCommands = String.join("\n", List.of(
                buildUsageMessage("quit", null, "Quit the Chess client, logging out if necessary."),
                buildUsageMessage("help", null, "Display this message.")
        ));

        return String.join("\n", List.of(stateCommands, statelessCommands));
    }

    private String quit() {
        if (state != ClientState.PreLogin) {
            System.out.println("Logging out...");
            try {
                server.logout(authToken);
            } catch (ResponseException e) {
                System.out.println("Logout unsuccessful. Quitting without logging out.");
            }
            System.out.println("Logout successful.");
        }

        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "♕ Goodbye! ♕" + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR);
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

        return "login successful! Welcome, " + SET_TEXT_BOLD + username + RESET_TEXT_BOLD_FAINT + "!" +
                " Type `help` for a list of post-login commands.";
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

        return "Registration successful! You are now logged in as " + SET_TEXT_BOLD + username + RESET_TEXT_BOLD_FAINT + "." +
                " Type `help` for a list of post-login commands.";
    }

    private String logout() throws ResponseException {
        assertLoggedIn();

        server.logout(authToken);
        username = "";
        authToken = "";
        state = ClientState.PreLogin;

        return "Logout successful!";
    }

    private String createGame(String[] args) throws ResponseException {
        assertLoggedIn();
        if (args.length < 1) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "create", "<name>"
            ));
        }

        String gameName = String.join(" ", args);

        server.createGame(authToken, gameName);

        return "game " + SET_TEXT_BOLD + gameName + RESET_TEXT_BOLD_FAINT + " successfully created!";
    }

    private String listGames(String[] args) throws ResponseException {
        assertLoggedIn();

        gameList = List.copyOf(server.listGames(authToken));

        return displayGameList();
    }

    private String displayGameList() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(SET_TEXT_COLOR_BLUE).append("====Games List====").append(RESET_TEXT_COLOR).append("\n");
        for (int i = 0; i < gameList.size(); i++) {
            GameData game = gameList.get(i);
            stringBuilder
                    .append(SET_TEXT_BOLD).append(i + 1).append(". ").append(RESET_TEXT_BOLD_FAINT)
                    .append(SET_TEXT_COLOR_YELLOW).append(game.gameName())
                    .append("; ").append(SET_TEXT_COLOR_BLUE).append(SET_TEXT_BOLD).append("White: ").append(RESET_TEXT_BOLD_FAINT)
                    .append((game.whiteUsername() != null) ? game.whiteUsername() : "none")
                    .append("; ").append(SET_TEXT_COLOR_MAGENTA).append(SET_TEXT_BOLD).append("Black: ").append(RESET_TEXT_BOLD_FAINT)
                    .append((game.blackUsername() != null) ? game.blackUsername() : "none")
                    .append(RESET_TEXT_COLOR).append("\n")
            ;
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // remove last newline

        return stringBuilder.toString();
    }

    private String playGame(String[] args) throws ResponseException {
        assertLoggedIn();
        if (args.length < 2) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "play", "<ID> [WHITE|BLACK]"
            ));
        }
        int gameListID;
        try {
            gameListID = Integer.parseInt(args[0]) - 1; // 1 indexed
        } catch (NumberFormatException e) {
            throw new ResponseException("Error: ID must be an integer.");
        }

        TeamColor playerColor = switch (args[1].toLowerCase()) {
            case "white" -> TeamColor.WHITE;
            case "black" -> TeamColor.BLACK;
            default -> throw new ResponseException("Error: Second argument must be WHITE or BLACK.");
        };

        int realGameID;
        GameData game;
        try {
            game = gameList.get(gameListID);
            realGameID = game.gameID();
        } catch (Throwable e) {
            throw new ResponseException("Error: No game found with ID " + (gameListID + 1) + ".");
        }

        server.joinGame(authToken, playerColor, realGameID);

        return "Successfully joined game " + game.gameName() + " as " + playerColor + ".";
    }

    private String observeGame(String[] args) throws ResponseException {
        assertLoggedIn();
        if (args.length < 1) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "observe", "<ID>"
            ));
        }
        int gameListID;
        try {
            gameListID = Integer.parseInt(args[0]) - 1; // 1 indexed
        } catch (NumberFormatException e) {
            throw new ResponseException("Error: ID must be an integer.");
        }

        int realGameID;
        GameData game;
        try {
            game = gameList.get(gameListID);
            realGameID = game.gameID();
        } catch (Throwable e) {
            throw new ResponseException("Error: No game found with ID " + (gameListID + 1) + ".");
        }

        // TODO Phase 6: Join as an observer

        return "Successfully joined game " + game.gameName() + " as an observer.";
    }

    private String promptInput(Scanner scanner) {
        printPromptString();

        System.out.print(SET_TEXT_COLOR_GREEN + SET_TEXT_ITALIC);
        String input = scanner.nextLine();
        System.out.print(RESET_TEXT_COLOR + RESET_TEXT_ITALIC);

        return input;
    }

    private void printPromptString() {
        var prompt = "";
        if (state != ClientState.PreLogin) {
            prompt += username + " ";
        }
        prompt += ">>> ";
        System.out.print(prompt);
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

    private void assertLoggedIn() throws ResponseException {
        if (state == ClientState.PreLogin) {
            throw new ResponseException(
                    "Error: You are not logged in. Please log in using `login`, or create a new account using 'register'."
            );
        }
    }


    /**
     * Loads a game from the server.
     *
     * @param game The game data to load.
     */
    @Override
    public void loadGame(ChessGame game) {
        board = game.getBoard();
        System.out.println("\r" + boardStringRenderer.renderBoard(board, playerColor));
        printPromptString();
    }

    /**
     * Display an error message from the server.
     *
     * @param errorMessage The error from the server.
     */
    @Override
    public void notifyError(String errorMessage) {
        System.out.println("\r" + SET_TEXT_COLOR_RED + "Error: " + errorMessage + RESET_TEXT_COLOR);
        printPromptString();
    }

    /**
     * Display a notification from the server.
     *
     * @param message notification from the server.
     */
    @Override
    public void notify(String message) {
        System.out.println("\r" + SET_TEXT_COLOR_GREEN + message + RESET_TEXT_COLOR);
        printPromptString();
    }
}
