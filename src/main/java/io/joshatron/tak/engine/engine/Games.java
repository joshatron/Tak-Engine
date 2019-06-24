package io.joshatron.tak.engine.engine;

import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.game.*;
import io.joshatron.tak.engine.turn.TakTurn;
import io.joshatron.tak.engine.turn.TurnType;

public class Games {

    private int numGames;
    private int boardSize;
    private Player firstPlayer;
    private TakPlayer whitePlayer;
    private TakPlayer blackPlayer;
    private GameHooks hooks;

    private TakEngine currentState;
    private boolean newGame;
    private int game;
    private GameSetResult setResults;

    public Games(int numGames, int boardSize, Player firstPlayer, TakPlayer whitePlayer, TakPlayer blackPlayer, GameHooks hooks) {
        this.numGames = numGames;
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

    public TakStatus playTurn() throws BoardGameEngineException {
        TakStatus result;
        //confirm you haven't finished all the numGames
        if(game < numGames) {
            //if a game is finished and set to null, initialize another one
            if (newGame) {
                currentState = new TakEngine(firstPlayer, boardSize);
                newGame = false;
                if(hooks != null) {
                    hooks.beforeGame(new TakEngine(currentState), game);
                }
            }

            //have current player complete turn
            if(hooks != null) {
                    hooks.beforeTurn(new TakEngine(currentState));
            }
            if(currentState.isWhiteTurn()) {
                TakTurn turn = whitePlayer.getTurn(new TakEngine(currentState));
                if (turn == null || turn.getType() == TurnType.SURRENDER) {
                    result = new TakStatus(true, Player.BLACK, WinReason.SURRENDER, boardSize * boardSize);
                    game = numGames;
                    setResults.addGame(result);
                    return result;
                }
                try {
                    currentState.executeTurn(turn);
                } catch (BoardGameEngineException e) {
                    result = new TakStatus(true, Player.BLACK, WinReason.SURRENDER, boardSize * boardSize);
                    game = numGames;
                    setResults.addGame(result);
                    return result;
                }
            }
            else {
                TakTurn turn = blackPlayer.getTurn(new TakEngine(currentState));
                if (turn == null || turn.getType() == TurnType.SURRENDER) {
                    result = new TakStatus(true, Player.WHITE, WinReason.SURRENDER, boardSize * boardSize);
                    game = numGames;
                    setResults.addGame(result);
                    return result;
                }
                try {
                    currentState.executeTurn(turn);
                } catch (BoardGameEngineException e) {
                    result = new TakStatus(true, Player.WHITE, WinReason.SURRENDER, boardSize * boardSize);
                    game = numGames;
                    setResults.addGame(result);
                    return result;
                }
            }
            if(hooks != null) {
                hooks.afterTurn(new TakEngine(currentState));
            }

            result = currentState.checkForWinner();

            //If game finished, reset for the next one
            if(result.isFinished()) {
                if(hooks != null) {
                    hooks.afterGame(new TakEngine(currentState), game);
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

    public TakStatus playGame() throws BoardGameEngineException {
        TakStatus result = playTurn();
        while (result != null && !result.isFinished()) {
            result = playTurn();
        }

        return result;
    }

    public GameSetResult playGames() throws BoardGameEngineException {
        TakStatus result = playTurn();
        while(result != null) {
            result = playTurn();
        }

        return setResults;
    }

    public TakEngine getCurrentState() {
        return currentState;
    }

    public GameSetResult getSetResults() {
        return setResults;
    }

    public int getGame() {
        return game;
    }
}
