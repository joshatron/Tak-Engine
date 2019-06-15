package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.game.GameResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class TurnDiff {
    private Turn turn;
    private HashMap<BoardLocation, LocationDiff> locationDiffs;
    private HashMap<BoardLocation, Integer> pathBoardDiffs;
    private GameResult result;
    private int filledDiff;
    private int whiteStoneDiff;
    private int whiteCapstoneDiff;
    private int blackStoneDiff;
    private int blackCapstoneDiff;

    public TurnDiff() {
        locationDiffs = new HashMap<>();
        pathBoardDiffs = new HashMap<>();
    }
}
