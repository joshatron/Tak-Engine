package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.engines.InOrderGameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.bgt.engine.state.TurnLog;
import io.joshatron.tak.engine.board.Piece;
import io.joshatron.tak.engine.board.PieceStack;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.TakPlaceTurn;

import java.util.List;
import java.util.stream.Collectors;

public class TakEngineFirstTurns extends InOrderGameEngine {

    @Override
    protected boolean isTurnValid(GameState gameState, Turn turn) {
        try {
            if(gameState instanceof TakState && turn instanceof TakPlaceTurn) {
                TakState state = (TakState) gameState;
                TakPlaceTurn t = (TakPlaceTurn) turn;
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
    protected void updateState(GameState gameState, Turn turn) {
        try {
            TakPlayerInfo otherInfo = (TakPlayerInfo) ((TakState)gameState).getNextPlayerInfo();
            ((PieceStack)((TakState)gameState).getBoard().getTile(((TakPlaceTurn)turn).getLocation()))
                    .addPiece(new Piece(otherInfo.getIdentifier(), PieceType.STONE));
            otherInfo.getStones().removePieces(1);
            gameState.getGameLog().add(new TurnLog(turn, null));
        } catch(BoardGameEngineException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Turn> getPossibleTurns(GameState gameState) throws BoardGameEngineException {
        return ((TakState)gameState).getBoard().getAllTiles().parallelStream()
                .filter(boardTile -> ((PieceStack)boardTile).isEmpty())
                .map(boardTile -> {
                    try {
                        return new TakPlaceTurn(((TakState)gameState).getCurrentPlayerInfo().getIdentifier(),
                                (GridBoardLocation) boardTile.getLocation(), PieceType.STONE);
                    } catch(BoardGameEngineException e) {
                        return null;
                    }})
                .collect(Collectors.toList());
    }
}
