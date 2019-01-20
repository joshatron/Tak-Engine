package io.joshatron.tak.engine.game;

public class PlayerInfo {
    private int stones;
    private int capstones;
    private int points;

    public PlayerInfo(int stones, int capstones) {
        this.stones = stones;
        this.capstones = capstones;
        this.points = 0;
    }

    public int getStones() {
        return stones;
    }

    public void setStones(int stones) {
        this.stones = stones;
    }

    public void incrementStones() {
        stones++;
    }

    public void decrementStones() {
        stones--;
    }

    public int getCapstones() {
        return capstones;
    }

    public void setCapstones(int capstones) {
        this.capstones = capstones;
    }

    public void incrementCapstones() {
        capstones++;
    }

    public void decrementCapstones() {
        capstones--;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void incrementPoints() {
        points++;
    }

    public void decrementPoints() {
        points--;
    }

    public int getScore() {
        return stones + capstones;
    }
}
