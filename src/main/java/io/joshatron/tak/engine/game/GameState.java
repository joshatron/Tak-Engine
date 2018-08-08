package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.*;
import io.joshatron.tak.engine.turn.MoveTurn;
import io.joshatron.tak.engine.turn.PlaceTurn;
import io.joshatron.tak.engine.turn.Turn;
import io.joshatron.tak.engine.turn.TurnType;

import java.util.ArrayList;

public class GameState implements Cloneable {

    private GameBoard board;

    private Player firstTurn;
    private Player currentTurn;

    private ArrayList<Turn> turns;

    private int whiteNormalPieces;
    private int whiteCapstones;
    private int blackNormalPieces;
    private int blackCapstones;

    public GameState(Player firstTurn, int boardSize) {
        this.firstTurn = firstTurn;
        this.turns = new ArrayList<>();

        this.currentTurn = this.firstTurn;

        switch(boardSize) {
            case 3:
                board = new GameBoard(boardSize);
                whiteNormalPieces = 10;
                whiteCapstones = 0;
                blackNormalPieces = 10;
                blackCapstones = 0;
                break;
            case 4:
                board = new GameBoard(boardSize);
                whiteNormalPieces = 15;
                whiteCapstones = 0;
                blackNormalPieces = 15;
                blackCapstones = 0;
                break;
            case 5:
                board = new GameBoard(boardSize);
                whiteNormalPieces = 21;
                whiteCapstones = 1;
                blackNormalPieces = 21;
                blackCapstones = 1;
                break;
            case 6:
                board = new GameBoard(boardSize);
                whiteNormalPieces = 30;
                whiteCapstones = 1;
                blackNormalPieces = 30;
                blackCapstones = 1;
                break;
            case 8:
                board = new GameBoard(boardSize);
                whiteNormalPieces = 50;
                whiteCapstones = 2;
                blackNormalPieces = 50;
                blackCapstones = 2;
                break;
        }
    }

    public boolean isLegalTurn(Turn turn) {
        // Make sure game isn't already over
        if(checkForWinner().isFinished()) {
            return false;
        }

        // Check place
        if(turn.getType() == TurnType.PLACE) {
            PlaceTurn place = (PlaceTurn)turn;
            // Check if enough pieces.
            // Ignore first 2 turns because placing other's pieces and impossible to be out
            if(turns.size() >= 2) {
                if(currentTurn == Player.WHITE) {
                    if((place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) &&
                       whiteNormalPieces < 1) {
                        return false;
                    }
                    if(place.getPieceType() == PieceType.CAPSTONE && whiteCapstones < 1) {
                        return false;
                    }
                }
                else {
                    if((place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) &&
                            blackNormalPieces < 1) {
                        return false;
                    }
                    if(place.getPieceType() == PieceType.CAPSTONE && blackCapstones < 1) {
                        return false;
                    }
                }
            }

            // Check if it is the first couple turns that only stones are placed
            if(turns.size() < 2 && place.getPieceType() != PieceType.STONE) {
                return false;
            }

            // Check the location is valid
            if(!board.onBoard(place.getLocation())) {
                return false;
            }

            // Check the location is empty
            if(board.getPosition(place.getLocation()).getPieces().size() == 0) {
                return true;
            }
            else {
                return false;
            }
        }
        // Check move
        else {
            MoveTurn move = (MoveTurn)turn;

            // No moves can be done in the first 2 turns
            if(turns.size() < 2) {
                return false;
            }

            // Check that the picked up pieces is legal
            if(move.getPickedUp() < 1 || move.getPickedUp() > board.getBoardSize()) {
                return false;
            }

            // Check that stack has enough pieces
            if(board.getPosition(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
                return false;
            }

            // Check that the player owns the stack
            if(currentTurn == Player.WHITE) {
                if(board.getPosition(move.getStartLocation()).getTopPiece().isBlack()) {
                    return false;
                }
            }
            else {
                if(board.getPosition(move.getStartLocation()).getTopPiece().isWhite()) {
                    return false;
                }
            }

            // Check that each position of move is legal
            BoardLocation currentLocation = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
            int[] toPlace = move.getPlaced();
            ArrayList<Piece> pieces = board.getPosition(currentLocation).getTopPieces(move.getPickedUp());
            for(int i = 0; i < toPlace.length; i++) {
                // Check that at least one piece was placed
                if(toPlace[i] < 1) {
                    return false;
                }

                currentLocation.move(move.getDirection());

                //Check that location is legal
                if(!board.onBoard(currentLocation)) {
                    return false;
                }

                //Check that it is okay to place there
                if(board.getPosition(currentLocation).getPieces().size() > 0) {
                    // If there is a capstone, fail
                    if(board.getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE) {
                        return false;
                    }

                    // If there is a wall and you don't have only a capstone, fail
                    if(board.getPosition(currentLocation).getTopPiece().getType() == PieceType.WALL) {
                        if(pieces.size() != 1 || pieces.get(0).getType() != PieceType.CAPSTONE) {
                            return false;
                        }
                    }
                }

                for(int j = 0; j < toPlace[i]; j++) {
                    pieces.remove(0);
                }
            }

            // Nothing found wrong with the move
            return true;
        }
    }

    public GameResult checkForWinner() {
        // Check if someone is out of pieces
        if((whiteNormalPieces == 0 && whiteCapstones == 0) ||
           (blackNormalPieces == 0 && blackCapstones == 0)) {
            int white = 0;
            int black = 0;
            for(int x = 0; x < board.getBoardSize(); x++) {
                for(int y = 0; y < board.getBoardSize(); y++) {
                    if(board.getPosition(x, y).getPieces().size() > 0 &&
                       board.getPosition(x, y).getTopPiece().getType() == PieceType.STONE) {
                        if(board.getPosition(x, y).getTopPiece().isWhite()) {
                            white++;
                        }
                        else {
                            black++;
                        }
                    }
                }
            }

            if(white > black) {
                return new GameResult(true, Player.WHITE, WinReason.OUT_OF_PIECES);
            }
            else if(black > white) {
                return new GameResult(true, Player.BLACK, WinReason.OUT_OF_PIECES);
            }
            else if(whiteCapstones > blackCapstones) {
                return new GameResult(true, Player.WHITE, WinReason.OUT_OF_PIECES);
            }
            else if(blackCapstones > whiteCapstones) {
                return new GameResult(true, Player.BLACK, WinReason.OUT_OF_PIECES);
            }
            else {
                return new GameResult(true, Player.NONE, WinReason.OUT_OF_PIECES);
            }
        }


        //Check for a full board
        boolean empty = false;
        int white = 0;
        int black = 0;
        for(int x = 0; x < getBoardSize(); x++) {
            for(int y = 0; y < getBoardSize(); y++) {
                if(board.getPosition(x,y).getPieces().size() == 0) {
                    empty = true;
                }
                else {
                    if(board.getPosition(x,y).getTopPiece().isWhite()) {
                        white++;
                    }
                    else {
                        black++;
                    }
                }
            }
        }

        if(!empty) {
            if(white > black) {
                return new GameResult(true, Player.WHITE, WinReason.BOARD_FULL);
            }
            else if(black > white) {
                return new GameResult(true, Player.BLACK, WinReason.BOARD_FULL);
            }
            else if(whiteCapstones > blackCapstones) {
                return new GameResult(true, Player.WHITE, WinReason.BOARD_FULL);
            }
            else if(blackCapstones > whiteCapstones) {
                return new GameResult(true, Player.BLACK, WinReason.BOARD_FULL);
            }
            else {
                return new GameResult(true, Player.NONE, WinReason.BOARD_FULL);
            }
        }

        //Check for each possible path
        boolean whitePath = false;
        boolean blackPath = false;

        for(int i = 0; i < board.getBoardSize(); i++) {
            if(board.getPosition(0, i).getTopPiece() != null) {
                if (isWinPath(new BoardLocation(0, i), new boolean[board.getBoardSize()][board.getBoardSize()], true, board.getPosition(0, i).getTopPiece().isWhite())) {
                    if (board.getPosition(0, i).getTopPiece().isWhite()) {
                        whitePath = true;
                    } else {
                        blackPath = true;
                    }
                }
            }
            if(board.getPosition(i, 0).getTopPiece() != null) {
                if (isWinPath(new BoardLocation(i, 0), new boolean[board.getBoardSize()][board.getBoardSize()], false, board.getPosition(i, 0).getTopPiece().isWhite())) {
                    if (board.getPosition(i, 0).getTopPiece().isWhite()) {
                        whitePath = true;
                    } else {
                        blackPath = true;
                    }
                }
            }
        }

        if(whitePath && !blackPath) {
            return new GameResult(true, Player.WHITE, WinReason.PATH);
        }
        else if(!whitePath && blackPath) {
            return new GameResult(true, Player.BLACK, WinReason.PATH);
        }
        else if(whitePath && blackPath && currentTurn == Player.BLACK) {
            return new GameResult(true, Player.WHITE, WinReason.PATH);
        }
        else if(whitePath && blackPath && currentTurn == Player.WHITE) {
            return new GameResult(true, Player.BLACK, WinReason.PATH);
        }

        return new GameResult();
    }

    private boolean isWinPath(BoardLocation current, boolean[][] checked, boolean horizontal, boolean white) {
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
                if(topPiece != null) {
                    if (!checked[current.getX()][current.getY()] && topPiece.isWhite() == white &&
                            (topPiece.getType() == PieceType.CAPSTONE || topPiece.getType() == PieceType.STONE)) {
                        checked[current.getX()][current.getY()] = true;
                        if (isWinPath(current, checked, horizontal, white)) {
                            return true;
                        }
                    }
                }
            }
            current.moveOpposite(dirs[i]);
        }

        return false;
    }

    public boolean executeTurn(Turn turn) {
        if(isLegalTurn(turn)) {
            if(turn.getType() == TurnType.PLACE) {
                PlaceTurn place = (PlaceTurn)turn;
                Player player = currentTurn;
                if(turns.size() < 2) {
                    if(player == Player.WHITE) {
                        player = Player.BLACK;
                    }
                    else {
                        player = Player.WHITE;
                    }
                }
                board.getPosition(place.getLocation()).addPiece(new Piece(player, place.getPieceType()));

                if(player == Player.WHITE) {
                    if(place.getPieceType() == PieceType.CAPSTONE) {
                        whiteCapstones--;
                    }
                    else {
                        whiteNormalPieces--;
                    }
                }
                else {
                    if(place.getPieceType() == PieceType.CAPSTONE) {
                        blackCapstones--;
                    }
                    else {
                        blackNormalPieces--;
                    }
                }

            }
            else {
                MoveTurn move = (MoveTurn)turn;
                ArrayList<Piece> pieces = board.getPosition(move.getStartLocation()).removePieces(move.getPickedUp());
                BoardLocation current = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
                for(int i = 0; i < move.getPlaced().length; i++) {
                    current.move(move.getDirection());
                    // If there is a wall, collapse it
                    if(board.getPosition(current).getPieces().size() > 0 && board.getPosition(current).getTopPiece().getType() == PieceType.WALL) {
                        board.getPosition(current).collapseTopPiece();
                        move.flatten();
                    }
                    // Place the right number of pieces in
                    for(int j = 0; j < move.getPlaced()[i]; j++) {
                        board.getPosition(current).addPiece(pieces.remove(0));
                    }
                }
            }

            turns.add(turn);
            if(currentTurn == Player.WHITE) {
                currentTurn = Player.BLACK;
            }
            else {
                currentTurn = Player.WHITE;
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void undoTurn() {
        Turn turn = turns.remove(turns.size() - 1);

        //Undo a place move
        if(turn.getType() == TurnType.PLACE) {
            PlaceTurn place = (PlaceTurn)turn;

            board.getPosition(place.getLocation()).removePieces(1);
            //white made last turn
            if(turns.size() < 2) {
                if (currentTurn == Player.WHITE) {
                    if (place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) {
                        whiteNormalPieces++;
                    } else {
                        whiteCapstones++;
                    }
                } else {
                    if (place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) {
                        blackNormalPieces++;
                    } else {
                        blackCapstones++;
                    }
                }
            }
            else {
                if (currentTurn == Player.BLACK) {
                    if (place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) {
                        whiteNormalPieces++;
                    } else {
                        whiteCapstones++;
                    }
                } else {
                    if (place.getPieceType() == PieceType.STONE || place.getPieceType() == PieceType.WALL) {
                        blackNormalPieces++;
                    } else {
                        blackCapstones++;
                    }
                }
            }
        }
        //Undo a move turn
        else {
            MoveTurn move = (MoveTurn)turn;

            BoardLocation current = new BoardLocation(move.getStartLocation().getX(), move.getStartLocation().getY());
            for(int i = 0; i < move.getPlaced().length; i++) {
                current.move(move.getDirection());
            }
            ArrayList<Piece> pickedUp = new ArrayList<>();
            for(int i = move.getPlaced().length - 1; i >= 0; i--) {
                pickedUp.addAll(0, board.getPosition(current).removePieces(move.getPlaced()[i]));
                if(i == move.getPlaced().length - 1 && move.didFlatten()) {
                    board.getPosition(current).uncollapseTopPiece();
                }

                current.moveOpposite(move.getDirection());
            }

            board.getPosition(current).addPieces(pickedUp);
        }

        if(currentTurn == Player.WHITE) {
            currentTurn = Player.BLACK;
        }
        else {
            currentTurn = Player.WHITE;
        }
    }

    public ArrayList<Turn> getPossibleTurns() {
        ArrayList<Turn> turns = new ArrayList<>();

        if(this.turns.size() < 2) {
            for(int x = 0; x < getBoardSize(); x++) {
                for(int y = 0; y < getBoardSize(); y++) {
                    if(board.getPosition(x, y).getHeight() == 0) {
                        turns.add(new PlaceTurn(x, y, PieceType.STONE));
                    }
                }
            }

        }
        else {
            //Iterate through each position to process possible moves
            for (int x = 0; x < getBoardSize(); x++) {
                for (int y = 0; y < getBoardSize(); y++) {
                    //If it is empty, add possible places
                    if (board.getPosition(x, y).getHeight() == 0) {
                        if (currentTurn == Player.WHITE) {
                            if (whiteNormalPieces > 0) {
                                turns.add(new PlaceTurn(x, y, PieceType.STONE));
                                turns.add(new PlaceTurn(x, y, PieceType.WALL));
                            }
                            if (whiteCapstones > 0) {
                                turns.add(new PlaceTurn(x, y, PieceType.CAPSTONE));
                            }
                        } else {
                            if (blackNormalPieces > 0) {
                                turns.add(new PlaceTurn(x, y, PieceType.STONE));
                                turns.add(new PlaceTurn(x, y, PieceType.WALL));
                            }
                            if (blackCapstones > 0) {
                                turns.add(new PlaceTurn(x, y, PieceType.CAPSTONE));
                            }
                        }
                    }
                    //Otherwise iterate through possible moves if player owns the stack
                    else if (board.getPosition(x, y).getTopPiece().getPlayer() == currentTurn) {
                        turns.addAll(getMoves(x, y, Direction.NORTH));
                        turns.addAll(getMoves(x, y, Direction.SOUTH));
                        turns.addAll(getMoves(x, y, Direction.EAST));
                        turns.addAll(getMoves(x, y, Direction.WEST));
                    }
                }
            }
        }

        return turns;
    }


    private ArrayList<Turn> getMoves(int x, int y, Direction dir) {
        ArrayList<Turn> turns = new ArrayList<>();

        int numPieces = Math.min(board.getPosition(x, y).getHeight(), getBoardSize());
        int distToBlock = 0;
        BoardLocation loc = new BoardLocation(x, y);
        loc.move(dir);
        while(board.onBoard(loc) &&
              (board.getPosition(loc).getHeight() == 0 ||
              board.getPosition(loc).getTopPiece().getType() == PieceType.STONE)) {
            distToBlock++;
            loc.move(dir);
        }
        boolean canFlatten = false;
        if(board.onBoard(loc) && board.getPosition(loc).getHeight() > 0 &&
           board.getPosition(loc).getTopPiece().getType() == PieceType.WALL &&
           board.getPosition(x, y).getTopPiece().getType() == PieceType.CAPSTONE) {
            canFlatten = true;
        }

        if(distToBlock > 0) {
            while (numPieces > 0) {
                turns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces, new ArrayList<Integer>(), x, y, dir, numPieces));
                numPieces--;
            }
        }

        return turns;
    }

    private ArrayList<Turn> getMovesInner(int distToBlock, boolean canFlatten, int numPieces, ArrayList<Integer> drops, int x, int y, Direction dir, int pickup) {
        ArrayList<Turn> turns = new ArrayList<>();
        //at last spot
        if(distToBlock == 0) {
            turns.add(buildMove(x, y, pickup, dir, drops, numPieces));
            if(canFlatten && numPieces > 1) {
                drops.add(numPieces - 1);
                turns.add(buildMove(x, y, pickup, dir, drops, 1));
            }
        }
        //iterate through everything else
        else {
            turns.add(buildMove(x, y, pickup, dir, drops, numPieces));
            int piecesLeft = numPieces - 1;
            while(piecesLeft > 0) {
                drops.add(piecesLeft);
                turns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces - piecesLeft, new ArrayList<Integer>(drops), x, y, dir, pickup));
                drops.remove(drops.size() - 1);
                piecesLeft--;
            }
        }

        return turns;
    }

    private MoveTurn buildMove(int x, int y, int pickup, Direction dir, ArrayList<Integer> drops, int current) {
        int[] drop = new int[drops.size() + 1];
        for(int i = 0; i < drops.size(); i++) {
            drop[i] = drops.get(i).intValue();
        }
        drop[drop.length - 1] = current;
        return new MoveTurn(x, y, pickup, dir, drop);
    }


    public GameBoard getBoard() {
        return board;
    }

    public void printBoard() {
        System.out.println("WS: " + whiteNormalPieces + " WC: " + whiteCapstones +
                           " BS: " + blackNormalPieces + " BC: " + blackCapstones);
        board.printBoard();
    }

    public int getBoardSize() {
        return board.getBoardSize();
    }

    public Player getFirstPlayer() {
        return firstTurn;
    }

    public boolean isWhiteFirst() {
        return firstTurn == Player.WHITE;
    }

    public Player getCurrentPlayer() {
        return currentTurn;
    }

    public boolean isWhiteTurn() {
        return currentTurn == Player.WHITE;
    }

    public ArrayList<Turn> getTurns() {
        return turns;
    }

    public int getWhiteNormalPiecesLeft() {
        return whiteNormalPieces;
    }

    public int getWhiteCapstonesLeft() {
        return whiteCapstones;
    }

    public int getBlackNormalPiecesLeft() {
        return blackNormalPieces;
    }

    public int getBlackCapstonesLeft() {
        return blackCapstones;
    }

    @Override
    public Object clone() {
        GameState state = new GameState(firstTurn, getBoardSize());
        for(Turn turn : turns) {
            state.executeTurn(turn);
        }

        return state;
    }
}
