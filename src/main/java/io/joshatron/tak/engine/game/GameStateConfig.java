package io.joshatron.tak.engine.game;

public class GameStateConfig {
    private boolean fast;
    private boolean narrowPossible;

    public GameStateConfig() {
        this.fast = false;
        this.narrowPossible = false;
    }

    public GameStateConfig(boolean fast, boolean narrowPossible) {
        this.fast = fast;
        this.narrowPossible = narrowPossible;
    }

    public boolean isFast() {
        return fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public boolean isNarrowPossible() {
        return narrowPossible;
    }

    public void setNarrowPossible(boolean narrowPossible) {
        this.narrowPossible = narrowPossible;
    }
}
