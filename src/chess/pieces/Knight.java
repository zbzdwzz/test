package chess.pieces;

import chess.Player;

/**
 * The Knight class
 */
public class Knight extends Piece {
	
    public Knight(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'n';
    }
}
