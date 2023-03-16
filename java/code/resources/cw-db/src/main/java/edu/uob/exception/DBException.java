package edu.uob.exception;

import java.io.IOException;

public class DBException extends IOException {
    private String message;

    public DBException(String message) {
        this.message = message;
    }

    public DBException(Response response) {
        this.message = response.getMessage();
    }
}
