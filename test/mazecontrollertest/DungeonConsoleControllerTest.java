package mazecontrollertest;

import org.junit.Test;

import java.io.StringReader;

import maze.CustomRandomInteger;
import maze.Dungeon;
import maze.NonWrappingDungeon;
import maze.RandomInteger;
import mazeconsolecontroller.DungeonConsoleController;
import mazeconsolecontroller.DungeonController;

import static org.junit.Assert.assertEquals;

/**
 * Testing class to test {@link DungeonConsoleController} along with the model.
 */
public class DungeonConsoleControllerTest {
  private Dungeon nonWrap;
  private final String travelTillSmellPickup;
  private final String inputForPredefinedT;

  /**
   * Initializes variables common to all the tests.
   */
  public DungeonConsoleControllerTest() {
    RandomInteger randP = new CustomRandomInteger(true);
    do {
      nonWrap = new NonWrappingDungeon(
              "player1", 4, 5, 1, 100, 1, randP);
      nonWrap.enter();
    }
    while (!nonWrap.getPlayerLocation().equals("0,0"));
    inputForPredefinedT = "T E M E M E W S N 7 2 M E M E M E e S M W s M W M W S "
            + "W 4 M W M S M E M E M E M S E N S M 1 M S M W M E M W M W M E";
    travelTillSmellPickup = "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 0,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 0,0\n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 3]\n" +
            "\n" +
            "move or pickup treasure or pickup weapon or shoot (M-T-W-S): \n" +
            "picked up 20 DIAMONDS.\n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 3]\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 0,0, 0,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 0,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 0,1, 0,3, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 0,2\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "picked up 1 CROOKEDARROW.\n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 4]\n" +
            "\n" +
            "move or shoot (M-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "invalid range: 7\n" +
            "distance (1-5): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 3]\n" +
            "\n" +
            "move or shoot (M-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 0,2, 0,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 0,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 0,3, null, 1,4]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 0,4\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, S): \n" +
            "invalid move.\n" +
            "select one of the door (W, S): \n" +
            "select one of the door (W, S): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [0,4, 1,3, null, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 1,4\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (N, W): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 1,2, 1,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 1,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 1,1, 1,3, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 1,2\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 1,0, 1,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 1,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 2]\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 1,1, 2,0]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 1,0\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (E, S): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [1,0, null, 2,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 2,0\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (N, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 2,0, 2,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 2,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 2,1, 2,3, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 2,2\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 2,2, 2,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 2,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "invalid move.\n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 2,3, null, 3,4]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 2,4\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "invalid direction: M\n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 2]\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, S): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [2,4, 3,3, null, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 3,4\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (N, W): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,2, 3,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 3,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [2,4, 3,3, null, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 3,4\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (N, W): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,2, 3,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 3,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,1, 3,3, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "there is a light pungent smell.\n" +
            "\n" +
            "player location: 3,2\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,2, 3,4, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "\n" +
            "player location: 3,3\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,1, 3,3, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "there is a light pungent smell.\n" +
            "\n" +
            "player location: 3,2\n" +
            "\n" +
            "player is alive.\n" +
            "\n";
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidController() {
    StringReader input = new StringReader(" 2 1 1 3 3 1 2 1 3 2 3 2 1 3 1 3 2");
    DungeonController control = new DungeonConsoleController(input, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidController1() {
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(null, gameLog);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidModel() {
    StringReader input = new StringReader("E 2 1 1 3 3 1 2 1 3 2 3 2 1 3 1 3 2");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(null);
  }

  @Test(expected = IllegalStateException.class)
  public void playGameFailingAppendable() {
    StringReader input = new StringReader("E 2 1 1 3 3 1 2 1 3 2 3 2 1 3 1 3 2");
    Appendable gameLog = new FailingAppendable();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(nonWrap);
    StringBuffer expected = new StringBuffer();
    assertEquals("output state should match", gameLog.toString());
  }

  @Test
  public void playGamePlayerDeceased() {
    StringReader input = new StringReader(inputForPredefinedT + " M W M W M W P L Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(nonWrap);
    String expected = travelTillSmellPickup + "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,0, 3,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "player location: 3,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 2]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "player location: 3,0\n" +
            "\n" +
            "player is deceased.\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 2]\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 2]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("output state should match",
            gameLog.toString(), expected);
  }

  @Test
  public void playGameMonsterSlainOnce() {
    StringReader input = new StringReader(inputForPredefinedT + " M W S W 1 M W M W P L Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(nonWrap);
    String expected = travelTillSmellPickup + "move or pickup weapon or shoot (M-W-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "\n" +
            "there is a light pungent smell.\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,0, 3,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "player location: 3,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 1]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "player location: 3,0\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 1]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("output state should match",
            gameLog.toString(), expected);
  }

  @Test
  public void playGameMonsterSlainTwice() {
    StringReader input = new StringReader(inputForPredefinedT
            + " M W S W 1 M W S W 1 M W P L Q");
    Appendable gameLog = new StringBuffer();
    DungeonController control = new DungeonConsoleController(input, gameLog);
    control.playGame(nonWrap);
    String expected = travelTillSmellPickup + "move or pickup weapon or shoot (M-W-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "\n" +
            "there is a light pungent smell.\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: TUNNEL\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, 3,0, 3,2, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [null]\n" +
            "you smell something terrible nearby.\n" +
            "\n" +
            "player location: 3,1\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "move or pickup weapon or shoot (M-W-S): \n" +
            "direction to shoot (N-E-W-S): \n" +
            "distance (1-5): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 20, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 0]\n" +
            "\n" +
            "move or pickup weapon (M-W): \n" +
            "select one of the door (W, E): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 0]\n" +
            "\n" +
            "player location: 3,0\n" +
            "\n" +
            "player is alive.\n" +
            "\n" +
            "game ended.\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "\n" +
            "player details...\n" +
            "treasure: [DIAMONDS 40, RUBIES 0, SAPPHIRES 0]\n" +
            "weapon: [CROOKEDARROW 0]\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n" +
            "location description...\n" +
            "type: CAVE\n" +
            "treasure: [DIAMONDS 0, RUBIES 0, SAPPHIRES 0]\n" +
            "possible moves [NORTH, WEST, EAST, SOUTH]: [null, null, 3,1, null]\n" +
            "weapon: [CROOKEDARROW 1]\n" +
            "monster: [OTYUGH 2 0]\n" +
            "\n" +
            "reset or display player or describe location or quit (R-P-L-Q): \n";
    assertEquals("output state should match",
            gameLog.toString(), expected);
  }
}