package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.dtos.Turn;
import org.json.JSONObject;

public class TakTurn extends Turn {

    private TurnType type;

    public TakTurn(TurnType type) {
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
