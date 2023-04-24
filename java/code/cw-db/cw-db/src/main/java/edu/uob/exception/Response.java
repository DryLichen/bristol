package edu.uob.exception;

public enum Response {
    LACK_TERMINATOR("[ERROR]: Semi colon missing at end of line"),
    NOT_COMMA("[ERROR]: Expect a comma between attributes"),
    NOT_RIGHT_BRACKET("[ERROR]: Expect a right bracket"),
    NOT_LEFT_BRACKET("[ERROR]: Expect a left bracket"),

    NOT_TABLE_KEY("[ERROR]: Expect a TABLE key word"),
    NOT_DATABASE_KEY("[ERROR]: Expect a DATABASE key word"),
    NOT_DATABASE_TABLE("[ERROR]: Expect a DATABASE or TABLE key word"),
    NOT_INTO_KEY("[ERROR]: Expect a INTO key word"),
    NOT_VALUES_KEY("[ERROR]: Expect a VALUES key word"),
    NOT_FROM_KEY("[ERROR]: Expect a FROM key word"),
    NOT_SET_KEY("[ERROR]: Expect a SET key word"),
    NOT_WHERE_KEY("[ERROR]: Expect a WHERE key word"),
    NOT_AND_KEY("[ERROR]: Expect a AND key word"),
    NOT_ON_KEY("[ERROR]: Expect a ON key word"),
    NOT_EQUAL_KEY("[ERROR]: Expect a = key word"),

    NOT_COMMAND_TYPE("[ERROR]: Invalid command type: "),
    NOT_IDENTIFIER("[ERROR]: Expect an identifier"),
    NOT_ATTRIBUTE("[ERROR]: Invalid attribute name"),
    NOT_ALTERATION("[ERROR]: Expect an alterationType"),
    NOT_VALUE("[ERROR]: Invalid value"),

    WRONG_PARAMETER_NUM("[ERROR]: Wrong number of parameters: "),
    WRONG_ATTR_LIST("[ERROR]: Wrong format of attribute lists"),
    WRONG_VALUE_LIST("[ERROR]: Wrong format of value list"),

    // mainly for use command
    DB_NOT_EXIST("[ERROR]: The database doesn't exist"),
    TABLE_NOT_EXIST("[ERROR]: The table doesn't exist"),

    // mainly for create command
    CREATE_EXIST_DB("[ERROR]: Fail to create an existing database"),
    CREATE_EXIST_TABLE("[ERROR]: Fail to create an existing table"),
    DB_NOT_SPECIFIED("[ERROR]: Please specify a database"),
    INITIATE_ID("[ERROR]: Id is handled by server automatically"),
    DUPLICATE_ATTR("[ERROR]: The attribute already exists"),

    // mainly for insert command
    NOT_COMPATIBLE_VALUES("[ERROR]: Too many (or too few) values for table"),
    EMPTY_TABLE("[ERROR]: Table is empty"),
    GENERATE_ID_FAIL("[ERROR]: Can not generate the next id"),

    // mainly for alter command
    FORBID_DROP_ID("[ERROR]: Can not drop id column"),
    ATTR_NOT_EXIST("[ERROR]: Attribute required doesn't exist"),

    //mainly for condition command
    WRONG_FORMAT_CONDITION("[ERROR]: Format of conditions is incorrect"),
    NOT_COMPARATOR("[ERROR]: a comparator must follow a attribute"),

    // mainly for update
    FORBID_MODIFY_ID("[ERROR]: Can't modify ID"),

    // error related to IO operations
    CREATE_TABLE_FAIL("[ERROR: Fail to create table file"),
    WRITE_TABLE_FAIL("[ERROR]: Fail to write into table file"),
    DROP_DB_FAIL("[ERROR]: Fail to delete the database directory"),
    DROP_TABLE_FAIL("[ERROR]: Fail to delete the table file"),
    READ_TABLE_FAIL("[ERROR]: Fail to read the table file"),
    ;


    Response(String message) {
        this.message = message;
    }

    private String message;

    public String getMessage() {
        return message;
    }
}
