package io.joshatron.tak.engine.board;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Piece {
    private String player;
    private PieceType type;
}
