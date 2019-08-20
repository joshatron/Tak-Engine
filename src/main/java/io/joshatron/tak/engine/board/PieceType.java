package io.joshatron.tak.engine.board;

import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;

public enum PieceType {
    STONE("s"),
    WALL("w"),
    CAPSTONE("c");

    private String acronym;

    PieceType(String acronym) {
        this.acronym = acronym;
    }

    public String getAcronym() {
        return acronym;
    }

    public static PieceType fromString(String pieceType) throws BoardGameEngineException {
        for(PieceType piece : PieceType.values()) {
            if(piece.acronym.equalsIgnoreCase(pieceType)) {
                return piece;
            }
        }

        throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PIECE_TYPE);
    }
}
