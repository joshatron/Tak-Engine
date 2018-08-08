package io.joshatron.tak.engine.player;

import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.turn.Turn;

public interface TakPlayer {

    public Turn getTurn(GameState state);
}
