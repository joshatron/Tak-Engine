package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.GameEngine;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.state.GameState;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.bgt.engine.state.TurnStyle;
import io.joshatron.tak.engine.board.Piece;
import io.joshatron.tak.engine.board.PieceStack;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.TakPlaceTurn;

import java.util.List;
import java.util.stream.Collectors;

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
        try {
            ((PieceStack)((TakState)gameState).getBoard().getTile(((TakPlaceTurn)turn).getLocation())).addPiece(new Piece(turn.getPlayer(), PieceType.STONE));
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
                        return new TakPlaceTurn(gameState.getCurrentPlayerInfo().getIdentifier(),
                                (GridBoardLocation) boardTile.getLocation(), PieceType.STONE);
                    } catch(BoardGameEngineException e) {
                        return null;
                    }})
                .collect(Collectors.toList());
    }
}
