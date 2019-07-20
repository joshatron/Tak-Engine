package io.joshatron.tak.engine.board;

import io.joshatron.bgt.engine.player.PlayerIndicator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Piece {
    private PlayerIndicator player;
    private PieceType type;
}
