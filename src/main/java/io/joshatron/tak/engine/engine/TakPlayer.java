package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.turn.TakTurn;

public interface TakPlayer {

    TakTurn getTurn(TakEngine state) throws TakEngineException;
}
