package io.joshatron.tak.engine.game;

public class GameStateConfig {
    private boolean fast;
    private boolean removeLosing;

    public GameStateConfig() {
        this.fast = false;
        this.removeLosing = false;
    }

    public GameStateConfig(boolean fast, boolean removeLosing) {
        this.fast = fast;
        this.removeLosing = removeLosing;
    }

    public boolean isFast() {
        return fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    public boolean isRemoveLosing() {
        return removeLosing;
    }

    public void setRemoveLosing(boolean removeLosing) {
        this.removeLosing = removeLosing;
    }
}
