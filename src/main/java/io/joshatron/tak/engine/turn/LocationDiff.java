package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.PieceStack;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LocationDiff {
    private int toRemove;
    private PieceStack toAdd;
}
