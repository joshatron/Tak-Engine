package io.joshatron.engine;

import java.util.ArrayList;

public class PieceStack {

    //from bottom to top
    private ArrayList<Piece> pieces;

    public PieceStack() {
        pieces = new ArrayList<>();
    }

    public void addPieces(ArrayList<Piece> pieces) {
        this.pieces.addAll(pieces);
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public ArrayList<Piece> removePieces(int toRemove) {
        if(toRemove > pieces.size()) {
            return null;
        }

        int pieceLoc = pieces.size() - toRemove;
        ArrayList<Piece> removed = new ArrayList<>();
        for(int i = 0; i < toRemove; i++) {
            removed.add(pieces.remove(pieceLoc));
        }

        return removed;
    }

    public ArrayList<Piece> getTopPieces(int num) {
        if(num > pieces.size()) {
            return null;
        }

        ArrayList<Piece> top = new ArrayList<>();
        for(int i = 0; i < num; i++) {
            top.add(pieces.get(pieces.size() - num + i));
        }

        return top;
    }

    public void collapseTopPiece() {
        int top = pieces.size() - 1;
        pieces.set(top, new Piece(pieces.get(top).getPlayer(), PieceType.STONE));
    }

    public void uncollapseTopPiece() {
        int top = pieces.size() - 1;
        pieces.set(top, new Piece(pieces.get(top).getPlayer(), PieceType.WALL));
    }

    public Piece getTopPiece() {
        if(pieces.size() == 0) {
            return null;
        }
        return pieces.get(pieces.size() - 1);
    }

    public ArrayList<Piece> getPieces() {
        return new ArrayList<Piece>(pieces);
    }

    public int getHeight() {
        return pieces.size();
    }

    // Prints top to bottom according to tak by mail rules
    public String getString() {
        if(pieces.size() == 0) {
            return "";
        }

        String str = "";

        for(int i = pieces.size() - 1; i >= 0; i--) {
            if(pieces.get(i).isWhite()) {
                switch(pieces.get(i).getType()) {
                    case STONE:
                        str += "s";
                        break;
                    case WALL:
                        str += "w";
                        break;
                    case CAPSTONE:
                        str += "c";
                        break;
                }
            }
            else {
                switch(pieces.get(i).getType()) {
                    case STONE:
                        str += "S";
                        break;
                    case WALL:
                        str += "W";
                        break;
                    case CAPSTONE:
                        str += "C";
                        break;
                }
            }
        }

        return str;
    }
}
