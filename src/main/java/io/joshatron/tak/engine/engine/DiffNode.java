package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.turn.TurnDiff;
import lombok.Data;

import java.util.ArrayList;

@Data
public class DiffNode {
    ArrayList<DiffNode> children;
    TurnDiff diff;

    public DiffNode(TurnDiff diff) {
        this.diff = diff;
        children = new ArrayList<>();
    }
}
