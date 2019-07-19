package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.player.Pieces;
import io.joshatron.bgt.engine.player.PlayerInfo;
import lombok.Data;

@Data
public class TakPlayerInfo extends PlayerInfo {
    private Pieces stones;
    private Pieces capstones;

    public TakPlayerInfo(String identifier, Pieces stones, Pieces capstones) {
        super(identifier);
        this.stones = stones;
        this.capstones = capstones;
    }

    @Override
    public String toString() {
        return getIdentifier().charAt(0) + "S: " + stones + ", " + getIdentifier().charAt(0) + "C: " + capstones;
    }
}
