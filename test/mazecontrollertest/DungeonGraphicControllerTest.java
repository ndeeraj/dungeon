package mazecontrollertest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import maze.CustomRandomInteger;
import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;
import maze.NonWrappingDungeon;
import maze.PlayerDescription;
import maze.RandomInteger;
import maze.WeaponType;
import mazegraphiccontroller.DungeonControllerFeatures;
import mazegraphiccontroller.DungeonGraphicController;
import mazegraphiccontroller.DungeonView;
import mazegraphiccontroller.DungeonViewImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for {@link DungeonGraphicController} using real model.
 */
public class DungeonGraphicControllerTest {
  private final StringBuffer called;
  private final DungeonControllerFeatures control;
  private final DungeonControllerFeatures controlWM;
  private final Dungeon model;
  private final StringBuffer called2;

  /**
   * Initializes common variables for all tests.
   */
  public DungeonGraphicControllerTest() {
    called = new StringBuffer();
    DungeonView view = new MockDungeonView(called);
    control = new DungeonGraphicController(view);

    called2 = new StringBuffer();
    model = new NonWrappingDungeon(
            "player",
            5,
            6,
            2,
            100,
            1,
            null);
    DungeonView view2 = new MockDungeonView(called2);
    controlWM = new DungeonGraphicController(view2, model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidView() {
    new DungeonGraphicController(null);
  }

  @Test(expected = IllegalStateException.class)
  public void collectTNullModel() {
    DungeonControllerFeatures control2 = new DungeonGraphicController(new DungeonViewImpl());
    control2.collectTreasure();
  }

  @Test
  public void collectTreasure() {
    List<String> treasureQ = model.describePlayer().get(PlayerDescription.TREASURE);
    String exp = called2.toString();
    controlWM.collectTreasure();
    if (treasureQ.containsAll(model.describePlayer().get(PlayerDescription.TREASURE))) {
      // since the model is configured to have treasure at all location.
      fail("treasure quantity should have changed");
    }
    if (!called2.toString().equals(exp + "repaintDungeon\n")) {
      fail("repaint in the view should have been called.");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void collectANullModel() {
    DungeonControllerFeatures control2 = new DungeonGraphicController(new DungeonViewImpl());
    control2.pickWeapon();
  }

  @Test
  public void collectArrow() {
    List<String> weaponQ = model.describePlayer().get(PlayerDescription.WEAPON);
    String exp = called2.toString();
    controlWM.pickWeapon();
    if (weaponQ.containsAll(model.describePlayer().get(PlayerDescription.WEAPON))) {
      // since the model is configured to have treasure at all location.
      fail("weapon quantity should have changed");
    }
    if (!called2.toString().equals(exp + "repaintDungeon\n")) {
      fail("repaint in the view should have been called.");
    }
  }

  @Test
  public void playGame() {
    if (!model.gameStarted()) {
      fail("game should have started when controller already has a model.");
    }
    String exp = called2.toString();
    controlWM.playGame();

    if (!called2.toString().equals(exp + "setListeners\nsetVisible\n")) {
      fail("controller didn't call the right methods on view.");
    }
  }

  @Test
  public void playGame1() {
    String exp = called.toString();
    System.out.println(called);
    control.playGame();

    if (!called.toString().equals(exp + "setListeners\nsetVisible\nshowNewGameScreen\n")) {
      fail("controller didn't call the right methods on view.");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void moveNullModel() {
    DungeonControllerFeatures view2 = new DungeonGraphicController(new DungeonViewImpl());
    view2.move(Direction.NORTH);
  }

  @Test(expected = IllegalArgumentException.class)
  public void moveNullDir() {
    controlWM.move(null);
  }

  @Test
  public void move() {
    List<String> possM = model.describeLocation().get(LocationDescription.MOVES);
    Direction validD = null;
    Direction invalidD = null;
    String exp = called2.toString();
    String loc1 = model.getPlayerLocation();
    int i = 0;

    // finding a possible valid and invalid move from a location.
    for (String l : possM) {
      switch (i) {
        case 0: {
          if (l.equals("null")) {
            invalidD = Direction.NORTH;
          } else {
            validD = Direction.NORTH;
          }
          break;
        }
        case 1: {
          if (l.equals("null")) {
            invalidD = Direction.WEST;
          } else {
            validD = Direction.WEST;
          }
          break;
        }
        case 2: {
          if (l.equals("null")) {
            invalidD = Direction.EAST;
          } else {
            validD = Direction.EAST;
          }
          break;
        }
        case 3: {
          if (l.equals("null")) {
            invalidD = Direction.SOUTH;
          } else {
            validD = Direction.SOUTH;
          }
          break;
        }
        default:
      }
      if ((validD != null) && (invalidD != null)) {
        break;
      }
      i++;
    }
    controlWM.move(validD);
    if (model.getPlayerLocation().equals(loc1)) {
      fail("location should have changed after move.");
    }
    assertEquals("controller didn't call the right methods on view.", called2.toString(),
            exp + "repaintDungeon\n");

    Direction moveBack = null;
    if (validD == Direction.NORTH) {
      moveBack = Direction.SOUTH;
    } else if (validD == Direction.SOUTH) {
      moveBack = Direction.NORTH;
    } else if (validD == Direction.EAST) {
      moveBack = Direction.WEST;
    } else if (validD == Direction.WEST) {
      moveBack = Direction.EAST;
    }
    controlWM.move(moveBack);
    controlWM.move(invalidD);
  }

  @Test
  public void moveToEnd() {
    StringBuffer callT = new StringBuffer();
    Dungeon modelT = new NonWrappingDungeon(
            "player",
            5,
            6,
            2,
            100,
            1,
            null);
    DungeonView viewT = new MockDungeonView(callT);
    DungeonControllerFeatures controlT = new DungeonGraphicController(viewT, modelT);

    String exp = null;
    RandomInteger rand = new CustomRandomInteger();
    List<String> possibleMoves = null;
    Direction randomVD = null;

    while (!modelT.gameEnded()) {
      possibleMoves = modelT.describeLocation().get(LocationDescription.MOVES);
      List<Integer> m = new ArrayList<>();
      m.add(0);
      m.add(1);
      m.add(2);
      m.add(3);
      while (true) {
        int random = rand.nextInt(0, m.size());
        if (possibleMoves.get(m.get(random)).equals("null")) {
          m.remove(random);
        } else {
          if (m.get(random) == 0) {
            randomVD = Direction.NORTH;
            break;
          } else if (m.get(random) == 1) {
            randomVD = Direction.WEST;
            break;
          } else if (m.get(random) == 2) {
            randomVD = Direction.EAST;
            break;
          } else if (m.get(random) == 3) {
            randomVD = Direction.SOUTH;
            break;
          }
        }
      }
      exp = callT.toString();
      controlT.move(randomVD);
    }
    assertEquals("controller didn't call the right methods on view.", callT.toString(),
            exp + "repaintDungeon\nshowGameEndedScreen\n");
  }

  @Test(expected = IllegalStateException.class)
  public void shootNullModel() {
    DungeonControllerFeatures control2 = new DungeonGraphicController(new DungeonViewImpl());
    control2.shootArrow(Direction.SOUTH, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootNullDir() {
    controlWM.shootArrow(null, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootInvalidDist() {
    controlWM.shootArrow(Direction.EAST, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shootInvalidDist2() {
    controlWM.shootArrow(Direction.EAST, -1);
  }

  @Test
  public void shootValid() {
    List<String> playerW = model.describePlayer().get(PlayerDescription.WEAPON);
    int arrQ = -1;
    int i = 0;
    for (String p : playerW) {
      String[] weaponQ = p.split("\\s");
      for (WeaponType w : WeaponType.values()) {
        if ((weaponQ[0].equals(w.name())) && (i == 0)) {
          arrQ = Integer.parseInt(weaponQ[1]);
        }
      }
      i++;
    }
    String exp = called2.toString();
    controlWM.shootArrow(Direction.EAST, 2);

    playerW = model.describePlayer().get(PlayerDescription.WEAPON);
    int newArrQ = -1;
    int j = 0;
    for (String p : playerW) {
      String[] weaponQ = p.split("\\s");
      for (WeaponType w : WeaponType.values()) {
        if ((weaponQ[0].equals(w.name())) && (j == 0)) {
          newArrQ = Integer.parseInt(weaponQ[1]);
        }
      }
      j++;
    }
    assertEquals("after shooting arrow, arrow quantity should decrease.",
            newArrQ, arrQ - 1);
    assertEquals("controller didn't call the right methods on view.", called2.toString(),
            exp + "showShootFeedback\nrepaintDungeon\n");
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvP() {
    control.setUpGame(
            null, 5, 5, 1, 50, 1, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvR() {
    control.setUpGame(
            "player", 3, 5, 1, 50, 1, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvC() {
    control.setUpGame(
            "player", 5, 3, 1, 50, 1, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvInterC() {
    control.setUpGame(
            "player", 5, 5, -1, 50, 1, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvT() {
    control.setUpGame(
            "player", 5, 5, 1, -50, 1, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setUpGameInvD() {
    controlWM.setUpGame(
            "player", 5, 5, 1, 50, -1, false);
  }

  @Test
  public void setUpGame() {
    String exp = called.toString();
    control.setUpGame(
            "player", 5, 5, 1, 50, 1, false);
    assertEquals("controller didn't call the right methods on view.", called.toString(),
            exp + "assignReadOnlyModel\nrepaintDungeon\n");

    exp = called2.toString();
    controlWM.setUpGame(
            "player", 5, 5, 1, 50, 1, false);
    assertEquals("controller didn't call the right methods on view.", called2.toString(),
            exp + "assignReadOnlyModel\nrepaintDungeon\n");
  }
}
