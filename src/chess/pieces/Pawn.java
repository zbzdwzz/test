package chess.pieces;

import chess.Player;

/**
 * The Pawn
 */
public class Pawn extends Piece {
	
    public Pawn(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'p';
    }
}
