package chess;


import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Piece;
import chess.pieces.Queen;
import chess.pieces.Rook;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class that represents the current state of the game. 
 * Basically, what pieces are in which positions on the
 * board.
 */
public class GameState {

    /** The current player */
    private Player currentPlayer = Player.White;

    /** A map of board positions to pieces at that position  */
    private Map<Position, Piece> board;

    /** Create the game state. */
    public GameState() {
        board = new HashMap<>();
    }

    /** 
     * Fetch the current play
     * @return The player whose turn it is
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * All possible moves for the remaining pieces and their moves.
     * @return
     */
    public String list() {
        StringBuilder sb = new StringBuilder();
        Map<Position, Set<Position>> moves = generatePossibleMoves();
        moves.forEach(this::filterCheck);
        moves.forEach((key, value) -> value.forEach(p -> sb.append(key).append(" ").append(p).append("\n")));
        return sb.toString();
    }

    public boolean isCheckMate() {
        // Checkmate: if king under attack and after all possible moves it is still under attack
        Position kingPosition = getKingPosition();
        revertPlayer();
        // if we can attack king
        boolean check = generatePossiblePositions().contains(kingPosition);
        revertPlayer();

        return check && isDraw();
    }
    
    public boolean isCheck() {
        // Checkmate: if king under attack 
        Position kingPosition = getKingPosition();
        revertPlayer();
        // if we can attack king
        boolean check = generatePossiblePositions().contains(kingPosition);
        revertPlayer();

        return check;
    }

    public boolean isDraw() {
        Map<Position, Set<Position>> moves = generatePossibleMoves();
        moves.forEach(this::filterCheck);
        // opponent can't move
        return 0 == moves.values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet())
                .size();
    }

    public boolean move(String start, String end) {
        Position startPosition = new Position(start);
        Position endPosition = new Position(end);
        // is move valid
        Map<Position, Set<Position>> moves = generatePossibleMoves();
        moves.forEach(this::filterCheck);
        if (!moves.get(startPosition).contains(endPosition)) {
            return false;
        }
        // make move
        move(startPosition, endPosition);
        revertPlayer();
        return true;
    }

    /**
     * Call to initialize the game state into the starting positions
     */
    public void reset() {
        // White Pieces
        board.put(new Position("a1"), new Rook(Player.White));
        board.put(new Position("b1"), new Knight(Player.White));
        board.put(new Position("c1"), new Bishop(Player.White));
        board.put(new Position("d1"), new Queen(Player.White));
        board.put(new Position("e1"), new King(Player.White));
        board.put(new Position("f1"), new Bishop(Player.White));
        board.put(new Position("g1"), new Knight(Player.White));
        board.put(new Position("h1"), new Rook(Player.White));
        board.put(new Position("a2"), new Pawn(Player.White));
        board.put(new Position("b2"), new Pawn(Player.White));
        board.put(new Position("c2"), new Pawn(Player.White));
        board.put(new Position("d2"), new Pawn(Player.White));
        board.put(new Position("e2"), new Pawn(Player.White));
        board.put(new Position("f2"), new Pawn(Player.White));
        board.put(new Position("g2"), new Pawn(Player.White));
        board.put(new Position("h2"), new Pawn(Player.White));

        // Black Pieces
        board.put(new Position("a8"), new Rook(Player.Black));
        board.put(new Position("b8"), new Knight(Player.Black));
        board.put(new Position("c8"), new Bishop(Player.Black));
        board.put(new Position("d8"), new Queen(Player.Black));
        board.put(new Position("e8"), new King(Player.Black));
        board.put(new Position("f8"), new Bishop(Player.Black));
        board.put(new Position("g8"), new Knight(Player.Black));
        board.put(new Position("h8"), new Rook(Player.Black));
        board.put(new Position("a7"), new Pawn(Player.Black));
        board.put(new Position("b7"), new Pawn(Player.Black));
        board.put(new Position("c7"), new Pawn(Player.Black));
        board.put(new Position("d7"), new Pawn(Player.Black));
        board.put(new Position("e7"), new Pawn(Player.Black));
        board.put(new Position("f7"), new Pawn(Player.Black));
        board.put(new Position("g7"), new Pawn(Player.Black));
        board.put(new Position("h7"), new Pawn(Player.Black));
    }

    /**
     * Get the piece at the position specified by the String
     * @param colrow The string indication of position; i.e. "d5"
     * @return The piece at that position, or null if it does not exist.
     */
    public Piece getPieceAt(String colrow) {
        return board.get(new Position(colrow));
    }

    /**
     * Execute a move.
     * @param start the starting position
     * @param end the ending position
     */
    private void move(Position start, Position end) {
        board.put(end, board.remove(start));
    }

    /**
     * Set the alternate player
     */
    private void revertPlayer() {
        currentPlayer = currentPlayer == Player.White ? Player.Black : Player.White;
    }

    /**
     * Determines if the current Piece is owned by the current player
     * @param currentPiece The current Piece
     * @return true of the Piece owner is the current player; false otherwise 
     * @see Piece
     */
    private boolean isNotOwnPiece(Piece currentPiece) {
        return currentPiece.getOwner() != currentPlayer;
    }

    private boolean attackItsOwnPiece(Piece attackedPiece) {
        return attackedPiece != null && attackedPiece.getOwner() == currentPlayer;
    }

    private Set<Position> generatePossiblePositions() {
        return generatePossibleMoves()
                .values()
                .stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Fetch the position of the current player's king
     * @return the Position of the king
     * @see Position
     */
    private Position getKingPosition() {
        return board.entrySet()
                .stream()
                .filter(e -> e.getValue() != null && e.getValue().getClass() == King.class && e.getValue().getOwner() == currentPlayer)
                .findFirst()
                .get()
                .getKey();
    }

    /**
     * Check the board's possible moves to see of the game is in a state of 'check'.
     * @param startPosition The starting position
     * @param possibleMoves the set of viable moves that can be made in check
     * @see Position
     */
    private void filterCheck(Position startPosition, Set<Position> possibleMoves) {
    		
        possibleMoves.removeIf(endPosition -> {
            Piece attackedPiece = board.get(endPosition);
            // make move
            move(startPosition, endPosition);

            // get king position
            Position kingPosition = getKingPosition();

            revertPlayer();
            boolean result = generatePossiblePositions().contains(kingPosition);
            revertPlayer();

            // revert move
            move(endPosition, startPosition);
            if (attackedPiece != null) {
                board.put(endPosition, attackedPiece);
            }
          
            return result;
        });
    }

    /**
     * Populate the set of possible moves
     * @param startPosition The starting position
     * @param possibleMoves the set of viable moves that can be made
     * @see Position
     */
    private void filterPossibleMoves(Position startPosition, Set<Position> possibleMoves) {
        possibleMoves.removeIf(Objects::isNull); // clear from nulls
        if (isNotOwnPiece(board.get(startPosition))) { // is not own piece
            possibleMoves.clear();
        }
        possibleMoves.removeIf(endPosition -> attackItsOwnPiece(board.get(endPosition))); // attack its own piece
    }

    /**
     * Create a set of possible moves for each Piece
     * @return The set of moves for each Piece on the board
     * @see Piece
     */
    private Map<Position, Set<Position>> generatePossibleMoves() {
        Map<Position, Set<Position>> moves = new HashMap<>();

        // if we use board we get ConcurrentModificationException
        new HashMap<>(board).forEach((position, piece) -> {
            if (piece == null) {
                return;
            }
            Class clazz = piece.getClass();
            if (King.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForKing(position));
            } else if (Knight.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForKnight(position));
            } else if (Pawn.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForPawn(position));
            } else if (Rook.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForRook(position));
            } else if (Bishop.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForBishop(position));
            } else if (Queen.class.isAssignableFrom(clazz)) {
                moves.put(position, generatePossibleMovesForQueen(position));
            }
        });

        moves.forEach(this::filterPossibleMoves);

        return moves;
    }

    /** 
     * Generate moves for the knight
     * @param startPosition the starting position
     * @return a set of moves for the knight Piece
     * @see Knight
     */
    private Set<Position> generatePossibleMovesForKnight(Position startPosition) {
        int x = startPosition.getX();
        int y = startPosition.getY();

        Set<Position> result = new HashSet<>();
        // up right
        result.add(PositionFactory.createPosition(x + 1, y + 2));
        // up left
        result.add(PositionFactory.createPosition(x - 1, y + 2));
        // down right
        result.add(PositionFactory.createPosition(x + 1, y - 2));
        // down left
        result.add(PositionFactory.createPosition(x - 1, y - 2));
        // left up
        result.add(PositionFactory.createPosition(x - 2, y + 1));
        // left down
        result.add(PositionFactory.createPosition(x - 2, y - 1));
        // right up
        result.add(PositionFactory.createPosition(x + 2, y + 1));
        // right down
        result.add(PositionFactory.createPosition(x + 2, y - 1));

        return result;
    }

    /**
     * Generate moves for the Pawn Piece
     * @param startPosition the starting position
     * @return a set of moves for the pawn Piece
     * @see Pawn
     */
    private Set<Position> generatePossibleMovesForPawn(Position startPosition) {
        int x = startPosition.getX();
        int y = startPosition.getY();

        Set<Position> result = new HashSet<>();
        Position endPosition = null;
        if (currentPlayer == Player.White) {
            endPosition = PositionFactory.createPosition(x, y + 1);
            if (endPosition != null && board.get(endPosition) == null) { // if we don't have piece ahead
                result.add(endPosition); // normal move
                if (y == 1) { // if white pawn start position
                    endPosition = PositionFactory.createPosition(x, y + 2);
                    if (endPosition != null && board.get(endPosition) == null) { // if we don't have piece ahead
                        result.add(endPosition); // normal move
                    }
                }
            }

            // if attack opponent
            endPosition = PositionFactory.createPosition(x + 1, y + 1);
            if (isPawnAttacks(Player.Black, endPosition)) {
                result.add(endPosition);
            }
            endPosition = PositionFactory.createPosition(x - 1, y + 1);
            if (isPawnAttacks(Player.Black, endPosition)) {
                result.add(endPosition);
            }
        }
        if (currentPlayer == Player.Black) {
            endPosition = PositionFactory.createPosition(x, y - 1);
            if (endPosition != null && board.get(endPosition) == null) { // if we don't have piece ahead
                result.add(endPosition); // normal move
                if (y == 6) { // if black pawn start position
                    endPosition = PositionFactory.createPosition(x, y - 2);
                    if (endPosition != null && board.get(endPosition) == null) { // if we don't have piece ahead
                        result.add(endPosition); // normal move
                    }
                }
            }

            // if attack opponent
            endPosition = PositionFactory.createPosition(x + 1, y - 1);
            if (isPawnAttacks(Player.White, endPosition)) {
                result.add(endPosition);
            }
            endPosition = PositionFactory.createPosition(x - 1, y - 1);
            if (isPawnAttacks(Player.White, endPosition)) {
                result.add(endPosition);
            }
        }

        return result;
    }

    /**
     * return true if Pawn can successfully attack, false otherwise  
     * @param attackedPlayer the player under attack
     * @param endPosition the position of the attacking move
     * @return  true if Pawn can successfully attack, false otherwise  
     */
    private boolean isPawnAttacks(Player attackedPlayer, Position endPosition) {
        if (endPosition != null) {
            Piece attackedPiece = board.get(endPosition);
            if (attackedPiece != null && attackedPiece.getOwner() == attackedPlayer) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate moves for the King Piece
     * @param startPosition the starting position
     * @return a set of moves for the king Piece
     * @see King
     */
    private Set<Position> generatePossibleMovesForKing(Position startPosition) {
        int x = startPosition.getX();
        int y = startPosition.getY();

        Set<Position> result = new HashSet<>();
        // up left
        result.add(PositionFactory.createPosition(x - 1, y + 1));
        // up direct
        result.add(PositionFactory.createPosition(x, y + 1));
        // up right
        result.add(PositionFactory.createPosition(x + 1, y + 1));
        // right
        result.add(PositionFactory.createPosition(x + 1, y));
        // down right
        result.add(PositionFactory.createPosition(x + 1, y - 1));
        // down direct
        result.add(PositionFactory.createPosition(x, y - 1));
        // down left
        result.add(PositionFactory.createPosition(x - 1, y - 1));
        // left
        result.add(PositionFactory.createPosition(x - 1, y));

        return result;
    }

    /**
     * Generate moves for the Rook Piece
     * @param startPosition the starting position
     * @return a set of moves for the rook Piece
     * @see Rook
     */
    private Set<Position> generatePossibleMovesForRook(Position startPosition) {
        int x = startPosition.getX();
        int y = startPosition.getY();

        Set<Position> result = new HashSet<>();
        // up
        for (int i = y + 1; i <= Position.MAX_AXIS; i++) {
            Position endPosition = PositionFactory.createPosition(x, i);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // down
        for (int i = y - 1; i >= Position.MIN_AXIS; i--) {
            Position endPosition = PositionFactory.createPosition(x, i);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // left
        for (int i = x - 1; i >= Position.MIN_AXIS; i--) {
            Position endPosition = PositionFactory.createPosition(i, y);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // right
        for (int i = x + 1; i <= Position.MAX_AXIS; i++) {
            Position endPosition = PositionFactory.createPosition(i, y);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }

        return result;
    }

    /**
     * Generate moves for the Bishop Piece
     * @param startPosition the starting position
     * @return a set of moves for the bishop Piece
     * @see Bishop
     */
    private Set<Position> generatePossibleMovesForBishop(Position startPosition) {
        int x = startPosition.getX();
        int y = startPosition.getY();

        Set<Position> result = new HashSet<>();
        // up left
        for (int i = x - 1, j = y + 1; i >= Position.MIN_AXIS && j <= Position.MAX_AXIS; i--, j++) {
            Position endPosition = PositionFactory.createPosition(i, j);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // up right
        for (int i = x + 1, j = y + 1; i <= Position.MAX_AXIS && j <= Position.MAX_AXIS; i++, j++) {
            Position endPosition = PositionFactory.createPosition(i, j);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // down right
        for (int i = x + 1, j = y - 1; i <= Position.MAX_AXIS && j >= Position.MIN_AXIS; i++, j--) {
            Position endPosition = PositionFactory.createPosition(i, j);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }
        // down left
        for (int i = x - 1, j = y - 1; i >= Position.MIN_AXIS && j >= Position.MIN_AXIS; i--, j--) {
            Position endPosition = PositionFactory.createPosition(i, j);
            if (endPosition == null) {
                break;
            }
            result.add(endPosition);
            if (board.get(endPosition) != null) {
                break;
            }
        }

        return result;
    }

    /**
     * Generate moves for the Queen Piece
     * @param startPosition the starting position
     * @return a set of moves for the queen Piece
     * @see Queen
     */
    private Set<Position> generatePossibleMovesForQueen(Position startPosition) {
        Set<Position> result = generatePossibleMovesForBishop(startPosition);
        result.addAll(generatePossibleMovesForRook(startPosition));

        return result;
    }
}
