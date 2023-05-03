package edu.uob.exception;

import java.io.IOException;

/**
 * customised exception
 */
public class STAGException extends IOException {
    private static final long serialVersionUID = 123456789L;
    private String message;

    public STAGException(Response response) {
        this.message = response.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
