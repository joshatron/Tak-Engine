package io.joshatron.tak.engine.engine;

public interface GameHooks {

    void beforeGame(GameEngine state, int game);
    void afterGame(GameEngine state, int game);
    void beforeTurn(GameEngine state);
    void afterTurn(GameEngine state);
}
