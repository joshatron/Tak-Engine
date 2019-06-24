package io.joshatron.tak.engine.engine;

public interface GameHooks {

    void beforeGame(TakEngine state, int game);
    void afterGame(TakEngine state, int game);
    void beforeTurn(TakEngine state);
    void afterTurn(TakEngine state);
}
