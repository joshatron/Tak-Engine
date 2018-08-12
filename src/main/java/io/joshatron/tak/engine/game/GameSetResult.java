package io.joshatron.tak.engine.game;

public class GameSetResult {

    private int boardSize;
    private Player firstPlayer;

    private int games;
    private int whiteWins;
    private int whitePathWins;
    private int whiteFullWins;
    private int whitePieceWins;
    private int blackWins;
    private int blackPathWins;
    private int blackFullWins;
    private int blackPieceWins;

    public GameSetResult(int boardSize, Player firstPlayer) {
        this.boardSize = boardSize;
        this.firstPlayer = firstPlayer;
    }

    public void addGame(GameResult result) {
        games++;
        if(result.isFinished()) {
            if(result.getWinner() == Player.WHITE) {
                whiteWins++;
                if(result.getReason() == WinReason.PATH) {
                    whitePathWins++;
                }
                else if(result.getReason() == WinReason.BOARD_FULL) {
                    whiteFullWins++;
                }
                else if(result.getReason() == WinReason.OUT_OF_PIECES) {
                    whitePieceWins++;
                }
            }
            else if(result.getWinner() == Player.BLACK) {
                blackWins++;
                if(result.getReason() == WinReason.PATH) {
                    blackPathWins++;
                }
                else if(result.getReason() == WinReason.BOARD_FULL) {
                    blackFullWins++;
                }
                else if(result.getReason() == WinReason.OUT_OF_PIECES) {
                    blackPieceWins++;
                }
            }
        }
    }

    public int getWhiteWins() {
        return whiteWins;
    }

    public int getWhitePathWins() {
        return whitePathWins;
    }

    public int getWhiteFullWins() {
        return whiteFullWins;
    }

    public int getWhitePieceWins() {
        return whitePieceWins;
    }

    public int getBlackWins() {
        return blackWins;
    }

    public int getBlackPathWins() {
        return blackPathWins;
    }

    public int getBlackFullWins() {
        return blackFullWins;
    }

    public int getBlackPieceWins() {
        return blackPieceWins;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
