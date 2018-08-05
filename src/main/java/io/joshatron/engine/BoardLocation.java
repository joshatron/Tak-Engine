package io.joshatron.engine;

public class BoardLocation {

    private int x;
    private int y;

    public BoardLocation(int x, int y) {
        this.x = x;
        this.y = y;
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

    public String toBoardString() {
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
        }

        str += Integer.toString(y + 1);

        return str;
    }
}
