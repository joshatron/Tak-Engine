package io.joshatron.tak.engine.turn;

import org.json.JSONObject;

public class Turn {

    private TurnType type;

    public Turn(TurnType type) {
        this.type = type;
    }

    public JSONObject exportToJson() {
        JSONObject toReturn = new JSONObject();
        toReturn.put("type", TurnType.PLACE.name());

        return toReturn;
    }

    public TurnType getType() {
        return type;
    }
}
