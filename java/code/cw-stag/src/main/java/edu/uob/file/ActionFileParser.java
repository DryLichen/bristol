package edu.uob.file;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
import edu.uob.entity.GameEntity;
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
import java.util.Set;

/**
 * parse actions file and store data into database
 */
public class ActionFileParser {
    private File actionsFile;
    private ActionData actionData;
    private EntityData entityData;

    public ActionFileParser(File actionsFile, ActionData actionData, EntityData entityData) {
        this.actionsFile = actionsFile;
        this.actionData = actionData;
        this.entityData = entityData;
    }

    /**
     * parse action file and store data into action database
     */
    public void parseActions() throws STAGException {
        // get all the action nodes
        NodeList actionNodes = getActionNodes();

        // parse action nodes and store them into action database
        // only the odd items are actions
        for (int i = 1; i < actionNodes.getLength(); i += 2) {
            GameAction gameAction = new GameAction();
            Element action = (Element) actionNodes.item(i);

            // add triggers into gameAction instance
            HashSet<String> triggers = getActionElements(action, "triggers", "keyphrase");
            gameAction.getTriggerSet().addAll(triggers);
            // add subjects into gameAction instance
            HashSet<String> subjects = getActionElements(action, "subjects", "entity");
            for (String subject : subjects) {
                gameAction.getSubjectSet().add(entityData.getEntityByName(subject));
            }
            // add consumed entities into gameAction instance
            HashSet<String> consumed = getActionElements(action, "consumed", "entity");
            for (String consume : consumed) {
                gameAction.getConsumeSet().add(entityData.getEntityByName(consume));
            }
            // add produced entities into gameAction instance
            HashSet<String> produced = getActionElements(action, "produced", "entity");
            for (String produce : produced) {
                gameAction.getProduceSet().add(entityData.getEntityByName(produce));
            }

            // add narration into gameAction instance
            String narration = action.getElementsByTagName("narration").item(0).getTextContent();
            gameAction.setNarration(narration);

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
