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
        if(!(o instanceof GameResult)) {
            return false;
        }

        if(((GameResult) o).finished == this.finished &&
           ((GameResult) o).winner == this.winner &&
           ((GameResult) o).reason == this.reason &&
           ((GameResult) o).score == this.score) {
            return true;
        }

        return false;
    }
}
