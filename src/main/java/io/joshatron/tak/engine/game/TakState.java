package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.grid.GridBoard;
import io.joshatron.bgt.engine.component.PiecePile;
import io.joshatron.bgt.engine.component.PieceStack;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.InOrderGameState;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.board.TakPiece;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TakState extends InOrderGameState<TakStatus,TakPlayerInfo> {
    private int size;
    private PlayerIndicator first;
    private GridBoard<PieceStack<TakPiece>> board;

    public TakState(PlayerIndicator first, int size) throws BoardGameEngineException {
        super(new TakStatus());

        this.size = size;
        this.first = first;
        if(first == PlayerIndicator.BLACK) {
            setCurrentPlayer(1);
        }

        switch(size) {
            case 3:
                board = new GridBoard<>(size, size, new PieceStack<>());
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE), 10),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE),10),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 0)));
                break;
            case 4:
                board = new GridBoard<>(size, size, new PieceStack<>());
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE), 15),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 0)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK,
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.STONE), 15),
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.CAPSTONE), 0)));
                break;
            case 5:
                board = new GridBoard<>(size, size, new PieceStack<>());
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE), 21),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 1)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK,
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.STONE), 21),
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.CAPSTONE), 1)));
                break;
            case 6:
                board = new GridBoard<>(size, size, new PieceStack<>());
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE), 30),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 1)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK,
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.STONE), 30),
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.CAPSTONE), 1)));
                break;
            case 8:
                board = new GridBoard<>(size, size, new PieceStack<>());
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.WHITE,
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.STONE), 50),
                        new PiecePile<>(new TakPiece(PlayerIndicator.WHITE, PieceType.CAPSTONE), 2)));
                getPlayers().add(new TakPlayerInfo(PlayerIndicator.BLACK,
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.STONE), 50),
                        new PiecePile<>(new TakPiece(PlayerIndicator.BLACK, PieceType.CAPSTONE), 2)));
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
