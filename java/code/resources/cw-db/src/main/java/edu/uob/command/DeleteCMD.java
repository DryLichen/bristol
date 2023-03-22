package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.IO.FileIO;
import edu.uob.comman.ConditionUtils;
import edu.uob.comman.Utils;
import edu.uob.exception.DBException;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.io.File;
import java.util.*;

public class DeleteCMD extends DBcmd {

    @Override
    public String query(DBServer s) throws DBException {
        FileIO fileIO = new FileIO();
        File tableFile = Utils.getTableFile(s, fileIO, getTableNames().get(0));

        // get relation from table file
        Relation relation = fileIO.getRelationFromFile(tableFile);

        // select tuples according to conditions
        Condition condition = this.getCondition();
        ConditionUtils conditionUtils = new ConditionUtils();
        HashSet<Integer> idSet = conditionUtils.getTupleIds(condition, relation);

        // delete tuples
        relation.deleteTuples(idSet);

        // write back to file
        fileIO.setRelationToFile(relation,
                fileIO.getDatabase(s.getStorageFolderPath(), s.getSpecifiedDb()));

        return "[OK]";
    }

}
