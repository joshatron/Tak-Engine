package io.joshatron.tak.engine.player;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.turn.Turn;

public interface TakPlayer {

    Turn getTurn(GameState state) throws TakEngineException;
}
