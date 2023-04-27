package edu.uob.exception;

public enum Response {
    FILE_NOT_FOUND("Can't open file because it's nonexistent"),
    FAIL_PARSE_DOT("ParseException was thrown when attempting to read basic entities file"),
    FAIL_TO_REFLECT("Fail when using reflect to interpret entities for location")
    ;


    private String message;

    private Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
