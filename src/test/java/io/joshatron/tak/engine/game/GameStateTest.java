package io.joshatron.tak.engine.game;

import io.joshatron.tak.engine.board.BoardLocation;
import io.joshatron.tak.engine.board.Direction;
import io.joshatron.tak.engine.board.Piece;
import io.joshatron.tak.engine.board.PieceType;
import io.joshatron.tak.engine.turn.MoveTurn;
import io.joshatron.tak.engine.turn.PlaceTurn;
import io.joshatron.tak.engine.turn.Turn;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class GameStateTest {
    //These tests are set up to be closer to black box testing.
    //This is done to make sure no rules can be broken instead of focusing on line coverage.
    //Assert.assertTrue is around every execute turn to make sure it happened
    // so the next tests happen in the correct state.

    //Initialize state and get first 2 moves out of the way
    private GameState initializeState(int size) {
        GameState state = new GameState(Player.WHITE, size);
        PlaceTurn turn = new PlaceTurn(new BoardLocation(0,0), PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(new BoardLocation(1,0), PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));

        return state;
    }

    //Tests placing each type of piece
    @Test
    public void isLegalTurnPlaceNormal() {
        GameState state = initializeState(8);

        //Test stone placement for each color
        PlaceTurn turn = new PlaceTurn(1,1, PieceType.STONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(2,1, PieceType.STONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));

        //Test wall placement for each color
        turn = new PlaceTurn(2,2, PieceType.WALL);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(3,2, PieceType.WALL);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));

        //Test capstone placement for each color
        turn = new PlaceTurn(3,3, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(4,3, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
    }

    //Test that when a type of piece is out it can't play
    @Test
    public void isLegalTurnPlaceOutOfPieces() {

        //Capstones
        GameState state = initializeState(8);
        //Place legal
        PlaceTurn turn = new PlaceTurn(2,0, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(3,0, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(4,0, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(5,0, PieceType.CAPSTONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        //Place illegal capstone white
        turn = new PlaceTurn(6,0, PieceType.CAPSTONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        //Place legal stone white
        turn = new PlaceTurn(6,0, PieceType.STONE);
        Assert.assertTrue(state.isLegalTurn(turn));
        Assert.assertTrue(state.executeTurn(turn));
        //Place illegal capstone black
        turn = new PlaceTurn(7,0, PieceType.CAPSTONE);
        Assert.assertFalse(state.isLegalTurn(turn));

        //Stones
        state = initializeState(8);
        //Fill up board to get to no stones
        for(int i = 0; i < 2; i++) {
            for (int y = 1; y < 7; y++) {
                turn = new PlaceTurn(0, y, PieceType.STONE);
                state.executeTurn(turn);
                for (int x = 1; x < 8 - i; x++) {
                    turn = new PlaceTurn(x, y, PieceType.STONE);
                    Assert.assertTrue(state.executeTurn(turn));
                    MoveTurn move = new MoveTurn(x - 1, y, x, Direction.EAST, new int[]{x});
                    Assert.assertTrue(state.executeTurn(move));
                }
            }
        }
        for(int i = 0; i < 4; i++) {
            turn = new PlaceTurn(i * 2,7, PieceType.STONE);
            Assert.assertTrue(state.executeTurn(turn));
            turn = new PlaceTurn(i * 2 + 1,7, PieceType.STONE);
            Assert.assertTrue(state.executeTurn(turn));
        }
        //Illegal white move, out of pieces
        turn = new PlaceTurn(0, 1, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(0, 1, PieceType.WALL);
        Assert.assertFalse(state.isLegalTurn(turn));
        //Legal capstone placement to make black turn
        turn = new PlaceTurn(0, 1, PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(turn));
        //Illegal black move
        turn = new PlaceTurn(0, 2, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(0, 2, PieceType.WALL);
        Assert.assertFalse(state.isLegalTurn(turn));
    }

    //Tests that you can't place a piece off the board
    @Test
    public void isLegalTurnPlaceOffBoard() {
        GameState state = new GameState(Player.BLACK, 3);
        //Black
        PlaceTurn turn = new PlaceTurn(new BoardLocation(-1,-1), PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,3, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(3,1, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(3,3, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,1, PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
        //White
        turn = new PlaceTurn(-1,-1, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,3, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(3,1, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(3,3, PieceType.STONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,2, PieceType.STONE);
        Assert.assertTrue(state.isLegalTurn(turn));
    }

    //Tests that you can't place pieces on other ones
    @Test
    public void isLegalTurnPlaceOnOtherPieces() {
        //Initalize with every type of piece
        GameState state = initializeState(8);
        PlaceTurn turn = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(1,2,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(2,2,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(1,3,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(turn));
        turn = new PlaceTurn(2,3,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(turn));

        //Test white placing
        for(int x = 1; x < 3; x++) {
            for(int y = 1; y < 4; y++) {
                turn = new PlaceTurn(x,y, PieceType.STONE);
                Assert.assertFalse(state.isLegalTurn(turn));
                turn = new PlaceTurn(x,y, PieceType.WALL);
                Assert.assertFalse(state.isLegalTurn(turn));
                turn = new PlaceTurn(x,y, PieceType.CAPSTONE);
                Assert.assertFalse(state.isLegalTurn(turn));
            }
        }

        turn = new PlaceTurn(5,5, PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));

        //Test black placing
        for(int x = 1; x < 3; x++) {
            for(int y = 1; y < 4; y++) {
                turn = new PlaceTurn(x,y, PieceType.STONE);
                Assert.assertFalse(state.isLegalTurn(turn));
                turn = new PlaceTurn(x,y, PieceType.WALL);
                Assert.assertFalse(state.isLegalTurn(turn));
                turn = new PlaceTurn(x,y, PieceType.CAPSTONE);
                Assert.assertFalse(state.isLegalTurn(turn));
            }
        }
    }

    //Makes sure you can't place anything besides stones for the first 2 turns
    @Test
    public void isLegalTurnPlaceBadFirstMoves() {
        GameState state = new GameState(Player.WHITE, 5);
        //white illegal turns
        PlaceTurn turn = new PlaceTurn(0,0,PieceType.CAPSTONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(0,0,PieceType.WALL);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
        //black illegal turns
        turn = new PlaceTurn(1,1,PieceType.CAPSTONE);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,1,PieceType.WALL);
        Assert.assertFalse(state.isLegalTurn(turn));
        turn = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(turn));
    }

    //Tests that some legal moves are legal
    @Test
    public void isLegalTurnMoveNormal() {
        GameState state = initializeState(5);

        MoveTurn move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(0,2,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(4,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,2,Direction.SOUTH,new int[]{1,1});
        Assert.assertTrue(state.isLegalTurn(move));
    }

    //Tests if you try to make a move off board
    @Test
    public void isLegalTurnMoveOffBoard() {
        GameState state = initializeState(5);

        PlaceTurn place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        MoveTurn move = new MoveTurn(0,0,1,Direction.EAST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,0,2,Direction.NORTH,new int[]{2});
        Assert.assertFalse(state.isLegalTurn(move));
        move = new MoveTurn(1,0,2,Direction.WEST,new int[]{1,1});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests if you try to grab a pile you don't own
    @Test
    public void isLegalTurnMoveIllegalPickup() {
        GameState state = initializeState(5);

        //Entire stack is owned by other player
        MoveTurn move = new MoveTurn(0,0,1,Direction.EAST,new int[]{1});
        Assert.assertFalse(state.isLegalTurn(move));
        move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        //Only top of stack is owned by other player
        move = new MoveTurn(0,0,2,Direction.EAST,new int[]{2});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests if you try to cover a wall or capstone illegally
    @Test
    public void isLegalTurnMoveIllegalCover() {
        GameState state = initializeState(5);

        MoveTurn move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(0,1,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,1,Direction.SOUTH,new int[]{1});
        Assert.assertFalse(state.isLegalTurn(move));
        place = new PlaceTurn(1,0,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(1,0,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,2,Direction.SOUTH,new int[]{1,1});
        Assert.assertFalse(state.isLegalTurn(move));
        move = new MoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
        Assert.assertFalse(state.isLegalTurn(move));
        move = new MoveTurn(0,0,1,Direction.EAST,new int[]{1});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests if you try to pick up more than the max height
    @Test
    public void isLegalTurnMoveTooManyPickup() {
        GameState state = initializeState(3);

        MoveTurn move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,1,3,Direction.EAST,new int[]{3});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,1,4,Direction.EAST,new int[]{4});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests if you try to not leave at least 1 piece in each spot in the path
    @Test
    public void isLegalTurnMoveEmptySpots() {
        GameState state = initializeState(5);

        MoveTurn move = new MoveTurn(1,0,1,Direction.SOUTH,new int[]{0,1});
        Assert.assertFalse(state.isLegalTurn(move));
        move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(1,1,PieceType.STONE);
        move = new MoveTurn(0,0,2,Direction.EAST,new int[]{1,0,1});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests if you try to do a move in the first 2 turns
    @Test
    public void isLegalTurnMoveBadFirstMoves() {
        GameState state = new GameState(Player.WHITE,5);

        PlaceTurn place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        MoveTurn move = new MoveTurn(0,0,1,Direction.SOUTH,new int[]{1});
        Assert.assertFalse(state.isLegalTurn(move));
    }

    //Tests a straight horizontal win path
    @Test
    public void checkForWinnerStraightHorizontal() {
        GameState state = new GameState(Player.WHITE,3);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.PATH),state.checkForWinner());
    }

    //Tests a straight vertical win path
    @Test
    public void checkForWinnerStraightVertical() {
        GameState state = new GameState(Player.BLACK,3);

        PlaceTurn place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.BLACK,WinReason.PATH),state.checkForWinner());
    }

    //Tests a non-straight horizontal win path
    @Test
    public void checkForWinnerCurvyHorizontal() {
        GameState state = new GameState(Player.WHITE,6);

        MoveTurn moveDown = new MoveTurn(5,0,1,Direction.SOUTH,new int[]{1});
        MoveTurn moveUp = new MoveTurn(5,1,1,Direction.NORTH,new int[]{1});

        PlaceTurn place = new PlaceTurn(5,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(3,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(3,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(3,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(0,3,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(0,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(1,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(2,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(3,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(4,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(5,4,PieceType.STONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.PATH),state.checkForWinner());
    }

    //Tests a non-straight vertical win path
    @Test
    public void checkForWinnerCurvyVertical() {
        GameState state = new GameState(Player.BLACK,6);

        MoveTurn moveDown = new MoveTurn(5,0,1,Direction.SOUTH,new int[]{1});
        MoveTurn moveUp = new MoveTurn(5,1,1,Direction.NORTH,new int[]{1});

        PlaceTurn place = new PlaceTurn(5,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(3,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(3,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(3,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(0,3,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(0,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveUp));
        place = new PlaceTurn(1,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(state.executeTurn(moveDown));
        place = new PlaceTurn(1,5,PieceType.STONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.BLACK,WinReason.PATH),state.checkForWinner());
    }

    //Tests that walls can't be in win paths
    @Test
    public void checkForWinnerWallInPath() {
        GameState state = new GameState(Player.WHITE,3);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,0,PieceType.WALL);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(),state.checkForWinner());
    }

    //Tests that capstones can be in win paths
    @Test
    public void checkForWinnerCapstoneInPath() {
        GameState state = new GameState(Player.WHITE,5);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(3,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(3,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(4,0,PieceType.CAPSTONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.PATH),state.checkForWinner());
    }

    //Tests that the top spot in a stack is the one counted
    @Test
    public void checkForWinnerStacks() {
        GameState state = new GameState(Player.WHITE,3);

        PlaceTurn place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        MoveTurn move = new MoveTurn(1,0,1,Direction.SOUTH,new int[]{1});
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(move));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.PATH),state.checkForWinner());
    }

    //Tests that diagonals don't count toward win paths
    @Test
    public void checkForWinnerDiagonals() {
        GameState state = new GameState(Player.BLACK,3);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(),state.checkForWinner());
    }

    //Tests the win condition that happens with a full board
    @Test
    public void checkForWinnerFullBoard() {
        GameState state = initializeState(3);

        PlaceTurn place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertEquals(new GameResult(), state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.BOARD_FULL), state.checkForWinner());
    }

    //Tests the win condition that happens when a player runs out of pieces
    @Test
    public void checkForWinnerOutOfPieces() {
        GameState state = new GameState(Player.WHITE,5);

        PlaceTurn place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        for(int i = 0; i < 2; i++) {
            for(int y = 1; y < 5; y++) {
                place = new PlaceTurn(0,y,PieceType.STONE);
                Assert.assertTrue(state.executeTurn(place));
                for(int x = 1; x < 5 - i; x++) {
                    place = new PlaceTurn(x,y,PieceType.STONE);
                    Assert.assertTrue(state.executeTurn(place));
                    MoveTurn move = new MoveTurn(x-1,y,x,Direction.EAST,new int[]{x});
                    Assert.assertTrue(state.executeTurn(move));
                }
            }
        }

        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(3,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.CAPSTONE);
        Assert.assertEquals(new GameResult(),state.checkForWinner());
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertEquals(new GameResult(true,Player.BLACK,WinReason.OUT_OF_PIECES),state.checkForWinner());
    }

    //Makes sure the right player wins when both players get a road in the final move
    @Test
    public void checkForWinnerDoubleRoad() {
        GameState state = new GameState(Player.WHITE,3);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        MoveTurn move = new MoveTurn(2,2,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,2,2,Direction.NORTH,new int[]{1,1});
        Assert.assertEquals(new GameResult(), state.checkForWinner());
        Assert.assertTrue(state.executeTurn(move));
        Assert.assertEquals(new GameResult(true,Player.WHITE,WinReason.PATH), state.checkForWinner());
    }

    //Tests that places end up with the right state and undo correctly
    @Test
    public void executeAndUndoPlace() {
        GameState state = initializeState(5);

        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,2,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,3,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,3,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));

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

        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(1,3).getHeight());
        Assert.assertFalse(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(0,3).getHeight());
        Assert.assertTrue(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(1,2).getHeight());
        Assert.assertFalse(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(0,2).getHeight());
        Assert.assertTrue(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(1,1).getHeight());
        Assert.assertFalse(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(0,1).getHeight());
        Assert.assertTrue(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
        Assert.assertFalse(state.isWhiteTurn());
        state.undoTurn();
        Assert.assertEquals(0,state.getBoard().getPosition(0,0).getHeight());
        Assert.assertTrue(state.isWhiteTurn());
        Assert.assertEquals(21,state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(1,state.getWhiteCapstonesLeft());
        Assert.assertEquals(21,state.getBlackNormalPiecesLeft());
        Assert.assertEquals(1,state.getBlackCapstonesLeft());
    }

    //Tests that moves end up with the right state and undo correctly
    @Test
    public void executeAndUndoMove() {
        GameState state = initializeState(5);

        PlaceTurn place = new PlaceTurn(1,1,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(2,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        MoveTurn move = new MoveTurn(1,1,1,Direction.EAST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(2,1,2,Direction.SOUTH,new int[]{2});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(2,3,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(2,2,3,Direction.SOUTH,new int[]{3});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(2,1,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(2,3,3,Direction.NORTH,new int[]{2,1});
        Assert.assertTrue(state.executeTurn(move));

        //Verify final state
        Assert.assertTrue(state.getBoard().getPosition(2,3).getTopPiece().isBlack());
        Assert.assertEquals(1, state.getBoard().getPosition(2,3).getHeight());
        Assert.assertEquals(PieceType.STONE, state.getBoard().getPosition(2,3).getTopPiece().getType());
        ArrayList<Piece> pieces = state.getBoard().getPosition(2,2).getPieces();
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
        Assert.assertFalse(state.isWhiteTurn());

        //Test undoing last move
        state.undoTurn();
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
        Assert.assertTrue(state.isWhiteTurn());
    }

    //Tests undo move on first 2 moves
    @Test
    public void undoMoveFirstTurns() {
        GameState state = initializeState(3);
        state.undoTurn();
        Assert.assertTrue(state.getBoard().getPosition(0,0).getTopPiece().isBlack());
        Assert.assertEquals(1, state.getBoard().getPosition(0,0).getHeight());
        Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
        Assert.assertEquals(10,state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(0,state.getWhiteCapstonesLeft());
        Assert.assertEquals(9,state.getBlackNormalPiecesLeft());
        Assert.assertEquals(0,state.getBlackCapstonesLeft());
        state.undoTurn();
        Assert.assertEquals(0, state.getBoard().getPosition(0,0).getHeight());
        Assert.assertEquals(0,state.getBoard().getPosition(1,0).getHeight());
        Assert.assertEquals(10,state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(0,state.getWhiteCapstonesLeft());
        Assert.assertEquals(10,state.getBlackNormalPiecesLeft());
        Assert.assertEquals(0,state.getBlackCapstonesLeft());
    }

    //Makes sure the initial number of pieces are correct to the rules
    @Test
    public void initializeTest() {
        //3x3
        GameState state = new GameState(Player.WHITE, 3);
        Assert.assertEquals(10, state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(0, state.getWhiteCapstonesLeft());
        Assert.assertEquals(10, state.getBlackNormalPiecesLeft());
        Assert.assertEquals(0, state.getBlackCapstonesLeft());
        Assert.assertEquals(3, state.getBoardSize());
        //4x4
        state = new GameState(Player.WHITE, 4);
        Assert.assertEquals(15, state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(0, state.getWhiteCapstonesLeft());
        Assert.assertEquals(15, state.getBlackNormalPiecesLeft());
        Assert.assertEquals(0, state.getBlackCapstonesLeft());
        Assert.assertEquals(4, state.getBoardSize());
        //5x5
        state = new GameState(Player.WHITE, 5);
        Assert.assertEquals(21, state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(1, state.getWhiteCapstonesLeft());
        Assert.assertEquals(21, state.getBlackNormalPiecesLeft());
        Assert.assertEquals(1, state.getBlackCapstonesLeft());
        Assert.assertEquals(5, state.getBoardSize());
        //6x6
        state = new GameState(Player.WHITE, 6);
        Assert.assertEquals(30, state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(1, state.getWhiteCapstonesLeft());
        Assert.assertEquals(30, state.getBlackNormalPiecesLeft());
        Assert.assertEquals(1, state.getBlackCapstonesLeft());
        Assert.assertEquals(6, state.getBoardSize());
        //8x8
        state = new GameState(Player.WHITE, 8);
        Assert.assertEquals(50, state.getWhiteNormalPiecesLeft());
        Assert.assertEquals(2, state.getWhiteCapstonesLeft());
        Assert.assertEquals(50, state.getBlackNormalPiecesLeft());
        Assert.assertEquals(2, state.getBlackCapstonesLeft());
        Assert.assertEquals(8, state.getBoardSize());
    }

    //For all tests of getPossibleTurns, verification is done by making sure
    //all given turns are legal and that there are the right number of them.
    //This gives reasonable certainty of correctness without building large
    //lists and having to sort and compare them
    private boolean verifyState(GameState state, int possible) {
        ArrayList<Turn> turns = state.getPossibleTurns();

        //makes sure there are the correct number of possible turns
        if(turns.size() != possible) {
            System.out.println("Verify getPossibleTurns failed on: illegal size (" + turns.size() + ")");
            return false;
        }

        //verify each possible turn
        for(int i = 0; i < turns.size(); i++) {
            //make sure the possible turn is legal
            if(!state.isLegalTurn(turns.get(i))) {
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
        GameState state = initializeState(5);
        Assert.assertTrue(verifyState(state, 72));
        PlaceTurn place = new PlaceTurn(2,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 68));
        place = new PlaceTurn(1,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 70));
        place = new PlaceTurn(2,1,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 66));
        place = new PlaceTurn(3,3,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 48));
        MoveTurn move = new MoveTurn(2,1,1,Direction.SOUTH,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        Assert.assertTrue(verifyState(state, 69));
        place = new PlaceTurn(2,3,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 53));
    }

    //Tests correct behavior when a stack is taller than the max pickup height
    @Test
    public void getPossibleTurnsMaxHeight() {
        GameState state = initializeState(3);
        MoveTurn move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,2,Direction.EAST,new int[]{2});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(1,0,3,Direction.SOUTH,new int[]{3});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state,26));
    }

    //Tests correct behavior when a move can go off the board and when different
    //pieces are in the way
    @Test
    public void getPossibleTurnsBoardEdgeAndPieceInWay() {
        GameState state = initializeState(5);
        MoveTurn move = new MoveTurn(1,0,1,Direction.WEST,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        PlaceTurn place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,0,2,Direction.SOUTH,new int[]{2});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        move = new MoveTurn(0,1,3,Direction.EAST,new int[]{3});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(4,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state,105));
        place = new PlaceTurn(1,0,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(1,3,PieceType.CAPSTONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(3,1,PieceType.WALL);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state,54));
        move = new MoveTurn(1,0,1,Direction.SOUTH,new int[]{1});
        Assert.assertTrue(state.executeTurn(move));
        place = new PlaceTurn(4,4,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state,64));
    }

    //Tests correct behavior when a player is out of a certain type of piece
    @Test
    public void getPossibleTurnsOutOfPieceType() {
        GameState state = new GameState(Player.WHITE,5);

        PlaceTurn place = new PlaceTurn(1,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        for(int i = 0; i < 2; i++) {
            for(int y = 1; y < 5; y++) {
                place = new PlaceTurn(0,y,PieceType.STONE);
                Assert.assertTrue(state.executeTurn(place));
                for(int x = 1; x < 5 - i; x++) {
                    place = new PlaceTurn(x,y,PieceType.STONE);
                    Assert.assertTrue(state.executeTurn(place));
                    MoveTurn move = new MoveTurn(x-1,y,x,Direction.EAST,new int[]{x});
                    Assert.assertTrue(state.executeTurn(move));
                }
            }
        }

        place = new PlaceTurn(2,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(3,0,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        place = new PlaceTurn(0,2,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state,211));
    }

    //Tests correct behavior when it is the first 2 turns of the game
    @Test
    public void getPossibleTurnsFirstTurns() {
        GameState state = new GameState(Player.WHITE, 5);
        Assert.assertTrue(verifyState(state, 25));
        PlaceTurn place = new PlaceTurn(1,1,PieceType.STONE);
        Assert.assertTrue(state.executeTurn(place));
        Assert.assertTrue(verifyState(state, 24));
    }
}