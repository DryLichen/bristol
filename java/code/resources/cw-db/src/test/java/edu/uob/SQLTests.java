package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {
    String messageSuccessOK = "A valid query was made, however an [OK] tag was not returned";
    String messageSuccessERROR = "A valid query was made, however an [ERROR] tag was returned";
    String messageFailOK = "A invalid query was made, however an [OK] tag was returned";
    String messageFailERROR = "A invalid query was made, however an [ERROR] tag was not returned";

    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    public void setupTable() {
        sendCommandToServer("create database test;");
        sendCommandToServer("use test;");
        sendCommandToServer("create table people;");
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testCreate() {
        String response = sendCommandToServer("Create dataBASE test;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        sendCommandToServer("use test;");
        response = sendCommandToServer("Create taBle table1;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        response = sendCommandToServer("Create taBle table2(name, email);");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        sendCommandToServer("drop database test;");
    }

    @Test
    public void testFailCreate() {
        // test for wrong grammar
        String response = sendCommandToServer("create databae test;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // test for create two databases with same name
        sendCommandToServer("Create dataBASE test;");
        response = sendCommandToServer("Create dataBASE test;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // test for create table without specify database
        response = sendCommandToServer("Create table table2;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        sendCommandToServer("drop database test;");
    }

    @Test
    public void testUse() {
        sendCommandToServer("Create database test;");
        String response = sendCommandToServer("Use test;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        response = sendCommandToServer("drop database test;");
        assertTrue(response.contains("OK"), messageSuccessOK);
    }

    @Test
    public void testFailUse() {
        // use nonexistent database
        String response = sendCommandToServer("Use test;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // grammar error
        sendCommandToServer("create database test;");
        response = sendCommandToServer("use a test;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        response = sendCommandToServer("drop database test;");
        assertTrue(response.contains("OK"), messageSuccessOK);
    }

    @Test
    public void testDrop() {
        setupTable();
        String response = sendCommandToServer("drop table people;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);
        response = sendCommandToServer("Drop Database test;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);
    }

    @Test
    public void testFailDrop() {
        // drop nonexistent database
        String response = sendCommandToServer("DROP database test;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // drop nonexistent table
        setupTable();
        response = sendCommandToServer("drop table tt;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // wrong grammar
        response = sendCommandToServer("drop table +23;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        sendCommandToServer("drop database TEST;");
    }

    @Test
    public void testAlter() {
        setupTable();
        sendCommandToServer("alter table people add name;");
        String response = sendCommandToServer("alter table people add email;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        response = sendCommandToServer("alter table people drop EMAIL;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);
        response = sendCommandToServer("alter table people drop Name;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertFalse(response.contains("[ERROR]"), messageSuccessERROR);

        sendCommandToServer("drop database test;");
    }

    @Test
    public void testFailAlter() {
        // grammar error
        String response = sendCommandToServer("alter table tableName set ;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // add id
        setupTable();
        response = sendCommandToServer("alter table people add id;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // add column which is already existent
        sendCommandToServer("alter table people add Name;");
        response = sendCommandToServer("alter table people add name;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        // drop nonexistent
        response = sendCommandToServer("alter table people drop email;");
        assertTrue(response.contains("[ERROR]"), messageFailERROR);
        assertFalse(response.contains("[OK]"), messageFailOK);

        sendCommandToServer("drop database test;");
    }

    /**
     * test for update, delete, insert, join and select
     */
    @Test
    public void testComprehensive() {
        setupTable();
        sendCommandToServer("CREATE TABLE marks (name, age, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve',20, +65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave',20, 55.3, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob',21, 35.0, false);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive',22, 20.2, FALSE);");

        sendCommandToServer("CREATE TABLE coursework (task, submission, age);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3, 20);");
        sendCommandToServer("INSERT INTO coursework VALUES ('DB', 1, 21);");
        sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 4, 20);");
        sendCommandToServer("INSERT INTO coursework VALUES ('STAG', 2, 20);");

        // basic select
        String response = sendCommandToServer("select * from marks;");
        assertTrue(response.contains("[OK]"), messageSuccessOK);
        assertTrue(response.contains("+65"), "Fail to add +65 in table marks");

        // alter table to add attribute
        response = sendCommandToServer("alter table marks add email;");
        assertFalse(response.contains("NULL"), "Fail to add NULL in new column");

        // join tables
        response = sendCommandToServer("join marks and coursework on age and age;");
        assertTrue(response.contains("coursework.task"), "fail to join tables");
        assertFalse(response.contains("Clive"), "Join extra data");

        // select some attributes in condition
        response = sendCommandToServer("select mark, id, name from marks where name <= 'Bob';");
        assertTrue(response.contains("Bob"), "select bob fail");
        assertFalse(response.contains("Dave"), "Dave shouldn't be select");

        // update attributes
        sendCommandToServer("update marks set name = 'Sarah' where (name LIKE 'e') and age >= 22;");
        response = sendCommandToServer("select * from marks;");
        assertTrue(response.contains("Sarah"), "Fail to update Clive to Sarah");
        assertFalse(response.contains("Clive"), "Fail to update Clive to Sarah");

        // delete attributes
        sendCommandToServer("delete from marks where pass == true AND (name liKe 'e' oR age == 22) OR name != 'Sarah';");
        response = sendCommandToServer("select * from marks;");
        System.out.println(response);
        assertFalse(response.contains("Bob"), "Fail to delete expected tuples");
        assertTrue(response.contains("Sarah"), "Fail to delete expected tuples");
        assertFalse(response.contains("Steve"), "Fail to delete expected tuples");
        assertFalse(response.contains("Dave"), "Fail to delete expected tuples");

        sendCommandToServer("drop database test;");
    }

}
