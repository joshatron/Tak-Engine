package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.game.Player;
import org.json.JSONObject;

public class Piece {

    private Player player;
    private PieceType type;

    public Piece(Player player, PieceType type) {
        this.player = player;
        this.type = type;
    }

    public JSONObject exportToJson() {
        JSONObject piece = new JSONObject();
        piece.put("player", player.name());
        piece.put("type", type.name());

        return piece;
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
