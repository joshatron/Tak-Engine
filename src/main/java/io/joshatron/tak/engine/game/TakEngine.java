package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.engines.AggregateGameEngine;
import io.joshatron.bgt.engine.engines.GameEngine;
import io.joshatron.bgt.engine.state.GameState;

public class TakEngine extends AggregateGameEngine {
    private TakEngineFirstTurns firstTurns;
    private TakEngineMainTurns mainTurns;

    public TakEngine() {
        firstTurns = new TakEngineFirstTurns();
        mainTurns = new TakEngineMainTurns();
    }

    @Override
    public GameEngine getEngineForState(GameState gameState) {
        if(gameState.getGameLog().size() < 2) {
            return firstTurns;
        }

        return mainTurns;
    }
}
