package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;

public class MoveTurn extends Turn {

    private BoardLocation startLocation;
    private int pickedUp;
    private Direction direction;
    private int[] placed;
    private boolean flattened;

    public MoveTurn(BoardLocation startLocation, int pickedUp,
                    Direction direction, int[] placed) {
        super(TurnType.MOVE);
        this.startLocation = startLocation;
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public MoveTurn(int x, int y, int pickedUp,
                    Direction direction, int[] placed) {
        super(TurnType.MOVE);
        this.startLocation = new BoardLocation(x, y);
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public BoardLocation getStartLocation() {
        return startLocation;
    }

    public int getPickedUp() {
        return pickedUp;
    }

    public Direction getDirection() {
        return direction;
    }

    public int[] getPlaced() {
        return placed;
    }

    public void flatten() {
        flattened = true;
    }

    public boolean didFlatten() {
        return flattened;
    }

    public String toString() {
        String str = "m";
        switch(direction) {
            case NORTH:
                str += "n";
                break;
            case SOUTH:
                str += "s";
                break;
            case EAST:
                str += "e";
                break;
            case WEST:
                str += "w";
                break;
        }

        str += " " + startLocation.toBoardString();

        str += " g" + pickedUp;
        for(int i = 0; i < placed.length; i++) {
            str += " " + placed[i];
        }

        return str;
    }
}
