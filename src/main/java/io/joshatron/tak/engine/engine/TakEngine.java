package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnDiff;

public class TakEngine {

    private GameState state;
    private DiffNode root;

    public TakEngine(Player first, int size) throws TakEngineException {
        state = new GameState(first, size);
        root = new DiffNode(new TurnDiff());
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
