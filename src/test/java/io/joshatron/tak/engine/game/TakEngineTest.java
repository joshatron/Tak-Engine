package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.dtos.GameStatus;
import io.joshatron.bgt.engine.dtos.Status;
import io.joshatron.bgt.engine.dtos.Turn;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;
import io.joshatron.tak.engine.board.Piece;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.TakMoveTurn;
import io.joshatron.tak.engine.turn.TakPlaceTurn;
import io.joshatron.tak.engine.turn.TakTurn;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TakEngineTest {
    //These tests are set up to be closer to black box testing.
    //This is done to make sure no rules can be broken instead of focusing on line coverage.

    //Initialize state and get first 2 moves out of the way
    private TakState initializeState(int size) throws BoardGameEngineException {
        TakEngine engine = new TakEngine();
        TakState state = new TakState(Player.WHITE, size);
        TakPlaceTurn turn = new TakPlaceTurn(new BoardLocation(0, 0), PieceType.STONE);
        engine.executeTurn(state, turn);
        turn = new TakPlaceTurn(new BoardLocation(1, 0), PieceType.STONE);
        engine.executeTurn(state, turn);

        return state;
    }

    //Tests placing each type of piece
    @Test
    public void isLegalTurnPlaceNormal() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(8);

            //Test stone placement for each color
            TakPlaceTurn turn = new TakPlaceTurn(1, 1, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(2, 1, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);

            //Test wall placement for each color
            turn = new TakPlaceTurn(2, 2, PieceType.WALL);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(3, 2, PieceType.WALL);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);

            //Test capstone placement for each color
            turn = new TakPlaceTurn(3, 3, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(4, 3, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Test that when a type of piece is out it can't play
    @Test
    public void isLegalTurnPlaceOutOfPieces() {

        try {
            //Capstones
            TakEngine engine = new TakEngine();
            TakState state = initializeState(8);
            //Place legal
            TakPlaceTurn turn = new TakPlaceTurn(2,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(3,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(4,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(5,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            //Place illegal capstone white
            turn = new TakPlaceTurn(6,0, PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            //Place legal stone white
            turn = new TakPlaceTurn(6,0, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            //Place illegal capstone black
            turn = new TakPlaceTurn(7,0, PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));

            //Stones
            state = initializeState(8);
            //Fill up board to get to no stones
            for(int i = 0; i < 2; i++) {
                for (int y = 1; y < 7; y++) {
                    turn = new TakPlaceTurn(0, y, PieceType.STONE);
                    engine.executeTurn(state, turn);
                    for (int x = 1; x < 8 - i; x++) {
                        turn = new TakPlaceTurn(x, y, PieceType.STONE);
                        engine.executeTurn(state, turn);
                        TakMoveTurn move = new TakMoveTurn(x - 1, y, x, Direction.EAST, new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }
            for(int i = 0; i < 4; i++) {
                turn = new TakPlaceTurn(i * 2,7, PieceType.STONE);
                engine.executeTurn(state, turn);
                turn = new TakPlaceTurn(i * 2 + 1,7, PieceType.STONE);
                engine.executeTurn(state, turn);
            }
            //Illegal white move, out of pieces
            turn = new TakPlaceTurn(0, 1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(0, 1, PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            //Legal capstone placement to make black turn
            turn = new TakPlaceTurn(0, 1, PieceType.CAPSTONE);
            engine.executeTurn(state, turn);
            //Illegal black move
            turn = new TakPlaceTurn(0, 2, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(0, 2, PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that you can't place a piece off the board
    @Test
    public void isLegalTurnPlaceOffBoard() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.BLACK, 3);
            //Black
            TakPlaceTurn turn = new TakPlaceTurn(new BoardLocation(-1,-1), PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(3,1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(3,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,1, PieceType.STONE);
            engine.executeTurn(state, turn);
            //White
            turn = new TakPlaceTurn(-1,-1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(3,1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(3,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,2, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that you can't place pieces on other ones
    @Test
    public void isLegalTurnPlaceOnOtherPieces() {
        try {
            //Initialize with every type of piece
            TakEngine engine = new TakEngine();
            TakState state = initializeState(8);
            TakPlaceTurn turn = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(1,2,PieceType.WALL);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(2,2,PieceType.WALL);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(1,3,PieceType.CAPSTONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(2,3,PieceType.CAPSTONE);
            engine.executeTurn(state, turn);

            //Test white placing
            for(int x = 1; x < 3; x++) {
                for(int y = 1; y < 4; y++) {
                    turn = new TakPlaceTurn(x,y, PieceType.STONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(x,y, PieceType.WALL);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(x,y, PieceType.CAPSTONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                }
            }

            turn = new TakPlaceTurn(5,5, PieceType.STONE);
            engine.executeTurn(state, turn);

            //Test black placing
            for(int x = 1; x < 3; x++) {
                for(int y = 1; y < 4; y++) {
                    turn = new TakPlaceTurn(x,y, PieceType.STONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(x,y, PieceType.WALL);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(x,y, PieceType.CAPSTONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                }
            }
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Makes sure you can't place anything besides stones for the first 2 turns
    @Test
    public void isLegalTurnPlaceBadFirstMoves() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 5);
            //white illegal turns
            TakPlaceTurn turn = new TakPlaceTurn(0,0,PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(0,0,PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, turn);
            //black illegal turns
            turn = new TakPlaceTurn(1,1,PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,1,PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, turn);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that some legal moves are legal
    @Test
    public void isLegalTurnMoveNormal() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(0,2,PieceType.WALL);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,2,Direction.SOUTH,new int[]{1,1});
            Assert.assertTrue(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to make a move off board
    @Test
    public void isLegalTurnMoveOffBoard() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakPlaceTurn place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(0,0,1,Direction.EAST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,0,2,Direction.NORTH,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(1,0,2,Direction.WEST,new int[]{1,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to grab a pile you don't own
    @Test
    public void isLegalTurnMoveIllegalPickup() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            //Entire stack is owned by other player
            TakMoveTurn move = new TakMoveTurn(0,0,1,Direction.EAST,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            //Only top of stack is owned by other player
            move = new TakMoveTurn(0,0,2,Direction.EAST,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to cover a wall or capstone illegally
    @Test
    public void isLegalTurnMoveIllegalCover() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.WALL);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            place = new TakPlaceTurn(1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,2,Direction.SOUTH,new int[]{1,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(0,0,1,Direction.EAST,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to pick up more than the max height
    @Test
    public void isLegalTurnMoveTooManyPickup() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(3);

            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,1,3,Direction.EAST,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,1,4,Direction.EAST,new int[]{4});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to not leave at least 1 piece in each spot in the path
    @Test
    public void isLegalTurnMoveEmptySpots() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.SOUTH,new int[]{0,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(1,1,PieceType.STONE);
            move = new TakMoveTurn(0,0,2,Direction.EAST,new int[]{1,0,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to do a move in the first 2 turns
    @Test
    public void isLegalTurnMoveBadFirstMoves() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(0,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a straight horizontal win path
    @Test
    public void checkForWinnerStraightHorizontal() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a straight vertical win path
    @Test
    public void checkForWinnerStraightVertical() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.BLACK,3);

            TakPlaceTurn place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.BLACK,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a non-straight horizontal win path
    @Test
    public void checkForWinnerCurvyHorizontal() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,6);

            TakMoveTurn moveDown = new TakMoveTurn(5,0,1,Direction.SOUTH,new int[]{1});
            TakMoveTurn moveUp = new TakMoveTurn(5,1,1,Direction.NORTH,new int[]{1});

            TakPlaceTurn place = new TakPlaceTurn(5,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(3,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(0,3,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(0,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(1,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(2,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(3,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(5,4,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 51),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a non-straight vertical win path
    @Test
    public void checkForWinnerCurvyVertical() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.BLACK,6);

            TakMoveTurn moveDown = new TakMoveTurn(5,0,1,Direction.SOUTH,new int[]{1});
            TakMoveTurn moveUp = new TakMoveTurn(5,1,1,Direction.NORTH,new int[]{1});

            TakPlaceTurn place = new TakPlaceTurn(5,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(3,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(0,3,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(0,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(1,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(1,5,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.BLACK,WinReason.PATH, 54),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that walls can't be in win paths
    @Test
    public void checkForWinnerWallInPath() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.WALL);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new GameStatus(),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that capstones can be in win paths
    @Test
    public void checkForWinnerCapstoneInPath() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(4,0,PieceType.CAPSTONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 42),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that the top spot in a stack is the one counted
    @Test
    public void checkForWinnerStacks() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, move);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that diagonals don't count toward win paths
    @Test
    public void checkForWinnerDiagonals() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.BLACK,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertEquals(new GameStatus(),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests the win condition that happens with a full board
    @Test
    public void checkForWinnerFullBoard() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(3);

            TakPlaceTurn place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            Assert.assertEquals(new GameStatus(), state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.BOARD_FULL, 14), state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests the win condition that happens when a player runs out of pieces
    @Test
    public void checkForWinnerOutOfPieces() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            for(int i = 0; i < 2; i++) {
                for(int y = 1; y < 5; y++) {
                    place = new TakPlaceTurn(0,y,PieceType.STONE);
                    engine.executeTurn(state, place);
                    for(int x = 1; x < 5 - i; x++) {
                        place = new TakPlaceTurn(x,y,PieceType.STONE);
                        engine.executeTurn(state, place);
                        TakMoveTurn move = new TakMoveTurn(x-1,y,x,Direction.EAST,new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }

            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.CAPSTONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.BLACK,WinReason.OUT_OF_PIECES, 26),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Makes sure the right player wins when both players get a road in the final move
    @Test
    public void checkForWinnerDoubleRoad() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(2,2,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,2,2,Direction.NORTH,new int[]{1,1});
            Assert.assertEquals(new GameStatus(), state.getStatus());
            engine.executeTurn(state, move);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16), state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that checking for winner doesn't change the game state
    @Test
    public void checkForWinnerDoesntChangeBoard() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(3);

            TakPlaceTurn place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            TakState toCheck = new TakState(state);
            Assert.assertEquals(new GameStatus(), state.getStatus());
            Assert.assertEquals(toCheck, state);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.BOARD_FULL, 14), state.getStatus());
            Assert.assertEquals(toCheck, state);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that places end up with the right state and undo correctly
    @Test
    public void undoTurnUndoPlace() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,2,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,3,PieceType.WALL);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,3,PieceType.WALL);
            engine.executeTurn(state, place);

            //Check final state
            Assert.assertTrue(state.getBoard().getPosition(0,0).getTopPiece().isBlack());
            Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(0,0).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(1,0).getTopPiece().isWhite());
            Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(1,0).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(0,1).getTopPiece().isWhite());
            Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(0,1).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(1,1).getTopPiece().isBlack());
            Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(1,1).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(0,2).getTopPiece().isWhite());
            Assert.assertEquals(PieceType.CAPSTONE, state.getBoard().getPosition(0,2).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(1,2).getTopPiece().isBlack());
            Assert.assertEquals(PieceType.CAPSTONE, state.getBoard().getPosition(1,2).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(0,3).getTopPiece().isWhite());
            Assert.assertEquals(PieceType.WALL, state.getBoard().getPosition(0,3).getTopPiece().getType());
            Assert.assertTrue(state.getBoard().getPosition(1,3).getTopPiece().isBlack());
            Assert.assertEquals(PieceType.WALL, state.getBoard().getPosition(1,3).getTopPiece().getType());

            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(1,3).getHeight());
            Assert.assertFalse(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(0,3).getHeight());
            Assert.assertTrue(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(1,2).getHeight());
            Assert.assertFalse(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(0,2).getHeight());
            Assert.assertTrue(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(1,1).getHeight());
            Assert.assertFalse(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(0,1).getHeight());
            Assert.assertTrue(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
            Assert.assertFalse(state.getCurrent() == Player.WHITE);
            engine.undoTurn(state);
            Assert.assertEquals(0,state.getBoard().getPosition(0,0).getHeight());
            Assert.assertTrue(state.getCurrent() == Player.WHITE);
            Assert.assertEquals(21,state.getWhiteStones());
            Assert.assertEquals(1,state.getWhiteCapstones());
            Assert.assertEquals(21,state.getBlackStones());
            Assert.assertEquals(1,state.getBlackCapstones());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that moves end up with the right state and undo correctly
    @Test
    public void undoTurnUndoMove() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);

            TakPlaceTurn place = new TakPlaceTurn(1,1,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(1,1,1,Direction.EAST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(2,1,2,Direction.SOUTH,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(2,3,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(2,2,3,Direction.SOUTH,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(2,1,PieceType.WALL);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(2,3,3,Direction.NORTH,new int[]{2,1});
            engine.executeTurn(state, move);

            //Verify final state
            Assert.assertTrue(state.getBoard().getPosition(2,3).getTopPiece().isBlack());
            Assert.assertEquals(1, state.getBoard().getPosition(2,3).getHeight());
            Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(2,3).getTopPiece().getType());
            List<Piece> pieces = state.getBoard().getPosition(2,2).getPieces();
            Assert.assertEquals(2, pieces.size());
            Assert.assertTrue(pieces.get(0).isBlack());
            Assert.assertTrue(pieces.get(1).isBlack());
            Assert.assertEquals(PieceType.STONE, pieces.get(0).getType());
            Assert.assertEquals(PieceType.STONE, pieces.get(1).getType());
            pieces = state.getBoard().getPosition(2,1).getPieces();
            Assert.assertEquals(2, pieces.size());
            Assert.assertTrue(pieces.get(0).isBlack());
            Assert.assertTrue(pieces.get(1).isWhite());
            Assert.assertEquals(PieceType.STONE, pieces.get(0).getType());
            Assert.assertEquals(PieceType.CAPSTONE, pieces.get(1).getType());
            Assert.assertFalse(state.getCurrent() == Player.WHITE);

            //Test undoing last move
            engine.undoTurn(state);
            Assert.assertTrue(state.getBoard().getPosition(2,1).getTopPiece().isBlack());
            Assert.assertEquals(1, state.getBoard().getPosition(2,1).getHeight());
            Assert.assertEquals(PieceType.WALL, state.getBoard().getPosition(2,1).getTopPiece().getType());
            pieces = state.getBoard().getPosition(2,3).getPieces();
            Assert.assertEquals(4, pieces.size());
            Assert.assertTrue(pieces.get(0).isBlack());
            Assert.assertTrue(pieces.get(1).isBlack());
            Assert.assertTrue(pieces.get(2).isBlack());
            Assert.assertTrue(pieces.get(3).isWhite());
            Assert.assertEquals(PieceType.STONE, pieces.get(0).getType());
            Assert.assertEquals(PieceType.STONE, pieces.get(1).getType());
            Assert.assertEquals(PieceType.STONE, pieces.get(2).getType());
            Assert.assertEquals(PieceType.CAPSTONE, pieces.get(3).getType());
            Assert.assertTrue(state.getCurrent() == Player.WHITE);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests undo move on first 2 moves
    @Test
    public void undoTurnFirstTurns() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(3);
            engine.undoTurn(state);
            Assert.assertTrue(state.getBoard().getPosition(0,0).getTopPiece().isBlack());
            Assert.assertEquals(1, state.getBoard().getPosition(0,0).getHeight());
            Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
            Assert.assertEquals(10,state.getWhiteStones());
            Assert.assertEquals(0,state.getWhiteCapstones());
            Assert.assertEquals(9,state.getBlackStones());
            Assert.assertEquals(0,state.getBlackCapstones());
            engine.undoTurn(state);
            Assert.assertEquals(0, state.getBoard().getPosition(0,0).getHeight());
            Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
            Assert.assertEquals(10,state.getWhiteStones());
            Assert.assertEquals(0,state.getWhiteCapstones());
            Assert.assertEquals(10,state.getBlackStones());
            Assert.assertEquals(0,state.getBlackCapstones());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests undoing and redoing winning move
    @Test
    public void undoTurnUndoAndRedoWinningMove() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16),state.getStatus());
            engine.undoTurn(state);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests undoing all moves
    @Test
    public void undoTurnUndoAllTurns() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(2,0,PieceType.STONE);
            Assert.assertEquals(new GameStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,Player.WHITE,WinReason.PATH, 16),state.getStatus());
            engine.undoTurn(state);
            Assert.assertEquals(4, state.getTurns().size());
            engine.undoTurn(state);
            Assert.assertEquals(3, state.getTurns().size());
            engine.undoTurn(state);
            Assert.assertEquals(2, state.getTurns().size());
            engine.undoTurn(state);
            Assert.assertEquals(1, state.getTurns().size());
            engine.undoTurn(state);
            Assert.assertEquals(0, state.getTurns().size());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Test that doing a turn and then undoing it results in the same state
    @Test
    public void undoTurnMakeSureSameState() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3);
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            TakState toCheck = new TakState(state);
            engine.executeTurn(state, new TakPlaceTurn(0, 1, PieceType.STONE));
            engine.undoTurn(state);
            Assert.assertEquals(toCheck, state);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }


    //Makes sure the initial number of pieces are correct to the rules
    @Test
    public void initializeTest() {
        try {
            TakEngine engine = new TakEngine();
            //3x3
            TakState state = new TakState(Player.WHITE, 3);
            Assert.assertEquals(10, state.getWhiteStones());
            Assert.assertEquals(0, state.getWhiteCapstones());
            Assert.assertEquals(10, state.getBlackStones());
            Assert.assertEquals(0, state.getBlackCapstones());
            Assert.assertEquals(3, state.getSize());
            //4x4
            state = new TakState(Player.WHITE, 4);
            Assert.assertEquals(15, state.getWhiteStones());
            Assert.assertEquals(0, state.getWhiteCapstones());
            Assert.assertEquals(15, state.getBlackStones());
            Assert.assertEquals(0, state.getBlackCapstones());
            Assert.assertEquals(4, state.getSize());
            //5x5
            state = new TakState(Player.WHITE, 5);
            Assert.assertEquals(21, state.getWhiteStones());
            Assert.assertEquals(1, state.getWhiteCapstones());
            Assert.assertEquals(21, state.getBlackStones());
            Assert.assertEquals(1, state.getBlackCapstones());
            Assert.assertEquals(5, state.getSize());
            //6x6
            state = new TakState(Player.WHITE, 6);
            Assert.assertEquals(30, state.getWhiteStones());
            Assert.assertEquals(1, state.getWhiteCapstones());
            Assert.assertEquals(30, state.getBlackStones());
            Assert.assertEquals(1, state.getBlackCapstones());
            Assert.assertEquals(6, state.getSize());
            //8x8
            state = new TakState(Player.WHITE, 8);
            Assert.assertEquals(50, state.getWhiteStones());
            Assert.assertEquals(2, state.getWhiteCapstones());
            Assert.assertEquals(50, state.getBlackStones());
            Assert.assertEquals(2, state.getBlackCapstones());
            Assert.assertEquals(8, state.getSize());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //For all tests of getPossibleTurns, verification is done by making sure
    //all given turns are legal and that there are the right number of them.
    //This gives reasonable certainty of correctness without building large
    //lists and having to sort and compare them
    private boolean verifyState(TakState state, int possible) throws BoardGameEngineException {
        TakEngine engine = new TakEngine();
        List<Turn> turns = engine.getPossibleTurns(state);

        //makes sure there are the correct number of possible turns
        if(turns.size() != possible) {
            System.out.println("Verify getPossibleTurns failed on: illegal size (" + turns.size() + ")");
            return false;
        }

        //verify each possible turn
        for(int i = 0; i < turns.size(); i++) {
            //make sure the possible turn is legal
            if(!engine.isLegalTurn(state, turns.get(i))) {
                System.out.println("Verify getPossibleTurns failed on: illegal turn");
                return false;
            }

            //verify that no two possible moves are the same
            for(int j = i + 1; j < turns.size(); j++) {
                if(turns.get(i).toString().equals(turns.get(j).toString())) {
                    System.out.println("Verify getPossibleTurns failed on: duplicate");
                    return false;
                }
            }
        }

        return true;
    }

    //Tests basic operation of getPossibleTurns
    @Test
    public void getPossibleTurnsNormal() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);
            Assert.assertTrue(verifyState(state, 72));
            TakPlaceTurn place = new TakPlaceTurn(2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 68));
            place = new TakPlaceTurn(1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 70));
            place = new TakPlaceTurn(2,1,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 66));
            place = new TakPlaceTurn(3,3,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 48));
            TakMoveTurn move = new TakMoveTurn(2,1,1,Direction.SOUTH,new int[]{1});
            engine.executeTurn(state, move);
            Assert.assertTrue(verifyState(state, 69));
            place = new TakPlaceTurn(2,3,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 53));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a stack is taller than the max pickup height
    @Test
    public void getPossibleTurnsMaxHeight() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(3);
            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,2,Direction.EAST,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(1,0,3,Direction.SOUTH,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state,26));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a move can go off the board and when different
    //pieces are in the way
    @Test
    public void getPossibleTurnsBoardEdgeAndPieceInWay() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = initializeState(5);
            TakMoveTurn move = new TakMoveTurn(1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(0,1,3,Direction.EAST,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(4,1,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state,105));
            place = new TakPlaceTurn(1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(1,3,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(3,1,PieceType.WALL);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state,54));
            move = new TakMoveTurn(1,0,1,Direction.SOUTH,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state,64));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a player is out of a certain type of piece
    @Test
    public void getPossibleTurnsOutOfPieceType() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            for(int i = 0; i < 2; i++) {
                for(int y = 1; y < 5; y++) {
                    place = new TakPlaceTurn(0,y,PieceType.STONE);
                    engine.executeTurn(state, place);
                    for(int x = 1; x < 5 - i; x++) {
                        place = new TakPlaceTurn(x,y,PieceType.STONE);
                        engine.executeTurn(state, place);
                        TakMoveTurn move = new TakMoveTurn(x-1,y,x,Direction.EAST,new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }

            place = new TakPlaceTurn(2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state,211));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when it is the first 2 turns of the game
    @Test
    public void getPossibleTurnsFirstTurns() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 5);
            Assert.assertTrue(verifyState(state, 25));
            TakPlaceTurn place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(verifyState(state, 24));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    /*
    //Tests correct behavior for the narrowPossible flag when you are in tak
    @Test
    public void getPossibleTurnsNarrowPossibleInTak() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            //Without flag it should be 14 possible
            Assert.assertTrue(verifyState(state, 3));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior for the narrowPossible flag when you are able to win
    @Test
    public void getPossibleTurnsNarrowPossibleCanWin() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(2, 0, PieceType.STONE));
            //Without flag it should be 17 possible
            Assert.assertTrue(verifyState(state, 1));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior for the narrowPossible flag when you are able to win and are in tak
    @Test
    public void getPossibleTurnsNarrowPossibleCanWinAndInTak() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(0, 1, PieceType.STONE));
            //Without flag it should be 17 possible
            Assert.assertTrue(verifyState(state, 1));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that get possible turns doesn't change the board
    @Test
    public void getPossibleTurnsDoesntChangeState() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            TakState toCheck = new TakState(state);
            engine.getPossibleTurns(state);
            Assert.assertEquals(toCheck, state);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that inTak works correctly and doesn't change the board
    @Test
    public void inTak() {
        try {
            TakEngine engine = new TakEngine();
            TakState state = new TakState(Player.WHITE, 3);
            TakPlaceTurn place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            TakState toCheck = new TakState(state);
            Assert.assertFalse(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(1, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(Player.BLACK, state.getCurrent());

            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertFalse(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(2, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(Player.WHITE, state.getCurrent());

            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertTrue(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(3, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 1).getHeight());
            Assert.assertEquals(Player.BLACK, state.getCurrent());

            place = new TakPlaceTurn(1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertTrue(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(4, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 1).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 1).getHeight());
            Assert.assertEquals(Player.WHITE, state.getCurrent());

            place = new TakPlaceTurn(0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertTrue(state.getStatus().isComplete());
            toCheck = new TakState(state);
            Assert.assertFalse(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(5, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 1).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 1).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 2).getHeight());

        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }
    */
}