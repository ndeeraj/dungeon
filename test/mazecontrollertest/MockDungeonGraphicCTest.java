package mazecontrollertest;

import org.junit.Before;
import org.junit.Test;

import maze.Direction;
import maze.Dungeon;
import mazegraphiccontroller.DungeonControllerFeatures;
import mazegraphiccontroller.DungeonGraphicController;
import mazegraphiccontroller.DungeonView;
import mazegraphiccontroller.DungeonViewImpl;

import static org.junit.Assert.assertEquals;

/**
 * Testing class to test {@link DungeonGraphicController} independent of the model.
 */
public class MockDungeonGraphicCTest {

  private StringBuffer log;
  private DungeonControllerFeatures control;
  private StringBuffer calledF;
  private int numMoves;

  @Before
  public void setup() {
    numMoves = 6;
    log = new StringBuffer();
    Dungeon dungeon = new MockDungeonModel(log, numMoves);
    calledF = new StringBuffer();
    DungeonView view = new MockDungeonView(calledF);
    control = new DungeonGraphicController(view, dungeon);
  }

  @Test(expected = IllegalStateException.class)
  public void shootNullModel() {
    DungeonControllerFeatures control2 = new DungeonGraphicController(new DungeonViewImpl());
    control2.shootArrow(Direction.SOUTH, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootNullDir() {
    control.shootArrow(null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootInvalidDist() {
    control.shootArrow(Direction.EAST, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootInvalidDist2() {
    String expected = "Direction: WEST, Distance: -7\n";
    control.shootArrow(Direction.WEST, -7);
    assertEquals("input passed to model should match.", log.toString(), expected);
  }

  @Test
  public void shootValid() {
    String exp = calledF.toString();
    String expected = "Direction: EAST, Distance: 2\n";
    control.shootArrow(Direction.EAST, 2);
    assertEquals("input passed should match.", log.toString(), expected);
    exp += "showShootFeedback\nrepaintDungeon\n";
    assertEquals("controller didn't call the right methods on view.", calledF.toString(),
            exp);

    expected += "Direction: WEST, Distance: 7\n";
    control.shootArrow(Direction.WEST, 7);
    assertEquals("input passed to model should match.", log.toString(), expected);
    exp += "showShootFeedback\nrepaintDungeon\n";
    assertEquals("controller didn't call the right methods on view.", calledF.toString(),
            exp);
  }

  @Test(expected = IllegalArgumentException.class)
  public void moveInvalidDir() {
    control.move(null);
  }

  @Test
  public void move() {
    String exp = calledF.toString();
    String expected = "";
    for (int i = 0; i < numMoves; i++) {
      expected += "Direction: EAST\n";
      control.move(Direction.EAST);
      assertEquals("input passed should match.", log.toString(), expected);
      exp += "repaintDungeon\n";
      if (i == 5) {
        exp += "showGameEndedScreen\n";
      }
      assertEquals("controller didn't call the right methods on view.",
              calledF.toString(), exp);
    }
  }
}