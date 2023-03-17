package edu.uob.IO;

import edu.uob.table.Attribute;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

public class FileIO {
    private final String suffix = ".tab";

    public File getDatabase(String root, String dBName) {
        File file = new File(root + File.separator + dBName);
        return file;
    }

    public File getTable(String root, String dBName, String tableName) {
        File file = new File(getDatabase(root, dBName),File.separator + tableName + suffix);
        return file;
    }

    // read a table file and return a relation instance
    public Relation getRelation(File tableFile) {
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

}
