package io.joshatron.tak.engine.board;

import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import org.json.JSONArray;

public class GameBoard {

    //[x][y]
    private PieceStack[][] board;
    private int boardSize;

    public GameBoard(int boardSize) throws TakEngineException {
        this.boardSize = boardSize;

        switch(boardSize) {
            case 3:
                board = new PieceStack[3][3];
                break;
            case 4:
                board = new PieceStack[4][4];
                break;
            case 5:
                board = new PieceStack[5][5];
                break;
            case 6:
                board = new PieceStack[6][6];
                break;
            case 8:
                board = new PieceStack[8][8];
                break;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }

        for(int x = 0; x < boardSize; x++) {
            for(int y = 0; y < boardSize; y++) {
                board[x][y] = new PieceStack();
            }
        }
    }

    public boolean onBoard(int x, int y) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;

    }

    public boolean onBoard(BoardLocation loc) {
        return onBoard(loc.getX(), loc.getY());
    }

    public void printBoard() {
        int maxSize = 1;
        for(int x = 0; x < boardSize; x++) {
            for(int y = 0; y < boardSize; y++) {
                if(board[x][y].toString().length() > maxSize) {
                    maxSize = board[x][y].toString().length();
                }
            }
        }

        System.out.print("    ");
        char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        for(int i = 0; i < boardSize; i++) {
            System.out.print(chars[i] + "   ");
            for(int j = 0; j < maxSize - 1; j++) {
                System.out.print(" ");
            }
        }
        System.out.println();
        System.out.print("  ");
        for(int i = 0; i < ((maxSize + 3) * boardSize + 1); i++) {
            System.out.print("-");
        }
        System.out.println();
        for(int y = 0; y < boardSize; y++) {
            System.out.print((y + 1) + " ");
            for(int x = 0; x < boardSize; x++) {
                System.out.print("| ");
                System.out.print(board[x][y].toString() + " ");
                int len = board[x][y].toString().length();
                for(int i = 0; i < maxSize - len; i++) {
                    System.out.print(" ");
                }
            }
            System.out.println("|");
            System.out.print("  ");
            for(int i = 0; i < ((maxSize + 3) * boardSize + 1); i++) {
                System.out.print("-");
            }
            System.out.println();
        }
    }

    public JSONArray exportToJson() {
        JSONArray toReturn = new JSONArray();
        for(int i = 0; i < boardSize; i++) {
            JSONArray row = new JSONArray();
            for(int j = 0; j < boardSize; j++) {
                row.put(this.board[i][j].exportToJson());
            }
            toReturn.put(row);
        }

        return toReturn;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public PieceStack getPosition(BoardLocation location) {
        return board[location.getX()][location.getY()];
    }

    public PieceStack getPosition(int x, int y) {
        return board[x][y];
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof GameBoard) {
            GameBoard other = (GameBoard) o;
            if(boardSize != other.getBoardSize()) {
                return false;
            }

            for(int i = 0; i < boardSize; i++) {
                for(int j = 0; j < boardSize; j++) {
                    if(!board[i][j].equals(other.getPosition(i, j))) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }
}
