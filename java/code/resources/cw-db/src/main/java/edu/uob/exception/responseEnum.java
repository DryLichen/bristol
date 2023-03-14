package edu.uob.exception;

public enum responseEnum {
    SUCCESS(0, "[OK]"),
    ERROR(-1, "[ERROR]"),

    FAIL_TO_FIND_TABLE(1, "1"),
    FAIL_TO_CLOSE_FILE(2,"2"),
    FAIL_TO_READ_FILE(3," 3")
    ;


    private Integer code;
    private String message;

    responseEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
