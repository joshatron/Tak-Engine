package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.state.GameStatus;
import io.joshatron.bgt.engine.state.Status;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class TakStatus extends GameStatus {
    private Player winner;
    private WinReason reason;
    private int score;

    public TakStatus(Status status, Player winner, WinReason reason, int score) {
        this.status = status;
        this.winner = winner;
        this.reason = reason;
        this.score = score;
    }

    public TakStatus() {
        super();
        winner = Player.NONE;
        reason = WinReason.NONE;
        score = 0;
    }

    public Player getWinner() {
        return winner;
    }

    public WinReason getReason() {
        return reason;
    }

    public int getScore() {
        return score;
    }
}
