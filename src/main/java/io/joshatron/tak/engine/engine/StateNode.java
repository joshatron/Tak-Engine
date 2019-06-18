package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.game.GameStateDTO;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"children", "childrenFull"})
public class StateNode {
    private StateNode parent;
    private Set<StateNode> children;
    private GameStateDTO state;
    private boolean childrenFull;

    public StateNode(GameStateDTO state) {
        this.parent = null;
        this.children = new HashSet<>();
        this.state = state;
        this.childrenFull = false;
    }

    public void addChildren(List<StateNode> nodes) {
        children.addAll(nodes);
        nodes.stream().forEach(node -> node.setParent(this));
    }

    public void addChild(StateNode child) {
        children.add(child);
        child.setParent(this);
    }
}
