package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameStateDTO;
import io.joshatron.tak.engine.turn.Turn;

import java.util.Optional;

public class GameTree {
    private StateNode root;

    public GameTree(GameStateDTO initialState) {
        this.root = new StateNode(initialState);
    }

    public StateNode getRoot() {
        return root;
    }

    public void executeTurn(Turn turn) {
        Optional<StateNode> selected = root.getChildren().stream().filter(node -> node.getState().getLatesTurn().equals(turn)).findFirst();
        if(selected.isPresent()) {
            root = selected.get();
            root.setParent(null);
        }
        else {
            root = new StateNode(root.getState());
            TakEngine.executeTurn(root.getState(), turn);
        }
    }

    public void undoTurn() throws TakEngineException {
        StateNode newRoot = new StateNode(new GameStateDTO(root.getState()));
        TakEngine.undoTurn(newRoot.getState());
        newRoot.addChild(root);
        root = newRoot;
    }
}
