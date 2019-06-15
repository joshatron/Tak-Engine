package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnDiff;

import java.util.ArrayList;
import java.util.List;

public class TakEngine {

    private GameState state;
    private DiffNode root;
    private boolean restrictTurns;

    public TakEngine(Player first, int size) throws TakEngineException {
        this(first, size, false);
    }

    public TakEngine(Player first, int size, boolean restrictTurns) throws TakEngineException {
        state = new GameState(first, size);
        root = new DiffNode(new TurnDiff());
        this.restrictTurns = restrictTurns;
    }

    public List<Turn> getPossibleTurns() {
        return getPossibleTurns(root);
    }

    private List<Turn> getPossibleTurns(DiffNode currentRoot) {
        if(!currentRoot.isChildrenFull()) {
            List<BoardLocation> locations = new ArrayList<>();

            for(int x = 0; x < state.getBoardSize(); x++) {
                for(int y = 0; y < state.getBoardSize(); y++) {
                    locations.add(new BoardLocation(x, y));
                }
            }

            locations.parallelStream().map(location -> getPossibleDiffsForLocation(location, currentRoot))
                    .forEach(diffs -> currentRoot.getChildren().addAll(diffs));
        }

        //TODO: restrict if neccessary, then return
        return null;
    }

    private List<DiffNode> getPossibleDiffsForLocation(BoardLocation location, DiffNode currentRoot) {
        return null;
    }

    public void executeTurn(Turn turn) throws TakEngineException {
        state.executeTurn(turn);
    }

    public void undoTurn() throws TakEngineException {
        state.undoTurn();
    }

    public GameState getState() {
        return state;
    }
}
