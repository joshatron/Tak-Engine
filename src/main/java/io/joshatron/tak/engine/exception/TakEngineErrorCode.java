package io.joshatron.tak.engine.exception;

import io.joshatron.bgt.engine.exception.BoardGameErrorCode;

public enum TakEngineErrorCode implements BoardGameErrorCode {
    INVALID_BOARD_SIZE,
    NOT_ENOUGH_STONES,
    NOT_ENOUGH_CAPSTONES,
    STACK_NOT_EMPTY,
    CANT_MOVE_IN_FIRST_TURN,
    INVALID_PICKUP_AMOUNT,
    DO_NOT_OWN_STACK,
    INVALID_PLACE_AMOUNT,
    BLOCKED_FROM_PLACING,
    GAME_FINISHED,
    TOO_MANY_PIECES_SPECIFIED,
    INVALID_PIECE_TYPE,
    INVALID_TURN_STRING,
    ILLEGAL_TYPE;

    @Override
    public String getName() {
        return this.name();
    }
}
