package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.player.TakPlayer;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;

public class Games {

    private int games;
    private int boardSize;
    private Player firstPlayer;
    private TakPlayer whitePlayer;
    private TakPlayer blackPlayer;
    private GameHooks hooks;

    private GameState currentState;
    private boolean newGame;
    private int game;
    private GameSetResult setResults;

    public Games(int games, int boardSize, Player firstPlayer, TakPlayer whitePlayer, TakPlayer blackPlayer, GameHooks hooks) {
        this.games = games;
        this.boardSize = boardSize;
        this.firstPlayer = firstPlayer;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.hooks = hooks;

        currentState = null;
        newGame = true;
        game = 0;
        setResults = new GameSetResult(boardSize, firstPlayer);
    }

    public GameResult playTurn() {
        GameResult result;
        //confirm you haven't finished all the games
        if(game < games) {
            //if a game is finished and set to null, initialize another one
            if (newGame) {
                currentState = new GameState(firstPlayer, boardSize);
                newGame = false;
                if(hooks != null) {
                    hooks.beforeGame((GameState) currentState.clone(), game);
                }
            }

            //have current player complete turn
            if(hooks != null) {
                    hooks.beforeTurn((GameState) currentState.clone());
            }
            if(currentState.isWhiteTurn()) {
                Turn turn = whitePlayer.getTurn((GameState) currentState.clone());
                if (turn == null || turn.getType() == TurnType.SURRENDER) {
                    result = new GameResult(true, Player.BLACK, WinReason.SURRENDER);
                    game = games;
                    setResults.addGame(result);
                    return result;
                }
                if(!currentState.executeTurn(turn)) {
                    result = new GameResult(true, Player.BLACK, WinReason.SURRENDER);
                    game = games;
                    setResults.addGame(result);
                    return result;
                }
            }
            else {
                Turn turn = blackPlayer.getTurn((GameState) currentState.clone());
                if (turn == null || turn.getType() == TurnType.SURRENDER) {
                    result = new GameResult(true, Player.WHITE, WinReason.SURRENDER);
                    game = games;
                    setResults.addGame(result);
                    return result;
                }
                if(!currentState.executeTurn(turn)) {
                    result = new GameResult(true, Player.WHITE, WinReason.SURRENDER);
                    game = games;
                    setResults.addGame(result);
                    return result;
                }
            }
            if(hooks != null) {
                hooks.afterTurn((GameState) currentState.clone());
            }

            result = currentState.checkForWinner();

            //If game finished, reset for the next one
            if(result.isFinished()) {
                if(hooks != null) {
                    hooks.afterGame((GameState) currentState.clone(), game);
                }
                if(firstPlayer == Player.WHITE) {
                    firstPlayer = Player.BLACK;
                }
                else {
                    firstPlayer = Player.WHITE;
                }
                game++;
                newGame = true;
                setResults.addGame(result);
            }

            return result;
        }
        else {
            return null;
        }
    }

    public GameResult playGame() {
        GameResult result = playTurn();
        while (result != null && !result.isFinished()) {
            result = playTurn();
        }

        return result;
    }

    public GameSetResult playGames() {
        GameResult result = playTurn();
        while(result != null) {
            result = playTurn();
        }

        return setResults;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public GameSetResult getSetResults() {
        return setResults;
    }

    public int getGame() {
        return game;
    }
}
