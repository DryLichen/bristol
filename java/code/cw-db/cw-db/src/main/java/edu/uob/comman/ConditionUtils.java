package edu.uob.comman;

import edu.uob.command.Condition;
import edu.uob.exception.DBException;
import edu.uob.table.Relation;
import edu.uob.table.Tuple;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ConditionUtils {
    /**
     * recursively traverse the condition
     * @return a set containing the primary keys of selected tuples
     */
    public HashSet<Integer> getTupleIds(Condition condition, Relation relation) throws DBException {
        List<Condition> conditions = condition.getConditions();
        // return result set when it's a basic case
        if (conditions.size() == 0) {
            return getIdsByComparator(condition, relation);
        }

        // iterate the condition list in condition
        HashSet<Integer> idSet = getTupleIds(conditions.get(0), relation);
        for (int i = 0; i < conditions.size() - 1; i++) {
            HashSet<Integer> idSet2 = getTupleIds(conditions.get(i + 1), relation);
            if ("AND".equalsIgnoreCase(condition.getOperators().get(i))) {
                idSet.retainAll(idSet2);
            } else {
                idSet.addAll(idSet2);
            }
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByComparator(Condition condition, Relation relation) throws DBException {
        HashSet<Integer> idSet = new HashSet<>();
        String attribute = condition.getAttribute();
        String comparator = condition.getComparator();
        String value = condition.getValue();

        // get the index of the target attribute
        LinkedList<String> attributes = relation.getAttributes();
        int index = Utils.getAttributeIndex(attributes, attribute);

        if ("LIKE".equalsIgnoreCase(comparator)) {
            idSet = getIdsByLike(relation, index, value);
        } else if (">".equals(comparator)) {
            idSet = getIdsByLarger(relation, index, value);
        } else if ("<".equals(comparator)) {
            idSet = getIdsByLess(relation, index, value);
        } else if (">=".equals(comparator)) {
            HashSet<Integer> idsByEqual = getIdsByEqual(relation, index, value);
            HashSet<Integer> idsByLarger = getIdsByLarger(relation, index, value);
            idSet.addAll(idsByEqual);
            idSet.addAll(idsByLarger);
        } else if ("<=".equals(comparator)) {
            HashSet<Integer> idsByEqual = getIdsByEqual(relation, index, value);
            HashSet<Integer> idsByLess = getIdsByLess(relation, index, value);
            idSet.addAll(idsByEqual);
            idSet.addAll(idsByLess);
        } else if ("==".equals(comparator)) {
            idSet = getIdsByEqual(relation, index, value);
        } else if ("!=".equals(comparator)) {
            idSet = getIdsByNotEqual(relation, index, value);
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByLike(Relation relation, int index, String value) {
        HashSet<Integer> idSet = new HashSet<>();

        for (Tuple tuple : relation.getTuples()) {
            if (index == 0) {
                // compare ID
                if (Utils.isLike(value, tuple.getPrimaryId().toString())) {
                    idSet.add(tuple.getPrimaryId());
                }
            } else {
                // compare other attributes
                if (Utils.isLike(value, tuple.getData().get(index - 1))) {
                    idSet.add(tuple.getPrimaryId());
                }
            }
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByLarger(Relation relation, int index, String value) {
        HashSet<Integer> idSet = new HashSet<>();

        for (Tuple tuple : relation.getTuples()) {
            if (index == 0) {
                // compare ID
                if(Utils.compareValue(value, tuple.getPrimaryId().toString()) == 1) {
                    idSet.add(tuple.getPrimaryId());
                }
            } else {
                // compare other attributes
                if (Utils.compareValue(value, tuple.getData().get(index - 1)) == 1) {
                    idSet.add(tuple.getPrimaryId());
                }
            }
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByLess(Relation relation, int index, String value) {
        HashSet<Integer> idSet = new HashSet<>();

        for (Tuple tuple : relation.getTuples()) {
            if (index == 0) {
                // compare ID
                if(Utils.compareValue(value, tuple.getPrimaryId().toString()) == -1) {
                    idSet.add(tuple.getPrimaryId());
                }
            } else {
                // compare other attributes
                if (Utils.compareValue(value, tuple.getData().get(index - 1)) == -1) {
                    idSet.add(tuple.getPrimaryId());
                }
            }
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByEqual(Relation relation, int index, String value) {
        HashSet<Integer> idSet = new HashSet<>();

        for (Tuple tuple : relation.getTuples()) {
            if (index == 0) {
                // compare ID
                if(Utils.isEqual(value, tuple.getPrimaryId().toString())) {
                    idSet.add(tuple.getPrimaryId());
                }
            } else {
                // compare other attributes
                if (Utils.isEqual(value, tuple.getData().get(index - 1))) {
                    idSet.add(tuple.getPrimaryId());
                }
            }
        }

        return idSet;
    }

    private HashSet<Integer> getIdsByNotEqual(Relation relation, int index, String value) {
        HashSet<Integer> idSet = new HashSet<>();

        for (Tuple tuple : relation.getTuples()) {
            if (index == 0) {
                // compare ID
                if(!Utils.isEqual(value, tuple.getPrimaryId().toString())) {
                    idSet.add(tuple.getPrimaryId());
                }
            } else {
                // compare other attributes
                if (!Utils.isEqual(value, tuple.getData().get(index - 1))) {
                    idSet.add(tuple.getPrimaryId());
                }
            }
        }

        return idSet;
    }
}
