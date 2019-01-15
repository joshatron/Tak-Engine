package io.joshatron.tak.engine.exception;

public class TakEngineException extends Exception {

    private TakEngineErrorCode code;

    public TakEngineException(TakEngineErrorCode code) {
        super("The tak engine encountered an exception of type: " + code.name());
        this.code = code;
    }

    public TakEngineErrorCode getCode() {
        return code;
    }
}
