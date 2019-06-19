package io.joshatron.tak.engine.engine;

import io.joshatron.tak.engine.board.*;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.game.*;
import io.joshatron.tak.engine.turn.*;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public static boolean isLegalTurn(GameState state, Turn turn) {
        try {
            validateTurn(state, turn);
            return true;
        } catch (TakEngineException e) {
            return false;
        }
    }

    private static void validateTurn(GameState state, Turn turn) throws TakEngineException {
        // Make sure game isn't already over
        fillOutWinner(state);
        if (state.getResult().isFinished()) {
            throw new TakEngineException(TakEngineErrorCode.GAME_FINISHED);
        }

        //Check based on turn type
        if (turn.getType() == TurnType.PLACE) {
            validatePlace(state, (PlaceTurn) turn);
        } else if (turn.getType() == TurnType.MOVE) {
            validateMove(state, (MoveTurn) turn);
        }
    }

    private static void validatePlace(GameState state, PlaceTurn place) throws TakEngineException {
        // Check if enough pieces.
        if (place.getPieceType() != PieceType.CAPSTONE && getCurrentPlayerStones(state) < 1) {
            throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
        }
        if (place.getPieceType() == PieceType.CAPSTONE && getCurrentPlayerCapstones(state) < 1) {
            throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
        }

        // Check if it is the first couple turns that only stones are placed
        if (state.getTurns().size() < 2 && place.getPieceType() != PieceType.STONE) {
            throw new TakEngineException(TakEngineErrorCode.ILLEGAL_FIRST_MOVE_TYPE);
        }

        // Check the location is empty
        if(!state.getBoard().getPosition(place.getLocation()).getPieces().isEmpty()) {
            throw new TakEngineException(TakEngineErrorCode.STACK_NOT_EMPTY);
        }
    }

    private static void validateMove(GameState state, MoveTurn move) throws TakEngineException {
        // No moves can be done in the first 2 turns
        if (state.getTurns().size() < 2) {
            throw new TakEngineException(TakEngineErrorCode.CANT_MOVE_IN_FIRST_TURN);
        }

        // Check that the picked up pieces is legal
        if (move.getPickedUp() < 1 || move.getPickedUp() > state.getSize()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that stack has enough pieces
        if (state.getBoard().getPosition(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that the player owns the stack
        if (state.getBoard().getPosition(move.getStartLocation()).getTopPiece().getPlayer() != state.getCurrent()) {
            throw new TakEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
        }

        validateMovePlacements(state, move);
    }

    private static void validateMovePlacements(GameState state, MoveTurn move) throws TakEngineException {
        // Check that each position of move is legal
        BoardLocation currentLocation = new BoardLocation(move.getStartLocation());
        boolean topCapstone = state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE;
        int piecesLeft = move.getPickedUp();
        for(int i = 0; i < move.getPlaced().length; i++) {
            // Check that at least one piece was placed
            if(move.getPlaced()[i] < 1) {
                throw new TakEngineException(TakEngineErrorCode.INVALID_PLACE_AMOUNT);
            }

            currentLocation.move(move.getDirection());

            //Check that it is okay to place there
            if(!state.getBoard().getPosition(currentLocation).getPieces().isEmpty()) {
                // If there is a capstone, fail
                if(state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE) {
                    throw new TakEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }

                // If there is a wall and you don't have only a capstone, fail
                if(state.getBoard().getPosition(currentLocation).getTopPiece().getType() == PieceType.WALL &&
                   (piecesLeft != 1 || !topCapstone)) {
                    throw new TakEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }
            }

            piecesLeft -= move.getPlaced()[i];
        }
    }

    public static void fillOutWinner(GameState state) throws TakEngineException {
        if(state.getResult() != null) {
            return;
        }

        // Check if someone is out of pieces
        if(getOtherPlayerStones(state) == 0 && getOtherPlayerCapstones(state) == 0) {
            state.setResult(getWinnerFromPoints(state, WinReason.OUT_OF_PIECES));
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
            state.setResult(getWinnerFromPoints(state, WinReason.BOARD_FULL));
            return;
        }

        //Check for each possible path
        for(int i = 0; i < state.getSize(); i++) {
            if(state.getBoard().getPosition(0, i).getTopPiece() != null &&
               state.getBoard().getPosition(0, i).getStackOwner() == state.getCurrent().opposite() &&
               state.getBoard().getPosition(0, i).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new BoardLocation(0, i), new boolean[state.getSize()][state.getSize()],
                       true, state.getBoard().getPosition(0, i).getTopPiece().isWhite())) {
                state.setResult(new GameResult(true, state.getCurrent().opposite(), WinReason.PATH, getScore(state, state.getCurrent().opposite())));
                return;
            }
            if(state.getBoard().getPosition(i, 0).getTopPiece() != null &&
               state.getBoard().getPosition(i, 0).getStackOwner() == state.getCurrent().opposite() &&
               state.getBoard().getPosition(i, 0).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(state.getBoard(), new BoardLocation(i, 0), new boolean[state.getSize()][state.getSize()],
                       false, state.getBoard().getPosition(i, 0).getTopPiece().isWhite())) {
                state.setResult(new GameResult(true, state.getCurrent().opposite(), WinReason.PATH, getScore(state, state.getCurrent().opposite())));
                return;
            }
        }

        state.setResult(new GameResult());
    }

    private static GameResult getWinnerFromPoints(GameState state, WinReason reason) throws TakEngineException {
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
            return new GameResult(true, Player.WHITE, reason, getScore(state, Player.WHITE));
        }
        else if(blackPoints > whitePoints) {
            return new GameResult(true, Player.BLACK, reason, getScore(state, Player.BLACK));
        }
        else if(state.getWhiteCapstones() > state.getBlackCapstones()) {
            return new GameResult(true, Player.WHITE, reason, getScore(state, Player.WHITE));
        }
        else if(state.getBlackCapstones() > state.getWhiteCapstones()) {
            return new GameResult(true, Player.BLACK, reason, getScore(state, Player.BLACK));
        }
        else {
            return new GameResult(true, Player.NONE, reason, 0);
        }
    }

    private static int getScore(GameState state, Player player) throws TakEngineException {
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

    private static boolean isWinPath(GameBoard board, BoardLocation current, boolean[][] checked, boolean horizontal, boolean white) throws TakEngineException {
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

    public static void executeTurn(GameState state, Turn turn) throws TakEngineException {
        validateTurn(state, turn);
        applyTurn(state, turn);
        fillOutWinner(state);
    }

    private static void applyTurn(GameState state, Turn turn) throws TakEngineException {
        if(turn.getType() == TurnType.PLACE) {
            applyPlace(state, (PlaceTurn) turn);
        }
        else if(turn.getType() == TurnType.MOVE) {
            applyMove(state, (MoveTurn) turn);
        }

        state.getTurns().add(turn);
        state.setCurrent(state.getCurrent().opposite());
    }

    private static void applyPlace(GameState state, PlaceTurn place) throws TakEngineException {
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

    private static void applyMove(GameState state, MoveTurn move) throws TakEngineException {
        List<Piece> pieces = board.getPosition(move.getStartLocation()).removePieces(move.getPickedUp());
        if(board.getPosition(move.getStartLocation()).getHeight() == 0) {
            getInfo(getCurrentForInfo()).decrementPoints();
            piecesFilled--;
        }
        else if(board.getPosition(move.getStartLocation()).getStackOwner() != currentTurn) {
            getInfo(getCurrentForInfo()).decrementPoints();
            getInfo(getCurrentForInfo().opposite()).incrementPoints();
        }
        BoardLocation current = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
        for(int i = 0; i < move.getPlaced().length; i++) {
            current.move(move.getDirection());
            Player oldOwner = board.getPosition(current).getStackOwner();
            // If there is a wall, collapse it
            if(!board.getPosition(current).getPieces().isEmpty() && board.getPosition(current).getTopPiece().getType() == PieceType.WALL) {
                board.getPosition(current).collapseTopPiece();
                move.flatten();
            }
            // Place the right number of pieces in
            for(int j = 0; j < move.getPlaced()[i]; j++) {
                board.getPosition(current).addPiece(pieces.remove(0));
            }
            if(oldOwner == null) {
                piecesFilled++;
                getInfo(board.getPosition(current).getStackOwner()).incrementPoints();
            }
            else if(board.getPosition(current).getStackOwner() != oldOwner) {
                getInfo(oldOwner).decrementPoints();
                getInfo(oldOwner.opposite()).incrementPoints();
            }
        }
    }

    public static Turn undoTurn() throws TakEngineException {
        Turn turn = turns.remove(turns.size() - 1);
        currentTurn = currentTurn.opposite();

        //Undo a place turn
        if(turn.getType() == TurnType.PLACE) {
            undoPlace((PlaceTurn) turn);
        }
        //Undo a move turn
        else if(turn.getType() == TurnType.MOVE) {
            undoMove((MoveTurn) turn);
        }

        result = null;

        return turn;
    }

    private static void undoPlace(PlaceTurn place) throws TakEngineException {
        board.getPosition(place.getLocation()).removePieces(1);
        piecesFilled--;
        PlayerInfo info = getInfo(getCurrentForInfo());

        if (place.getPieceType() == PieceType.STONE) {
            info.incrementStones();
            info.decrementPoints();
        }
        else if(place.getPieceType() == PieceType.WALL) {
            info.incrementStones();
        } else {
            info.incrementCapstones();
        }
    }

    private static void undoMove(MoveTurn move) throws TakEngineException {
        BoardLocation current = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
        for(int i = 0; i < move.getPlaced().length; i++) {
            current.move(move.getDirection());
        }
        ArrayList<Piece> pickedUp = new ArrayList<>();
        for(int i = move.getPlaced().length - 1; i >= 0; i--) {
            Player oldOwner = board.getPosition(current).getStackOwner();
            pickedUp.addAll(0, board.getPosition(current).removePieces(move.getPlaced()[i]));
            if(i == move.getPlaced().length - 1 && move.didFlatten()) {
                board.getPosition(current).uncollapseTopPiece();
            }

            if(board.getPosition(current).getHeight() == 0) {
                piecesFilled--;
                getInfo(oldOwner).decrementPoints();
            }
            else if(oldOwner != board.getPosition(current).getStackOwner()) {
                getInfo(oldOwner).decrementPoints();
                getInfo(oldOwner.opposite()).incrementPoints();
            }

            current.moveOpposite(move.getDirection());
        }

        Player oldOwner = board.getPosition(current).getStackOwner();
        board.getPosition(current).addPieces(pickedUp);
        if(oldOwner == null) {
            piecesFilled++;
            getInfo(board.getPosition(current).getStackOwner()).incrementPoints();
        }
        else if(oldOwner != board.getPosition(current).getStackOwner()) {
            getInfo(oldOwner).decrementPoints();
            getInfo(oldOwner.opposite()).incrementPoints();
        }
    }

    public static void printBoard(GameState state) {
        System.out.println("WS: " + state.getWhiteStones() + " WC: " + state.getWhiteCapstones() +
                           " BS: " + state.getBlackStones() + " BC: " + state.getBlackCapstones());

        state.getBoard().printBoard();
    }

    private static int getCurrentPlayerStones(GameState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
           (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getWhiteStones();
        }
        else {
            return state.getBlackStones();
        }
    }

    private static int getCurrentPlayerCapstones(GameState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getWhiteCapstones();
        }
        else {
            return state.getBlackCapstones();
        }
    }

    private static int getOtherPlayerStones(GameState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getBlackStones();
        }
        else {
            return state.getWhiteStones();
        }
    }

    private static int getOtherPlayerCapstones(GameState state) {
        if((state.getTurns().size() < 2 && state.getCurrent() == Player.BLACK) ||
                (state.getTurns().size() >= 2 && state.getCurrent() == Player.WHITE)) {
            return state.getBlackCapstones();
        }
        else {
            return state.getWhiteCapstones();
        }
    }
}
