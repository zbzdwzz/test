package chess.pieces;

import chess.Player;

/**
 * The Queen class
 */
public class Queen extends Piece {
	
    public Queen(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'q';
    }
}
