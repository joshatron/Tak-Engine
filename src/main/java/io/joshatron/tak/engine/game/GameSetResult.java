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

    public int getGames() {
        return games;
    }

    public int getWhiteWins() {
        return whiteWins;
    }

    public double getWhiteWinPercentage() {
        return (double) whiteWins / games * 100.;
    }

    public int getWhitePathWins() {
        return whitePathWins;
    }

    public double getWhitePathWinPercentage() {
        return (double) whitePathWins / whiteWins * 100.;
    }

    public int getWhiteFullWins() {
        return whiteFullWins;
    }

    public double getWhiteFullWinPercentage() {
        return (double) whiteFullWins / whiteWins * 100.;
    }

    public int getWhitePieceWins() {
        return whitePieceWins;
    }

    public double getWhitePieceWinPercentage() {
        return (double) whitePieceWins / whiteWins * 100.;
    }

    public int getBlackWins() {
        return blackWins;
    }

    public double getBlackWinPercentage() {
        return (double) blackWins / games * 100.;
    }

    public int getBlackPathWins() {
        return blackPathWins;
    }

    public double getBlackPathWinPercentage() {
        return (double) blackPathWins / blackWins * 100.;
    }

    public int getBlackFullWins() {
        return blackFullWins;
    }

    public double getBlackFullWinPercentage() {
        return (double) blackFullWins / blackWins * 100.;
    }

    public int getBlackPieceWins() {
        return blackPieceWins;
    }

    public double getBlackPieceWinPercentage() {
        return (double) blackPieceWins / blackWins * 100.;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
