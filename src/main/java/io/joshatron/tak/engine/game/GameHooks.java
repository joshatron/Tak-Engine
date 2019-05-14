package io.joshatron.tak.engine.game;

public interface GameHooks {

    void beforeGame(GameState state, int game);
    void afterGame(GameState state, int game);
    void beforeTurn(GameState state);
    void afterTurn(GameState state);
}
