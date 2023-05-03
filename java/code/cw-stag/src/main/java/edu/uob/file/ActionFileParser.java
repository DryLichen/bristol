package edu.uob.file;

import edu.uob.action.GameAction;
import edu.uob.database.ActionData;
import edu.uob.database.EntityData;
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

        // parse each action node and store it into action database
        for (int i = 1; i < actionNodes.getLength(); i += 2) {
            GameAction gameAction = new GameAction();
            Element action = (Element) actionNodes.item(i);

            // store triggers into gameAction instance
            HashSet<String> triggers = getActionElements(action, "triggers", "keyphrase");
            gameAction.getTriggerSet().addAll(triggers);

            // store subjects into gameAction instance
            HashSet<String> subjects = getActionElements(action, "subjects", "entity");
            for (String subject : subjects) {
                gameAction.getSubjectSet().add(entityData.getEntityByName(subject));
            }

            // store consumed entities into gameAction instance
            HashSet<String> consumedEntities = getActionElements(action, "consumed", "entity");
            storeConEntities(consumedEntities, gameAction);

            // store produced entities into gameAction instance
            HashSet<String> producedEntities = getActionElements(action, "produced", "entity");
            storeProEntities(producedEntities, gameAction);

            // store narration into gameAction instance
            String narration = action.getElementsByTagName("narration").item(0).getTextContent();
            gameAction.setNarration(narration);

            // map triggers to gameAction set
            mapTriggerAction(triggers, gameAction);
        }
    }

    /**
     * store consumed entities into gameAction instance
     */
    private void storeConEntities(HashSet<String> consumedEntities, GameAction gameAction) {
        for (String consumedEntity : consumedEntities) {
            // case: consume health
            if ("health".equalsIgnoreCase(consumedEntity)) {
                gameAction.setConsumeHealth(true);
                // general case
            } else {
                gameAction.getConsumeSet().add(entityData.getEntityByName(consumedEntity));
            }
        }
    }

    /**
     * store produced entities into gameAction instance
     */
    private void storeProEntities(HashSet<String> producedEntities, GameAction gameAction) {
        for (String producedEntity : producedEntities) {
            // case: produce health
            if ("health".equalsIgnoreCase(producedEntity)) {
                gameAction.setProduceHealth(true);
                // general case
            } else {
                gameAction.getProduceSet().add(entityData.getEntityByName(producedEntity));
            }
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
