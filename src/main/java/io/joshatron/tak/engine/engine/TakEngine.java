package io.joshatron.tak.engine.engine;

import io.joshatron.bgt.engine.GameEngine;
import io.joshatron.bgt.engine.dtos.GameState;
import io.joshatron.bgt.engine.dtos.Status;
import io.joshatron.bgt.engine.dtos.Turn;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.board.*;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.game.*;
import io.joshatron.tak.engine.turn.*;

import java.util.ArrayList;
import java.util.List;

public class TakEngine implements GameEngine {

    @Override
    public boolean isLegalTurn(GameState state, Turn turn) {
        try {
            if(!(state instanceof TakState) || !(turn instanceof TakTurn)) {
                throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_TYPE);
            }
            validateTurn((TakState)state, (TakTurn)turn);
            return true;
        } catch (BoardGameEngineException e) {
            return false;
        }
    }

    private void validateTurn(TakState state, TakTurn turn) throws BoardGameEngineException {
        // Make sure game isn't already over
        fillOutStatus(state);
        if (state.getStatus().isComplete()) {
            throw new BoardGameEngineException(TakEngineErrorCode.GAME_FINISHED);
        }

        //Check based on turn type
        if (turn.getType() == TurnType.PLACE) {
            validatePlace(state, (TakPlaceTurn) turn);
        } else if (turn.getType() == TurnType.MOVE) {
            validateMove(state, (TakMoveTurn) turn);
        }
    }

    private void validatePlace(TakState state, TakPlaceTurn place) throws BoardGameEngineException {
        // Check if enough pieces.
        if (place.getPieceType() != PieceType.CAPSTONE && getCurrentPlayerStones(state) < 1) {
            throw new BoardGameEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
        }
        if (place.getPieceType() == PieceType.CAPSTONE && getCurrentPlayerCapstones(state) < 1) {
            throw new BoardGameEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
        }

        // Check if it is the first couple turns that only stones are placed
        if (state.getTurns().size() < 2 && place.getPieceType() != PieceType.STONE) {
            throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_FIRST_MOVE_TYPE);
        }

        // Check the location is empty
        if(!state.getBoard().getPosition(place.getLocation()).getPieces().isEmpty()) {
            throw new BoardGameEngineException(TakEngineErrorCode.STACK_NOT_EMPTY);
        }
    }

    private void validateMove(TakState state, TakMoveTurn move) throws BoardGameEngineException {
        // No moves can be done in the first 2 turns
        if (state.getTurns().size() < 2) {
            throw new BoardGameEngineException(TakEngineErrorCode.CANT_MOVE_IN_FIRST_TURN);
        }

        // Check that the picked up pieces is legal
        if (move.getPickedUp() < 1 || move.getPickedUp() > state.getSize()) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that stack has enough pieces
        if (state.getBoard().getPosition(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that the player owns the stack
        if (state.getBoard().getPosition(move.getStartLocation()).getTopPiece().getPlayer() != state.getCurrent()) {
            throw new BoardGameEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
        }

        validateMovePlacements(state, move);
    }

    private void validateMovePlacements(TakState state, TakMoveTurn move) throws BoardGameEngineException {
        // Check that each position of move is legal
        BoardLocation currentLocation = new BoardLocation(move.getStartLocation());
        boolean topCapstone = state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE;
        int piecesLeft = move.getPickedUp();
        for(int i = 0; i < move.getPlaced().length; i++) {
            // Check that at least one piece was placed
            if(move.getPlaced()[i] < 1) {
                throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PLACE_AMOUNT);
            }

            currentLocation.move(move.getDirection());

            //Check that it is okay to place there
            if(!state.getBoard().getPosition(currentLocation).getPieces().isEmpty()) {
                // If there is a capstone, fail
                if(state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE) {
                    throw new BoardGameEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }

                // If there is a wall and you don't have only a capstone, fail
                if(state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.WALL &&
                   (piecesLeft != 1 || !topCapstone)) {
                    throw new BoardGameEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }
            }

            piecesLeft -= move.getPlaced()[i];
        }
    }

    @Override
    public List<Turn> getPossibleTurns(GameState gameState) throws BoardGameEngineException {
        if(!(gameState instanceof TakState)) {
            throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_TYPE);
        }
        TakState state = (TakState)gameState;

        List<BoardLocation> locations = new ArrayList<>();

        for(int x = 0; x < state.getSize(); x++) {
            for(int y = 0; y < state.getSize(); y++) {
                locations.add(new BoardLocation(x, y));
            }
        }

        List<Turn> turns = new ArrayList<>();
        locations.parallelStream().forEach(loc -> {
            try {
                turns.addAll(getTurnsForLocation(state, loc));
            } catch(BoardGameEngineException e) {
                e.printStackTrace();
            }
        });

        return turns;
    }

    private List<Turn> getTurnsForLocation(TakState state, BoardLocation location) throws BoardGameEngineException {
        if(state.getBoard().getPosition(location).getHeight() == 0) {
            return getPlaceTurns(state, location);
        }
        else {
            return getMoveTurns(state, location);
        }
    }

    private List<Turn> getPlaceTurns(TakState state, BoardLocation location) {
        List<Turn> turns = new ArrayList<>();

        if(state.getCurrent() == Player.WHITE) {
            if(state.getWhiteStones() > 0) {
                turns.add(new TakPlaceTurn(location, PieceType.STONE));
                if(state.getTurns().size() > 2) {
                    turns.add(new TakPlaceTurn(location, PieceType.WALL));
                }
            }
            if(state.getWhiteCapstones() > 0) {
                turns.add(new TakPlaceTurn(location, PieceType.CAPSTONE));
            }
        }
        else {
            if(state.getBlackStones() > 0) {
                turns.add(new TakPlaceTurn(location, PieceType.STONE));
                if(state.getTurns().size() > 2) {
                    turns.add(new TakPlaceTurn(location, PieceType.WALL));
                }
            }
            if(state.getBlackCapstones() > 0) {
                turns.add(new TakPlaceTurn(location, PieceType.CAPSTONE));
            }
        }

        return turns;
    }

    private List<Turn> getMoveTurns(TakState state, BoardLocation location) throws BoardGameEngineException {
        List<Turn> turns = new ArrayList<>();

        if(state.getCurrent() == state.getBoard().getPosition(location).getStackOwner()) {
            int height = Math.min(state.getSize(), state.getBoard().getPosition(location).getHeight());

            for(int i = 1; i <= height; i++) {
                turns.addAll(getMovesInDirection(state, location, i, Direction.NORTH));
                turns.addAll(getMovesInDirection(state, location, i, Direction.SOUTH));
                turns.addAll(getMovesInDirection(state, location, i, Direction.EAST));
                turns.addAll(getMovesInDirection(state, location, i, Direction.WEST));
            }
        }

        return turns;
    }

    private List<Turn> getMovesInDirection(TakState state, BoardLocation location, int height, Direction direction) {
        List<Turn> turns = new ArrayList<>();

        return turns;
    }

    private void fillOutStatus(TakState state) throws BoardGameEngineException {
        if(state.getStatus() != null) {
            return;
        }

        // Check if someone is out of pieces
        if(getOtherPlayerStones(state) == 0 && getOtherPlayerCapstones(state) == 0) {
            state.setStatus(getWinnerFromPoints(state, WinReason.OUT_OF_PIECES));
            return;
        }

        //Check for a full board
        boolean full = true;
        for(int x = 0; x < state.getSize(); x++) {
            for(int y = 0; y < state.getSize(); y++) {
                if(state.getBoard().getPosition(x, y).getHeight() == 0) {
                    full = false;
                    break;
                }
            }
        }

        if(full) {
            state.setStatus(getWinnerFromPoints(state, WinReason.BOARD_FULL));
            return;
        }

        //Check for each possible path
        for(int i = 0; i < state.getSize(); i++) {
            if(state.getBoard().getPosition(0, i).getTopPiece() != null &&
               state.getBoard().getPosition(0, i).getStackOwner() == state.getCurrent().opposite() &&
               state.getBoard().getPosition(0, i).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new BoardLocation(0, i), new boolean[state.getSize()][state.getSize()],
                       true, state.getBoard().getPosition(0, i).getTopPiece().isWhite())) {
                state.setStatus(new TakStatus(Status.COMPLETE, state.getCurrent().opposite(), WinReason.PATH, getScore(state, state.getCurrent().opposite())));
                return;
            }
            if(state.getBoard().getPosition(i, 0).getTopPiece() != null &&
               state.getBoard().getPosition(i, 0).getStackOwner() == state.getCurrent().opposite() &&
               state.getBoard().getPosition(i, 0).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new BoardLocation(i, 0), new boolean[state.getSize()][state.getSize()],
                       false, state.getBoard().getPosition(i, 0).getTopPiece().isWhite())) {
                state.setStatus(new TakStatus(Status.COMPLETE, state.getCurrent().opposite(), WinReason.PATH, getScore(state, state.getCurrent().opposite())));
                return;
            }
        }

        state.setStatus(new TakStatus());
    }

    private TakStatus getWinnerFromPoints(TakState state, WinReason reason) throws BoardGameEngineException {
        int whitePoints = 0;
        int blackPoints = 0;

        for(int x = 0; x < state.getSize(); x++) {
            for(int y = 0; y < state.getSize(); y++) {
                Player owner = state.getBoard().getPosition(x, y).getStackOwner();
                if(owner == Player.WHITE) {
                    whitePoints++;
                }
                else if(owner == Player.BLACK) {
                    blackPoints++;
                }
            }
        }

        if(whitePoints > blackPoints) {
            return new TakStatus(Status.COMPLETE, Player.WHITE, reason, getScore(state, Player.WHITE));
        }
        else if(blackPoints > whitePoints) {
            return new TakStatus(Status.COMPLETE, Player.BLACK, reason, getScore(state, Player.BLACK));
        }
        else if(state.getWhiteCapstones() > state.getBlackCapstones()) {
            return new TakStatus(Status.COMPLETE, Player.WHITE, reason, getScore(state, Player.WHITE));
        }
        else if(state.getBlackCapstones() > state.getWhiteCapstones()) {
            return new TakStatus(Status.COMPLETE, Player.BLACK, reason, getScore(state, Player.BLACK));
        }
        else {
            return new TakStatus(Status.COMPLETE, Player.NONE, reason, 0);
        }
    }

    private int getScore(TakState state, Player player) throws BoardGameEngineException {
        int points = 0;
        for(int x = 0; x < state.getSize(); x++) {
            for(int y = 0; y < state.getSize(); y++) {
                if(state.getBoard().getPosition(x, y).getStackOwner() == player) {
                    points++;
                }
            }
        }

        return state.getSize() * state.getSize() + points;
    }

    private boolean isWinPath(GameBoard board, BoardLocation current, boolean[][] checked, boolean horizontal, boolean white) throws BoardGameEngineException {
        if((horizontal && current.getX() == board.getBoardSize() - 1) ||
           (!horizontal && current.getY() == board.getBoardSize() - 1)) {
            return true;
        }

        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for(int i = 0; i < dirs.length; i++) {
            current.move(dirs[i]);
            if (current.getY() >= 0 && current.getY() < board.getBoardSize() &&
                current.getX() >= 0 && current.getX() < board.getBoardSize()) {
                Piece topPiece = board.getPosition(current).getTopPiece();
                if(topPiece != null && !checked[current.getX()][current.getY()] && topPiece.isWhite() == white &&
                   (topPiece.getType() == PieceType.CAPSTONE || topPiece.getType() == PieceType.STONE)) {
                    checked[current.getX()][current.getY()] = true;
                    if (isWinPath(board, current, checked, horizontal, white)) {
                        return true;
                    }
                }
            }
            current.moveOpposite(dirs[i]);
        }

        return false;
    }

    @Override
    public GameState executeTurn(GameState state, Turn turn) throws BoardGameEngineException {
        if(!(state instanceof TakState) || !(turn instanceof TakTurn)) {
            throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_TYPE);
        }
        validateTurn((TakState)state, (TakTurn)turn);
        applyTurn((TakState)state, (TakTurn)turn);
        fillOutStatus((TakState)state);

        return state;
    }

    private void applyTurn(TakState state, TakTurn turn) throws BoardGameEngineException {
        if(turn.getType() == TurnType.PLACE) {
            applyPlace(state, (TakPlaceTurn) turn);
        }
        else if(turn.getType() == TurnType.MOVE) {
            applyMove(state, (TakMoveTurn) turn);
        }

        state.getTurns().add(turn);
        state.setCurrent(state.getCurrent().opposite());
    }

    private void applyPlace(TakState state, TakPlaceTurn place) throws BoardGameEngineException {
        Player player = state.getCurrent();
        if(state.getTurns().size() < 2) {
            player = player.opposite();
        }
        state.getBoard().getPosition(place.getLocation()).addPiece(new Piece(player, place.getPieceType()));

        if(place.getPieceType() == PieceType.CAPSTONE) {
            if(player == Player.WHITE) {
                state.setWhiteCapstones(state.getWhiteCapstones() - 1);
            }
            else {
                state.setBlackCapstones(state.getBlackCapstones() - 1);
            }
        }
        else {
            if(player == Player.WHITE) {
                state.setWhiteStones(state.getWhiteStones() - 1);
            }
            else {
                state.setBlackStones(state.getBlackStones() - 1);
            }
        }
    }

    private void applyMove(TakState state, TakMoveTurn move) throws BoardGameEngineException {
        List<Piece> pieces = state.getBoard().getPosition(move.getStartLocation()).removePieces(move.getPickedUp());
        BoardLocation current = new BoardLocation(move.getStartLocation());
        for(int i = 0; i < move.getPlaced().length; i++) {
            current.move(move.getDirection());
            // If there is a wall, collapse it
            if(!state.getBoard().getPosition(current).getPieces().isEmpty() &&
               state.getBoard().getPosition(current).getTopPiece().getType() == PieceType.WALL) {
                state.getBoard().getPosition(current).collapseTopPiece();
                move.flatten();
            }
            // Place the right number of pieces in
            for(int j = 0; j < move.getPlaced()[i]; j++) {
                state.getBoard().getPosition(current).addPiece(pieces.remove(0));
            }
        }
    }

    @Override
    public GameState undoTurn(GameState state) throws BoardGameEngineException {
        if(!(state instanceof TakState)) {
            throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_TYPE);
        }

        TakState s = (TakState)state;

        TakTurn turn = (TakTurn)s.getTurns().remove(state.getTurns().size() - 1);
        s.setCurrent(s.getCurrent().opposite());

        //Undo a place turn
        if(turn.getType() == TurnType.PLACE) {
            undoPlace(s, (TakPlaceTurn) turn);
        }
        //Undo a move turn
        else if(turn.getType() == TurnType.MOVE) {
            undoMove(s, (TakMoveTurn) turn);
        }

        s.setStatus(null);

        return state;
    }

    private void undoPlace(TakState state, TakPlaceTurn place) throws BoardGameEngineException {
        state.getBoard().getPosition(place.getLocation()).removePieces(1);

        if(place.getPieceType() == PieceType.CAPSTONE) {
            if(state.getCurrent() == Player.WHITE) {
                state.setWhiteCapstones(state.getWhiteCapstones() + 1);
            }
            else {
                state.setBlackCapstones(state.getBlackCapstones() + 1);
            }
        }
        else {
            if(state.getCurrent() == Player.WHITE) {
                state.setWhiteStones(state.getWhiteStones() + 1);
            }
            else {
                state.setBlackStones(state.getBlackStones() + 1);
            }
        }
    }

    private void undoMove(TakState state, TakMoveTurn move) throws BoardGameEngineException {
        BoardLocation current = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
        for(int i = 0; i < move.getPlaced().length; i++) {
            current.move(move.getDirection());
        }
        ArrayList<Piece> pickedUp = new ArrayList<>();
        for(int i = move.getPlaced().length - 1; i >= 0; i--) {
            pickedUp.addAll(0, state.getBoard().getPosition(current).removePieces(move.getPlaced()[i]));
            if(i == move.getPlaced().length - 1 && move.didFlatten()) {
                state.getBoard().getPosition(current).uncollapseTopPiece();
            }

            current.moveOpposite(move.getDirection());
        }

        state.getBoard().getPosition(current).addPieces(pickedUp);
    }

    private int getCurrentPlayerStones(TakState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
           (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getWhiteStones();
        }
        else {
            return state.getBlackStones();
        }
    }

    private int getCurrentPlayerCapstones(TakState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getWhiteCapstones();
        }
        else {
            return state.getBlackCapstones();
        }
    }

    private int getOtherPlayerStones(TakState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getBlackStones();
        }
        else {
            return state.getWhiteStones();
        }
    }

    private int getOtherPlayerCapstones(TakState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getBlackCapstones();
        }
        else {
            return state.getWhiteCapstones();
        }
    }
}
