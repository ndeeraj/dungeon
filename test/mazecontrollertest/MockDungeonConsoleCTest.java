package mazecontrollertest;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import maze.Dungeon;
import mazeconsolecontroller.DungeonConsoleController;
import mazeconsolecontroller.DungeonController;

import static org.junit.Assert.assertEquals;

/**
 * Testing class to test {@link DungeonConsoleController} independent of the model.
 */
public class MockDungeonConsoleCTest {

  private Dungeon dungeon;
  private StringBuffer log;
  private final String travelPredef;
  private final String inputForPredefinedP;
  private final String paramTillPredef;

  /**
   * Initializes common variables for all tests.
   */
  public MockDungeonConsoleCTest() {
    inputForPredefinedP = "E T M E M E W S N 2 M E M E";
    paramTillPredef = "Direction: EAST\n" +
            "Direction: EAST\n" +
            "Direction: NORTH, Distance: 2\n" +
            "Direction: EAST\n" +
            "Direction: EAST\n";
    travelPredef = "\nenter or display player (E-D): \n" +
            "entering dungeon...\n" +
            "\n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" + "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "treasure already collected from location.\n" +
            "\nmove or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "weapon already collected from location.\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n";
  }

  @Before
  public void setup() {
    log = new StringBuffer();
    dungeon = new MockDungeonModel(log, 6);
  }

  @Test(expected = IllegalStateException.class)
  public void playGameFailingAppendable() {
    StringReader input = new StringReader("E 2 1 1 3 3 1 2 1 3 2 3 2 1 3 1 3 2");
    Appendable gameLog = new FailingAppendable();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(dungeon);
  }

  @Test
  public void allValidMoves() {
    StringReader input = new StringReader(inputForPredefinedP + " M E M E Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(dungeon);
    String expected = paramTillPredef + "Direction: EAST\n" +
            "Direction: EAST\n";
    assertEquals("input passed should match.", log.toString(), expected);
    String expectedGameLog = travelPredef + "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("game log should match.", gameLog.toString(), expectedGameLog);
  }

  @Test
  public void allValidMovesShootInvalidDist() {
    StringReader input = new StringReader(inputForPredefinedP + " S E 7 2 S inv 2 M E M E Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(dungeon);
    String expected = paramTillPredef
            + "Direction: EAST, Distance: 2\n"
            + "Direction: EAST\n"
            + "Direction: EAST\n";
    assertEquals("input passed should match.", log.toString(), expected);
    String expectedGameLog = travelPredef + "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "invalid range: 7\n" +
            "distance (1-5): \n" +
            "\nmove or pickup treasure or pickup weapon (M-T-W): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "invalid direction: inv\n" +
            "\nmove or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("game log should match.", gameLog.toString(), expectedGameLog);
  }

  @Test
  public void testInValidMoves() {
    StringReader input = new StringReader(inputForPredefinedP + " M y M E M E Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(dungeon);
    String expected = paramTillPredef + "Direction: EAST\n" +
            "Direction: EAST\n";
    assertEquals("input passed should match.", log.toString(), expected);
    String expectedGameLog = travelPredef + "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "select one of the door (E): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("game log should match.", gameLog.toString(), expectedGameLog);
  }

  @Test
  public void testInValidAction() {
    StringReader input = new StringReader(inputForPredefinedP + " inv M E M E Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(dungeon);
    String expected = paramTillPredef + "Direction: EAST\n" +
            "Direction: EAST\n";
    assertEquals("input passed should match.", log.toString(), expected);
    String expectedGameLog = travelPredef +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "\nmove or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon (M-T-W): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: null\n" +
            "treasure: [D 0, R 0, S 10]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, available, null]\n" +
            "weapon: [CARR 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("game log should match.", gameLog.toString(), expectedGameLog);
  }
}