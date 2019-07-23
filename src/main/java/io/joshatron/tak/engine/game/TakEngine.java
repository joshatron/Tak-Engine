package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.engines.AggregateGameEngine;
import io.joshatron.bgt.engine.engines.GameEngine;

public class TakEngine extends AggregateGameEngine<TakState> {
    private TakEngineFirstTurns firstTurns;
    private TakEngineMainTurns mainTurns;

    public TakEngine() {
        firstTurns = new TakEngineFirstTurns();
        mainTurns = new TakEngineMainTurns();
    }

    @Override
    public GameEngine<TakState> getEngineForState(TakState gameState) {
        if(gameState.getGameLog().size() < 2) {
            return firstTurns;
        }

        return mainTurns;
    }
}
