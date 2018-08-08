package io.joshatron.tak.engine.turn;

public class Turn {

    private TurnType type;

    public Turn(TurnType type) {
        this.type = type;
    }

    public TurnType getType() {
        return type;
    }
}
