package edu.uob.exception;

public enum Response {
    LACK_TERMINATOR("[ERROR]: Semi colon missing at end of line"),
    NOT_COMMAND_TYPE("[ERROR]: Invalid command type: "),
    NOT_COMMA("[ERROR]: Expect a comma between attributes"),
    NOT_IDENTIFIER("[ERROR]: Expect an identifier"),
    NOT_TABLE_KEY("[ERROR]: Expect a TABLE key word"),
    NOT_DATABASE_KEY("[ERROR]: Expect a DATABASE key word"),
    NOT_ATTRIBUTE("[ERROR]: Invalid attribute name"),
    WRONG_PARAMETER_NUM("[ERROR]: Wrong number of parameters: "),
    NOT_ALTERATION("[ERROR]: Expect an alterationType"),
    NOT_LEFT_BRACKET("[ERROR]: Expect a left bracket"),
    NOT_RIGHT_BRACKET("[ERROR]: Expect a right bracket"),
    NOT_INTO_KEY("[ERROR]: Expect a INTO key word"),
    NOT_VALUES_KEY("[ERROR]: Expect a VALUE key word"),
    WRONG_ATTR_LIST("[ERROR]: Wrong format of attribute lists"),
    WRONG_VALUE_LIST("[ERROR]: Wrong format of value list"),
    NOT_VALUE("[ERROR]: Invalid value")
    ;


    Response(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }
}
