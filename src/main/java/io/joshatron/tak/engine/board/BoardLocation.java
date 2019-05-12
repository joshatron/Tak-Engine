package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import org.json.JSONObject;

public class BoardLocation {

    private int x;
    private int y;

    public BoardLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public BoardLocation(BoardLocation location) {
        this.x = location.getX();
        this.y = location.getY();
    }

    public BoardLocation(JSONObject location) {
        this.x = location.getInt("x");
        this.y = location.getInt("y");
    }

    public void move(Direction direction) {
        switch(direction) {
            case NORTH:
                y -= 1;
                break;
            case SOUTH:
                y += 1;
                break;
            case EAST:
                x += 1;
                break;
            case WEST:
                x -= 1;
                break;
        }
    }

    public void moveOpposite(Direction direction) {
        switch(direction) {
            case SOUTH:
                y -= 1;
                break;
            case NORTH:
                y += 1;
                break;
            case WEST:
                x += 1;
                break;
            case EAST:
                x -= 1;
                break;
        }
    }

    public JSONObject exportToJson() {
        JSONObject location = new JSONObject();
        location.put("x", x);
        location.put("y", y);

        return location;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public String toBoardString() throws TakEngineException {
        String str = "";
        switch(x) {
            case 0:
                str += "a";
                break;
            case 1:
                str += "b";
                break;
            case 2:
                str += "c";
                break;
            case 3:
                str += "d";
                break;
            case 4:
                str += "e";
                break;
            case 5:
                str += "f";
                break;
            case 6:
                str += "g";
                break;
            case 7:
                str += "h";
                break;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_LOCATION);
        }

        str += Integer.toString(y + 1);

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BoardLocation) {
            BoardLocation location = (BoardLocation) o;
            return this.x == location.getX() && this.y == location.getY();
        }

        return false;
    }
}
