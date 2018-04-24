package chess.pieces;

import chess.Player;

/**
 * The King class
 */
public class King extends Piece {
	
    public King(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'k';
    }
}
