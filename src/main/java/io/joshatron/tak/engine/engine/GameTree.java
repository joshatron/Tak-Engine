package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.turn.Turn;

import java.util.Optional;

public class GameTree {
    private StateNode root;

    public GameTree(GameState initialState) {
        this.root = new StateNode(initialState);
    }

    public StateNode getRoot() {
        return root;
    }

    public void executeTurn(Turn turn) throws TakEngineException {
        Optional<StateNode> selected = root.getChildren().stream().filter(node -> node.getState().getLatestTurn().equals(turn)).findFirst();
        if(selected.isPresent()) {
            root = selected.get();
            root.setParent(null);
        }
        else {
            root = new StateNode(root.getState());
            DeterministicGameTree.executeTurn(turn);
        }
    }

    public void undoTurn() throws TakEngineException {
        StateNode newRoot = new StateNode(new GameState(root.getState()));
        DeterministicGameTree.undoTurn();
        newRoot.addChild(root);
        root = newRoot;
    }
}
