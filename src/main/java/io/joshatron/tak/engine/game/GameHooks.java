package io.joshatron.tak.engine.game;

public interface GameHooks {

    public void beforeGame(GameState state);
    public void afterGame(GameState state);
    public void beforeTurn(GameState state);
    public void afterTurn(GameState state);
}
