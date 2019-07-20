package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.engines.AggregateGameEngineManager;
import io.joshatron.bgt.engine.engines.GameEngine;
import io.joshatron.bgt.engine.state.GameState;

public class TakEngineManager implements AggregateGameEngineManager {
    private TakEngineFirstTurns firstTurns;
    private TakEngineMainTurns mainTurns;

    public TakEngineManager() {
        firstTurns = new TakEngineFirstTurns();
        mainTurns = new TakEngineMainTurns();
    }

    @Override
    public GameEngine getInitialEngine() {
        return firstTurns;
    }

    @Override
    public GameEngine updateEngine(GameState gameState, GameEngine gameEngine) {
        if(gameState.getGameLog().size() < 2) {
            return firstTurns;
        }
        else {
            return mainTurns;
        }
    }
}
