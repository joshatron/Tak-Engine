package io.joshatron.tak.engine.board;

public class GameBoard {

    //[x][y]
    private PieceStack[][] board;
    private int boardSize;

    public GameBoard(int boardSize) {
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
        }

        for(int x = 0; x < boardSize; x++) {
            for(int y = 0; y < boardSize; y++) {
                board[x][y] = new PieceStack();
            }
        }
    }

    public boolean onBoard(int x, int y) {
        if(x >= 0 && x < boardSize && y >= 0 && y < boardSize) {
            return true;
        }

        return false;
    }

    public boolean onBoard(BoardLocation loc) {
        return onBoard(loc.getX(), loc.getY());
    }

    public void printBoard() {
        int maxSize = 1;
        for(int x = 0; x < boardSize; x++) {
            for(int y = 0; y < boardSize; y++) {
                if(board[x][y].getString().length() > maxSize) {
                    maxSize = board[x][y].getString().length();
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
                System.out.print(board[x][y].getString() + " ");
                int len = board[x][y].getString().length();
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

    public int getBoardSize() {
        return boardSize;
    }

    public PieceStack getPosition(BoardLocation location) {
        return board[location.getX()][location.getY()];
    }

    public PieceStack getPosition(int x, int y) {
        return board[x][y];
    }
}
