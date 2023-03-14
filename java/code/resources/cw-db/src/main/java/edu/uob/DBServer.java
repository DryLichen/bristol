package edu.uob;

import edu.uob.table.Attribute;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;

/** This class implements the DB server. */
public class DBServer {
    private static final char END_OF_TRANSMISSION = 4;
    private String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    private File getDatabase(String dbName) {
        File file = new File(storageFolderPath + File.separator + dbName);
        return file;
    }

    private File getTable(File db, String tableName) {
        File file = new File(db, File.separator + tableName);
        return file;
    }

    // read a table file and return a relation instance
    private Relation getRelation(File tableFile) {
        Relation relation = new Relation();
        relation.setName(tableFile.getPath());
        FileReader fr = null;
        BufferedReader bf = null;

        try {
            fr = new FileReader(tableFile);
            bf = new BufferedReader(fr);
            String record = null;
            try {
                record = bf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (record == null) {
                return relation;
            }

            // fill attributes
            LinkedList<Attribute> attributeList = new LinkedList<>();
            String[] attributes = record.split("\t");
            for (int i = 0; i < attributes.length; i++) {
                Attribute attribute = new Attribute();
                attribute.setName(attributes[i]);
                attributeList.add(attribute);
            }
            relation.setAttributes(attributeList);

            // fill tuples
            try {
                record = bf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LinkedList<Tuple> tupleList = new LinkedList<>();

            while (record != null) {
                Tuple tuple = new Tuple();
                String[] words = record.split("\t");
                tuple.setPrimaryId(Integer.valueOf(words[0]));
                // check whether the record is deleted
                if (words.length != 1) {
                    LinkedList<String> tupleData = new LinkedList<>(Arrays.asList(words));
                    tupleData.remove(0);
                    tuple.setData(tupleData);
                }
                tupleList.add(tuple);

                try {
                    record = bf.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            relation.setTuples(tupleList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return relation;
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here

        return "";
    }



























    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
