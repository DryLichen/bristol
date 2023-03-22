package edu.uob.parser;

import edu.uob.comman.Utils;
import edu.uob.command.*;
import edu.uob.exception.Assert;
import edu.uob.exception.DBException;
import edu.uob.exception.Response;

import java.util.*;

public class Parser {
    private String command;
    private Tokenizer tokenizer;

    public void setCommand(String command) {
        this.command = command;
    }

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
        if ("UPDATE".equalsIgnoreCase(commandType)) {
            return getUpdateCmd(tokens);
        }
        if ("DELETE".equalsIgnoreCase(commandType)) {
            return getDeleteCmd(tokens);
        }
        if ("JOIN".equalsIgnoreCase(commandType)) {
            return getJoinCmd(tokens);
        }

        return null;
    }

    /**
     * @return a useCMD with database name
     */
    private UseCMD getUseCmd(List<Token> tokens) throws DBException {
        UseCMD useCMD = new UseCMD();
        useCMD.setCommandType("USE");
        Assert.correctParamNum(3, tokens.size());

        Assert.equalType(TokenType.IDENTIFIER, tokens.get(1), Response.NOT_IDENTIFIER);
        useCMD.setDBname(tokens.get(1).getTokenValue());

        return useCMD;
    }

    /**
     * @return a createCMD with database name or a table name.
     * when creating a table, attributes can be initiated or not.
     */
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
                getCreateAttributes(createCMD, tokens);
            } else {
                Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
                Assert.correctParamNum(4, size);
            }
        } else {
            throw new DBException(Response.NOT_DATABASE_TABLE);
        }

        return createCMD;
    }

    /**
     * helper to set attributes in a create command
     */
    private void getCreateAttributes(CreateCMD createCMD, List<Token> tokens) throws DBException {
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

        // check duplicate column names
        HashSet<Object> set = new HashSet<>();
        set.add("id");
        for (String column : columns) {
            Assert.isTrue(!"id".equalsIgnoreCase(column), Response.INITIATE_ID);
            Assert.isTrue(set.add(column.toLowerCase()), Response.DUPLICATE_ATTR);
        }

        createCMD.setColumnNames(columns);
    }

    /**
     * @return dropCMD with database name or a table name
     */
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
            throw new DBException(Response.NOT_DATABASE_TABLE);
        }

        return dropCMD;
    }

    /**
     * @return an alterCMD with table name, alter type and column name
     */
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

    /**
     * @return insertCMD with table name and a list of values
     */
    private InsertCMD getInsertCmd(List<Token> tokens) throws DBException {
        InsertCMD insertCMD = new InsertCMD();
        insertCMD.setCommandType("INSERT");
        int size = tokens.size();
        if (size < 8) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        Assert.equalValue("INTO", tokens.get(1), Response.NOT_INTO_KEY);
        Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
        Assert.equalValue("VALUES", tokens.get(3), Response.NOT_VALUES_KEY);
        Assert.equalValue("(", tokens.get(4), Response.NOT_LEFT_BRACKET);
        Assert.equalValue(")", tokens.get(size - 2), Response.NOT_RIGHT_BRACKET);

        insertCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
        getValueList(insertCMD, tokens);

        return insertCMD;
    }

    /**
     * @return value list for insert command
     */
    private void getValueList(InsertCMD insertCMD, List<Token> tokens) throws DBException {
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
                Token token = tokens.get(i);
                Assert.isValue(token);
                // delete the single quotes for string value
                values.add(Utils.getTokenValue(token));
            } else {
                // check if even indexes refer to comma
                Assert.equalValue(",", tokens.get(i), Response.NOT_COMMA);
            }
        }

        insertCMD.setValueList(values);
    }

    /**
     * @return a selectCMD with attributes, table name and conditions.
     * conditions can be null
     */
    private SelectCMD getSelectCmd(List<Token> tokens) throws DBException {
        SelectCMD selectCMD = new SelectCMD();
        selectCMD.setCommandType("SELECT");
        int size = tokens.size();
        if (size < 5) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        if (Utils.equalTokenType(TokenType.WILD_ATTRIBUTE, tokens.get(1))) {
            // 1. wild attribute list
            // 1.1. without condition
            Assert.equalValue("FROM", tokens.get(2), Response.NOT_FROM_KEY);
            Assert.equalType(TokenType.IDENTIFIER, tokens.get(3), Response.NOT_IDENTIFIER);
            selectCMD.setTableNames(Arrays.asList(tokens.get(3).getTokenValue()));

            // 1.2. with condition
            if (size > 5) {
                Assert.equalValue("WHERE", tokens.get(4), Response.NOT_WHERE_KEY);
                List<Token> normalConTokens = normalConTokens(tokens, 5);
                Condition condition = new Condition();
                getCondition(condition, normalConTokens);
                selectCMD.setCondition(condition);
            }

        } else {
            // 2. not wild attribute list
            if (Utils.equalTokenValue("FROM", tokens.get(size - 3))) {
                // 2.1. without condition
                int nextIndex = getSelectAttributes(selectCMD, tokens);
                Assert.isTrue(nextIndex + 3 == size, Response.WRONG_PARAMETER_NUM);
                Assert.equalValue("FROM", tokens.get(nextIndex), Response.NOT_FROM_KEY);
                Assert.equalType(TokenType.IDENTIFIER, tokens.get(nextIndex + 1), Response.NOT_IDENTIFIER);
                selectCMD.setTableNames(Arrays.asList(tokens.get(nextIndex + 1).getTokenValue()));
            } else {
                // 2.2. with condition
                int nextIndex = getSelectAttributes(selectCMD, tokens);
                Assert.isTrue(nextIndex + 6 < size, Response.WRONG_PARAMETER_NUM);
                Assert.equalValue("FROM", tokens.get(nextIndex), Response.NOT_FROM_KEY);
                Assert.equalType(TokenType.IDENTIFIER, tokens.get(nextIndex + 1), Response.NOT_IDENTIFIER);
                selectCMD.setTableNames(Arrays.asList(tokens.get(nextIndex + 1).getTokenValue()));
                Assert.equalValue("WHERE", tokens.get(nextIndex + 2), Response.NOT_WHERE_KEY);
                // set condition
                List<Token> normalConTokens = normalConTokens(tokens, nextIndex + 3);
                Condition condition = new Condition();
                getCondition(condition, normalConTokens);
                selectCMD.setCondition(condition);
            }
        }

        return selectCMD;
    }

    /**
     * get attributes in non-wild case for select command
     * @return index of the start of remaining tokens
     */
    private int getSelectAttributes(SelectCMD selectCMD, List<Token> tokens) throws DBException {
        int i = 1;
        List<String> columnNames = new ArrayList<>();
        while (!Utils.equalTokenValue("FROM", tokens.get(i))) {
            // even index must refer to comma
            if (i % 2 == 0) {
                Assert.equalValue(",", tokens.get(i), Response.NOT_COMMA);
            } else {
                Assert.isAttribute(tokens.get(i));
                columnNames.add(tokens.get(i).getTokenValue());
            }
            // if there is no FROM key word, throw exception
            Assert.isTrue(++i < tokens.size(), Response.WRONG_PARAMETER_NUM);
        }
        selectCMD.setColumnNames(columnNames);

        return i;
    }

    /**
     * @return a updateCMD with table name, nameValuePairs, and conditions
     */
    private UpdateCMD getUpdateCmd(List<Token> tokens) throws DBException {
        UpdateCMD updateCMD = new UpdateCMD();
        updateCMD.setCommandType("UPDATE");
        int size = tokens.size();
        if (size < 11) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        Assert.equalType(TokenType.IDENTIFIER, tokens.get(1), Response.NOT_IDENTIFIER);
        updateCMD.setTableNames(Arrays.asList(tokens.get(1).getTokenValue()));
        Assert.equalValue("SET", tokens.get(2), Response.NOT_SET_KEY);

        // set nameValue list
        int next = getNameValueList(updateCMD, tokens);
        Assert.equalValue("WHERE", tokens.get(next), Response.NOT_WHERE_KEY);

        // set condition
        List<Token> normalConTokens = normalConTokens(tokens, next + 1);
        Condition condition = new Condition();
        getCondition(condition, normalConTokens);
        updateCMD.setCondition(condition);

        return updateCMD;
    }

    /**
     * @return the index of the next token need to be parsed
     */
    private int getNameValueList(UpdateCMD updateCMD, List<Token> tokens) throws DBException {
        int index = 3;
        // get the index of WHERE key word
        int whereIndex = getTokenIndex("WHERE", tokens);
        List<String> columnNames = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        // get the time of loop
        int timeToLoop = getTimeToLoop(whereIndex);
        for (int i = 0; i < timeToLoop; i++) {
            Assert.isAttribute(tokens.get(index));
            columnNames.add(tokens.get(index++).getTokenValue());
            Assert.equalValue("=", tokens.get(index++), Response.NOT_EQUAL_KEY);
            Assert.isValue(tokens.get(index));
            valueList.add(tokens.get(index++).getTokenValue());
            if (index != whereIndex) {
                Assert.equalValue(",", tokens.get(index++), Response.NOT_COMMA);
            }
        }

        updateCMD.setColumnNames(columnNames);
        updateCMD.setValueList(valueList);
        return whereIndex;
    }

    private int getTokenIndex(String tokenValue, List<Token> tokens) throws DBException {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getTokenValue().equalsIgnoreCase(tokenValue)) {
                return i;
            }
        }

        throw new DBException(Response.WRONG_PARAMETER_NUM);
    }

    /**
     * @return time to loop to get name value list
     * @throws DBException if can't get a time
     */
    private int getTimeToLoop(int whereIndex) throws DBException {
        if ((whereIndex - 6) % 4 == 0) {
            return (whereIndex - 6) / 4 + 1;
        }

        throw new DBException(Response.WRONG_PARAMETER_NUM);
    }

    /**
     * @return a deleteCMD with table name and conditions
     */
    private DeleteCMD getDeleteCmd(List<Token> tokens) throws DBException {
        DeleteCMD deleteCMD = new DeleteCMD();
        deleteCMD.setCommandType("DELETE");
        int size = tokens.size();
        if (size < 8) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        Assert.equalValue("FROM", tokens.get(1), Response.NOT_FROM_KEY);
        Assert.equalType(TokenType.IDENTIFIER, tokens.get(2), Response.NOT_IDENTIFIER);
        deleteCMD.setTableNames(new ArrayList<>(Arrays.asList(tokens.get(2).getTokenValue())));
        Assert.equalValue("WHERE", tokens.get(3), Response.NOT_WHERE_KEY);

        // conditions
        List<Token> normalConTokens = normalConTokens(tokens, 4);
        Condition condition = new Condition();
        getCondition(condition, normalConTokens);
        deleteCMD.setCondition(condition);

        return deleteCMD;
    }

    /**
     * @return a joinCMD with table names and column names
     */
    private JoinCMD getJoinCmd(List<Token> tokens) throws DBException {
        JoinCMD joinCMD = new JoinCMD();
        joinCMD.setCommandType("JOIN");
        int size = tokens.size();
        if (size < 9) {
            throw new DBException(Response.WRONG_PARAMETER_NUM.getMessage() + size);
        }

        Assert.equalType(TokenType.IDENTIFIER, tokens.get(1), Response.NOT_IDENTIFIER);
        Assert.equalValue("AND", tokens.get(2), Response.NOT_AND_KEY);
        Assert.equalType(TokenType.IDENTIFIER, tokens.get(3), Response.NOT_IDENTIFIER);
        Assert.equalValue("ON", tokens.get(4), Response.NOT_ON_KEY);
        Assert.isAttribute(tokens.get(5));
        Assert.equalValue("AND", tokens.get(6), Response.NOT_AND_KEY);
        Assert.isAttribute(tokens.get(7));

        joinCMD.setTableNames(new ArrayList<>(
                Arrays.asList(tokens.get(1).getTokenValue(), tokens.get(3).getTokenValue())
        ));
        joinCMD.setColumnNames(new ArrayList<>(
                Arrays.asList(tokens.get(5).getTokenValue(), tokens.get(7).getTokenValue())
        ));

        return joinCMD;
    }

    /**
     * @return normalized condition tokens
     */
    private List<Token> normalConTokens(List<Token> tokens, int start) throws DBException {
        // create a new token list which only contains condition tokens
        List<Token> subTokens = tokens.subList(start, tokens.size() - 1);
        List<Token> conTokens = new ArrayList<>(subTokens);

        // check the number of brackets
        int leftCount = 0, rightCount = 0;
        for (int i = 0; i < conTokens.size(); i++) {
            if ("(".equals(conTokens.get(i))) {
                leftCount++;
            } else if (")".equals(conTokens.get(i))) {
                rightCount++;
            }
        }
        Assert.isTrue(leftCount == rightCount, Response.WRONG_FORMAT_CONDITION);

        // check the grammar of the condition tokens
        for (int i = 0; i < conTokens.size(); i++) {

            // token is attribute and not value
            // because the tokenizer can't classify number and attribute
            if (Utils.isAttribute(conTokens.get(i)) && !Utils.isValue(conTokens.get(i))) {
                // attribute name must be followed by a comparator and a value
                if (i + 2 >= conTokens.size()) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.equalType(TokenType.COMPARATOR, conTokens.get(i + 1),
                        Response.NOT_COMPARATOR);
                Assert.isValue(conTokens.get(i + 2));
                // if it's not the first item, a ( or an operator can be before it
                if (i - 1 >= 0) {
                    Assert.isTrue(Utils.equalTokenValue("(", conTokens.get(i - 1)) ||
                            Utils.equalTokenType(TokenType.OPERATOR, conTokens.get(i - 1)),
                            Response.WRONG_FORMAT_CONDITION);
                }

            } else if (conTokens.get(i).getTokenType().equals(TokenType.COMPARATOR)) {
                // comparator must between a name and a value
                if (i - 1 < 0 || i + 1 >= conTokens.size()) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.isAttribute(conTokens.get(i - 1));
                Assert.isValue(conTokens.get(i + 1));

            } else if (Utils.isValue(conTokens.get(i))) {
                // a value must be after a name and a comparator
                if (i - 2 < 0) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.isAttribute(conTokens.get(i - 2));
                Assert.equalType(TokenType.COMPARATOR, conTokens.get(i - 1),
                        Response.NOT_COMPARATOR);

                // if it's not the last item, a ) or an operator can be after it
                if (i + 1 < conTokens.size()) {
                    Assert.isTrue(Utils.equalTokenValue(")", conTokens.get(i + 1)) ||
                            Utils.equalTokenType(TokenType.OPERATOR, conTokens.get(i + 1)),
                            Response.WRONG_FORMAT_CONDITION);
                }

            } else if (conTokens.get(i).getTokenType().equals(TokenType.OPERATOR)) {
                // check the location of bool operators
                // they must between two valid condition or brackets
                if (i + 1 >= conTokens.size() || i - 1 < 0) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.isTrue(Utils.equalTokenValue(")", conTokens.get(i - 1)) ||
                        Utils.isValue(conTokens.get(i - 1)),
                        Response.WRONG_FORMAT_CONDITION);
                Assert.isTrue(Utils.equalTokenValue("(", conTokens.get(i + 1)) ||
                       Utils.isAttribute(conTokens.get(i + 1)),
                        Response.WRONG_FORMAT_CONDITION);

            } else if ("(".equals(conTokens.get(i).getTokenValue())) {
                // a ( can only be after a ( or an operator
                if (i - 1 >= 0) {
                    Assert.isTrue(Utils.equalTokenValue("(", conTokens.get(i - 1)) ||
                            Utils.equalTokenType(TokenType.OPERATOR, conTokens.get(i - 1)),
                            Response.WRONG_FORMAT_CONDITION);
                }

                // a ( can only be followed by a ( or an attribute
                if (i + 1 >= conTokens.size()) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.isTrue(Utils.equalTokenValue("(", conTokens.get(i + 1)) ||
                        Utils.isAttribute(conTokens.get(i + 1)),
                        Response.WRONG_FORMAT_CONDITION);

            } else if (")".equals(conTokens.get(i).getTokenValue())) {
                // a ) can only be after a ) or a value
                if (i - 1 < 0) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                Assert.isTrue(Utils.equalTokenValue(")", conTokens.get(i - 1)) ||
                        Utils.isValue(conTokens.get(i - 1)),
                        Response.WRONG_FORMAT_CONDITION);

                // a ) can only be followed by a ) or an operator
                if (i + 1 < conTokens.size()) {
                    Assert.isTrue(Utils.equalTokenValue(")", conTokens.get(i + 1)) ||
                            Utils.equalTokenType(TokenType.OPERATOR, conTokens.get(i + 1)),
                            Response.WRONG_FORMAT_CONDITION);
                }

            } else {
                // other type of tokens found in conditions
                throw new DBException(Response.WRONG_FORMAT_CONDITION);
            }
        }

        // delete extra brackets that only around a pure comparison expression
        for (int i = 0; i < conTokens.size(); i++) {
            if (Utils.equalTokenValue("(", conTokens.get(i))) {
                if (i + 4 >= conTokens.size()) {
                    throw new DBException(Response.WRONG_FORMAT_CONDITION);
                }
                // check whether the later three tokens form a comparison
                if (Utils.isAttribute(conTokens.get(i + 1)) &&
                        Utils.equalTokenValue(")", conTokens.get(i + 4))) {
                    conTokens.remove(i + 4);
                    conTokens.remove(i);
                }
            }
        }

        // delete meaningless brackets
        for (int i = 0; i < conTokens.size(); i++) {
            // delete brackets around brackets
            if (Utils.equalTokenValue("(", conTokens.get(i))) {
                // find the paired right bracket index
                int rightIndex = getRightIndex(i, conTokens);

                // delete brackets around the whole comparison expression
                if (i == 0 && rightIndex == conTokens.size() - 1) {
                    conTokens.remove(rightIndex);
                    conTokens.remove(i);
                } else {
                    // check if there are brackets around them
                    if (i - 1 >= 0 && rightIndex + 1 < conTokens.size()) {
                        if (Utils.equalTokenValue("(", conTokens.get(i - 1)) &&
                                Utils.equalTokenValue(")", conTokens.get(rightIndex + 1))) {
                            conTokens.remove(rightIndex + 1);
                            conTokens.remove(i - 1);
                        }
                    }
                }
            }
        }

        return conTokens;
    }

    /**
     * @return parse normalized condition tokens to get condition recursively
     */
    private void getCondition(Condition condition, List<Token> tokens) throws DBException {
        // basic case, fill and return
        if (tokens.size() == 3) {
            condition.setAttribute(tokens.get(0).getTokenValue());
            condition.setComparator(tokens.get(1).getTokenValue());
            condition.setValue(Utils.getTokenValue(tokens.get(2)));
            return;
        }

        // initiate condition list
        for (int i = 0; i < tokens.size(); ) {
            // when detect a left bracket, create new condition list
            // find the right bracket for the given left bracket
            if (Utils.equalTokenValue("(", tokens.get(i))) {
                int rightIndex = getRightIndex(i, tokens);
                // extract the tokens in brackets
                ArrayList<Token> newTokens = new ArrayList<>(tokens.subList(i + 1, rightIndex));
                Condition newCondition = new Condition();
                getCondition(newCondition, newTokens);
                condition.getConditions().add(newCondition);
                i = rightIndex + 1;
            } else if (tokens.get(i).getTokenType().equals(TokenType.OPERATOR)) {
                // fill the list for bool operators
                condition.getOperators().add(tokens.get(i).getTokenValue());
                i++;
            } else if (Utils.isAttribute(tokens.get(i)) && !Utils.isValue(tokens.get(i))) {
                // when detect attribute, fill basic three fields in condition
                Condition newCondition = new Condition();
                // extract the tokens in brackets
                ArrayList<Token> newTokens = new ArrayList<>(tokens.subList(i, i + 3));
                getCondition(newCondition, newTokens);
                condition.getConditions().add(newCondition);
                i += 3;
            }
        }
    }

    /**
     * @return the index of the paired right bracket index for a left bracket
     */
    private int getRightIndex(int leftIndex, List<Token> tokens) throws DBException {
        int count = 0;
        for (int i = leftIndex; i < tokens.size(); i++) {
            if (Utils.equalTokenValue("(", tokens.get(i))) {
                count++;
            } else if (Utils.equalTokenValue(")", tokens.get(i))) {
                count--;
            }

            if (count == 0) {
                return i;
            }
        }

        throw new DBException(Response.NOT_RIGHT_BRACKET);
    }
}
