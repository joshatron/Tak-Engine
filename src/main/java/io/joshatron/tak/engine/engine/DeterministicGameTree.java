package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnDiff;

import java.util.*;
import java.util.stream.Collectors;

public class DeterministicGameTree {

    private StateNode root;

    /*
     * Functions used in normal game play
     */
    public void executeTurn(Turn turn) throws TakEngineException {
        Optional<StateNode> selected = root.getChildren().stream().filter(node -> node.getState().getLatestTurn().equals(turn)).findFirst();
        if(selected.isPresent()) {
            root = selected.get();
            root.setParent(null);
        }
        else {
            root = new StateNode(root.getState());
            GameEngine.executeTurn(root.getState(), turn);
        }
    }

    public void undoTurn() throws TakEngineException {
        StateNode newRoot = new StateNode(new GameState(root.getState()));
        GameEngine.undoTurn(newRoot.getState());
        newRoot.addChild(root);
        root = newRoot;
    }

    public GameState getState() {
        return root.getState();
    }

    public List<Turn> getPossibleTurns() {
        return getPossibleTurns(false);
    }

    public List<Turn> getPossibleTurns(boolean restrictBad) {
        fillOutChildren(root);
        Set<StateNode> nodes = root.getChildren();

        if(restrictBad) {
            if(canWin()) {
                return nodes.stream().filter(node -> node.getState().getResult().isFinished())
                        .map(stateNode -> stateNode.getState().getLatestTurn()).collect(Collectors.toList());
            }
            else if(inTak()) {
                return nodes.stream().filter(this::canWin).map(stateNode -> stateNode.getState().getLatestTurn())
                        .collect(Collectors.toList());
            }
        }

        return nodes.stream().map(stateNode -> stateNode.getState().getLatestTurn()).collect(Collectors.toList());
    }

    public boolean inTak() {
        fillOutChildren(root);
        Set<StateNode> nodes = root.getChildren();
        for(StateNode node : nodes) {
            if(canWin(node)) {
                return true;
            }
        }

        return false;
    }

    public boolean canWin() {
        return canWin(root);
    }

    /*
     * Functions used by AI exploring game tree
     */
    public StateNode getRootNode() {
        return root;
    }

    public void fillOutChildren(StateNode node) {
        if(!node.isChildrenFull()) {
            List<BoardLocation> locations = new ArrayList<>();

            for(int x = 0; x < node.getState().getSize(); x++) {
                for(int y = 0; y < node.getState().getSize(); y++) {
                    locations.add(new BoardLocation(x, y));
                }
            }

            locations.parallelStream().map(location -> getPossibleDiffsForLocation(location, node))
                    .forEach(diffs -> node.addChildren(diffs));
            node.setChildrenFull(true);
        }
    }

    //TODO: implement
    public GameState getStateFromNode(StateNode node) {
        return null;
    }

    /*
     * Helper functions
     */
    //TODO: implement
    private boolean canWin(StateNode currentRoot) {
        return false;
    }

    //TODO: implement
    private List<StateNode> getPossibleDiffsForLocation(BoardLocation location, StateNode currentRoot) {
        List<StateNode> ancestry = getNodeAncestry(currentRoot);
        return null;
    }

    private List<StateNode> getNodeAncestry(StateNode node) {
        ArrayList<StateNode> nodes = new ArrayList<>();
        while(node.getParent() != null && node.getState() == null) {
            nodes.add(node);
            node = node.getParent();
        }
        nodes.add(node);

        return nodes;
    }

    //TODO: implement
    private TurnDiff getDiffFromTurn(Turn turn, StateNode currentRoot) {
        return null;
    }
}
