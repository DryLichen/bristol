package edu.uob.exception;

public enum Response {
    // file parsing related exceptions
    FILE_NOT_FOUND("Can't open file because it's nonexistent"),
    FAIL_PARSE_DOT("ParseException was thrown when attempting to read basic entities file"),
    FAIL_TO_REFLECT("Fail when using reflect to parse sub entities for location"),
    FAIL_PARSE_ACTION_NODES("Fail to get action node list from action file"),

    // tokenizer exceptions
    WRONG_PLAYER_NAME("Format of player name is wrong"),
    LACK_COMMAND("Empty command content"),
    ;


    private String message;

    private Response(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
