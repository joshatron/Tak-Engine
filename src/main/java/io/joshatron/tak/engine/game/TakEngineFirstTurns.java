package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.GameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.bgt.engine.state.TurnStyle;

import java.util.List;

public class TakEngineFirstTurns extends GameEngine {

    public TakEngineFirstTurns() {
        super(TurnStyle.IN_ORDER);
    }

    @Override
    protected boolean isTurnValid(GameState gameState, Turn turn) {
        return false;
    }

    @Override
    protected void updateState(GameState gameState, Turn turn) {

    }

    @Override
    public List<Turn> getPossibleTurns(GameState gameState) throws BoardGameEngineException {
        return null;
    }
}
