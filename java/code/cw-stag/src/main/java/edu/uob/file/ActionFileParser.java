package edu.uob.file;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * parse actions file and store data into database
 */
public class ActionFileParser {
    private File actionsFile;
    private ActionData actionData;

    public ActionFileParser(File actionsFile, ActionData actionData) {
        this.actionsFile = actionsFile;
        this.actionData = actionData;
    }

    /**
     * parse action file and store data into action database
     */
    public void parseActions() throws STAGException {
        // get all the action nodes
        NodeList actionNodes = getActionNodes();

        // parse action nodes and store them into action database
        // only the odd items are actions
        for (int i = 1; i < actionNodes.getLength(); i+=2) {
            GameAction gameAction = new GameAction();
            Element action = (Element) actionNodes.item(i);

            // add elements into gameAction instance
            HashSet<String> triggers = getActionElements(action, "triggers", "keyphrase");
            gameAction.getTriggerSet().addAll(triggers);
            HashSet<String> subjects = getActionElements(action, "subjects", "entity");
            gameAction.getSubjectSet().addAll(subjects);
            HashSet<String> consumed = getActionElements(action, "consumed", "entity");
            gameAction.getConsumeSet().addAll(consumed);
            HashSet<String> produced = getActionElements(action, "produced", "entity");
            gameAction.getProduceSet().addAll(produced);

            // map triggers to gameAction set
            mapTriggerAction(triggers, gameAction);
        }
    }

    /**
     * @return action node list
     */
    private NodeList getActionNodes() throws STAGException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();
            NodeList actionNodes = root.getChildNodes();

            return actionNodes;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new STAGException(Response.FAIL_PARSE_ACTION_NODES);
        }
    }

    /**
     * @return hashSet containing one type of element of an action
     */
    private HashSet<String> getActionElements(Element action, String elementTag, String subTag) {
        HashSet<String> elementSet = new HashSet<>();
        Element subjects = (Element) action.getElementsByTagName(elementTag).item(0);
        NodeList subjectNodes = subjects.getElementsByTagName(subTag);
        for (int j = 0; j < subjectNodes.getLength(); j++) {
            elementSet.add(subjectNodes.item(j).getTextContent());
        }
        return elementSet;
    }

    /**
     * map triggers and set of actions
     */
    private void mapTriggerAction(HashSet<String> triggers,GameAction gameAction) {
        HashMap<String, HashSet<GameAction>> actionMap = actionData.getActionMap();
        // if trigger was added to the map, add gameAction to existent set
        // otherwise create a new key-value and add gameAction to new set
        for (String trigger : triggers) {
            if (actionMap.containsKey(trigger)) {
                actionMap.get(trigger).add(gameAction);
            } else {
                HashSet<GameAction> actionSet = new HashSet<>();
                actionSet.add(gameAction);
                actionMap.put(trigger, actionSet);
            }
        }
    }
}
