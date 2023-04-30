package edu.uob.file;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import edu.uob.database.EntityData;
import edu.uob.entity.*;
import edu.uob.entity.Character;
import edu.uob.exception.Response;
import edu.uob.exception.STAGException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * parse entities file and store data into database
 */
public class EntityFileParser {
    private File entitiesFile;
    private EntityData entityData;

    public EntityFileParser(File entitiesFile, EntityData entityData) {
        this.entitiesFile = entitiesFile;
        this.entityData = entityData;
    }

    /**
     * parse entities file and store data in entity classes
     * @throws STAGException exceptions will be handled by game server
     */
    public void parseEntities() throws STAGException {
        // parse dot file and get all the entity sections
        ArrayList<Graph> sections = getSections(entitiesFile);

        // iterate location graphs and store data in game entities
        ArrayList<Graph> locationGraphs = sections.get(0).getSubgraphs();
        storeLocations(locationGraphs);

        // parse paths and store in locations
        ArrayList<Edge> pathEdges = sections.get(1).getEdges();
        storePaths(pathEdges);

        // iterate locations to get data of artefacts, furniture and characters
        storeArtefacts();
        storeFurniture();
        storeCharacters();
    }

    /**
     * @return sections parsed from entities file
     * @throws STAGException exceptions will be handled by game server
     */
    private ArrayList<Graph> getSections(File entitiesFile) throws STAGException {
        try {
            Parser parser = new Parser();
            FileReader fileReader = new FileReader(entitiesFile);
            parser.parse(fileReader);
            Graph rootGraph = parser.getGraphs().get(0);
            ArrayList<Graph> sections = rootGraph.getSubgraphs();

            return sections;
        } catch (FileNotFoundException e) {
            throw new STAGException(Response.FILE_NOT_FOUND);
        } catch (ParseException e) {
            throw new STAGException(Response.FAIL_PARSE_DOT);
        }
    }

    /**
     * iterate location graphs and store location data
     */
    private void storeLocations(ArrayList<Graph> locationGraphs) throws STAGException {
        for (int i = 0; i < locationGraphs.size(); i++) {
            Graph locationGraph = locationGraphs.get(i);
            // create location instance to store data
            Node locationDetails = locationGraph.getNodes(false).get(0);
            String locationName = locationDetails.getId().getId();
            String locationDescription = locationDetails.getAttribute("description");
            Location location = new Location(locationName, locationDescription);
            // fill entities in location such as artefacts, furniture and characters
            fillLocation(locationGraph, location);

            // store location into entity database
            entityData.getLocationSet().add(location);

            // store special location: spawn point
            if (i == 0) {
                entityData.setSpawnPoint(location);
            }
            // store special location: storeroom
            if ("storeroom".equalsIgnoreCase(locationName)) {
                entityData.setStoreroom(location);
            }
        }
    }

    /**
     * fill entities in location such as artefacts, furniture and characters
     */
    private void fillLocation(Graph locationGraph, Location location) throws STAGException {
        // get entities of this location
        ArrayList<Graph> locationEntities = locationGraph.getSubgraphs();
        for (Graph locationEntity : locationEntities) {
            String entityType = locationEntity.getId().getId();
            // store artefacts data
            if ("artefacts".equalsIgnoreCase(entityType)) {
                HashSet<Artefact> artefactSet = getLocationEntities(Artefact.class, locationEntity);
                location.getArtefactSet().addAll(artefactSet);
                continue;
            }
            // store furniture data
            if ("furniture".equalsIgnoreCase(entityType)) {
                HashSet<Furniture> furnitureSet = getLocationEntities(Furniture.class, locationEntity);
                location.getFurnitureSet().addAll(furnitureSet);
                continue;
            }
            // store character data
            if ("characters".equalsIgnoreCase(entityType)) {
                HashSet<Character> characterSet = getLocationEntities(Character.class, locationEntity);
                location.getCharacterSet().addAll(characterSet);
            }
        }
    }

    /**
     * @return one type of entitySet in a given location
     */
    private <T> HashSet<T> getLocationEntities(Class<T> clazz, Graph entityGraph) throws STAGException {
        HashSet<T> entitySet = new HashSet<>();

        ArrayList<Node> entityNodes = entityGraph.getNodes(false);
        for (Node entityNode : entityNodes) {
            String entityName = entityNode.getId().getId();
            String entityDescription = entityNode.getAttribute("description");

            try {
                Constructor<T> constructor = clazz.getConstructor(String.class, String.class);
                T t = constructor.newInstance(entityName, entityDescription);
                entitySet.add(t);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new STAGException(Response.FAIL_TO_REFLECT);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new STAGException(Response.FAIL_TO_REFLECT);
            }
        }

        return entitySet;
    }

    /**
     * parses path edges and store path data in location database
     */
    private void storePaths(ArrayList<Edge> pathEdges) {
        for (Edge pathEdge : pathEdges) {
            // create path instance
            Node fromLocationNode = pathEdge.getSource().getNode();
            String fromLocationName = fromLocationNode.getId().getId();
            Node toLocationNode = pathEdge.getTarget().getNode();
            String toLocationName = toLocationNode.getId().getId();

            // check if a location is at the beginning of the path
            for (GameEntity location : entityData.getLocationSet()) {
                if (fromLocationName.equalsIgnoreCase(location.getName())) {
                    ((Location) location).getToLocationSet().add(toLocationName);
                }
            }
        }
    }

    /**
     * get artefacts data by iterating location data in entity database
     */
    private void storeArtefacts() {
        for (GameEntity location : entityData.getLocationSet()) {
            entityData.getArtefactSet().addAll(((Location) location).getArtefactSet());
        }
    }

    /**
     * get furniture data by iterating location data in entity database
     */
    private void storeFurniture() {
        for (GameEntity location : entityData.getLocationSet()) {
            entityData.getFurnitureSet().addAll(((Location) location).getFurnitureSet());
        }
    }

    /**
     * get character data by iterating location data in entity database
     */
    private void storeCharacters() {
        for (GameEntity location : entityData.getLocationSet()) {
            entityData.getCharacterSet().addAll(((Location) location).getCharacterSet());
        }
    }
}
