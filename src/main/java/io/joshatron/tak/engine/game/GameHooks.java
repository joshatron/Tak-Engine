package io.joshatron.tak.engine.game;

public interface GameHooks {

    public void beforeGame(GameState state, int game);
    public void afterGame(GameState state, int game);
    public void beforeTurn(GameState state);
    public void afterTurn(GameState state);
}
