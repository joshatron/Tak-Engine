package io.joshatron.tak.engine.board;

import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;

public class GameBoard {

    //[x][y]
    private PieceStack[][] board;
    private int boardSize;

    public GameBoard(int boardSize) throws BoardGameEngineException {
        this.boardSize = boardSize;
        if(boardSize >= 3 && (boardSize <= 6 || boardSize == 8)) {
            this.board = new PieceStack[boardSize][boardSize];
        }
        else {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }

        for(int x = 0; x < boardSize; x++) {
            for(int y = 0; y < boardSize; y++) {
                this.board[x][y] = new PieceStack();
            }
        }
    }

    public GameBoard(GameBoard gameBoard) throws BoardGameEngineException {
        this.boardSize = gameBoard.getBoardSize();
        this.board = new PieceStack[this.boardSize][this.boardSize];

        for(int x = 0; x < this.boardSize; x++) {
            for(int y = 0; y < this.boardSize; y++) {
                this.board[x][y] = new PieceStack(gameBoard.getPosition(x, y));
            }
        }
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

    public int getBoardSize() {
        return boardSize;
    }

    public PieceStack getPosition(BoardLocation location) throws BoardGameEngineException {
        validateOnBoard(location.getX(), location.getY());
        return board[location.getX()][location.getY()];
    }

    public PieceStack getPosition(int x, int y) throws BoardGameEngineException {
        validateOnBoard(x, y);
        return board[x][y];
    }

    public boolean isValidLocation(BoardLocation location) {
        try {
            validateOnBoard(location.getX(), location.getY());
            return true;
        } catch(BoardGameEngineException e) {
            return false;
        }
    }

    private void validateOnBoard(int x, int y) throws BoardGameEngineException {
        if(x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_LOCATION);
        }
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
                    try {
                        if(!board[i][j].equals(other.getPosition(i, j))) {
                            return false;
                        }
                    } catch(BoardGameEngineException e) {
                        //Won't reach here
                    }
                }
            }

            return true;
        }

        return false;
    }
}
