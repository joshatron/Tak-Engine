package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.engines.InOrderGameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.bgt.engine.state.InOrderGameState;
import io.joshatron.bgt.engine.turn.Action;
import io.joshatron.bgt.engine.turn.ActionResult;
import io.joshatron.tak.engine.board.Piece;
import io.joshatron.tak.engine.board.PieceStack;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.TakPlaceAction;

import java.util.List;
import java.util.stream.Collectors;

public class TakEngineFirstTurns extends InOrderGameEngine {

    @Override
    protected boolean isActionValid(InOrderGameState gameState, Action action) {
        try {
            if(gameState instanceof TakState && action instanceof TakPlaceAction) {
                TakState state = (TakState) gameState;
                TakPlaceAction t = (TakPlaceAction) action;
                if(t.getPieceType() == PieceType.STONE && ((PieceStack) state.getBoard().getTile(t.getLocation())).isEmpty()) {
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
    protected ActionResult updateState(InOrderGameState gameState, Action action) {
        try {
            TakPlayerInfo otherInfo = (TakPlayerInfo) ((TakState)gameState).getNextPlayerInfo();
            ((PieceStack)((TakState)gameState).getBoard().getTile(((TakPlaceAction)action).getLocation()))
                    .addPiece(new Piece(otherInfo.getIdentifier(), PieceType.STONE));
            otherInfo.getStones().removePieces(1);
        } catch(BoardGameEngineException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected boolean isTurnDone(InOrderGameState inOrderGameState) {
        return true;
    }

    @Override
    public List<Action> getPossibleActions(GameState gameState) throws BoardGameEngineException {
        return ((TakState)gameState).getBoard().getAllTiles().parallelStream()
                .filter(boardTile -> ((PieceStack)boardTile).isEmpty())
                .map(boardTile -> {
                    try {
                        return new TakPlaceAction(((TakState)gameState).getCurrentPlayerInfo().getIdentifier(),
                                (GridBoardLocation) boardTile.getLocation(), PieceType.STONE);
                    } catch(BoardGameEngineException e) {
                        return null;
                    }})
                .collect(Collectors.toList());
    }
}
