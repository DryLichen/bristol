package edu.uob.exception;

public enum Response {
    LACK_TERMINATOR("[ERROR]: Semi colon missing at end of line"),
    NOT_COMMA("[ERROR]: Expect a comma between attributes"),
    NOT_RIGHT_BRACKET("[ERROR]: Expect a right bracket"),
    NOT_LEFT_BRACKET("[ERROR]: Expect a left bracket"),

    NOT_TABLE_KEY("[ERROR]: Expect a TABLE key word"),
    NOT_DATABASE_KEY("[ERROR]: Expect a DATABASE key word"),
    NOT_INTO_KEY("[ERROR]: Expect a INTO key word"),
    NOT_VALUES_KEY("[ERROR]: Expect a VALUE key word"),
    NOT_FROM_KEY("[ERROR]: Expect a FROM key word"),
    NOT_SET_KEY("[ERROR]: Expect a SET key word"),

    NOT_COMMAND_TYPE("[ERROR]: Invalid command type: "),
    NOT_IDENTIFIER("[ERROR]: Expect an identifier"),
    NOT_ATTRIBUTE("[ERROR]: Invalid attribute name"),
    NOT_ALTERATION("[ERROR]: Expect an alterationType"),
    NOT_VALUE("[ERROR]: Invalid value"),

    WRONG_PARAMETER_NUM("[ERROR]: Wrong number of parameters: "),
    WRONG_ATTR_LIST("[ERROR]: Wrong format of attribute lists"),
    WRONG_VALUE_LIST("[ERROR]: Wrong format of value list"),

    FILE_NOT_EXISTS("[ERROR]: The databases folder doesn't exist")
    ;


    Response(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }
}
