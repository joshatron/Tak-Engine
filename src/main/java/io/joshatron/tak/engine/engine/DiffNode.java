package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.turn.TurnDiff;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"children", "childrenFull"})
public class DiffNode {
    private Set<DiffNode> children;
    private TurnDiff diff;
    private boolean childrenFull;

    public DiffNode(TurnDiff diff) {
        this.diff = diff;
        children = new HashSet<>();
        childrenFull = false;
    }


}
