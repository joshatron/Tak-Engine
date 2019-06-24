package io.joshatron.tak.engine.turn;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import org.json.JSONObject;

public class TurnUtils {

    private TurnUtils() {
        throw new IllegalStateException("This is a utility class");
    }

    public static TakTurn turnFromJson(JSONObject turn) throws TakEngineException {
        TurnType type = TurnType.valueOf(turn.getString("type"));
        if(type == TurnType.PLACE) {
            return new TakPlaceTurn(turn);
        }
        else if(type == TurnType.MOVE) {
            return new TakMoveTurn(turn);
        }

        throw new TakEngineException(TakEngineErrorCode.INVALID_TURN_TYPE);
    }

    public static TakTurn turnFromString(String str) throws TakEngineException {
        str = str.toLowerCase();
        if(str.charAt(0) == 'p') {
            PieceType type = null;
            switch(str.charAt(1)) {
                case 's':
                    type = PieceType.STONE;
                    break;
                case 'w':
                    type = PieceType.WALL;
                    break;
                case 'c':
                    type = PieceType.CAPSTONE;
                    break;
                default:
                    throw new TakEngineException(TakEngineErrorCode.INVALID_PIECE_TYPE);
            }

            int x = xToNum(str.charAt(3));
            int y = charToNum(str.charAt(4)) - 1;

            if(type != null && x != -1 && y != -1) {
                return new TakPlaceTurn(new BoardLocation(x, y), type);
            }
        }
        else if(str.charAt(0) == 'm') {
            Direction dir = null;
            switch(str.charAt(1)) {
                case 'n':
                    dir = Direction.NORTH;
                    break;
                case 's':
                    dir = Direction.SOUTH;
                    break;
                case 'e':
                    dir = Direction.EAST;
                    break;
                case 'w':
                    dir = Direction.WEST;
                    break;
                default:
                    throw new TakEngineException(TakEngineErrorCode.INVALID_DIRECTION);
            }

            int x = xToNum(str.charAt(3));
            int y = charToNum(str.charAt(4)) - 1;

            int pickUp = charToNum(str.charAt(7));

            int spots = (str.length() - 8) / 2;
            int[] drop = new int[spots];
            for(int i = 0; i < spots; i++) {
                drop[i] = charToNum(str.charAt(8 + (2 * i) + 1));
            }

            if(dir != null && x != -1 && y != -1 && pickUp != -1) {
                return new TakMoveTurn(new BoardLocation(x, y), pickUp, dir, drop);
            }
        }

        return null;
    }

    private static int xToNum(char c) throws TakEngineException {
        switch(c) {
            case 'a':
                return 0;
            case 'b':
                return 1;
            case 'c':
                return 2;
            case 'd':
                return 3;
            case 'e':
                return 4;
            case 'f':
                return 5;
            case 'g':
                return 6;
            case 'h':
                return 7;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_LOCATION);
        }
    }

    private static int charToNum(char c) throws TakEngineException {
        switch(c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_NUMBER);
        }
    }
}
