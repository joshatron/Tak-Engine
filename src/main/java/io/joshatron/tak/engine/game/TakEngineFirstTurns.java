package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.action.Action;
import io.joshatron.bgt.engine.action.ActionResult;
import io.joshatron.bgt.engine.component.PieceStack;
import io.joshatron.bgt.engine.engines.InOrderGameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.board.TakPiece;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.turn.TakPlaceAction;

import java.util.List;
import java.util.stream.Collectors;

public class TakEngineFirstTurns extends InOrderGameEngine<TakState> {

    @Override
    protected boolean isActionValid(TakState gameState, Action action) {
        try {
            if(action instanceof TakPlaceAction) {
                TakPlaceAction t = (TakPlaceAction) action;
                if(t.getPieceType() == PieceType.STONE && gameState.getBoard().getTile(t.getLocation()).isEmpty()) {
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
    protected ActionResult updateState(TakState gameState, Action action) throws BoardGameEngineException {
        if(!(action instanceof TakPlaceAction)) {
            throw new BoardGameEngineException(TakEngineErrorCode.CANT_MOVE_IN_FIRST_TURN);
        }

        TakPlayerInfo otherInfo = gameState.getNextPlayerInfo();
        gameState.getBoard().getTile(((TakPlaceAction)action).getLocation())
                .addPiece(new TakPiece(otherInfo.getIdentifier(), PieceType.STONE));
        otherInfo.getStones().removePieces(1);

        return null;
    }

    @Override
    protected boolean isTurnDone(TakState inOrderGameState) {
        return true;
    }

    @Override
    public List<Action> getPossibleActions(TakState gameState) throws BoardGameEngineException {
        return gameState.getBoard().getAllLocations().parallelStream()
                .filter(l -> {
                    try {
                        return gameState.getBoard().getTile(l).isEmpty();
                    } catch(BoardGameEngineException e) {
                        return false;
                    }
                })
                .map(location -> {
                    try {
                        return new TakPlaceAction(gameState.getCurrentPlayerInfo().getIdentifier(),
                                location, PieceType.STONE);
                    } catch(BoardGameEngineException e) {
                        return null;
                    }})
                .collect(Collectors.toList());
    }
}
