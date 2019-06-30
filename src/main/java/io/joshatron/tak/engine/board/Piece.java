package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.game.Player;

public class Piece {

    private Player player;
    private PieceType type;

    public Piece(Player player, PieceType type) {
        this.player = player;
        this.type = type;
    }

    public Piece(Piece piece) {
        this.player = piece.getPlayer();
        this.type = piece.getType();
    }

    public boolean isWhite() {
        return player == Player.WHITE;
    }

    public boolean isBlack() {
        return player == Player.BLACK;
    }

    public Player getPlayer() {
        return player;
    }

    public PieceType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Piece) {
            Piece other = (Piece) o;
            return player == other.getPlayer() && type == other.getType();
        }

        return false;
    }
}
