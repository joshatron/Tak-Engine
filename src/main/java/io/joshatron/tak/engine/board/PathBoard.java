package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.game.Player;

import java.util.HashMap;

public class PathBoard {

    private int[][] paths;

    public PathBoard(int size) {
        this.paths = new int[size][size];
    }

    public HashMap<BoardLocation, Integer> addPiece(Player player, int x, int y) {
        return addPiece(player, new BoardLocation(x, y));
    }

    public HashMap<BoardLocation, Integer> addPiece(Player player, BoardLocation location) {
        //TODO: implement
        return null;
    }

    public HashMap<BoardLocation, Integer> removePiece(Player player, int x, int y) {
        return removePiece(player, new BoardLocation(x, y));
    }

    public HashMap<BoardLocation, Integer> removePiece(Player player, BoardLocation location) {
        //TODO: implement
        return null;
    }
}
