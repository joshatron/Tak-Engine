package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.player.TakPlayer;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;

public class Games {

    public static GameSetResult playGames(int games, int boardSize, Player firstPlayer, TakPlayer whitePlayer, TakPlayer blackPlayer, GameHooks hooks) {
        GameSetResult results = new GameSetResult(boardSize, firstPlayer);

        for(int i = 0; i < games; i++) {
            GameState state = new GameState(firstPlayer, boardSize);
            hooks.beforeGame((GameState) state.clone(), i);
            GameResult result = new GameResult();
            while(!result.isFinished()) {
                hooks.beforeTurn((GameState) state.clone());
                if(state.isWhiteTurn()) {
                    Turn turn = whitePlayer.getTurn((GameState) state.clone());
                    if (turn == null || turn.getType() == TurnType.SURRENDER) {
                        result = new GameResult(true, Player.BLACK, WinReason.SURRENDER);
                        break;
                    }
                    if(!state.executeTurn(turn)) {
                        result = new GameResult(true, Player.BLACK, WinReason.SURRENDER);
                        break;
                    }
                }
                else {
                    Turn turn = blackPlayer.getTurn((GameState) state.clone());
                    if (turn == null || turn.getType() == TurnType.SURRENDER) {
                        result = new GameResult(true, Player.WHITE, WinReason.SURRENDER);
                        break;
                    }
                    if(!state.executeTurn(turn)) {
                        result = new GameResult(true, Player.WHITE, WinReason.SURRENDER);
                        break;
                    }
                }
                hooks.afterTurn((GameState) state.clone());
            }
            hooks.afterGame((GameState) state.clone(), i);
            results.addGame(result);

            if(firstPlayer == Player.WHITE) {
                firstPlayer = Player.BLACK;
            }
            else {
                firstPlayer = Player.WHITE;
            }
        }

        return results;
    }
}
