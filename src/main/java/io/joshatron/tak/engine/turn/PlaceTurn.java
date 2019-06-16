package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.exception.TakEngineException;
import org.json.JSONObject;

public class PlaceTurn extends Turn {

    private BoardLocation location;
    private PieceType pieceType;

    public PlaceTurn(BoardLocation location, PieceType pieceType) {
        super(TurnType.PLACE);
        this.location = location;
        this.pieceType = pieceType;
    }

    public PlaceTurn(int x, int y, PieceType pieceType) {
        super(TurnType.PLACE);
        this.location = new BoardLocation(x, y);
        this.pieceType = pieceType;
    }

    public PlaceTurn(JSONObject turn) {
        super(TurnType.PLACE);
        this.location = new BoardLocation(turn.getJSONObject("location"));
        this.pieceType = PieceType.valueOf(turn.getString("pieceType"));
    }

    public PlaceTurn(PlaceTurn turn) {
        super(TurnType.PLACE);
        this.location = new BoardLocation(turn.getLocation());
        this.pieceType = turn.getPieceType();
    }

    public JSONObject exportToJson() {
        JSONObject toReturn = new JSONObject();
        toReturn.put("type", TurnType.PLACE.name());
        toReturn.put("pieceType", pieceType.name());
        toReturn.put("location", location.exportToJson());

        return toReturn;
    }

    public BoardLocation getLocation() {
        return location;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public String toString() {
        String str = "p";
        switch(pieceType) {
            case STONE:
                str += "s";
                break;
            case WALL:
                str += "w";
                break;
            case CAPSTONE:
                str += "c";
                break;
        }

        str += " ";
        try {
            str += location.toBoardString();
        } catch(TakEngineException e) {
            e.printStackTrace();
        }

        return str;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PlaceTurn) {
            PlaceTurn other = (PlaceTurn) o;
            return location.equals(other.getLocation()) && pieceType == other.getPieceType();
        }

        return false;
    }
}
