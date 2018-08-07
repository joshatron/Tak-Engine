package io.joshatron.takengine.turn;

public class Turn {

    private TurnType type;

    public Turn(TurnType type) {
        this.type = type;
    }

    public TurnType getType() {
        return type;
    }
}
