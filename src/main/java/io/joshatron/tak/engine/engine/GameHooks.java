package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.game.GameState;

public interface GameHooks {

    void beforeGame(GameState state, int game);
    void afterGame(GameState state, int game);
    void beforeTurn(GameState state);
    void afterTurn(GameState state);
}
