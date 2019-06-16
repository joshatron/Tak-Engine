package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.turn.TurnDiff;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"children", "childrenFull"})
public class DiffNode {
    private DiffNode parent;
    private Set<DiffNode> children;
    private TurnDiff diff;
    private boolean childrenFull;

    public DiffNode(TurnDiff diff) {
        parent = null;
        children = new HashSet<>();
        this.diff = diff;
        childrenFull = false;
    }

    public void addChildren(List<DiffNode> nodes) {
        children.addAll(nodes);
        nodes.stream().forEach(node -> node.setParent(this));
    }

    public void addChild(DiffNode child) {
        children.add(child);
        child.setParent(this);
    }
}
