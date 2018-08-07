package io.joshatron.takengine.player;

import io.joshatron.takengine.engine.GameState;
import io.joshatron.takengine.turn.Turn;

public interface TakPlayer {

    public Turn getTurn(GameState state);
}
