package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.GameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.bgt.engine.state.TurnStyle;
import io.joshatron.tak.engine.board.PieceStack;
import io.joshatron.tak.engine.turn.TakPlaceTurn;

import java.util.List;

public class TakEngineFirstTurns extends GameEngine {

    public TakEngineFirstTurns() {
        super(TurnStyle.IN_ORDER);
    }

    @Override
    protected boolean isTurnValid(GameState gameState, Turn turn) {
        try {
            if(gameState instanceof TakState && turn instanceof TakPlaceTurn) {
                TakState state = (TakState) gameState;
                TakPlaceTurn t = (TakPlaceTurn) turn;
                if(((PieceStack) state.getBoard().getTile(t.getLocation())).isEmpty()) {
                    return true;
                }
            }
        }
        catch(BoardGameEngineException e) {
            return false;
        }

        return false;
    }

    @Override
    protected void updateState(GameState gameState, Turn turn) {

    }

    @Override
    public List<Turn> getPossibleTurns(GameState gameState) throws BoardGameEngineException {
        return null;
    }
}
