package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public MoveTurn(JSONObject turn) {
        super(TurnType.MOVE);
        this.startLocation = new BoardLocation(turn.getJSONObject("location"));
        this.pickedUp = turn.getInt("pickedUp");
        this.direction = Direction.valueOf(turn.getString("direction"));
        this.placed = new int[turn.getJSONArray("placed").length()];
        for(int i = 0; i < turn.getJSONArray("placed").length(); i++) {
            placed[i] = turn.getJSONArray("placed").getInt(i);
        }
        this.flattened = false;
    }

    public JSONObject exportToJson() {
        JSONObject toReturn = new JSONObject();
        toReturn.put("type", TurnType.MOVE.name());
        toReturn.put("location", startLocation.exportToJson());
        toReturn.put("pickedUp", pickedUp);
        toReturn.put("direction", direction.name());

        JSONArray place = new JSONArray();
        for(int i = 0; i < placed.length; i++) {
            place.put(placed[i]);
        }
        toReturn.put("placed", place);

        return toReturn;
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

    @Override
    public boolean equals(Object o) {
        if(o instanceof MoveTurn) {
            MoveTurn other = (MoveTurn) o;
            int[] otherPlaced = other.getPlaced();
            for(int i = 0; i < placed.length; i++) {
                if(placed[i] != otherPlaced[i]) {
                    return false;
                }
            }

            return startLocation.equals(other.startLocation) && pickedUp == other.getPickedUp() && direction == other.getDirection();
        }

        return false;
    }
}
