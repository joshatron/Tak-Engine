package io.joshatron.tak.engine.game;

public enum Player {
    BLACK,
    WHITE,
    NONE;

    private Player opposite;

    static {
        BLACK.opposite = WHITE;
        WHITE.opposite = BLACK;
        NONE.opposite = NONE;
    }

    public Player opposite() {
        return opposite;
    }
}
