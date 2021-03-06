package io.joshatron.tak.engine.board;

import io.joshatron.bgt.engine.component.Piece;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import lombok.Data;


@Data
public class TakPiece extends Piece {
    private PieceType type;

    public TakPiece(PlayerIndicator owner, PieceType type) {
        super(owner);
        this.type = type;
    }

    @Override
    public String toString() {
        if(getOwner() == PlayerIndicator.WHITE) {
            return type.getAcronym().toLowerCase();
        }
        else {
            return type.getAcronym().toUpperCase();
        }
    }
}
