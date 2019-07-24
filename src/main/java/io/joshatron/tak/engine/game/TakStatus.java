package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.GameStatus;
import io.joshatron.bgt.engine.state.Status;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@ToString
public class TakStatus extends GameStatus {
    private PlayerIndicator winner;
    private WinReason reason;
    private int score;

    public TakStatus(Status status, PlayerIndicator winner, WinReason reason, int score) {
        super(status);
        this.winner = winner;
        this.reason = reason;
        this.score = score;
    }

    public TakStatus() {
        super();
        winner = PlayerIndicator.NONE;
        reason = WinReason.NONE;
        score = 0;
    }
}
