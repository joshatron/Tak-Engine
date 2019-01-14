package io.joshatron.tak.engine.exception;

public class TakEngineException extends Exception {

    private ErrorCode code;

    public TakEngineException(ErrorCode code) {
        super("The tak engine encountered an exception of type: " + code.name());
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
