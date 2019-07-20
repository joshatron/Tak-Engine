package io.joshatron.tak.engine.game;

import io.joshatron.bgt.engine.board.grid.Direction;
import io.joshatron.bgt.engine.board.grid.GridBoardLocation;
import io.joshatron.bgt.engine.engines.AggregateGameEngine;
import io.joshatron.bgt.engine.engines.GameEngine;
import io.joshatron.bgt.engine.exception.BoardGameEngineException;
import io.joshatron.bgt.engine.player.PlayerIndicator;
import io.joshatron.bgt.engine.state.Status;
import io.joshatron.bgt.engine.state.Turn;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.TakMoveTurn;
import io.joshatron.tak.engine.turn.TakPlaceTurn;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TakEngineTest {
    //These tests are set up to be closer to black box testing.
    //This is done to make sure no rules can be broken instead of focusing on line coverage.

    //Initialize state and get first 2 moves out of the way
    private TakState initializeState(int size) throws BoardGameEngineException {
        GameEngine engine = new AggregateGameEngine(new TakEngineManager());
        TakState state = new TakState(PlayerIndicator.WHITE, size);
        TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), new GridBoardLocation(0, 0), PieceType.STONE);
        engine.executeTurn(state, turn);
        turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), new GridBoardLocation(1, 0), PieceType.STONE);
        engine.executeTurn(state, turn);

        return state;
    }

    //Tests placing each type of piece
    @Test
    public void isLegalTurnPlaceNormal() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(8);

            //Test stone placement for each color
            TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1, 1, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2, 1, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);

            //Test wall placement for each color
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2, 2, PieceType.WALL);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3, 2, PieceType.WALL);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);

            //Test capstone placement for each color
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3, 3, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4, 3, PieceType.CAPSTONE);
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
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(8);
            //Place legal
            TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,0, PieceType.CAPSTONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            //Place illegal capstone white
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 6,0, PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            //Place legal stone white
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 6,0, PieceType.STONE);
            Assert.assertTrue(engine.isLegalTurn(state, turn));
            engine.executeTurn(state, turn);
            //Place illegal capstone black
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 7,0, PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));

            //Stones
            state = initializeState(8);
            //Fill up board to get to no stones
            for(int i = 0; i < 2; i++) {
                for (int y = 1; y < 7; y++) {
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, y, PieceType.STONE);
                    engine.executeTurn(state, turn);
                    for (int x = 1; x < 8 - i; x++) {
                        turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x, y, PieceType.STONE);
                        engine.executeTurn(state, turn);
                        TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), x - 1, y, x, Direction.EAST, new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }
            for(int i = 0; i < 4; i++) {
                turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), i * 2,7, PieceType.STONE);
                engine.executeTurn(state, turn);
                turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), i * 2 + 1,7, PieceType.STONE);
                engine.executeTurn(state, turn);
            }
            //Illegal white move, out of pieces
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, 1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, 1, PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            //Legal capstone placement to make black turn
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, 1, PieceType.CAPSTONE);
            engine.executeTurn(state, turn);
            //Illegal black move
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, 2, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0, 2, PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that you can't place a piece off the board
    @Test
    public void isLegalTurnPlaceOffBoard() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.BLACK, 3);
            //Black
            TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), new GridBoardLocation(-1,-1), PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1, PieceType.STONE);
            engine.executeTurn(state, turn);
            //White
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), -1,-1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,3, PieceType.STONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2, PieceType.STONE);
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
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(8);
            TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.WALL);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.WALL);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,3,PieceType.CAPSTONE);
            engine.executeTurn(state, turn);
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,3,PieceType.CAPSTONE);
            engine.executeTurn(state, turn);

            //Test white placing
            for(int x = 1; x < 3; x++) {
                for(int y = 1; y < 4; y++) {
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.STONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.WALL);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.CAPSTONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                }
            }

            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,5, PieceType.STONE);
            engine.executeTurn(state, turn);

            //Test black placing
            for(int x = 1; x < 3; x++) {
                for(int y = 1; y < 4; y++) {
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.STONE);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.WALL);
                    Assert.assertFalse(engine.isLegalTurn(state, turn));
                    turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y, PieceType.CAPSTONE);
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
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 5);
            //white illegal turns
            TakPlaceTurn turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, turn);
            //black illegal turns
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.CAPSTONE);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.WALL);
            Assert.assertFalse(engine.isLegalTurn(state, turn));
            turn = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, turn);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that some legal moves are legal
    @Test
    public void isLegalTurnMoveNormal() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.WALL);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.SOUTH,new int[]{1,1});
            Assert.assertTrue(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to make a move off board
    @Test
    public void isLegalTurnMoveOffBoard() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,1,Direction.EAST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,2,Direction.NORTH,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,2,Direction.WEST,new int[]{1,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to grab a pile you don't own
    @Test
    public void isLegalTurnMoveIllegalPickup() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);

            //Entire stack is owned by other player
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,1,Direction.EAST,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            //Only top of stack is owned by other player
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.EAST,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to cover a wall or capstone illegally
    @Test
    public void isLegalTurnMoveIllegalCover() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1, Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.WALL);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.SOUTH,new int[]{1,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.SOUTH,new int[]{2});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,1,Direction.EAST,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to pick up more than the max height
    @Test
    public void isLegalTurnMoveTooManyPickup() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(3);

            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.SOUTH,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,3,Direction.EAST,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,4,Direction.EAST,new int[]{4});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to not leave at least 1 piece in each spot in the path
    @Test
    public void isLegalTurnMoveEmptySpots() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);

            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.SOUTH,new int[]{0,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.EAST,new int[]{1,0,1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests if you try to do a move in the first 2 turns
    @Test
    public void isLegalTurnMoveBadFirstMoves() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertFalse(engine.isLegalTurn(state, move));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a straight horizontal win path
    @Test
    public void checkForWinnerStraightHorizontal() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a straight vertical win path
    @Test
    public void checkForWinnerStraightVertical() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.BLACK,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.BLACK,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a non-straight horizontal win path
    @Test
    public void checkForWinnerCurvyHorizontal() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,6);

            TakMoveTurn moveDown = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,0,1,Direction.SOUTH,new int[]{1});
            TakMoveTurn moveUp = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,1,1,Direction.NORTH,new int[]{1});

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,3,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,4,PieceType.STONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.PATH, 51),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests a non-straight vertical win path
    @Test
    public void checkForWinnerCurvyVertical() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.BLACK,6);

            TakMoveTurn moveDown = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,0,1,Direction.SOUTH,new int[]{1});
            TakMoveTurn moveUp = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,1,1,Direction.NORTH,new int[]{1});

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 5,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,3,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveUp);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,4,PieceType.STONE);
            engine.executeTurn(state, place);
            engine.executeTurn(state, moveDown);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,5,PieceType.STONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.BLACK,WinReason.PATH, 54),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that walls can't be in win paths
    @Test
    public void checkForWinnerWallInPath() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.WALL);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that capstones can be in win paths
    @Test
    public void checkForWinnerCapstoneInPath() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,0,PieceType.CAPSTONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.PATH, 42),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that the top spot in a stack is the one counted
    @Test
    public void checkForWinnerStacks() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.SOUTH,new int[]{1});
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, move);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.PATH, 16),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that diagonals don't count toward win paths
    @Test
    public void checkForWinnerDiagonals() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.BLACK,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests the win condition that happens with a full board
    @Test
    public void checkForWinnerFullBoard() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            Assert.assertEquals(new TakStatus(), state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.BOARD_FULL, 14), state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests the win condition that happens when a player runs out of pieces
    @Test
    public void checkForWinnerOutOfPieces() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            for(int i = 0; i < 2; i++) {
                for(int y = 1; y < 5; y++) {
                    place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(),0,y,PieceType.STONE);
                    engine.executeTurn(state, place);
                    for(int x = 1; x < 5 - i; x++) {
                        place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(),x,y,PieceType.STONE);
                        engine.executeTurn(state, place);
                        TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(),x-1,y,x,Direction.EAST,new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }

            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.CAPSTONE);
            Assert.assertEquals(new TakStatus(),state.getStatus());
            engine.executeTurn(state, place);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.BLACK,WinReason.OUT_OF_PIECES, 26),state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Makes sure the right player wins when both players get a road in the final move
    @Test
    public void checkForWinnerDoubleRoad() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,2,Direction.NORTH,new int[]{1,1});
            Assert.assertEquals(new TakStatus(), state.getStatus());
            engine.executeTurn(state, move);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.PATH, 16), state.getStatus());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that checking for winner doesn't change the game state
    @Test
    public void checkForWinnerDoesntChangeBoard() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(3);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            TakState toCheck = (TakState) SerializationUtils.clone(state);
            Assert.assertEquals(new TakStatus(), state.getStatus());
            Assert.assertEquals(toCheck, state);
            engine.executeTurn(state, place);
            toCheck = (TakState) SerializationUtils.clone(state);
            Assert.assertEquals(new TakStatus(Status.COMPLETE,PlayerIndicator.WHITE,WinReason.BOARD_FULL, 14), state.getStatus());
            Assert.assertEquals(toCheck, state);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Makes sure the initial number of pieces are correct to the rules
    @Test
    public void initializeTest() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            //3x3
            TakState state = new TakState(PlayerIndicator.WHITE, 3);
            TakPlayerInfo whiteInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.WHITE);
            TakPlayerInfo blackInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.BLACK);
            Assert.assertEquals(10, whiteInfo.getStones().getPiecesLeft());
            Assert.assertEquals(0, whiteInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(10, blackInfo.getStones().getPiecesLeft());
            Assert.assertEquals(0, blackInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(3, state.getSize());
            //4x4
            state = new TakState(PlayerIndicator.WHITE, 4);
            whiteInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.WHITE);
            blackInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.BLACK);
            Assert.assertEquals(15, whiteInfo.getStones().getPiecesLeft());
            Assert.assertEquals(0, whiteInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(15, blackInfo.getStones().getPiecesLeft());
            Assert.assertEquals(0, blackInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(4, state.getSize());
            //5x5
            state = new TakState(PlayerIndicator.WHITE, 5);
            whiteInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.WHITE);
            blackInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.BLACK);
            Assert.assertEquals(21, whiteInfo.getStones().getPiecesLeft());
            Assert.assertEquals(1, whiteInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(21, blackInfo.getStones().getPiecesLeft());
            Assert.assertEquals(1, blackInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(5, state.getSize());
            //6x6
            state = new TakState(PlayerIndicator.WHITE, 6);
            whiteInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.WHITE);
            blackInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.BLACK);
            Assert.assertEquals(30, whiteInfo.getStones().getPiecesLeft());
            Assert.assertEquals(1, whiteInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(30, blackInfo.getStones().getPiecesLeft());
            Assert.assertEquals(1, blackInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(6, state.getSize());
            //8x8
            state = new TakState(PlayerIndicator.WHITE, 8);
            whiteInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.WHITE);
            blackInfo = (TakPlayerInfo) state.getPlayerInfo(PlayerIndicator.BLACK);
            Assert.assertEquals(50, whiteInfo.getStones().getPiecesLeft());
            Assert.assertEquals(2, whiteInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(50, blackInfo.getStones().getPiecesLeft());
            Assert.assertEquals(2, blackInfo.getCapstones().getPiecesLeft());
            Assert.assertEquals(8, state.getSize());
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //For all tests of getPossibleTurns, verification is done by making sure
    //all given turns are legal and that there are the right number of them.
    //This gives reasonable certainty of correctness without building large
    //lists and having to sort and compare them
    private void verifyState(TakState state, int possible) throws BoardGameEngineException {
        GameEngine engine = new AggregateGameEngine(new TakEngineManager());
        List<Turn> turns = engine.getPossibleTurns(state);

        //makes sure there are the correct number of possible turns
        if(turns.size() != possible) {
            Assert.fail("Verify getPossibleTurns failed on: illegal size (" + turns.size() + ")");
        }

        //verify each possible turn
        for(int i = 0; i < turns.size(); i++) {
            //make sure the possible turn is legal
            if(!engine.isLegalTurn(state, turns.get(i))) {
                Assert.fail("Verify getPossibleTurns failed on: illegal turn");
            }

            //verify that no two possible moves are the same
            for(int j = i + 1; j < turns.size(); j++) {
                if(turns.get(i).equals(turns.get(j))) {
                    Assert.fail("Verify getPossibleTurns failed on: duplicate");
                }
            }
        }
    }

    //Tests basic operation of getPossibleTurns
    @Test
    public void getPossibleTurnsNormal() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);
            verifyState(state, 72);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,2,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state, 68);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,2,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state, 70);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            verifyState(state, 66);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,3,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state, 48);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,1,1,Direction.SOUTH,new int[]{1});
            engine.executeTurn(state, move);
            verifyState(state, 69);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,3,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state, 53);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a stack is taller than the max pickup height
    @Test
    public void getPossibleTurnsMaxHeight() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(3);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.EAST,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,3,Direction.SOUTH,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state,26);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a move can go off the board and when different
    //pieces are in the way
    @Test
    public void getPossibleTurnsBoardEdgeAndPieceInWay() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = initializeState(5);
            TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.WEST,new int[]{1});
            engine.executeTurn(state, move);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,2,Direction.SOUTH,new int[]{2});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,3,Direction.EAST,new int[]{3});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,1,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state,105);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,3,PieceType.CAPSTONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,1,PieceType.WALL);
            engine.executeTurn(state, place);
            verifyState(state,54);
            move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,1,Direction.SOUTH,new int[]{1});
            engine.executeTurn(state, move);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 4,4,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state,64);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when a player is out of a certain type of piece
    @Test
    public void getPossibleTurnsOutOfPieceType() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE,5);

            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            for(int i = 0; i < 2; i++) {
                for(int y = 1; y < 5; y++) {
                    place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,y,PieceType.STONE);
                    engine.executeTurn(state, place);
                    for(int x = 1; x < 5 - i; x++) {
                        place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), x,y,PieceType.STONE);
                        engine.executeTurn(state, place);
                        TakMoveTurn move = new TakMoveTurn(state.getCurrentPlayerInfo().getIdentifier(), x-1,y,x,Direction.EAST,new int[]{x});
                        engine.executeTurn(state, move);
                    }
                }
            }

            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 2,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 3,0,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 0,2,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state,211);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior when it is the first 2 turns of the game
    @Test
    public void getPossibleTurnsFirstTurns() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 5);
            verifyState(state, 25);
            TakPlaceTurn place = new TakPlaceTurn(state.getCurrentPlayerInfo().getIdentifier(), 1,1,PieceType.STONE);
            engine.executeTurn(state, place);
            verifyState(state, 24);
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    /*
    //Tests correct behavior for the narrowPossible flag when you are in tak
    @Test
    public void getPossibleTurnsNarrowPossibleInTak() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            //Without flag it should be 14 possible
            verifyState(state, 3));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior for the narrowPossible flag when you are able to win
    @Test
    public void getPossibleTurnsNarrowPossibleCanWin() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(2, 0, PieceType.STONE));
            //Without flag it should be 17 possible
            verifyState(state, 1));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests correct behavior for the narrowPossible flag when you are able to win and are in tak
    @Test
    public void getPossibleTurnsNarrowPossibleCanWinAndInTak() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 3, new GameStateConfig(false, true));
            engine.executeTurn(state, new TakPlaceTurn(0, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 0, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(1, 1, PieceType.STONE));
            engine.executeTurn(state, new TakPlaceTurn(0, 1, PieceType.STONE));
            //Without flag it should be 17 possible
            verifyState(state, 1));
        } catch (BoardGameEngineException e) {
            Assert.fail();
        }
    }

    //Tests that get possible turns doesn't change the board
    @Test
    public void getPossibleTurnsDoesntChangeState() {
        try {
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 3, new GameStateConfig(false, true));
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
            GameEngine engine = new AggregateGameEngine(new TakEngineManager());
            TakState state = new TakState(PlayerIndicator.WHITE, 3);
            TakPlaceTurn place = new TakPlaceTurn(1,0,PieceType.STONE);
            engine.executeTurn(state, place);
            TakState toCheck = new TakState(state);
            Assert.assertFalse(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(1, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(PlayerIndicator.BLACK, state.getCurrent());

            place = new TakPlaceTurn(0,0,PieceType.STONE);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertFalse(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(2, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(PlayerIndicator.WHITE, state.getCurrent());

            place = new TakPlaceTurn(0,1,PieceType.STONE);
            engine.executeTurn(state, place);
            toCheck = new TakState(state);
            Assert.assertTrue(state.inTak());
            Assert.assertEquals(toCheck, state);
            Assert.assertEquals(3, state.getTurns().size());
            Assert.assertEquals(1, state.getBoard().getPosition(1, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 0).getHeight());
            Assert.assertEquals(1, state.getBoard().getPosition(0, 1).getHeight());
            Assert.assertEquals(PlayerIndicator.BLACK, state.getCurrent());

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
            Assert.assertEquals(PlayerIndicator.WHITE, state.getCurrent());

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