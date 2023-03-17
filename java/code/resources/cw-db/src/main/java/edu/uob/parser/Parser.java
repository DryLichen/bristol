package edu.uob.parser;

import edu.uob.command.*;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private String command;
    private Tokenizer tokenizer;
    private DBcmd dBcmd;

    /**
     * @return parse tokens and initiate DBcmd instances
     */
    public DBcmd parse() throws DBException {
        // initiate tokenizer
        tokenizer = new Tokenizer(command);
        tokenizer.setup();
        ArrayList<Token> tokenList = tokenizer.getTokenList();

        // check the terminator ;
        int size = tokenList.size();
        if (size == 0) {
            throw new DBException(Response.WRONG_PARAMETER_NUM);
        }
        Assert.equalType(TokenType.COMMAND_TYPE, tokenList.get(0), Response.NOT_COMMAND_TYPE);
        Assert.equalValue(";", tokenList.get(size - 1), Response.LACK_TERMINATOR);

        // get the command type and create subclass instance of DBcmd
        return getSpecificCmd(tokenList);
    }

    private DBcmd getSpecificCmd(ArrayList<Token> tokens) throws DBException {
        String commandType = tokens.get(0).getTokenValue();
        if ("USE".equalsIgnoreCase(commandType)) {
            return getUseCmd(tokens);
        }
        if ("CREATE".equalsIgnoreCase(commandType)) {
            return getCreateCmd(tokens);
        }
        if ("DROP".equalsIgnoreCase(commandType)) {
            return getDropCmd(tokens);
        }
        if ("ALTER".equalsIgnoreCase(commandType)) {
            return getAlterCmd(tokens);
        }
        if ("INSERT".equalsIgnoreCase(commandType)) {
            return getInsertCmd(tokens);
        }
        if ("SELECT".equalsIgnoreCase(commandType)) {
            return getSelectCmd(tokens);
        }
//        if ("UPDATE".equalsIgnoreCase(commandType)) {
//            return getUpdateCmd(tokens);
//        }
        if ("DELETE".equalsIgnoreCase(commandType)) {
            return getDeleteCmd(tokens);
        }
        if ("JOIN".equalsIgnoreCase(commandType)) {
            return getJoinCmd(tokens);
        }

        return null;
    }

    private UseCMD getUseCmd(List<Token> tokens) throws DBException {
        UseCMD useCMD = new UseCMD();
        useCMD.setCommandType("USE");
        Assert.correctParamNum(3, tokens.size());

        Assert.equalType(TokenType.IDENTIFIER, tokens.get(1), Response.NOT_IDENTIFIER);
        useCMD.setDBname(tokens.get(1).getTokenValue());

        return useCMD;
    }

    private CreateCMD getCreateCmd(List<Token> tokens) throws DBException {
        CreateCMD createCMD = new CreateCMD();
        createCMD.setCommandType("CREATE");
        int size = tokens.size();
        if (size < 4) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        String keyWord = tokens.get(1).getTokenValue();
        if ("DATABASE".equalsIgnoreCase(keyWord)) {
            Assert.correctParamNum(4, size);
            Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
            createCMD.setDBname(tokens.get(2).getTokenValue());
        } else if ("TABLE".equalsIgnoreCase(keyWord)) {
            createCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
            if (size >= 7) {
                setAttributes(createCMD, tokens);
            } else {
                Assert.correctParamNum(4, size);
            }
        } else {
            throw new DBException(Response.NOT_TABLE_KEY.getMessage() +
                    " or " + Response.NOT_DATABASE_KEY.getMessage());
        }

        return createCMD;
    }

    /**
     * helper to set attributes in a create command
     */
    private void setAttributes(CreateCMD createCMD, List<Token> tokens) throws DBException {
        int size = tokens.size();
        Assert.equalValue("(", tokens.get(3), Response.NOT_LEFT_BRACKET);
        Assert.equalValue(")", tokens.get(size - 2), Response.NOT_RIGHT_BRACKET);

        // the last item should not be a comma, so the size is an odd number
        if (size % 2 == 0) {
            throw new DBException(Response.WRONG_ATTR_LIST);
        }

        ArrayList<String> columns = new ArrayList<>();
        for (int i = 4; i < size - 2; i++) {
            // check if odd indexes refer to comma
            if (i % 2 != 0) {
                Assert.equalValue(",", tokens.get(i), Response.NOT_COMMA);
            } else {
                // check if the attribute names are valid
                Assert.isAttribute(tokens.get(i));
                columns.add(tokens.get(i).getTokenValue());
            }
        }
        createCMD.setColumnNames(columns);
    }

    private DropCMD getDropCmd(List<Token> tokens) throws DBException {
        DropCMD dropCMD = new DropCMD();
        dropCMD.setCommandType("DROP");
        int size = tokens.size();
        Assert.correctParamNum(4, size);

        String keyWord = tokens.get(1).getTokenValue();
        if ("DATABASE".equalsIgnoreCase(keyWord)) {
            Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
            dropCMD.setDBname(tokens.get(2).getTokenValue());
        } else if ("TABLE".equalsIgnoreCase(keyWord)) {
            Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
            dropCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
        } else {
            throw new DBException(Response.NOT_TABLE_KEY.getMessage() +
                    " or " + Response.NOT_DATABASE_KEY.getMessage());
        }

        return dropCMD;
    }

    private AlterCMD getAlterCmd(List<Token> tokens) throws DBException {
        AlterCMD alterCMD = new AlterCMD();
        alterCMD.setCommandType("ALTER");
        int size = tokens.size();
        Assert.correctParamNum(6, size);

        Assert.equalValue("TABLE", tokens.get(1), Response.NOT_TABLE_KEY);
        Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
        Assert.isAlterationType(tokens.get(3));
        Assert.isAttribute(tokens.get(4));

        alterCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
        alterCMD.setAlterationType(tokens.get(3).getTokenValue());
        alterCMD.setColumnNames(new ArrayList<>(Arrays.asList(tokens.get(4).getTokenValue())));

        return alterCMD;
    }

    private InsertCMD getInsertCmd(List<Token> tokens) throws DBException {
        InsertCMD insertCMD = new InsertCMD();
        insertCMD.setCommandType("INSERT");
        int size = tokens.size();
        if (size < 8) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        Assert.equalValue("INTO", tokens.get(1), Response.NOT_INTO_KEY);
        Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
        Assert.equalValue("VALUE", tokens.get(3), Response.NOT_VALUES_KEY);
        Assert.equalValue("(", tokens.get(4), Response.NOT_LEFT_BRACKET);
        Assert.equalValue(")", tokens.get(size - 2), Response.NOT_RIGHT_BRACKET);

        insertCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
        setValueList(insertCMD, tokens);

        return insertCMD;
    }

    private void setValueList(InsertCMD insertCMD, List<Token> tokens) throws DBException {
        int size = tokens.size();
        // the last item should not be a comma, so the size is an even number
        if (size % 2 != 0) {
            throw new DBException(Response.WRONG_VALUE_LIST);
        }

        // check if the value list is valid
        ArrayList<String> values = new ArrayList<>();
        for (int i = 5; i < size - 2; i++) {
            // check if odd indexes refer to value
            if (i % 2 != 0) {
                Assert.isValue(tokens.get(i));
                values.add(tokens.get(i).getTokenValue());
            } else {
                // check if even indexes refer to comma
                Assert.equalValue(",", tokens.get(i), Response.NOT_COMMA);
            }
        }

        insertCMD.setValueList(values);
    }

    private SelectCMD getSelectCmd(List<Token> tokens) throws DBException {
        SelectCMD selectCMD = new SelectCMD();
        selectCMD.setCommandType("SELECT");
        int size = tokens.size();
        if (size < 5) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        // without conditions
//        if (tokens.contains())
        if (size == 5) {
            // not wild attribute list
            // if columnsNames is null, we select all the attributes
            if (!"*".equals(tokens.get(1))) {
                Assert.isAttribute(tokens.get(1));
                selectCMD.setColumnNames(new ArrayList<>(Arrays.asList(tokens.get(1).getTokenValue())));
            }
            Assert.equalValue("FROM", tokens.get(2), Response.NOT_FROM_KEY);
            Assert.equalType(TokenType.IDENTIFIER, tokens.get(3), Response.NOT_IDENTIFIER);

            selectCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(3).getTokenValue())));
        } else {

        }

        // with conditions


        return selectCMD;
    }

//    private UpdateCMD getUpdateCmd(List<Token> tokens) throws DBException {
//        UpdateCMD updateCMD = new UpdateCMD();
//        updateCMD.setCommandType("UPDATE");
//        int size = tokens.size();
////        if (size < ) {
////            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
////        }
//
//        Assert.equalType(TokenType.IDENTIFIER, tokens.get(1), Response.NOT_IDENTIFIER);
//        Assert.equalValue("SET", tokens.get(2), Response.NOT_SET_KEY);
//
//        setNameValues(updateCMD, tokens);
//
//        return updateCMD;
//    }

//    private int setNameValues(UpdateCMD updateCMD, List<Token> tokens) {
//        for (int i = 3; i < tokens.size(); i+=3) {
//            // every three items as a pair
//            tokens.get(i)
//        }
//    }

    private DeleteCMD getDeleteCmd(List<Token> tokens) throws DBException {
        DeleteCMD deleteCMD = new DeleteCMD();
        deleteCMD.setCommandType("DELETE");
        int size = tokens.size();
//        if (size < ) {
//            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
//        }

        return deleteCMD;
    }

    private JoinCMD getJoinCmd(List<Token> tokens) throws DBException {
        JoinCMD joinCMD = new JoinCMD();
        joinCMD.setCommandType("JOIN");
        int size = tokens.size();
//        if (size < ) {
//            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
//        }

        return joinCMD;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
