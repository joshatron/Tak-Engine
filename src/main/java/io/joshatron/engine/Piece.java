package io.joshatron.engine;

public class Piece {

    private Player player;
    private PieceType type;

    public Piece(Player player, PieceType type) {
        this.player = player;
        this.type = type;
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
}
