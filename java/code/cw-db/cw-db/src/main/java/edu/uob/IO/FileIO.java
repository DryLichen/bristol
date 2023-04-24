package edu.uob.IO;

import edu.uob.exception.DBException;
import edu.uob.exception.Response;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
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

    /**
     * @return true if create the file successfully
     */
    public boolean createDatabase(String root, String dBName) {
        File file = new File(root + File.separator + dBName);
        return file.mkdir();
    }

    public boolean createTable(String root, String dBName, String tableName) throws IOException {
        File file = new File(root + File.separator + dBName +
                File.separator + tableName + suffix);
        return file.createNewFile();
    }

    public BufferedWriter getBufWriter(File file, boolean append) throws IOException {
        FileWriter fw = new FileWriter(file, append);
        BufferedWriter bw = new BufferedWriter(fw);
        return bw;
    }

    public BufferedReader getBufReader(File file) throws FileNotFoundException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        return br;
    }

    public void deleteFile(File file) throws IOException {
        Path path = file.toPath();
        if (file.isDirectory()) {
            // traverse the file tree and delete them all
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            Files.delete(file.toPath());
        }
    }

    /**
     * read a table file and return a relation instance
     * should not convert anything to lowercase
     */
    public Relation getRelationFromFile(File tableFile) throws DBException {
        // initiate relation instance with tablePath without suffix
        Relation relation = new Relation();
        relation.setName(tableFile.getName().split("\\.")[0]);

        try (BufferedReader bufReader = getBufReader(tableFile)) {
            // 1. fill attributes, but don't add data in it
            // no attribute case
            String record = bufReader.readLine();
            if (record == null || record.isEmpty()) {
                return relation;
            }
            LinkedList<String> attributeList = relation.getAttributes();
            String[] attributes = record.split("\t");
            for (int i = 0; i < attributes.length; i++) {
                attributeList.add(attributes[i]);
            }
            relation.setAttributes(attributeList);

            // 2. fill tuples
            LinkedList<Tuple> tupleList = relation.getTuples();
            record = bufReader.readLine();
            while (record != null && !"".equals(record)) {
                Tuple tuple = new Tuple();
                String[] words = record.split("\t");
                tuple.setPrimaryId(Integer.valueOf(words[0]));
                // when there is data in a tuple except primary key
                if (words.length > 1) {
                    LinkedList<String> tupleData = new LinkedList<>(Arrays.asList(words));
                    tupleData.remove(0);
                    tuple.setData(tupleData);
                }
                tupleList.add(tuple);
                record = bufReader.readLine();
            }

            return relation;
        } catch (FileNotFoundException e) {
            throw new DBException(Response.TABLE_NOT_EXIST);
        } catch (IOException e) {
            throw new DBException(Response.READ_TABLE_FAIL);
        }
    }

    /**
     * write a relation instance back to table file
     * convert only table name to lowercase
     */
    public void setRelationToFile(Relation relation, File database) throws DBException {
        // get table file
        File tableFile = new File(database, relation.getName().toLowerCase() + suffix);

        try (BufferedWriter bf = getBufWriter(tableFile, false)) {
            LinkedList<String> attributeList = relation.getAttributes();
            // write id and attributes
            for (int i = 0; i < attributeList.size(); i++) {
                // for last item, don't write tab but write line end
                if (i == attributeList.size() - 1) {
                    bf.write(attributeList.get(i));
                    bf.write("\r\n");
                    break;
                }
                bf.write(attributeList.get(i));
                bf.write("\t");
            }

            // no data case
            LinkedList<Tuple> tuples = relation.getTuples();
            // write tuples (aka data)
            for (int i = 0; i < tuples.size(); i++) {
                Tuple tuple = tuples.get(i);
                // write primary keys
                bf.write(tuple.getPrimaryId().toString());
                // write data
                for (int j = 0; j < tuple.getData().size(); j++) {
                    bf.write("\t");
                    if (j == tuple.getData().size() - 1) {
                        bf.write(tuple.getData().get(j));
                        break;
                    }
                    bf.write(tuple.getData().get(j));
                }
                bf.write("\r\n");
            }

        } catch (IOException e) {
            throw new DBException(Response.WRITE_TABLE_FAIL);
        }
    }
}
