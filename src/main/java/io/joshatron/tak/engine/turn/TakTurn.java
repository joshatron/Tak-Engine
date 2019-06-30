package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.dtos.Turn;

public class TakTurn extends Turn {

    private TurnType type;

    public TakTurn(TurnType type) {
        this.type = type;
    }

    public TurnType getType() {
        return type;
    }
}
