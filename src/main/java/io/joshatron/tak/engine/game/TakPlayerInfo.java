package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.player.PlayerInfo;
import lombok.Data;

@Data
public class TakPlayerInfo extends PlayerInfo {
    private int stones;
    private int capstones;

    public TakPlayerInfo(String identifier, int stones, int capstones) {
        super(identifier);
        this.stones = stones;
        this.capstones = capstones;
    }

    @Override
    public String toString() {
        return getIdentifier().charAt(0) + "S: " + stones + ", " + getIdentifier().charAt(0) + "C: " + capstones;
    }
}
