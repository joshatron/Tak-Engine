package io.joshatron.tak.engine.turn;

import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;
import org.json.JSONArray;
import org.json.JSONObject;

public class TakMoveTurn extends TakTurn {

    private BoardLocation startLocation;
    private int pickedUp;
    private Direction direction;
    private int[] placed;
    private boolean flattened;

    public TakMoveTurn(BoardLocation startLocation, int pickedUp,
                       Direction direction, int[] placed) {
        super(TurnType.MOVE);
        this.startLocation = startLocation;
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public TakMoveTurn(int x, int y, int pickedUp,
                       Direction direction, int[] placed) {
        super(TurnType.MOVE);
        this.startLocation = new BoardLocation(x, y);
        this.pickedUp = pickedUp;
        this.direction = direction;
        this.placed = placed;
        this.flattened = false;
    }

    public TakMoveTurn(JSONObject turn) {
        super(TurnType.MOVE);
        this.startLocation = new BoardLocation(turn.getJSONObject("location"));
        this.pickedUp = turn.getInt("pickedUp");
        this.direction = Direction.valueOf(turn.getString("direction"));
        this.placed = new int[turn.getJSONArray("placed").length()];
        for(int i = 0; i < this.placed.length; i++) {
            this.placed[i] = turn.getJSONArray("placed").getInt(i);
        }
        this.flattened = false;
    }

    public TakMoveTurn(TakMoveTurn turn) {
        super(TurnType.MOVE);
        this.startLocation = new BoardLocation(turn.getStartLocation());
        this.pickedUp = turn.getPickedUp();
        this.direction = turn.getDirection();
        this.placed = new int[turn.getPlaced().length];
        for(int i = 0; i < this.placed.length; i++) {
            this.placed[i] = turn.getPlaced()[i];
        }
        this.flattened = turn.flattened;
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
        StringBuilder str = new StringBuilder("m");
        switch(direction) {
            case NORTH:
                str.append("n");
                break;
            case SOUTH:
                str.append("s");
                break;
            case EAST:
                str.append("e");
                break;
            case WEST:
                str.append("w");
                break;
        }

        try {
            str.append(" ").append(startLocation.toBoardString());
        } catch(BoardGameEngineException e) {
            e.printStackTrace();
        }

        str.append(" g").append(pickedUp);
        for(int i = 0; i < placed.length; i++) {
            str.append(" ").append(placed[i]);
        }

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TakMoveTurn) {
            TakMoveTurn other = (TakMoveTurn) o;
            int[] otherPlaced = other.getPlaced();
            if(placed.length == otherPlaced.length) {
                for(int i = 0; i < placed.length; i++) {
                    if(placed[i] != otherPlaced[i]) {
                        return false;
                    }
                }
            }

            return startLocation.equals(other.startLocation) && pickedUp == other.getPickedUp() && direction == other.getDirection();
        }

        return false;
    }
}
