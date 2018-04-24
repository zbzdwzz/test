package chess;

import chess.pieces.Piece;

import java.io.*;
import java.util.regex.Pattern;

/**
 * This class provides the basic Command Line Interface (CLI) to the Chess game.
 */
public class ChessGame {
	
	/** The new line character */ 
    private static final String NEWLINE = System.getProperty("line.separator");
    
    /** Regular expression to capture specific moves*/
    private static final Pattern MOVE_PATTERN = Pattern.compile("^move\\s+[a-h][1-8]\\s+[a-h][1-8]\\s*$");

    /** convenience fields for input*/
    private final BufferedReader inReader;
    private final PrintStream outStream;

    /** The state of an active chess game*/
    private GameState gameState = null;

    public ChessGame(InputStream inputStream, PrintStream outStream) {
        this.inReader = new BufferedReader(new InputStreamReader(inputStream));
        this.outStream = outStream;
        writeOutput("Welcome to Chess!");
    }

    /**
     * Write the string to the output
     * @param str The string to write
     */
    private void writeOutput(String str) {
        this.outStream.println(str);
    }

    /**
     * Retrieve a string from the console, returning after the user hits the 'Return' key.
     * @return The input from the user, or an empty-length string if they did not type anything.
     */
    private String getInput() {
        try {
            this.outStream.print("> ");
            return inReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from input: ", e);
        }
    }

    /**
     * The main event loop for a game of chess. This loop iterates indefinitely until the game is terminated.
     */
    public void startEventLoop() {
        writeOutput("Type 'help' for a list of commands.");
        doNewGame();

        while (true) {
            showBoard();
            String currentPlayer = gameState.getCurrentPlayer().toString();
            writeOutput(currentPlayer + "'s Move");

            String input = getInput();
            if (input == null) {
                break; // No more input possible; this is the only way to exit the event loop
            } else if (input.length() > 0) {
                if (input.equals("help")) {
                    showCommands();
                } else if (input.equals("new")) {
                    doNewGame();
                } else if (input.equals("quit")) {
                    writeOutput("Goodbye!");
                    System.exit(0);
                } else if (input.equals("board")) {
                    writeOutput("Current Game:");
                } else if (input.equals("list")) {
                    writeOutput(gameState.list());
                } else if (MOVE_PATTERN.matcher(input).find()) {
                    String[] args = input.split("\\s+");
                    if (!gameState.move(args[1], args[2])) {
                        writeOutput("Invalid move");
                    } 
                    else if (gameState.isCheckMate()) {
                        showBoard();
                        writeOutput("The game is over. Congrats to " + currentPlayer + ".");
                        System.exit(0);
                    } else if (gameState.isDraw()) {
                        showBoard();
                        writeOutput("The game is over. It's Draw.");
                        System.exit(0);
                    }
                    else if (gameState.isCheck()) {
                        showBoard();
                        String otherPlayer = (gameState.getCurrentPlayer() == Player.White ? Player.Black.toString() : Player.White.toString());
                        writeOutput("Check! " + otherPlayer + " must save the king!" );
                    }
                } else {
                    writeOutput("I didn't understand that.  Type 'help' for a list of commands.");
                }
            }
        }
    }

    /**
     * Initiate a new game of chess
     */
    private void doNewGame() {
        gameState = new GameState();
        gameState.reset();
    }

    /**
     * Show the current chess board
     */
    private void showBoard() {
        writeOutput(getBoardAsString());
    }

    /**
     * List the available commands.
     */
    private void showCommands() {
        writeOutput("Possible commands: ");
        writeOutput("    'help'                       Show this menu");
        writeOutput("    'quit'                       Quit Chess");
        writeOutput("    'new'                        Create a new game");
        writeOutput("    'board'                      Show the chess board");
        writeOutput("    'list'                       List all possible moves");
        writeOutput("    'move <colrow> <colrow>'     Make a move");
    }

    /**
     * Display the board for the user(s)
     */
    public String getBoardAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append(NEWLINE);

        printColumnLabels(builder);
        for (int i = Position.MAX_ROW; i >= Position.MIN_ROW; i--) {
            printSeparator(builder);
            printSquares(i, builder);
        }

        printSeparator(builder);
        printColumnLabels(builder);

        return builder.toString();
    }

    /**
     * Display the rows/columns of the chess board.
     * 
     * @param rowLabel The name of the current row
     * @param builder The string output for the board.
     */
    private void printSquares(int rowLabel, StringBuilder builder) {
        builder.append(rowLabel);

        for (char c = Position.MIN_COLUMN; c <= Position.MAX_COLUMN; c++) {
            Piece piece = gameState.getPieceAt(String.valueOf(c) + rowLabel);
            char pieceChar = piece == null ? ' ' : piece.getIdentifier();
            builder.append(" | ").append(pieceChar);
        }
        builder.append(" | ").append(rowLabel).append(NEWLINE);
    }

    /**
     * Convenience function to print row/col separators
     * @param builder The string output for the board.
     */
    private void printSeparator(StringBuilder builder) {
        builder.append("  +---+---+---+---+---+---+---+---+").append(NEWLINE);
    }

    /**
     * Convenience function to display colums
     * @param builder The string output for the board.
     */
    private void printColumnLabels(StringBuilder builder) {
        builder.append("   ");
        for (char c = Position.MIN_COLUMN; c <= Position.MAX_COLUMN; c++) {
            builder.append(" ").append(c).append("  ");
        }

        builder.append(NEWLINE);
    }

    /**
     * Main driver
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ChessGame cli = new ChessGame(System.in, System.out);
        cli.startEventLoop();
    }
}
