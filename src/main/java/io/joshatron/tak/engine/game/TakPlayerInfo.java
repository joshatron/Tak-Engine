package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.player.Pieces;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.player.PlayerInfo;
import lombok.Data;

@Data
public class TakPlayerInfo extends PlayerInfo {
    private Pieces stones;
    private Pieces capstones;

    public TakPlayerInfo(PlayerIndicator identifier, Pieces stones, Pieces capstones) {
        super(identifier);
        this.stones = stones;
        this.capstones = capstones;
    }

    @Override
    public String toString() {
        return getIdentifier().name().charAt(0) + "S: " + stones.getPiecesLeft() + ", " + getIdentifier().name().charAt(0) + "C: " + capstones.getPiecesLeft();
    }
}
