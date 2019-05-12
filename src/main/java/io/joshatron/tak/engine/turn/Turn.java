package io.joshatron.tak.engine.turn;

import org.json.JSONObject;

public abstract class Turn {

    private TurnType type;

    public Turn(TurnType type) {
        this.type = type;
    }

    public abstract JSONObject exportToJson();

    public TurnType getType() {
        return type;
    }
}
