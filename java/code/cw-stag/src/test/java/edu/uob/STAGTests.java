package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class STAGTests {
    private GameServer server;

    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will time out if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    // test if player is created in spawn point
    @Test
    void testSpawn() {
        String response = sendCommandToServer("simon:LOok");
        response = response.toLowerCase();
        assertTrue(response.contains("cabin"), "Didn't see the name of the spawn point");
        assertTrue(response.contains("cabin in the woods"), "Didn't see the description of the spawn point");
        assertTrue(response.contains("sharp axe"), "Didn't see the description of the artefact axe");
        assertTrue(response.contains("wooden trapdoor"), "Didn't see the description of the the furniture trapdoor");
        assertTrue(response.contains("forest"), "Didn't see the available path to forest");
        assertFalse(response.contains("cellar"), "There should not be a path to the cellar now");
    }

    /**
     * test built-in actions
     * also test if the interpreter can handle decorative words and if it's case-insensitive
     */
    @Test
    void testBuiltIn() {
        // test goto and look
        sendCommandToServer("SIMon: I want to goto fORest");
        String response = sendCommandToServer("simon: look");
        response = response.toLowerCase();
        assertTrue(response.contains("dark forest"), response+"Fail to move to forest - can't see the description of forest");
        assertTrue(response.contains("key"), "Fail to see artefacts in forest");

        // test get and inv
        sendCommandToServer("siMON: get key");
        response = sendCommandToServer("simon: inv");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Fail to pick up the key in forest");

        // test drop
        sendCommandToServer("simon:  drOp key");
        response = sendCommandToServer("simon: my INVENTORY");
        response = response.toLowerCase();
        assertFalse(response.contains("key"), "Fail to drop key - it's still in player's inventory");
        response = sendCommandToServer("simon:look");
        response = response.toLowerCase();
        assertTrue(response.contains("key"), "Fail to drop key - it's not at the location");

        // test health
        sendCommandToServer("simon: goto cabin");
        // the maximum of health should be 3
        sendCommandToServer("simon: drink potion");
        response = sendCommandToServer("simon: PLease tell me my heAlth thank you so much");
        response = response.toLowerCase();
        assertTrue(response.contains("3"), "Fail to get the health of player");
    }

    /**
     * test multi-player
     */
    @Test
    void testMultiPlayer() {
        // can't use another player's entity
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: get COIN");
        sendCommandToServer("'sa- rah': get potion");
        sendCommandToServer("'SA- RAH': wanna get axe");
        sendCommandToServer("'sa- rah': open trapdoor");
        String response = sendCommandToServer("'sa- rah': look").toLowerCase();
        assertFalse(response.contains("cellar"), "Can't use other player's items");

        // when a player is dead, another player's status shouldn't be modified
        sendCommandToServer("simon: open with key");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("'sa- rah': goto cellar");
        sendCommandToServer("simon: hit elf");
        sendCommandToServer("simon: hit elf");
        sendCommandToServer("simon: hit elf");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("cabin"), "Fail to remove simon back to spawn point");
        response = sendCommandToServer("'sa- rah': look").toLowerCase();
        assertTrue(response.contains("cellar"), response+"Other players should stay at the same location");
        assertTrue(response.contains("coin"), "Dead player's items should be left at the leaving location");
        response = sendCommandToServer("'SA- RAH': INv");
        assertTrue(response.contains("potion"), "Other player's inventory shouldn't be cleared");
    }

    /**
     * when player is dead, them should be removed to spawn point
     * also inventory should be cleared and health should be recovered
     */
    @Test
    void testHealth() {
        pathToCellar();
        sendCommandToServer("simon: get some awful potion");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: get coin  ");
        sendCommandToServer("simon: let's goto cellar");

        // hit elf twice and simon's health should become 1 now
        sendCommandToServer("simon: elf was hit by me bravo");
        sendCommandToServer("simon: elf was hit by me bravo");
        String response = sendCommandToServer("simon: health check check").toLowerCase();
        assertTrue(response.contains("1"), "Fail to lose health when attack the elf");

        // recover one health
        sendCommandToServer("SIMON: Have to drink some potion");
        response = sendCommandToServer("simon: health check check").toLowerCase();
        assertTrue(response.contains("2"), "Fail to recover health after drinking potion");

        // player is dead
        sendCommandToServer("simon: hit the sweet elf");
        sendCommandToServer("simon: hit the sweet elf again");

        // test if play's health is recovered
        response = sendCommandToServer("simon: my health").toLowerCase();
        assertTrue(response.contains("3"), "Didn't reset player's health");

        // test if play's inventory is cleared
        response = sendCommandToServer("simon: INV").toLowerCase();
        assertFalse(response.contains("axe"), "Fail to clear player's inventory");

        // test if items are at cellar now
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("coin"), "Coin should not be at cabin");
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("silver coin"), "Coin should be at cellar now");
    }

    /**
     * test some normal actions
     */
    @Test
    void testActions() {
        // consume artefact
        pathToCellar();
        String response = sendCommandToServer("siMon: look").toLowerCase();
        assertTrue(response.contains("wooden trapdoor"), "Trapdoor should still be there");
        assertFalse(response.contains("key"), "Key should not at the location now");
        response = sendCommandToServer("simon: inV");
        assertFalse(response.contains("key"), "key should not in player's inventory");

        // produce artefacts
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: pay with my coin please");
        response = sendCommandToServer("simoN: inv").toLowerCase();
        assertFalse(response.contains("shovel"), "Produced artefact should not appear in player's inventory automatically");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("shovel"), "Produced artefact should appear at the location");

        // consume furniture
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: GET axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: use my axe to cut down tree yeah");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("tree"), "Fail to consume furniture - cut down tree");
        assertTrue(response.contains("log"), "Fail to produce artefacts - there is no log");

        // produce character
        sendCommandToServer("simon: get log");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: get HORN");
        sendCommandToServer("simoN: blow horn");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("lumberjack"), "Fail to produce character - lumberjack should be here");

        // consume character
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: kill elf");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("liquid"), "Fail to produce liquid");
        assertFalse(response.contains("elf"), "Fail to consume character - elf is still alive");
        sendCommandToServer("simon: get liquid");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("liquid"), "Fail to pick up liquid");

        // produce furniture
        sendCommandToServer("simon: blow   horn ");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("lumberjack"), "Fail to call lumber jack to cellar");
        sendCommandToServer("simon: drink lumberjack with unknown liquid");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("tree"), "Fail to produce furniture - tree is not at cellar");
        assertFalse(response.contains("lumberjack"), "Lumberjack should not be in the cellar now");

        // consume location
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: close with axe");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("cellar"), "Fail to consume location - can't reach cellar now");

        // produce location
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: bridge river with log");
        response = sendCommandToServer("simon: LOOK").toLowerCase();
        assertTrue(response.contains("clearing"), "Fail to produce location - player can't arrive clearing");
    }

    /**
     * test wrong format of built-in actions
     */
    @Test
    void testWrongBuiltIn() {
        // lack player
        String response = sendCommandToServer("look").toLowerCase();
        assertFalse(response.contains("cabin"), "Shouldn't see player's location because didn't specify player name");

        // invalid player name
        response = sendCommandToServer("si!: look").toLowerCase();
        assertFalse(response.contains("cabin"), "Shouldn't see player's location because player's name is invalid");

        // repeated actions
        response = sendCommandToServer("simon: look look").toLowerCase();
        assertFalse(response.contains("cabin"), response+"Shouldn't see player's location because extraneous action");
        response = sendCommandToServer("simon:look look look").toLowerCase();
        assertFalse(response.contains("cabin"), response+"Shouldn't see player's location because extraneous action");

        // typo
        response = sendCommandToServer("simon: lookkkkkkk").toLowerCase();
        assertFalse(response.contains("cabin"), "Shouldn't see cabin because typo command");

        // composite commands
        sendCommandToServer("simon: look and get potion");
        response = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(response.contains("potion"), "Shouldn't pick up potion");
    }

    /**
     * test wrong format of normal actions
     */
    @Test
    void testWrongActions() {
        // lack action
        sendCommandToServer("simon: axe");
        String response = sendCommandToServer("simon: inv").toLowerCase();
        assertFalse(response.contains("axe"), "Shouldn't pick up axe - command with entity but without action");

        // unavailable entity
        sendCommandToServer("simon: open trapdoor");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertFalse(response.contains("cellar"), "Shouldn't open trapdoor because player didn't get the key");

        // lack action's entity
        pathToCellar();
        sendCommandToServer("simon: get potion");
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: kill");
        response = sendCommandToServer("simon: look").toLowerCase();
        assertTrue(response.contains("elf"), "Elf shouldn't be killed because the command doesn't have entity");

        // unavailable subjects
        sendCommandToServer("simon: kill elf");
        sendCommandToServer("simon: get liquid");
        sendCommandToServer("simon: drink liquid with coin");
        response = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(response.contains("liquid"), "Shouldn't consume liquid because of the lack of subject");

        // ambiguous action
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: drink liquid");
        response = sendCommandToServer("simon: inv").toLowerCase();
        assertTrue(response.contains("liquid"), "Fail to stop ambiguous action");
    }

    /**
     * helper method: open the trapdoor between cabin and cellar
     */
    private void pathToCellar() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto the hhhhhhhh cabin");
        sendCommandToServer("SIMON: I'm gonna open the trapdoor");
    }
}
