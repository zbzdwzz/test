package chess.pieces;

import chess.Player;

/**
 * A base class for chess pieces. This class is an abstract class because it provides functionality 
 * common to all pieces, but cannot be instantiated directly
 */
public abstract class Piece {
	
	/** The player that owns this piece */
    private final Player owner;

    /** Protected constructor to guard against creation */
    protected Piece(Player owner) {
        this.owner = owner;
    }

    /**
     * Fetch the identifier for this piece.
     * @return The properly cased piece identifier
     */
    public char getIdentifier() {
        char id = getIdentifyingCharacter();
        if (owner.equals(Player.White)) {
            return Character.toLowerCase(id);
        } else {
            return Character.toUpperCase(id);
        }
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Each piece must know how to identify itself
     * @return The identifier for this piece
     */
    protected abstract char getIdentifyingCharacter();

    @Override
    /**
     * Basic stringify method
     */
    public String toString() {
        return owner.toString() + " " + this.getClass().getSimpleName();
    }
}
