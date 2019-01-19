package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.*;
import io.joshatron.tak.engine.exception.TakEngineErrorCode;
import io.joshatron.tak.engine.exception.TakEngineException;
import io.joshatron.tak.engine.turn.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private GameBoard board;

    private Player firstTurn;
    private Player currentTurn;

    private ArrayList<Turn> turns;

    private int whiteNormalPieces;
    private int whiteCapstones;
    private int blackNormalPieces;
    private int blackCapstones;

    private boolean fast;

    public GameState(Player firstTurn, int boardSize) throws TakEngineException {
        initializeGame(firstTurn, boardSize, false);
    }

    public GameState(Player firstTurn, int boardSize, boolean fast) throws TakEngineException {
        initializeGame(firstTurn, boardSize, fast);
    }

    public GameState(GameState state) throws TakEngineException {
        this(state, false);
    }

    public GameState(GameState state, boolean fast) throws TakEngineException {
        initializeGame(state.getFirstPlayer(), state.getBoardSize(), fast);
        for(Turn turn : state.getTurns()) {
            executeTurn(turn);
        }
    }

    public GameState(JSONObject json) throws TakEngineException {
        this(json, false);
    }

    public GameState(JSONObject json, boolean fast) throws TakEngineException {
        importFromJson(json, fast);
    }

    private void initializeGame(Player firstTurn, int boardSize, boolean fast) throws TakEngineException {
        this.firstTurn = firstTurn;
        this.fast = fast;
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
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    private void importFromJson(JSONObject json, boolean fast) throws TakEngineException {
        initializeGame(Player.valueOf(json.getString("first")), json.getInt("size"), fast);
        JSONArray moves = json.getJSONArray("turns");
        for(int i = 0; i < moves.length(); i++) {
            applyTurn(TurnUtils.turnFromString(moves.getString(i)));
        }
    }

    public JSONObject exportToJson() {
        JSONObject toExport = new JSONObject();
        toExport.put("size", board.getBoardSize());
        toExport.put("whiteStones", whiteNormalPieces);
        toExport.put("whiteCapstones", whiteCapstones);
        toExport.put("blackStones", blackNormalPieces);
        toExport.put("blackCapstones", blackCapstones);
        toExport.put("first", firstTurn.name());
        toExport.put("current", currentTurn.name());

        JSONArray moves = new JSONArray();
        for(Turn turn : turns) {
            moves.put(turn.toString());
        }
        toExport.put("turns", moves);

        JSONArray fullBoard = new JSONArray();
        for(int y = 0; y < board.getBoardSize(); y++) {
            JSONArray row = new JSONArray();
            for(int x = 0; x < board.getBoardSize(); x++) {
                row.put(board.getPosition(x, y).toString());
            }
            fullBoard.put(row);
        }
        toExport.put("board", fullBoard);

        return toExport;
    }

    public boolean isLegalTurn(Turn turn) {
        try {
            validateTurn(turn);
            return true;
        } catch (TakEngineException e) {
            return false;
        }
    }

    private void validateTurn(Turn turn) throws TakEngineException {
        // Make sure game isn't already over
        if (checkForWinner().isFinished()) {
            throw new TakEngineException(TakEngineErrorCode.GAME_FINISHED);
        }

        //Check based on turn type
        if (turn.getType() == TurnType.PLACE) {
            validatePlace((PlaceTurn) turn);
        } else if (turn.getType() == TurnType.MOVE) {
            validateMove((MoveTurn) turn);
        }
    }

    private void validatePlace(PlaceTurn place) throws TakEngineException {
        // Check if enough pieces.
        if (currentTurn == Player.WHITE) {
            if (place.getPieceType() != PieceType.CAPSTONE && whiteNormalPieces < 1) {
                throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
            }
            if (place.getPieceType() == PieceType.CAPSTONE && whiteCapstones < 1) {
                throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
            }
        } else {
            if (place.getPieceType() != PieceType.CAPSTONE && blackNormalPieces < 1) {
                throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
            }
            if (place.getPieceType() == PieceType.CAPSTONE && blackCapstones < 1) {
                throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
            }
        }

        // Check if it is the first couple turns that only stones are placed
        if (turns.size() < 2 && place.getPieceType() != PieceType.STONE) {
            throw new TakEngineException(TakEngineErrorCode.ILLEGAL_FIRST_MOVE_TYPE);
        }

        // Check the location is valid
        if (!board.onBoard(place.getLocation())) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_LOCATION);
        }

        // Check the location is empty
        if(!board.getPosition(place.getLocation()).getPieces().isEmpty()) {
            throw new TakEngineException(TakEngineErrorCode.STACK_NOT_EMPTY);
        }
    }

    private void validateMove(MoveTurn move) throws TakEngineException {
        // No moves can be done in the first 2 turns
        if(turns.size() < 2) {
            throw new TakEngineException(TakEngineErrorCode.MOVE_IN_FIRST_TURN);
        }

        // Check that the picked up pieces is legal
        if(move.getPickedUp() < 1 || move.getPickedUp() > board.getBoardSize()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that stack has enough pieces
        if(board.getPosition(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that the player owns the stack
        if(currentTurn == Player.WHITE) {
            if(board.getPosition(move.getStartLocation()).getTopPiece().isBlack()) {
                throw new TakEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
            }
        }
        else {
            if(board.getPosition(move.getStartLocation()).getTopPiece().isWhite()) {
                throw new TakEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
            }
        }

        // Check that each position of move is legal
        BoardLocation currentLocation = new BoardLocation(move.getStartLocation());
        boolean topCapstone = board.getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE;
        int piecesLeft = move.getPickedUp();
        for(int i = 0; i < move.getPlaced().length; i++) {
            // Check that at least one piece was placed
            if(move.getPlaced()[i] < 1) {
                throw new TakEngineException(TakEngineErrorCode.INVALID_PLACE_AMOUNT);
            }

            currentLocation.move(move.getDirection());

            //Check that location is legal
            if(!board.onBoard(currentLocation)) {
                throw new TakEngineException(TakEngineErrorCode.INVALID_LOCATION);
            }

            //Check that it is okay to place there
            if(!board.getPosition(currentLocation).getPieces().isEmpty()) {
                // If there is a capstone, fail
                if(board.getPosition(currentLocation).getTopPiece().getType() == PieceType.CAPSTONE) {
                    throw new TakEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }

                // If there is a wall and you don't have only a capstone, fail
                if(board.getPosition(currentLocation).getTopPiece().getType() == PieceType.WALL &&
                   (piecesLeft != 1 || !topCapstone)) {
                    throw new TakEngineException(TakEngineErrorCode.BLOCKED_FROM_PLACING);
                }
            }

            piecesLeft -= move.getPlaced()[i];
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
                    if(!board.getPosition(x, y).getPieces().isEmpty() &&
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
                if(board.getPosition(x,y).getPieces().isEmpty()) {
                    empty = true;
                    break;
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
            if(board.getPosition(0, i).getTopPiece() != null &&
               isWinPath(new BoardLocation(0, i), new boolean[board.getBoardSize()][board.getBoardSize()], true, board.getPosition(0, i).getTopPiece().isWhite())) {
                if (board.getPosition(0, i).getTopPiece().isWhite()) {
                    whitePath = true;
                } else {
                    blackPath = true;
                }
            }
            if(board.getPosition(i, 0).getTopPiece() != null &&
               isWinPath(new BoardLocation(i, 0), new boolean[board.getBoardSize()][board.getBoardSize()], false, board.getPosition(i, 0).getTopPiece().isWhite())) {
                    if (board.getPosition(i, 0).getTopPiece().isWhite()) {
                        whitePath = true;
                    } else {
                        blackPath = true;
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
                if(topPiece != null && !checked[current.getX()][current.getY()] && topPiece.isWhite() == white &&
                   (topPiece.getType() == PieceType.CAPSTONE || topPiece.getType() == PieceType.STONE)) {
                    checked[current.getX()][current.getY()] = true;
                    if (isWinPath(current, checked, horizontal, white)) {
                        return true;
                    }
                }
            }
            current.moveOpposite(dirs[i]);
        }

        return false;
    }

    public void executeTurn(Turn turn) throws TakEngineException {
        if(!fast) {
            validateTurn(turn);
        }

        applyTurn(turn);
    }

    private void applyTurn(Turn turn) {
        if(turn.getType() == TurnType.PLACE) {
            PlaceTurn place = (PlaceTurn)turn;
            Player player = currentTurn;
            if(turns.size() < 2) {
                player = player.opposite();
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
                if(!board.getPosition(current).getPieces().isEmpty() && board.getPosition(current).getTopPiece().getType() == PieceType.WALL) {
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
        currentTurn = currentTurn.opposite();
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

        currentTurn = currentTurn.opposite();
    }

    public List<Turn> getPossibleTurns() {
        ArrayList<Turn> possibleTurns = new ArrayList<>();

        if(this.turns.size() < 2) {
            for(int x = 0; x < getBoardSize(); x++) {
                for(int y = 0; y < getBoardSize(); y++) {
                    if(board.getPosition(x, y).getHeight() == 0) {
                        possibleTurns.add(new PlaceTurn(x, y, PieceType.STONE));
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
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.STONE));
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.WALL));
                            }
                            if (whiteCapstones > 0) {
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.CAPSTONE));
                            }
                        } else {
                            if (blackNormalPieces > 0) {
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.STONE));
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.WALL));
                            }
                            if (blackCapstones > 0) {
                                possibleTurns.add(new PlaceTurn(x, y, PieceType.CAPSTONE));
                            }
                        }
                    }
                    //Otherwise iterate through possible moves if player owns the stack
                    else if (board.getPosition(x, y).getTopPiece().getPlayer() == currentTurn) {
                        possibleTurns.addAll(getMoves(x, y, Direction.NORTH));
                        possibleTurns.addAll(getMoves(x, y, Direction.SOUTH));
                        possibleTurns.addAll(getMoves(x, y, Direction.EAST));
                        possibleTurns.addAll(getMoves(x, y, Direction.WEST));
                    }
                }
            }
        }

        return possibleTurns;
    }


    private ArrayList<Turn> getMoves(int x, int y, Direction dir) {
        ArrayList<Turn> possibleTurns = new ArrayList<>();

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
                possibleTurns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces, new ArrayList<Integer>(), x, y, dir, numPieces));
                numPieces--;
            }
        }

        return possibleTurns;
    }

    private ArrayList<Turn> getMovesInner(int distToBlock, boolean canFlatten, int numPieces, ArrayList<Integer> drops, int x, int y, Direction dir, int pickup) {
        ArrayList<Turn> possibleTurns = new ArrayList<>();
        //at last spot
        if(distToBlock == 0) {
            possibleTurns.add(buildMove(x, y, pickup, dir, drops, numPieces));
            if(canFlatten && numPieces > 1) {
                drops.add(numPieces - 1);
                possibleTurns.add(buildMove(x, y, pickup, dir, drops, 1));
            }
        }
        //iterate through everything else
        else {
            possibleTurns.add(buildMove(x, y, pickup, dir, drops, numPieces));
            int piecesLeft = numPieces - 1;
            while(piecesLeft > 0) {
                drops.add(piecesLeft);
                possibleTurns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces - piecesLeft, new ArrayList<Integer>(drops), x, y, dir, pickup));
                drops.remove(drops.size() - 1);
                piecesLeft--;
            }
        }

        return possibleTurns;
    }

    private MoveTurn buildMove(int x, int y, int pickup, Direction dir, ArrayList<Integer> drops, int current) {
        int[] drop = new int[drops.size() + 1];
        for(int i = 0; i < drops.size(); i++) {
            drop[i] = drops.get(i);
        }
        drop[drop.length - 1] = current;
        return new MoveTurn(x, y, pickup, dir, drop);
    }

    public boolean inTak() {
        if(checkForWinner().isFinished()) {
            return false;
        }

        currentTurn = currentTurn.opposite();
        List<Turn> possible = getPossibleTurns();
        boolean finished = false;
        for(Turn turn : possible) {
            applyTurn(turn);
            if(checkForWinner().isFinished()) {
                finished = true;
                undoTurn();
                break;
            }
            undoTurn();
        }
        currentTurn = currentTurn.opposite();

        return finished;
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

    public List<Turn> getTurns() {
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
}
