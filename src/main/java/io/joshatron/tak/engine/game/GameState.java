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

    private PlayerInfo whiteInfo;
    private PlayerInfo blackInfo;

    private int piecesFilled;

    private GameResult result;

    private GameStateConfig config;

    public GameState(Player firstTurn, int boardSize) throws TakEngineException {
        this(firstTurn, boardSize, new GameStateConfig());
    }

    public GameState(Player firstTurn, int boardSize, GameStateConfig config) throws TakEngineException {
        initializeGame(firstTurn, boardSize, config);
    }

    public GameState(GameState state) throws TakEngineException {
        this(state, new GameStateConfig());
    }

    public GameState(GameState state, GameStateConfig config) throws TakEngineException {
        initializeGame(state.getFirstPlayer(), state.getBoardSize(), config);
        for(Turn turn : state.getTurns()) {
            executeTurn(turn);
        }
    }

    public GameState(JSONObject json) throws TakEngineException {
        this(json, new GameStateConfig());
    }

    public GameState(JSONObject json, GameStateConfig config) throws TakEngineException {
        importFromJson(json, config);
    }

    private void initializeGame(Player firstTurn, int boardSize, GameStateConfig config) throws TakEngineException {
        this.firstTurn = firstTurn;
        this.config = config;
        this.turns = new ArrayList<>();

        piecesFilled = 0;
        result = null;

        this.currentTurn = this.firstTurn;

        switch(boardSize) {
            case 3:
                board = new GameBoard(boardSize);
                whiteInfo = new PlayerInfo(10, 0);
                blackInfo = new PlayerInfo(10, 0);
                break;
            case 4:
                board = new GameBoard(boardSize);
                whiteInfo = new PlayerInfo(15, 0);
                blackInfo = new PlayerInfo(15, 0);
                break;
            case 5:
                board = new GameBoard(boardSize);
                whiteInfo = new PlayerInfo(21, 1);
                blackInfo = new PlayerInfo(21, 1);
                break;
            case 6:
                board = new GameBoard(boardSize);
                whiteInfo = new PlayerInfo(30, 1);
                blackInfo = new PlayerInfo(30, 1);
                break;
            case 8:
                board = new GameBoard(boardSize);
                whiteInfo = new PlayerInfo(50, 2);
                blackInfo = new PlayerInfo(50, 2);
                break;
            default:
                throw new TakEngineException(TakEngineErrorCode.INVALID_BOARD_SIZE);
        }
    }

    private void importFromJson(JSONObject json, GameStateConfig config) throws TakEngineException {
        initializeGame(Player.valueOf(json.getString("first")), json.getInt("size"), config);
        JSONArray moves = json.getJSONArray("turns");
        for(int i = 0; i < moves.length(); i++) {
            applyTurn(TurnUtils.turnFromJson(moves.getJSONObject(i)));
        }
    }

    public JSONObject exportToJson() {
        JSONObject toExport = new JSONObject();
        toExport.put("size", board.getBoardSize());
        toExport.put("whiteStones", whiteInfo.getStones());
        toExport.put("whiteCapstones", whiteInfo.getCapstones());
        toExport.put("blackStones", blackInfo.getStones());
        toExport.put("blackCapstones", blackInfo.getCapstones());
        toExport.put("first", firstTurn.name());
        toExport.put("current", currentTurn.name());

        JSONArray moves = new JSONArray();
        for(Turn turn : turns) {
            moves.put(turn.exportToJson());
        }
        toExport.put("turns", moves);

        toExport.put("board", board.exportToJson());

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
        PlayerInfo info = getInfo(getCurrentForInfo());

        // Check if enough pieces.
        if (place.getPieceType() != PieceType.CAPSTONE && info.getStones() < 1) {
            throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_STONES);
        }
        if (place.getPieceType() == PieceType.CAPSTONE && info.getCapstones() < 1) {
            throw new TakEngineException(TakEngineErrorCode.NOT_ENOUGH_CAPSTONES);
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
        if (turns.size() < 2) {
            throw new TakEngineException(TakEngineErrorCode.MOVE_IN_FIRST_TURN);
        }

        // Check that the picked up pieces is legal
        if (move.getPickedUp() < 1 || move.getPickedUp() > board.getBoardSize()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that stack has enough pieces
        if (board.getPosition(move.getStartLocation()).getPieces().size() < move.getPickedUp()) {
            throw new TakEngineException(TakEngineErrorCode.INVALID_PICKUP_AMOUNT);
        }

        // Check that the player owns the stack
        if (board.getPosition(move.getStartLocation()).getTopPiece().getPlayer() != currentTurn) {
            throw new TakEngineException(TakEngineErrorCode.DO_NOT_OWN_STACK);
        }

        validateMovePlacements(move);
    }

    private void validateMovePlacements(MoveTurn move) throws TakEngineException {
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

        if(result != null) {
            return result;
        }

        PlayerInfo info = getInfo(getCurrentForInfo().opposite());

        // Check if someone is out of pieces
        if(info.getStones() == 0 && info.getCapstones() == 0) {
            result = getWinnerFromPoints(WinReason.OUT_OF_PIECES);
            return result;
        }

        //Check for a full board
        if(piecesFilled == board.getBoardSize() * board.getBoardSize()) {
            result = getWinnerFromPoints(WinReason.BOARD_FULL);
            return result;
        }

        //Check for each possible path
        for(int i = 0; i < board.getBoardSize(); i++) {
            if(board.getPosition(0, i).getTopPiece() != null &&
               board.getPosition(0, i).getStackOwner() == currentTurn.opposite() &&
               board.getPosition(0, i).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(new BoardLocation(0, i), new boolean[board.getBoardSize()][board.getBoardSize()],
                       true, board.getPosition(0, i).getTopPiece().isWhite())) {
                result = new GameResult(true, currentTurn.opposite(), WinReason.PATH, getScore(currentTurn.opposite()));
                return result;
            }
            if(board.getPosition(i, 0).getTopPiece() != null &&
               board.getPosition(i, 0).getStackOwner() == currentTurn.opposite() &&
               board.getPosition(i, 0).getTopPiece().getType() != PieceType.WALL &&
               isWinPath(new BoardLocation(i, 0), new boolean[board.getBoardSize()][board.getBoardSize()],
                       false, board.getPosition(i, 0).getTopPiece().isWhite())) {
                result = new GameResult(true, currentTurn.opposite(), WinReason.PATH, getScore(currentTurn.opposite()));
                return result;
            }
        }

        return new GameResult();
    }

    private GameResult getWinnerFromPoints(WinReason reason) {
        if(whiteInfo.getPoints() > blackInfo.getPoints()) {
            return new GameResult(true, Player.WHITE, reason, getScore(Player.WHITE));
        }
        else if(blackInfo.getPoints() > whiteInfo.getPoints()) {
            return new GameResult(true, Player.BLACK, reason, getScore(Player.BLACK));
        }
        else if(whiteInfo.getCapstones() > blackInfo.getCapstones()) {
            return new GameResult(true, Player.WHITE, reason, getScore(Player.WHITE));
        }
        else if(blackInfo.getCapstones() > whiteInfo.getCapstones()) {
            return new GameResult(true, Player.BLACK, reason, getScore(Player.BLACK));
        }
        else {
            return new GameResult(true, Player.NONE, reason, 0);
        }
    }

    private int getScore(Player player) {
        return board.getBoardSize() * board.getBoardSize() + getInfo(player).getScore();
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
        if(!config.isFast()) {
            validateTurn(turn);
        }

        applyTurn(turn);
    }

    private void applyTurn(Turn turn) throws TakEngineException {
        if(turn.getType() == TurnType.PLACE) {
            applyPlace((PlaceTurn) turn);
        }
        else if(turn.getType() == TurnType.MOVE) {
            applyMove((MoveTurn) turn);
        }

        turns.add(turn);
        currentTurn = currentTurn.opposite();
    }

    private void applyPlace(PlaceTurn place) {
        PlayerInfo info = getInfo(getCurrentForInfo());

        Player player = currentTurn;
        if (turns.size() < 2) {
            player = player.opposite();
        }
        board.getPosition(place.getLocation()).addPiece(new Piece(player, place.getPieceType()));
        piecesFilled++;

        if (place.getPieceType() == PieceType.CAPSTONE) {
            info.decrementCapstones();
        } else if (place.getPieceType() == PieceType.STONE) {
            info.decrementStones();
            info.incrementPoints();
        } else {
            info.decrementStones();
        }
    }

    private void applyMove(MoveTurn move) throws TakEngineException {
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

    public void undoTurn() throws TakEngineException {
        Turn turn = turns.remove(turns.size() - 1);

        //Undo a place move
        if(turn.getType() == TurnType.PLACE) {
            undoPlace((PlaceTurn) turn);
        }
        else if(turn.getType() == TurnType.MOVE) {
            undoMove((MoveTurn) turn);
        }

        currentTurn = currentTurn.opposite();
        result = null;
    }

    private void undoPlace(PlaceTurn place) throws TakEngineException {
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

    private void undoMove(MoveTurn move) throws TakEngineException {
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

    public List<Turn> getPossibleTurns() throws TakEngineException {
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
                        PlayerInfo info = getInfo(getCurrentForInfo());

                        if (info.getStones() > 0) {
                            possibleTurns.add(new PlaceTurn(x, y, PieceType.STONE));
                            possibleTurns.add(new PlaceTurn(x, y, PieceType.WALL));
                        }
                        if (info.getCapstones() > 0) {
                            possibleTurns.add(new PlaceTurn(x, y, PieceType.CAPSTONE));
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

        if(config.isNarrowPossible()) {
            if(canWin(currentTurn)) {
                ArrayList<Turn> toRemove = new ArrayList<>();
                for(Turn turn : possibleTurns) {
                    applyTurn(turn);
                    if(!checkForWinner().isFinished()) {
                        toRemove.add(turn);
                    }
                    undoTurn();
                }
                possibleTurns.removeAll(toRemove);
            }
            else if(inTak()) {
                ArrayList<Turn> toRemove = new ArrayList<>();
                for(Turn turn : possibleTurns) {
                    applyTurn(turn);
                    if(canWin(currentTurn)) {
                        toRemove.add(turn);
                    }
                    undoTurn();
                }
                possibleTurns.removeAll(toRemove);
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
                possibleTurns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces, new ArrayList<>(), x, y, dir, numPieces));
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
                possibleTurns.addAll(getMovesInner(distToBlock - 1, canFlatten, numPieces - piecesLeft, new ArrayList<>(drops), x, y, dir, pickup));
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

    public boolean inTak() throws TakEngineException {
        return canWin(currentTurn.opposite());
    }

    public boolean canWin(Player player) throws TakEngineException {
        if(checkForWinner().isFinished()) {
            return false;
        }
        if(turns.size() < 3) {
            return false;
        }

        Player current = currentTurn;
        currentTurn = player;
        boolean oldNarrow = config.isNarrowPossible();
        config.setNarrowPossible(false);
        List<Turn> possible = getPossibleTurns();
        config.setNarrowPossible(oldNarrow);
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
        currentTurn = current;

        return finished;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void printBoard() {
        System.out.println("WS: " + whiteInfo.getStones() + " WC: " + whiteInfo.getCapstones() +
                           " BS: " + blackInfo.getStones() + " BC: " + blackInfo.getCapstones());
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
        return whiteInfo.getStones();
    }

    public int getWhiteCapstonesLeft() {
        return whiteInfo.getCapstones();
    }

    public int getBlackNormalPiecesLeft() {
        return blackInfo.getStones();
    }

    public int getBlackCapstonesLeft() {
        return blackInfo.getCapstones();
    }

    public int getWhitePoints() {
        return whiteInfo.getPoints();
    }

    public int getBlackPoints() {
        return blackInfo.getPoints();
    }

    public int getBoardLocationsFilled() {
        return piecesFilled;
    }

    private Player getCurrentForInfo() {
        if(turns.isEmpty()) {
            return firstTurn.opposite();
        }
        else if(turns.size() == 1) {
            return firstTurn;
        }
        else if(turns.size() % 2 == 0) {
            return firstTurn;
        }
        else {
            return firstTurn.opposite();
        }
    }

    private PlayerInfo getInfo(Player player) {
        if(player == Player.WHITE) {
            return whiteInfo;
        }
        else {
            return blackInfo;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof GameState) {
            GameState other = (GameState) o;
            for(int i = 0; i < turns.size(); i++) {
                if(!turns.get(i).equals(other.getTurns().get(i))) {
                    return false;
                }
            }

            return firstTurn == other.getFirstPlayer() && currentTurn == other.getCurrentPlayer() &&
                   whiteInfo.equals(other.whiteInfo) && blackInfo.equals(other.blackInfo);
        }

        return false;
    }
}
