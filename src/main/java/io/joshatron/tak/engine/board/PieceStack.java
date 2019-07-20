package io.joshatron.tak.engine.board;

import io.joshatron.bgt.engine.board.BoardLocation;
import io.joshatron.bgt.engine.board.BoardTile;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;

import java.util.ArrayList;
import java.util.List;

public class PieceStack extends BoardTile {

    //from bottom to top
    private ArrayList<Piece> pieces;

    public PieceStack(BoardLocation location) {
        super(location);
        pieces = new ArrayList<>();
    }

    public void addPieces(List<Piece> pieces) {
        this.pieces.addAll(pieces);
    }

    public void addPiece(Piece piece) {
        pieces.add(piece);
    }

    public List<Piece> removePieces(int toRemove) throws BoardGameEngineException {
        if(toRemove > pieces.size()) {
            throw new BoardGameEngineException(TakEngineErrorCode.TOO_MANY_PIECES_SPECIFIED);
        }

        int pieceLoc = pieces.size() - toRemove;
        ArrayList<Piece> removed = new ArrayList<>();
        for(int i = 0; i < toRemove; i++) {
            removed.add(pieces.remove(pieceLoc));
        }

        return removed;
    }

    public List<Piece> getTopPieces(int num) throws BoardGameEngineException {
        if(num > pieces.size()) {
            throw new BoardGameEngineException(TakEngineErrorCode.TOO_MANY_PIECES_SPECIFIED);
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

    public PlayerIndicator getStackOwner() {
        if(pieces.isEmpty()) {
            return null;
        }
        return pieces.get(pieces.size() - 1).getPlayer();
    }

    public List<Piece> getPieces() {
        return new ArrayList<>(pieces);
    }

    public int getHeight() {
        return pieces.size();
    }

    public boolean isEmpty() {
        return pieces.size() == 0;
    }

    // Prints top to bottom according to tak by mail rules
    public String toString() {
        if(pieces.isEmpty()) {
            return "";
        }

        StringBuilder str = new StringBuilder();

        for(int i = pieces.size() - 1; i >= 0; i--) {
            if(pieces.get(i).getPlayer() == PlayerIndicator.WHITE) {
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
