package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.action.Action;
import io.joshatron.bgt.engine.action.ActionResult;
import io.joshatron.bgt.engine.board.grid.Direction;
import io.joshatron.bgt.engine.board.grid.GridBoard;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.component.PiecePile;
import io.joshatron.bgt.engine.component.PieceStack;
import io.joshatron.bgt.engine.engines.InOrderGameEngine;
import io.joshatron.bgt.engine.exception.BoardGameCommonErrorCode;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.Status;
import io.joshatron.tak.engine.board.*;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.turn.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TakEngineMainTurns extends InOrderGameEngine<TakState> {
    @Override
    public boolean isActionValid(TakState state, Action action) {
        try {
            if(!(action instanceof TakPlaceAction || action instanceof TakMoveAction)) {
                throw new BoardGameEngineException(TakEngineErrorCode.ILLEGAL_TYPE);
            }
            validateAction(state, action);
            return true;
        } catch (BoardGameEngineException e) {
            return false;
        }
    }

    private void validateAction(TakState state, Action action) throws BoardGameEngineException {
        // Make sure game isn't already over
        fillOutStatus(state);
        if (state.getStatus().isComplete()) {
            throw new BoardGameEngineException(TakEngineErrorCode.GAME_FINISHED);
        }

        //Check based on action type
        if (action instanceof TakPlaceAction) {
            validatePlace(state, (TakPlaceAction) action);
        } else if (action instanceof TakMoveAction) {
            validateMove(state, (TakMoveAction) action);
        }
    }

    private void validatePlace(TakState state, TakPlaceAction place) throws BoardGameEngineException {
        // Check if enough pieces.
        if (place.getPieceType() != PieceType.CAPSTONE && state.getCurrentPlayerInfo().getStones().outOfPieces()) {
            throw new BoardGameEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
        }
        if (place.getPieceType() == PieceType.CAPSTONE && state.getCurrentPlayerInfo().getCapstones().outOfPieces()) {
            throw new BoardGameEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
        }

        // Check the location is empty
        if(!state.getBoard().getTile(place.getLocation()).getPieces().isEmpty()) {
            throw new BoardGameEngineException(TakEngineErrorCode.STACK_NOT_EMPTY);
        }
    }

    private void validateMove(TakState state, TakMoveAction move) throws BoardGameEngineException {
        // Check that the picked up pieces is legal
        if (move.getPickedUp() < 1 || move.getPickedUp() > state.getSize()) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Cannot move diagonally
        if(move.getDirection().isDiagonal()) {
            throw new BoardGameEngineException(BoardGameCommonErrorCode.INVALID_DIRECTION);
        }

        // Check that stack has enough pieces
        if (state.getBoard().getTile(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
            throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that the player owns the stack
        if (state.getBoard().getTile(move.getStartLocation()).getTopPiece().getOwner() != state.getCurrentPlayerInfo().getIdentifier()) {
            throw new BoardGameEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
        }

        validateMovePlacements(state, move);
    }

    private void validateMovePlacements(TakState state, TakMoveAction move) throws BoardGameEngineException {
        // Check that each position of move is legal
        GridBoardLocation currentLocation = new GridBoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
        boolean topCapstone = state.getBoard().getTile(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE;
        int piecesLeft = move.getPickedUp();
        for(int i = 0; i < move.getPlaced().length; i++) {
            // Check that at least one piece was placed
            if(move.getPlaced()[i] < 1) {
                throw new BoardGameEngineException(TakEngineErrorCode.INVALID_PLACE_AMOUNT);
            }

            currentLocation.move(move.getDirection(), 1);

            //Check that it is okay to place there
            if(!state.getBoard().getTile(currentLocation).getPieces().isEmpty()) {
                // If there is a capstone, fail
                if(state.getBoard().getTile(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE) {
                    throw new BoardGameEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }

                // If there is a wall and you don't have only a capstone, fail
                if(state.getBoard().getTile(currentLocation).getTopPiece().getType() == PieceType.WALL &&
                   (piecesLeft != 1 || !topCapstone)) {
                    throw new BoardGameEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }
            }

            piecesLeft -= move.getPlaced()[i];
        }
    }

    @Override
    public List<Action> getPossibleActions(TakState state) throws BoardGameEngineException {
        ArrayList<Action> possibleActions = new ArrayList<>();

        List<List<Action>> actions = state.getBoard().getAllLocations().parallelStream()
                .map(tile -> getPossibleForLocation(state, tile)).collect(Collectors.toList());

        for(List<Action> a : actions) {
            possibleActions.addAll(a);
        }

        return possibleActions;
    }

    private List<Action> getPossibleForLocation(TakState state, GridBoardLocation location) {
        try {
            PieceStack<TakPiece> tile = state.getBoard().getTile(location);
            ArrayList<Action> possibleTurns = new ArrayList<>();

            //If it is empty, add possible places
            if(tile.getHeight() == 0) {
                TakPlayerInfo info = state.getCurrentPlayerInfo();
                if(!info.getStones().outOfPieces()) {
                    possibleTurns.add(new TakPlaceAction(info.getIdentifier(), location, PieceType.STONE));
                    possibleTurns.add(new TakPlaceAction(info.getIdentifier(), location, PieceType.WALL));
                }
                if(!info.getCapstones().outOfPieces()) {
                    possibleTurns.add(new TakPlaceAction(info.getIdentifier(), location, PieceType.CAPSTONE));
                }
            }
            //Otherwise iterate through possible moves if player owns the stack
            else if(tile.getTopPiece().getOwner() == state.getCurrentPlayerInfo().getIdentifier()) {
                possibleTurns.addAll(getMoves(state, location, Direction.NORTH));
                possibleTurns.addAll(getMoves(state, location, Direction.SOUTH));
                possibleTurns.addAll(getMoves(state, location, Direction.EAST));
                possibleTurns.addAll(getMoves(state, location, Direction.WEST));
            }

            return possibleTurns;
        } catch(BoardGameEngineException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    private ArrayList<Action> getMoves(TakState state, GridBoardLocation location, Direction dir) throws BoardGameEngineException {
        PieceStack<TakPiece> tile = state.getBoard().getTile(location);
        ArrayList<Action> possibleTurns = new ArrayList<>();

        int numPieces = Math.min(tile.getHeight(), state.getSize());
        int distToBlock = 0;
        GridBoardLocation loc = new GridBoardLocation(location);
        loc.move(dir, 1);
        while(state.getBoard().onBoard(loc) &&
                (state.getBoard().getTile(loc).getHeight() == 0 ||
                        state.getBoard().getTile(loc).getTopPiece().getType() == PieceType.STONE)) {
            distToBlock++;
            loc.move(dir, 1);
        }
        boolean canFlatten = false;
        if(state.getBoard().onBoard(loc) && state.getBoard().getTile(loc).getHeight() > 0 &&
                state.getBoard().getTile(loc).getTopPiece().getType() == PieceType.WALL &&
                tile.getTopPiece().getType() == PieceType.CAPSTONE) {
            canFlatten = true;
        }

        if(distToBlock > 0) {
            while (numPieces > 0) {
                possibleTurns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces, new ArrayList<>(),
                        location, dir, numPieces, state.getCurrentPlayerInfo().getIdentifier()));
                numPieces--;
            }
        }

        return possibleTurns;
    }

    private List<Action> getMovesInner(int distToBlock, boolean canFlatten, int numPieces, List<Integer> drops, GridBoardLocation location, Direction dir, int pickup, PlayerIndicator player) throws BoardGameEngineException {
        List<Action> possibleActions = new ArrayList<>();
        //at last spot
        if(distToBlock == 0) {
            possibleActions.add(buildMove(location, pickup, dir, drops, numPieces, player));
            if(canFlatten && numPieces > 1) {
                drops.add(numPieces - 1);
                possibleActions.add(buildMove(location, pickup, dir, drops, 1, player));
            }
        }
        //iterate through everything else
        else {
            possibleActions.add(buildMove(location, pickup, dir, drops, numPieces, player));
            int piecesLeft = numPieces - 1;
            while(piecesLeft > 0) {
                drops.add(piecesLeft);
                possibleActions.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces - piecesLeft, new ArrayList<>(drops), location, dir, pickup, player));
                drops.remove(drops.size() - 1);
                piecesLeft--;
            }
        }

        return possibleActions;
    }

    private TakMoveAction buildMove(GridBoardLocation location, int pickup, Direction dir, List<Integer> drops, int current, PlayerIndicator player) throws BoardGameEngineException {
        int[] drop = new int[drops.size() + 1];
        for(int i = 0; i < drops.size(); i++) {
            drop[i] = drops.get(i);
        }
        drop[drop.length - 1] = current;
        return new TakMoveAction(player, location, pickup, dir, drop);
    }

    private void fillOutStatus(TakState state) throws BoardGameEngineException {
        if(state.getStatus() != null) {
            return;
        }

        // Check if someone is out of pieces
        TakPlayerInfo player = state.getCurrentPlayerInfo();
        if(player.getStones().outOfPieces() && player.getCapstones().outOfPieces()) {
            state.setStatus(getWinnerFromPoints(state, WinReason.OUT_OF_PIECES));
            return;
        }

        //Check for a full board
        boolean full = true;
        for(int x = 0; x < state.getSize(); x++) {
            for(int y = 0; y < state.getSize(); y++) {
                if(state.getBoard().getTile(x, y).getHeight() == 0) {
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
            PieceStack<TakPiece> tile = state.getBoard().getTile(0, i);
            if(tile.getTopPiece() != null && tile.getTopPiece().getOwner() == player.getIdentifier() &&
               tile.getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new GridBoardLocation(0, i), new boolean[state.getSize()][state.getSize()],
                       true, tile.getTopPiece().getOwner())) {
                state.setStatus(new TakStatus(Status.COMPLETE, player.getIdentifier(), WinReason.PATH, getScore(state, player.getIdentifier())));
                return;
            }
            tile = state.getBoard().getTile(i, 0);
            if(tile.getTopPiece() != null && tile.getTopPiece().getOwner() == player.getIdentifier() &&
               tile.getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new GridBoardLocation(i, 0), new boolean[state.getSize()][state.getSize()],
                       false, tile.getTopPiece().getOwner())) {
                state.setStatus(new TakStatus(Status.COMPLETE, player.getIdentifier(), WinReason.PATH, getScore(state, player.getIdentifier())));
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
                if(state.getBoard().getTile(x, y).getHeight() > 0 &&
                   state.getBoard().getTile(x, y).getTopPiece().getType() == PieceType.STONE) {
                    PlayerIndicator owner = state.getBoard().getTile(x, y).getTopPiece().getOwner();
                    if(owner == PlayerIndicator.WHITE) {
                        whitePoints++;
                    } else if(owner == PlayerIndicator.BLACK) {
                        blackPoints++;
                    }
                }
            }
        }

        PiecePile whiteCapstones = state.getPlayerInfo(PlayerIndicator.WHITE).getCapstones();
        PiecePile blackCapstones = state.getPlayerInfo(PlayerIndicator.BLACK).getCapstones();
        if(whitePoints > blackPoints) {
            return new TakStatus(Status.COMPLETE, PlayerIndicator.WHITE, reason, getScore(state, PlayerIndicator.WHITE));
        }
        else if(blackPoints > whitePoints) {
            return new TakStatus(Status.COMPLETE, PlayerIndicator.BLACK, reason, getScore(state, PlayerIndicator.BLACK));
        }
        else if(whiteCapstones.getPiecesLeft() > blackCapstones.getPiecesLeft()) {
            return new TakStatus(Status.COMPLETE, PlayerIndicator.WHITE, reason, getScore(state, PlayerIndicator.WHITE));
        }
        else if(blackCapstones.getPiecesLeft() > whiteCapstones.getPiecesLeft()) {
            return new TakStatus(Status.COMPLETE, PlayerIndicator.BLACK, reason, getScore(state, PlayerIndicator.BLACK));
        }
        else {
            return new TakStatus(Status.COMPLETE, null, reason, 0);
        }
    }

    private int getScore(TakState state, PlayerIndicator player) throws BoardGameEngineException {
        int points;
        TakPlayerInfo playerInfo = state.getPlayerInfo(player);
        points = playerInfo.getStones().getPiecesLeft() + playerInfo.getCapstones().getPiecesLeft();

        return state.getSize() * state.getSize() + points;
    }

    private boolean isWinPath(GridBoard<PieceStack<TakPiece>> board, GridBoardLocation current, boolean[][] checked, boolean horizontal, PlayerIndicator player) throws BoardGameEngineException {
        if((horizontal && current.getX() == board.getWidth() - 1) ||
           (!horizontal && current.getY() == board.getWidth() - 1)) {
            return true;
        }

        Direction[] dirs = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        for(int i = 0; i < dirs.length; i++) {
            current.move(dirs[i], 1);
            if (current.getY() >= 0 && current.getY() < board.getWidth() &&
                current.getX() >= 0 && current.getX() < board.getWidth()) {
                TakPiece topPiece = board.getTile(current).getTopPiece();
                if(topPiece != null && !checked[current.getX()][current.getY()] && topPiece.getOwner() == player &&
                   (topPiece.getType() == PieceType.CAPSTONE || topPiece.getType() == PieceType.STONE)) {
                    checked[current.getX()][current.getY()] = true;
                    if (isWinPath(board, current, checked, horizontal, player)) {
                        return true;
                    }
                }
            }
            current.move(dirs[i].opposite(), 1);
        }

        return false;
    }

    @Override
    public ActionResult updateState(TakState state, Action action) throws BoardGameEngineException {
        validateAction(state, action);
        applyAction(state, action);
        state.setStatus(null);
        fillOutStatus(state);

        return null;
    }

    @Override
    protected boolean isTurnDone(TakState inOrderGameState) {
        return true;
    }

    private void applyAction(TakState state, Action action) throws BoardGameEngineException {
        if(action instanceof TakPlaceAction) {
            applyPlace(state, (TakPlaceAction) action);
        }
        else if(action instanceof TakMoveAction) {
            applyMove(state, (TakMoveAction) action);
        }
    }

    private void applyPlace(TakState state, TakPlaceAction place) throws BoardGameEngineException {
        TakPlayerInfo player = state.getCurrentPlayerInfo();
        state.getBoard().getTile(place.getLocation()).addPiece(new TakPiece(player.getIdentifier(), place.getPieceType()));

        if(place.getPieceType() == PieceType.CAPSTONE) {
            player.getCapstones().removePieces(1);
        }
        else {
            player.getStones().removePieces(1);
        }
    }

    private void applyMove(TakState state, TakMoveAction move) throws BoardGameEngineException {
        List<TakPiece> pieces = state.getBoard().getTile(move.getStartLocation()).removePieces(move.getPickedUp());
        GridBoardLocation current = new GridBoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
        for(int i = 0; i < move.getPlaced().length; i++) {
            current.move(move.getDirection(), 1);
            TakPiece topPiece = state.getBoard().getTile(current).getTopPiece();
            // If there is a wall, collapse it
            if(topPiece != null && topPiece.getType() == PieceType.WALL) {
                topPiece.setType(PieceType.STONE);
                move.flatten();
            }
            // Place the right number of pieces in
            for(int j = 0; j < move.getPlaced()[i]; j++) {
                state.getBoard().getTile(current).addPiece(pieces.remove(0));
            }
        }
    }
}
