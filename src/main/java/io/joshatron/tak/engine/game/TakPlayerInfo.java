package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.component.PiecePile;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.player.PlayerInfo;
import lombok.Data;

@Data
public class TakPlayerInfo extends PlayerInfo {
    private PiecePile stones;
    private PiecePile capstones;

    public TakPlayerInfo(PlayerIndicator identifier, PiecePile stones, PiecePile capstones) {
        super(identifier);
        this.stones = stones;
        this.capstones = capstones;
    }

    @Override
    public String toString() {
        return getIdentifier().name().charAt(0) + "S: " + stones.getPiecesLeft() + ", " + getIdentifier().name().charAt(0) + "C: " + capstones.getPiecesLeft();
    }
}
