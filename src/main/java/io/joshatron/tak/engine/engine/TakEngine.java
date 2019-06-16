package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.GameState;
import io.joshatron.tak.engine.game.GameStateDTO;
import io.joshatron.tak.engine.game.Player;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnDiff;

import java.util.*;
import java.util.stream.Collectors;

public class TakEngine {

    private GameState state;
    private DiffNode root;

    public TakEngine(Player first, int size) throws TakEngineException {
        state = new GameState(first, size);
        root = new DiffNode(new TurnDiff());
    }

    /*
     * Functions used in normal game play
     */
    public void executeTurn(Turn turn) throws TakEngineException {
        state.executeTurn(turn);

        Optional<DiffNode> selected = root.getChildren().stream().filter(node -> node.getDiff().getTurn().equals(turn)).findFirst();
        if(selected.isPresent()) {
            root = selected.get();
            root.setDiff(new TurnDiff());
        }
        else {
            root.setChildren(new HashSet<>());
            root.setChildrenFull(false);
        }
    }

    public void undoTurn() throws TakEngineException {
        Turn turn = state.undoTurn();

        DiffNode newRoot = new DiffNode(new TurnDiff());
        root.setDiff(getDiffFromTurn(turn, newRoot));
        newRoot.addChild(root);
        root = newRoot;
    }

    public GameStateDTO getState() {
        return new GameStateDTO(state);
    }

    public List<Turn> getPossibleTurns() {
        return getPossibleTurns(false);
    }

    public List<Turn> getPossibleTurns(boolean restrictBad) {
        fillOutChildren(root);
        Set<DiffNode> nodes = root.getChildren();

        if(restrictBad) {
            if(canWin()) {
                return nodes.stream().filter(node -> node.getDiff().getResult().isFinished())
                        .map(diffNode -> diffNode.getDiff().getTurn()).collect(Collectors.toList());
            }
            else if(inTak()) {
                return nodes.stream().filter(this::canWin).map(diffNode -> diffNode.getDiff().getTurn())
                        .collect(Collectors.toList());
            }
        }

        return nodes.stream().map(diffNode -> diffNode.getDiff().getTurn()).collect(Collectors.toList());
    }

    public boolean inTak() {
        fillOutChildren(root);
        Set<DiffNode> nodes = root.getChildren();
        for(DiffNode node : nodes) {
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
    public DiffNode getRootNode() {
        return root;
    }

    public void fillOutChildren(DiffNode node) {
        if(!node.isChildrenFull()) {
            List<BoardLocation> locations = new ArrayList<>();

            for(int x = 0; x < state.getBoardSize(); x++) {
                for(int y = 0; y < state.getBoardSize(); y++) {
                    locations.add(new BoardLocation(x, y));
                }
            }

            locations.parallelStream().map(location -> getPossibleDiffsForLocation(location, node))
                    .forEach(diffs -> node.addChildren(diffs));
            node.setChildrenFull(true);
        }
    }

    //TODO: implement
    public GameStateDTO getStateFromNode(DiffNode node) {
        return null;
    }

    /*
     * Helper functions
     */
    //TODO: implement
    private boolean canWin(DiffNode currentRoot) {
        return false;
    }

    //TODO: implement
    private List<DiffNode> getPossibleDiffsForLocation(BoardLocation location, DiffNode currentRoot) {
        List<DiffNode> ancestry = getNodeAncestry(currentRoot);
        return null;
    }

    private List<DiffNode> getNodeAncestry(DiffNode node) {
        ArrayList<DiffNode> nodes = new ArrayList<>();
        while(node != null) {
            nodes.add(node);
            node = node.getParent();
        }

        return nodes;
    }

    //TODO: implement
    private TurnDiff getDiffFromTurn(Turn turn, DiffNode currentRoot) {
        return null;
    }
}
