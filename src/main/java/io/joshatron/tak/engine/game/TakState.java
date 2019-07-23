package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.PieceStack;
import io.joshatron.bgt.engine.board.grid.GridBoard;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.Pieces;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.InOrderGameState;
import io.joshatron.tak.engine.board.TakPiece;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import lombok.Data;

@Data
public class TakState extends InOrderGameState<TakStatus,TakPlayerInfo> {
    private int size;
    private PlayerIndicator first;
    private GridBoard<PieceStack<GridBoardLocation, TakPiece>> board;

    public TakState(PlayerIndicator first, int size) throws BoardGameEngineException {
        super(new TakStatus());

        this.size = size;
        this.first = first;
        if(first == PlayerIndicator.BLACK) {
            setCurrentPlayer(1);
        }

        switch(size) {
            case 3:
                board = new GridBoard<>(size, size, new PieceStack<>(new GridBoardLocation(0,0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE, new Pieces("STONE", 10), new Pieces("CAPSTONE", 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK, new Pieces("STONE", 10), new Pieces("CAPSTONE", 0)));
                break;
            case 4:
                board = new GridBoard<>(size, size, new PieceStack<>(new GridBoardLocation(0, 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE, new Pieces("STONE", 15), new Pieces("CAPSTONE", 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK, new Pieces("STONE", 15), new Pieces("CAPSTONE", 0)));
                break;
            case 5:
                board = new GridBoard<>(size, size, new PieceStack<>(new GridBoardLocation(0, 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE, new Pieces("STONE", 21), new Pieces("CAPSTONE", 1)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK, new Pieces("STONE", 21), new Pieces("CAPSTONE", 1)));
                break;
            case 6:
                board = new GridBoard<>(size, size, new PieceStack<>(new GridBoardLocation(0, 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE, new Pieces("STONE", 30), new Pieces("CAPSTONE", 1)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK, new Pieces("STONE", 30), new Pieces("CAPSTONE", 1)));
                break;
            case 8:
                board = new GridBoard<>(size, size, new PieceStack<>(new GridBoardLocation(0, 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE, new Pieces("STONE", 50), new Pieces("CAPSTONE", 2)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK, new Pieces("STONE", 50), new Pieces("CAPSTONE", 2)));
                break;
            default:
                throw new BoardGameEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    @Override
    public String getDisplayForPlayer(PlayerIndicator player) {
        return getPlayers().get(0).toString() + "\n" + getPlayers().get(1).toString() + "\n" + board.toString();
    }
}
