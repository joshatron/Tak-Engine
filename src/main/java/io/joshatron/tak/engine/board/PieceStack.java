package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.Player;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PieceStack {

    //from bottom to top
    private ArrayList<Piece> pieces;

    public PieceStack() {
        pieces = new ArrayList<>();
    }

    public PieceStack(PieceStack stack) {
        this.pieces = new ArrayList<>();
        for(Piece piece : stack.getPieces()) {
            this.pieces.add(new Piece(piece));
        }
    }

    public void addPieces(List<Piece> pieces) {
        this.pieces.addAll(pieces);
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public List<Piece> removePieces(int toRemove) throws TakEngineException {
        if(toRemove > pieces.size()) {
            throw new TakEngineException(TakEngineErrorCode.TOO_MANY_PIECES_SPECIFIED);
        }

        int pieceLoc = pieces.size() - toRemove;
        ArrayList<Piece> removed = new ArrayList<>();
        for(int i = 0; i < toRemove; i++) {
            removed.add(pieces.remove(pieceLoc));
        }

        return removed;
    }

    public List<Piece> getTopPieces(int num) throws TakEngineException {
        if(num > pieces.size()) {
            throw new TakEngineException(TakEngineErrorCode.TOO_MANY_PIECES_SPECIFIED);
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
        if(pieces.isEmpty()) {
            return null;
        }
        return pieces.get(pieces.size() - 1);
    }

    public Player getStackOwner() {
        if(pieces.isEmpty()) {
            return Player.NONE;
        }
        return pieces.get(pieces.size() - 1).getPlayer();
    }

    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    public int getHeight() {
        return pieces.size();
    }

    public JSONArray exportToJson() {
        JSONArray array = new JSONArray();
        for(Piece piece : pieces) {
            array.put(piece.exportToJson());
        }

        return array;
    }

    // Prints top to bottom according to tak by mail rules
    public String toString() {
        if(pieces.isEmpty()) {
            return "";
        }

        StringBuilder str = new StringBuilder();

        for(int i = pieces.size() - 1; i >= 0; i--) {
            if(pieces.get(i).isWhite()) {
                switch(pieces.get(i).getType()) {
                    case STONE:
                        str.append("s");
                        break;
                    case WALL:
                        str.append("w");
                        break;
                    case CAPSTONE:
                        str.append("c");
                        break;
                }
            }
            else {
                switch(pieces.get(i).getType()) {
                    case STONE:
                        str.append("S");
                        break;
                    case WALL:
                        str.append("W");
                        break;
                    case CAPSTONE:
                        str.append("C");
                        break;
                }
            }
        }

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PieceStack) {
            PieceStack other = (PieceStack) o;
            for(int i = 0; i < pieces.size(); i++) {
                if(!pieces.get(i).equals(other.getPieces().get(i))) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
