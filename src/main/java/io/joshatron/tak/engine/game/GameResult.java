package io.joshatron.tak.engine.game;

public class GameResult {
    private boolean finished;
    private Player winner;
    private WinReason reason;
    private int score;

    public GameResult(boolean finished, Player winner, WinReason reason, int score) {
        this.finished = finished;
        this.winner = winner;
        this.reason = reason;
        this.score = score;
    }

    public GameResult() {
        finished = false;
        winner = Player.NONE;
        reason = WinReason.NONE;
        score = 0;
    }

    public boolean isFinished() {
        return finished;
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof GameResult) {
            GameResult other = (GameResult) o;
            return finished == other.finished && winner == other.winner && reason == other.reason && score == other.score;
        }

        return false;
    }
}
