package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.game.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TurnDiff {
    private Turn turn;
    private LocationDiff[][] locationDiffs;
    private GameResult result;
    private int filledDiff;
    private int whiteStoneDiff;
    private int whiteCapstoneDiff;
    private int blackStoneDiff;
    private int blackCapstoneDiff;
}
