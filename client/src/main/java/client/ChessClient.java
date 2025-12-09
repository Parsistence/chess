package client;

import chess.*;
import chess.ChessGame.TeamColor;
import chess.ChessPiece.PieceType;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import ui.ChessBoardStringRenderer;
import websocket.ServerMessageHandler;
import websocket.ServerMessageObserver;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.util.*;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private final ServerFacade server;
    private final WebSocketFacade webSocket;
    private final ChessBoardStringRenderer boardStringRenderer = new ChessBoardStringRenderer();
    private final ChessMoveInterpreter moveInterpreter = new ChessMoveInterpreter();
    private ClientState state;
    private String username;
    private String authToken;
    private List<GameData> gameList;
    private ChessGame game;
    private TeamColor playerColor = TeamColor.WHITE;
    private int gameID;
    private Scanner scanner;

    public enum ClientState {
        PRE_LOGIN, POST_LOGIN, GAMEPLAY
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        var messageHandler = new ServerMessageHandler(this);
        webSocket = new WebSocketFacade(serverUrl, messageHandler);
        state = ClientState.PRE_LOGIN;
    }

    public void run() {
        System.out.println("♕ Welcome to the Chess Client! Type `help` to get started ♕");
        this.scanner = new Scanner(System.in);

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
                case "login" -> login(args);
                case "register" -> register(args);

                // PostLogin
                case "logout" -> logout();
                case "create" -> createGame(args);
                case "list" -> listGames();
                case "play" -> playGame(args);
                case "observe" -> observeGame(args);

                // Gameplay
                case "displayboard" -> displayBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(args);
                case "resign" -> resign();
                case "showmoves" -> showMoves(args);

                // Stateless/Always Allowed
                default -> help();
                case "quit" -> quit();
            };
        } catch (ResponseException e) {
            return SET_TEXT_COLOR_RED + e.getMessage() + RESET_TEXT_COLOR;
        }
    }

    private String help() {
        String stateCommands = switch (state) {
            case PRE_LOGIN -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Pre-Login Commands====" + RESET_TEXT_COLOR,
                    buildUsageMessage(
                            "login", "<username> <password>", "Log in with an existing account."
                    ),
                    buildUsageMessage(
                            "register", "<username> <password> <email>", "Register a new account and log in."
                    )
            ));
            case POST_LOGIN -> String.join("\n", List.of(
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
            case GAMEPLAY -> String.join("\n", List.of(
                    SET_TEXT_COLOR_BLUE + "====Gameplay Commands====" + RESET_TEXT_COLOR,
                    buildUsageMessage(
                            "displayboard",
                            null,
                            "Display the chessboard in its current state."
                    ),
                    buildUsageMessage("leave", "Leave the current game."),
                    buildUsageMessage(
                            "move",
                            "<startpos> <endpos> <promotion(optional)>",
                            "Move a piece from startpos to endpos. A position should be formatted like `B4` or `b4`.\n" +
                                    "A valid promotion piece should be given if applicable (e.g. `q` for Queen, `k` for Knight, etc.)"
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
        if (state != ClientState.PRE_LOGIN) {
            if (state == ClientState.GAMEPLAY) {
                System.out.println("Leaving game...");
                try {
                    leaveGame();
                    System.out.println("Successfully left game.");
                } catch (ResponseException e) {
                    System.out.println("Unable to leave game. Logging out without leaving game.");
                }
            }
            System.out.println("Logging out...");
            try {
                server.logout(authToken);
                System.out.println("Logout successful.");
            } catch (ResponseException e) {
                System.out.println("Logout unsuccessful. Quitting without logging out.");
            }
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
        state = ClientState.POST_LOGIN;

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
        state = ClientState.POST_LOGIN;

        return "Registration successful! You are now logged in as " + SET_TEXT_BOLD + username + RESET_TEXT_BOLD_FAINT + "." +
                " Type `help` for a list of post-login commands.";
    }

    private String logout() throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.POST_LOGIN);

        server.logout(authToken);
        username = "";
        authToken = "";
        state = ClientState.PRE_LOGIN;

        return "Logout successful!";
    }

    private String createGame(String[] args) throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.POST_LOGIN);
        if (args.length < 1) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "create", "<name>"
            ));
        }

        String gameName = String.join(" ", args);

        server.createGame(authToken, gameName);

        return "game " + SET_TEXT_BOLD + gameName + RESET_TEXT_BOLD_FAINT + " successfully created!";
    }

    private String listGames() throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.POST_LOGIN);

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
        assertState(ClientState.POST_LOGIN);
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

        playerColor = switch (args[1].toLowerCase()) {
            case "white" -> TeamColor.WHITE;
            case "black" -> TeamColor.BLACK;
            default -> throw new ResponseException("Error: Second argument must be WHITE or BLACK.");
        };

        GameData game;
        try {
            game = gameList.get(gameListID);
            gameID = game.gameID();
        } catch (Throwable e) {
            throw new ResponseException("Error: No game found with ID " + (gameListID + 1) + ".");
        }

        server.joinGame(authToken, playerColor, gameID);
        try {
            webSocket.connect(authToken, gameID);
        } catch (IOException e) {
            throw new ResponseException("There was an issue connecting to the server via websocket.");
        }

        state = ClientState.GAMEPLAY;
        return "Successfully joined game " + game.gameName() + " as " + playerColor + ".";
    }

    private String observeGame(String[] args) throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.POST_LOGIN);
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

        GameData game;
        try {
            game = gameList.get(gameListID);
            gameID = game.gameID();
        } catch (Throwable e) {
            throw new ResponseException("Error: No game found with ID " + (gameListID + 1) + ".");
        }

        try {
            webSocket.connect(authToken, gameID);
        } catch (IOException e) {
            throwWebSocketException();
        }

        playerColor = TeamColor.WHITE;
        state = ClientState.GAMEPLAY;

        return "Successfully joined game " + game.gameName() + " as an observer.";
    }

    private String displayBoard() throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.GAMEPLAY);
        return boardStringRenderer.renderBoard(game.getBoard(), playerColor);
    }

    private String leaveGame() throws ResponseException {
        try {
            webSocket.leave(authToken, gameID);
        } catch (IOException e) {
            throwWebSocketException();
        }
        state = ClientState.POST_LOGIN;
        return "You left the game.";
    }

    private String makeMove(String[] args) throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.GAMEPLAY);
        if (args.length < 2) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "move",
                    "<startpos> <endpos> <promotion(optional)>",
                    "Move a piece from startpos to endpos. A position should be formatted like `B4` or `b4`.\n" +
                            "A valid promotion piece should be given if applicable (e.g. `q` for Queen, `k` for Knight, etc.)"
            ));
        }

        String startPosString = args[0];
        String endPosString = args[1];
        ChessPosition startPos, endPos;
        PieceType promotionPiece = null;
        try {
            startPos = moveInterpreter.positionFromString(startPosString);
            endPos = moveInterpreter.positionFromString(endPosString);
            if (args.length > 2) {
                promotionPiece = moveInterpreter.promotionTypeFromString(args[2]);
            }
        } catch (Exception e) {
            throw new ResponseException("Error: " + e.getMessage());
        }

        var move = new ChessMove(startPos, endPos, promotionPiece);

        if (!game.validMoves(startPos).contains(move)) {
            throw new ResponseException("Error: move from " + startPosString + " to " + endPosString + " is not valid.");
        }

        var pieceMoved = game.getBoard().getPiece(startPos).getPieceType();

        try {
            webSocket.makeMove(authToken, gameID, move);
        } catch (IOException e) {
            throwWebSocketException();
        }

        return "Moved " + pieceMoved + " from " + startPosString + " to " + endPosString +
                ((promotionPiece != null) ? "(promoted to " + promotionPiece + ")" : "") + ".";
    }

    private String resign() throws ResponseException {
        System.out.println("Are you sure you want to resign? This will end the game. (y/n)");
        String response = promptInput(scanner);
        if (!response.isEmpty() && Character.toLowerCase(response.charAt(0)) == 'y') {
            try {
                webSocket.resign(authToken, gameID);
            } catch (IOException e) {
                throwWebSocketException();
            }

            return "You resigned.";
        } else {
            return "Resign cancelled.";
        }
    }

    private String showMoves(String[] args) throws ResponseException {
        assertLoggedIn();
        assertState(ClientState.GAMEPLAY);
        if (args.length < 1) {
            throw new ResponseException("Usage: " + buildUsageMessage(
                    "showmoves",
                    "<piecepos>",
                    "Highlight all legal moves a piece at piecepos can make. A position should be formatted like `B4` or `b4`."
            ));
        }

        String startPosString = args[0];
        ChessPosition startPos;
        Collection<ChessMove> validMoves = new HashSet<>();
        try {
            startPos = moveInterpreter.positionFromString(startPosString);
            validMoves.add(new ChessMove(startPos, startPos));
            validMoves = game.validMoves(startPos);
        } catch (Exception e) {
            throw new ResponseException("Error: " + e.getMessage());
        }

        return boardStringRenderer.renderBoard(game.getBoard(), playerColor, validMoves);
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
        if (state != ClientState.PRE_LOGIN) {
            prompt += username + " ";
        }
        prompt += ">>> ";
        System.out.print(prompt);
    }

    private void throwWebSocketException() throws ResponseException {
        throw new ResponseException("There was an issue connecting to the server via websocket.");
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
        if (state == ClientState.PRE_LOGIN) {
            throw new ResponseException(
                    "Error: You are not logged in. Please log in using `login`, or create a new account using 'register'."
            );
        }
    }

    private void assertState(ClientState clientState) throws ResponseException {
        if (state != clientState) {
            String stateDescription = switch (clientState) {
                case PRE_LOGIN -> "not logged in";
                case POST_LOGIN -> "logged in (but not in game)";
                case GAMEPLAY -> "gameplay";
            };
            throw new ResponseException(
                    "Error: You must be in the " + stateDescription + " state to use this command."
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
        this.game = game;
        System.out.println("\r" + boardStringRenderer.renderBoard(game.getBoard(), playerColor));
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
