package mazetest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import maze.CustomRandomInteger;
import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;
import maze.LocationType;
import maze.NonWrappingDungeon;
import maze.PlayerDescription;
import maze.RandomInteger;
import maze.Treasure;
import maze.WrappingDungeon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for {@link WrappingDungeon}, {@link NonWrappingDungeon}.
 * Tests only monster-less dungeon, also will not test functionality related to
 * arrows in the dungeon.
 */
public class DungeonTest {

  private Dungeon wrapR;
  private Dungeon wrap;
  private Dungeon nonWrapR;
  private Dungeon nonWrap;
  private RandomInteger rand;
  private RandomInteger randP;

  @Before
  public void setUp() throws Exception {
    rand = new CustomRandomInteger();
    randP = new CustomRandomInteger(true);
    // must have 1 as interconnectivity, dependency for playerDescription test.
    wrap = new WrappingDungeon("player1", 5, 6, 1, 50, randP);
    wrapR = new WrappingDungeon("player1", 5, 6, 4, 20);
    nonWrap = new NonWrappingDungeon("player1", 5, 6, 1, 50, randP);
    nonWrapR = new NonWrappingDungeon("player1", 5, 6, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidSizeWrapDungeon() {
    new WrappingDungeon("player1", 5, 5, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidSizeNonWrapDungeon() {
    new NonWrappingDungeon("player1", 3, 3, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidPlayerWrapDungeon() {
    new WrappingDungeon("", 5, 6, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidPlayerWrapDungeon1() {
    new WrappingDungeon(null, 5, 6, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidPlayerNonWrapDungeon() {
    new NonWrappingDungeon("", 3, 3, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidPlayerNonWrapDungeon1() {
    new NonWrappingDungeon(null, 3, 3, 4, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidInterCWrapDungeon() {
    new WrappingDungeon("player1", 5, 6, -1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidInterCNonWrapDungeon() {
    new NonWrappingDungeon("player1", 3, 4, -1, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidTreasureWrapDungeon() {
    new WrappingDungeon("player1", 5, 6, 2, -10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createInvalidTreasureNonWrapDungeon() {
    new NonWrappingDungeon("player1", 5, 6, 2, 1);
  }

  @Test
  public void enterWrapD() {
    wrap.enter();
    assertTrue("game must be started.", wrap.gameStarted());
    if (wrap.getPlayerLocation().length() == 0) {
      fail("player must be placed in the game after entering.");
    }
    wrapR.enter();
    assertTrue("game must be started.", wrapR.gameStarted());
    if (wrapR.getPlayerLocation().length() == 0) {
      fail("player must be placed in the game after entering.");
    }
  }

  @Test
  public void enterNonWrapD() {
    nonWrap.enter();
    assertTrue("game must be started.", nonWrap.gameStarted());
    if (nonWrap.getPlayerLocation().length() == 0) {
      fail("player must be placed in the game after entering.");
    }
    nonWrapR.enter();
    assertTrue("game must be started.", nonWrapR.gameStarted());
    if (nonWrapR.getPlayerLocation().length() == 0) {
      fail("player must be placed in the game after entering.");
    }
  }

  // helper to match treasure.
  private boolean treasureMatch(int diamondQ, int rubyQ, int sapphireQ, List<String> pTreasure) {
    List<String> treasure = pTreasure;
    for (String tres : treasure) {
      String[] tresC = tres.split("\\s");
      if (tresC[0].equals(Treasure.DIAMONDS.name())) {
        if (Integer.parseInt(tresC[1]) != diamondQ) {
          return false;
        }
      }
      if (tresC[0].equals(Treasure.RUBIES.name())) {
        if (Integer.parseInt(tresC[1]) != rubyQ) {
          return false;
        }
      }
      if (tresC[0].equals(Treasure.SAPPHIRES.name())) {
        if (Integer.parseInt(tresC[1]) != sapphireQ) {
          return false;
        }
      }
    }
    return true;
  }

  // helper to run describePlayer test.
  private boolean describePlayer(Dungeon predictive, Dungeon random) {
    Map<PlayerDescription, List<String>> playerD = predictive.describePlayer();
    assertEquals("player name should match",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 0, 0,
                    playerD.get(PlayerDescription.TREASURE)));
    predictive.enter();

    Map<LocationDescription, List<String>> locationD;
    List<String> possibleMoves;
    List<String> treasureAtLoc;
    List<Integer> treasureAtLocQ = new ArrayList<>();

    boolean treasureFound = false;
    locationD = predictive.describeLocation();
    if (!predictive.gameEnded()) {
      treasureAtLoc = locationD.get(LocationDescription.TREASURE);
      if (!treasureMatch(0, 0, 0, treasureAtLoc)) {
        treasureAtLocQ = parseTValAtLoc(treasureAtLoc);
        treasureFound = true;
      }
      predictive.collectTreasure();
    }

    while (!treasureFound) {
      possibleMoves = locationD.get(LocationDescription.MOVES);
      locationD = makeMove("R", possibleMoves, predictive, null);
      treasureAtLoc = locationD.get(LocationDescription.TREASURE);
      if (!treasureMatch(0, 0, 0, treasureAtLoc)) {
        treasureAtLocQ = parseTValAtLoc(treasureAtLoc);
        treasureFound = true;
      }
      if (!predictive.gameEnded()) {
        predictive.collectTreasure();
      } else {
        break;
      }
    }

    playerD = predictive.describePlayer();
    assertEquals("player name should match.",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    List<String> t = playerD.get(PlayerDescription.TREASURE);

    if ((t.size() == 3) && (t.get(0).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(1).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(2).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))) {
      // since initial treasure is 0.
      // after picking treasureAtLoc, the value should be equal to treasureAtLoc.
      if (!treasureMatch(treasureAtLocQ.get(0), treasureAtLocQ.get(1), treasureAtLocQ.get(2), t)) {
        fail("player's treasure doesn't match the expected quantity.");
      }
    }

    playerD = random.describePlayer();
    assertEquals("player name should match.",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    assertTrue("initial player treasure should match - wrap random dungeon.",
            treasureMatch(0, 0, 0,
                    playerD.get(PlayerDescription.TREASURE)));

    random.enter();

    List<Integer> treasureAtLocQ2 = new ArrayList<>();
    while (!random.gameEnded()) {
      locationD = random.describeLocation();
      possibleMoves = locationD.get(LocationDescription.MOVES);
      Map<LocationDescription, List<String>> movedLD = new HashMap<>();
      movedLD = makeMove("R", possibleMoves, random, null);

      treasureAtLoc = movedLD.get(LocationDescription.TREASURE);
      if (!random.gameEnded()) {
        random.collectTreasure();
      }
      if (!treasureMatch(0, 0, 0, treasureAtLoc)) {
        if (treasureAtLocQ.isEmpty()) {
          treasureAtLocQ = parseTValAtLoc(treasureAtLoc);
        } else {
          treasureAtLocQ2 = parseTValAtLoc(treasureAtLoc);
        }
      }
    }

    playerD = random.describePlayer();
    assertEquals("player name should match",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    t = playerD.get(PlayerDescription.TREASURE);
    if (!((t.size() == 3) && (t.get(0).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(1).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(2).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+")))) {
      int finalD = 0;
      int finalR = 0;
      int finalS = 0;
      if (!treasureAtLocQ.isEmpty()) {
        finalD += treasureAtLocQ.get(0);
        finalR += treasureAtLocQ.get(1);
        finalS += treasureAtLocQ.get(2);
      }
      if (!treasureAtLocQ2.isEmpty()) {
        finalD += treasureAtLocQ2.get(0);
        finalR += treasureAtLocQ2.get(1);
        finalS += treasureAtLocQ2.get(2);
      }
      if (!treasureMatch(finalD, finalR, finalS, t)) {
        fail("player's treasure doesn't match the expected quantity.");
      }
    }
    return true;
  }

  private List<Integer> parseTValAtLoc(List<String> treasureAtLoc) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      String treas = treasureAtLoc.get(i);
      String[] t = treas.split("\\s");
      result.add(Integer.parseInt(t[1]));
    }
    return result;
  }

  @Test
  public void describePlayerWrapD() {
    assertTrue("describe player should work for wrapping dungeon.",
            describePlayer(
                    new WrappingDungeon("player1", 5, 6,
                            1, 100, randP),
                    new WrappingDungeon("player1", 5, 6,
                            4, 100)));
  }

  private Map<LocationDescription, List<String>> makeMove(
          String dir, List<String> possibleMoves, Dungeon d, List<Direction> movedD) {
    Direction direction = null;
    switch (dir) {
      case "N": {
        if (!possibleMoves.get(0).equals("null")) {
          d.move(Direction.NORTH);
          direction = Direction.NORTH;
        }
      }
      break;
      case "E": {
        if (!possibleMoves.get(2).equals("null")) {
          d.move(Direction.EAST);
          direction = Direction.EAST;
        }
      }
      break;
      case "W": {
        if (!possibleMoves.get(1).equals("null")) {
          d.move(Direction.WEST);
          direction = Direction.WEST;
        }
      }
      break;
      case "S": {
        if (!possibleMoves.get(3).equals("null")) {
          d.move(Direction.SOUTH);
          direction = Direction.SOUTH;
        }
      }
      break;
      case "F": {
        for (int i = 0; i < 4; i++) {
          if (possibleMoves.get(i).equals("null")) {
            // continue;
          } else {
            if (i == 0) {
              d.move(Direction.NORTH);
              direction = Direction.NORTH;
              break;
            } else if (i == 1) {
              d.move(Direction.WEST);
              direction = Direction.WEST;
              break;
            } else if (i == 2) {
              d.move(Direction.EAST);
              direction = Direction.EAST;
              break;
            } else if (i == 3) {
              d.move(Direction.SOUTH);
              direction = Direction.SOUTH;
              break;
            }
          }
        }
      }
      break;
      case "R": {
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
              d.move(Direction.NORTH);
              direction = Direction.NORTH;
              break;
            } else if (m.get(random) == 1) {
              d.move(Direction.WEST);
              direction = Direction.WEST;
              break;
            } else if (m.get(random) == 2) {
              d.move(Direction.EAST);
              direction = Direction.EAST;
              break;
            } else if (m.get(random) == 3) {
              d.move(Direction.SOUTH);
              direction = Direction.SOUTH;
              break;
            }
          }
        }
      }
      break;
      default:
        // do nothing.
    }
    if (direction != null) {
      if (movedD != null) {
        if (!movedD.contains(direction)) {
          movedD.add(direction);
        }
      }
    }
    return d.describeLocation();
  }

  @Test
  public void describePlayerNonWrapD() {
    assertTrue("describe player should work for non wrapping dungeon.",
            describePlayer(
                    new NonWrappingDungeon("player1", 5, 6,
                            1, 100, randP),
                    new NonWrappingDungeon("player1", 5, 6,
                            4, 100)));
  }

  @Test(expected = IllegalStateException.class)
  public void describeLocationNonWrapDInvalid() {
    nonWrap.describeLocation();
  }

  @Test
  public void describeLocationNonWrapD() {
    Dungeon nonWrapT;
    while (true) {
      nonWrapT = new NonWrappingDungeon(
              "player1", 4, 5, 0, 100, randP);
      nonWrapT.enter();
      if (nonWrapT.getPlayerLocation().equals("0,0")) {
        break;
      }
    }

    Map<LocationDescription, List<String>> locationD = nonWrapT.describeLocation();
    List<String> expected;
    List<String> result;

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) == 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && ((locationD.get(LocationDescription.TYPE).get(0)
            .equals(LocationType.CAVE.name())))) {
      expected = new ArrayList<>();
      expected.add("null");
      expected.add("null");
      expected.add("0,1");
      expected.add("null");
      result = locationD.get(LocationDescription.MOVES);
      if (expected.size() == result.size()) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).equals(expected.get(i)))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
      List<Integer> treasureAtLoc = parseTValAtLoc(locationD.get(LocationDescription.TREASURE));
      int countZ = 0;
      for (int t : treasureAtLoc) {
        if (t == 0) {
          countZ++;
        }
      }
      if (countZ == 3) {
        fail("location signature fetched incorrectly, location must have treasure.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }

    nonWrapR.enter();
    locationD = nonWrapR.describeLocation();

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)
            && (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.CAVE.name()))) {

      result = locationD.get(LocationDescription.MOVES);
      if (result.size() == 4) {
        for (int i = 0; i < result.size(); i++) {
          if (!((result.get(i).equals("null")) || (result.get(i).matches("\\d+,\\d+")))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.TREASURE);
      if (result.size() == 3) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }

    makeMove("F", locationD.get(LocationDescription.MOVES), nonWrapR, null);
    locationD = nonWrapR.describeLocation();

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)
            && ((locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.TUNNEL.name()))
            || (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.CAVE.name())))) {

      result = locationD.get(LocationDescription.MOVES);
      if (result.size() == 4) {
        for (int i = 0; i < result.size(); i++) {
          if (!((result.get(i).equals("null")) || (result.get(i).matches("\\d+,\\d+")))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.TREASURE);
      if (result.size() == 3) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void describeLocationWrapDInvalid() {
    wrap.describeLocation();
  }

  @Test
  public void describeLocationWrapD() {
    Dungeon wrapT;
    while (true) {
      wrapT = new WrappingDungeon(
              "player1", 5, 6, 1, 100, randP);
      wrapT.enter();
      if (wrapT.getPlayerLocation().equals("0,0")) {
        break;
      }
    }

    Map<LocationDescription, List<String>> locationD = wrapT.describeLocation();
    List<String> expected;
    List<String> result;

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) == 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && ((locationD.get(LocationDescription.TYPE).get(0)
            .equals(LocationType.CAVE.name())))) {
      expected = new ArrayList<>();
      expected.add("null");
      expected.add("null");
      expected.add("0,1");
      expected.add("null");
      result = locationD.get(LocationDescription.MOVES);
      if (expected.size() == result.size()) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).equals(expected.get(i)))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
      List<Integer> treasureAtLoc = parseTValAtLoc(locationD.get(LocationDescription.TREASURE));
      int countZ = 0;
      for (int t : treasureAtLoc) {
        if (t == 0) {
          countZ++;
        }
      }
      if (countZ == 3) {
        fail("location signature fetched incorrectly, location must have treasure.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }

    wrapR.enter();
    locationD = wrapR.describeLocation();

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)
            && (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.CAVE.name()))) {

      result = locationD.get(LocationDescription.MOVES);
      if (result.size() == 4) {
        for (int i = 0; i < result.size(); i++) {
          if (!((result.get(i).equals("null")) || (result.get(i).matches("\\d+,\\d+")))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.TREASURE);
      if (result.size() == 3) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }

    makeMove("F", locationD.get(LocationDescription.MOVES), wrapR, null);
    locationD = wrapR.describeLocation();

    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)
            && ((locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.TUNNEL.name()))
            || (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.CAVE.name())))) {

      result = locationD.get(LocationDescription.MOVES);
      if (result.size() == 4) {
        for (int i = 0; i < result.size(); i++) {
          if (!((result.get(i).equals("null")) || (result.get(i).matches("\\d+,\\d+")))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.TREASURE);
      if (result.size() == 3) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }
    } else {
      fail("location signature fetched incorrectly.");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void moveInValidWrap() {
    wrap.enter();
    wrap.move(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void moveInValidNonWrap() {
    nonWrapR.enter();
    nonWrapR.move(null);
  }

  @Test(expected = IllegalStateException.class)
  public void moveInValidNonWrapW() {
    Dungeon nonWrapT;
    while (true) {
      nonWrapT = new NonWrappingDungeon(
              "player1", 4, 5, 0, 100, randP);
      nonWrapT.enter();
      if (nonWrapT.getPlayerLocation().equals("0,0")) {
        nonWrapT.collectTreasure();
        break;
      }
    }
    nonWrapT.move(Direction.WEST);
  }

  @Test
  public void moveWrap() {
    wrap.enter();
    Map<LocationDescription, List<String>> locationD = wrap.describeLocation();
    if (!(((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)))) {
      fail("wrap dungeon: didn't start at the predicted start.");
    }

    makeMove("R", locationD.get(LocationDescription.MOVES), wrap, null);
    locationD = wrap.describeLocation();

    if (!(((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)))) {
      fail("wrap dungeon: didn't move to the right location.");
    }

    List<Direction> movedD = new ArrayList<>();
    while (!wrap.gameEnded()) {
      locationD = wrap.describeLocation();
      makeMove("R", locationD.get(LocationDescription.MOVES), wrap, movedD);
    }
    assertTrue("game should have made move in more than 1 direction.",
            movedD.size() > 1);

    boolean isWrapping = false;

    for (int i = 0; i < 100; i++) {
      Dungeon wrapT;
      while (true) {
        wrapT = new WrappingDungeon(
                "player1", 5, 6, 0, 100);
        wrapT.enter();
        if (wrapT.getPlayerLocation().equals("1,0")) {
          wrapT.collectTreasure();
          break;
        }
      }
      while (!wrapT.gameEnded()) {
        locationD = wrapT.describeLocation();
        String prev = wrapT.getPlayerLocation();
        makeMove("R", locationD.get(LocationDescription.MOVES), wrapT, null);
        String next = wrapT.getPlayerLocation();
        isWrapping = isWrapLocation(prev, next, 5, 6);
        if (isWrapping) {
          break;
        }
      }
      if (isWrapping) {
        break;
      }
    }
    assertTrue("should involve at least 1 wrap move.", isWrapping);
  }

  private boolean isWrapLocation(String prev, String next, int row, int col) {
    String[] prevIJ = prev.split(",");
    int prevI = Integer.parseInt(prevIJ[0]);
    int prevJ = Integer.parseInt(prevIJ[1]);
    String[] nextIJ = next.split(",");
    int nextI = Integer.parseInt(nextIJ[0]);
    int nextJ = Integer.parseInt(nextIJ[1]);

    if (prevI < nextI) {
      if ((prevI == 0) && (nextI == (row - 1))) {
        return true;
      }
    } else if (prevI > nextI) {
      if ((prevI == (row - 1)) && (nextI == 0)) {
        return true;
      }
    } else if (prevI == nextI) {
      if (prevJ < nextJ) {
        if ((prevJ == 0) && (nextJ == (col - 1))) {
          return true;
        }
      } else if (prevJ > nextJ) {
        if ((prevJ == (col - 1)) && (nextJ == 0)) {
          return true;
        }
      }
    }
    return false;
  }

  @Test
  public void moveNonWrap() {
    nonWrapR.enter();
    Map<LocationDescription, List<String>> locationD = nonWrapR.describeLocation();
    if (!(((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)))) {
      fail("random non-wrap dungeon: didn't start at the predicted start.");
    }

    makeMove("F", locationD.get(LocationDescription.MOVES), nonWrapR, null);
    locationD = nonWrapR.describeLocation();

    if (!(((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) < 5)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) >= 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) < 6)))) {
      fail("random non-wrap dungeon: didn't move to the right location.");
    }

    List<Direction> movedD = new ArrayList<>();
    while (!nonWrapR.gameEnded()) {
      locationD = nonWrapR.describeLocation();
      makeMove("R", locationD.get(LocationDescription.MOVES), nonWrapR, movedD);
    }
    assertTrue("game should have made move in more than 1 direction.",
            movedD.size() > 1);
  }

  @Test(expected = IllegalStateException.class)
  public void collectTreasureInvalidWrapD() {
    wrap.collectTreasure();
  }

  @Test(expected = IllegalStateException.class)
  public void collectTreasureInvalidNonWrapD() {
    nonWrapR.collectTreasure();
  }

  @Test
  public void collectTreasureRepeatWrapD() {
    assertTrue("collect treasure should work for wrapping dungeon.",
            collectTreasureRepeat(true));
  }

  @Test
  public void collectTreasureRepeatNonWrapD() {
    assertTrue("collect treasure should work for non wrapping dungeon.",
            collectTreasureRepeat(false));
  }

  private boolean collectTreasureRepeat(boolean wrapDung) {
    Dungeon dungT;
    if (wrapDung) {
      dungT = new WrappingDungeon("player1", 5, 6, 0, 100);
    } else {
      dungT = new NonWrappingDungeon("player1", 5, 6, 0, 100);
    }
    dungT.enter();
    Map<Treasure, Integer> treasureAtLocQ = dungT.collectTreasure();

    int zCount = 0;
    for (Treasure t : Treasure.values()) {
      if (treasureAtLocQ.get(t) == 0) {
        zCount++;
      }
    }
    if (zCount == 3) {
      fail("location must have treasure.");
    }

    Map<PlayerDescription, List<String>> playerD = dungT.describePlayer();
    assertTrue("treasure should match.",
            treasureMatch(treasureAtLocQ.get(Treasure.DIAMONDS),
                    treasureAtLocQ.get(Treasure.RUBIES),
                    treasureAtLocQ.get(Treasure.SAPPHIRES),
                    playerD.get(PlayerDescription.TREASURE)));

    Map<Treasure, Integer> res = dungT.collectTreasure();
    playerD = dungT.describePlayer();
    assertTrue("treasure should match.",
            treasureMatch(treasureAtLocQ.get(Treasure.DIAMONDS),
                    treasureAtLocQ.get(Treasure.RUBIES),
                    treasureAtLocQ.get(Treasure.SAPPHIRES),
                    playerD.get(PlayerDescription.TREASURE)));
    return true;
  }

  private boolean collectTreasure(Dungeon random) {
    random.enter();
    Map<PlayerDescription, List<String>> playerD = random.describePlayer();
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 0, 0,
                    playerD.get(PlayerDescription.TREASURE)));

    playerD = random.describePlayer();
    if (!((playerD.get(PlayerDescription.TREASURE).get(0)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (playerD.get(PlayerDescription.TREASURE).get(1)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (playerD.get(PlayerDescription.TREASURE).get(2)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+")))) {
      fail("wrap random dungeon: treasure should be in right format after collecting treasure.");
    }

    random.collectTreasure();
    Map<LocationDescription, List<String>> locationD = random.describeLocation();
    List<String> possibleMoves = locationD.get(LocationDescription.MOVES);
    makeMove("R", possibleMoves, random, null);

    playerD = random.describePlayer();
    if (!((playerD.get(PlayerDescription.TREASURE).get(0)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (playerD.get(PlayerDescription.TREASURE).get(1)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (playerD.get(PlayerDescription.TREASURE).get(2)
            .matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+")))) {
      fail("wrap random dungeon: treasure should be in right format after collecting treasure.");
    }
    return true;
  }

  private Map<Treasure, Integer> nonZeroT(List<Integer> treasureAtLocQ) {
    Map<Treasure, Integer> result = new HashMap<>();
    for (int i = 0; i < 3; i++) {
      int tresQ = treasureAtLocQ.get(i);
      if (tresQ != 0) {
        if (i == 0) {
          result.put(Treasure.DIAMONDS, tresQ);
        } else if (i == 1) {
          result.put(Treasure.RUBIES, tresQ);
        } else if (i == 2) {
          result.put(Treasure.SAPPHIRES, tresQ);
        }
      }
    }
    return result;
  }

  private boolean checkNumTresCaves(boolean wrapDung) {
    Dungeon dungT;
    if (wrapDung) {
      dungT = new WrappingDungeon(
              "player1", 5, 6, 0, 100, randP);
    } else {
      dungT = new NonWrappingDungeon(
              "player1", 5, 6, 0, 100, randP);
    }
    dungT.enter();

    Map<LocationDescription, List<String>> locationD;
    List<String> possibleMoves;
    List<String> treasureAtLoc = new ArrayList<>();
    List<Integer> treasureAtLocQ = new ArrayList<>();
    Map<LocationDescription, List<String>> movedLD;
    Map<Treasure, Integer> nonZeroTreasure = new HashMap<>();
    List<String> treasureL = new ArrayList<>();

    while (true) {
      locationD = dungT.describeLocation();
      treasureAtLoc = locationD.get(LocationDescription.TREASURE);
      treasureAtLocQ = parseTValAtLoc(treasureAtLoc);
      nonZeroTreasure = nonZeroT(treasureAtLocQ);
      if (!nonZeroTreasure.isEmpty()) {
        if (!(treasureL.contains(dungT.getPlayerLocation()))) {
          treasureL.add(dungT.getPlayerLocation());
        }
      }
      if (!dungT.gameEnded()) {
        dungT.collectTreasure();
      }
      if (dungT.gameEnded()) {
        break;
      }
      possibleMoves = locationD.get(LocationDescription.MOVES);
      Map<PlayerDescription, List<String>> playerD = dungT.describePlayer();
      locationD = makeMove("R", possibleMoves, dungT, null);
      Map<PlayerDescription, List<String>> newPlayerD = dungT.describePlayer();

      int beforeMDiam = Integer.parseInt(playerD.get(PlayerDescription.TREASURE).get(0)
              .split("\\s")[1]);
      int beforeMRuby = Integer.parseInt(playerD.get(PlayerDescription.TREASURE).get(1)
              .split("\\s")[1]);
      int beforeMSap = Integer.parseInt(playerD.get(PlayerDescription.TREASURE).get(2)
              .split("\\s")[1]);
      boolean checkPlayerTreasure = treasureMatch(beforeMDiam, beforeMRuby, beforeMSap,
              newPlayerD.get(PlayerDescription.TREASURE));
      if (!checkPlayerTreasure) {
        // automatic collection of treasure happens only at end location.
        if (!(treasureL.contains(dungT.getPlayerLocation()))) {
          treasureL.add(dungT.getPlayerLocation());
        }
      }
    }

    if (wrapDung) {
      assertTrue("all caves should have treasure.", treasureL.size() == 2);
    } else {
      assertTrue("all caves should have treasure.", treasureL.size() == 2);
    }
    return true;
  }

  private boolean checkTreasureCons(boolean wrapDung) {
    Dungeon dungT;
    if (wrapDung) {
      dungT = new WrappingDungeon("player1", 5, 6, 1, 100);
    } else {
      dungT = new NonWrappingDungeon("player1", 5, 6, 1, 100);
    }
    dungT.enter();

    Map<PlayerDescription, List<String>> playerD = dungT.describePlayer();
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 0, 0,
                    playerD.get(PlayerDescription.TREASURE)));

    Map<LocationDescription, List<String>> locationD = dungT.describeLocation();
    List<String> possibleMoves;
    List<String> treasureAtLoc = new ArrayList<>();
    List<Integer> treasureAtLocQ = new ArrayList<>();
    Map<LocationDescription, List<String>> movedLD;
    List<Treasure> treasUniq = new ArrayList<>();
    List<Integer> treasureCAtLoc = new ArrayList<>();
    Map<Treasure, Integer> nonZeroTreasure = new HashMap<>();

    while (true) {
      treasureAtLoc = locationD.get(LocationDescription.TREASURE);
      treasureAtLocQ = parseTValAtLoc(treasureAtLoc);
      nonZeroTreasure = nonZeroT(treasureAtLocQ);
      if (!nonZeroTreasure.isEmpty()) {
        int s = 0;
        for (Map.Entry<Treasure, Integer> e : nonZeroTreasure.entrySet()) {
          if (!treasUniq.contains(e.getKey())) {
            treasUniq.add(e.getKey());
          }
          s++;
        }
        treasureCAtLoc.add(s);
      }
      if (dungT.gameEnded()) {
        break;
      }
      locationD = dungT.describeLocation();
      possibleMoves = locationD.get(LocationDescription.MOVES);
      locationD = makeMove("R", possibleMoves, dungT, null);
    }

    assertTrue("game should support more than 1 type of treasure.", treasUniq.size() != 1);
    if (!((treasureCAtLoc.contains(2)) || (treasureCAtLoc.contains(3)))) {
      fail("at least 1 cave should have more than 1 treasure.");
    }
    return true;
  }

  @Test
  public void collectTreasureNonWrapD() {
    assertTrue("collect treasure should work for non wrapping dungeon.",
            collectTreasure(nonWrapR));
  }

  @Test
  public void checkTreasureConsNonWrapD() {
    assertTrue("check treasure constraints should work for non wrapping dungeon.",
            checkTreasureCons(false));
  }

  @Test
  public void checkNumTreasLocNonWrapD() {
    assertTrue("checking treasure locations should work for non wrapping dungeon.",
            checkNumTresCaves(false));
  }

  @Test
  public void collectTreasureWrapD() {
    assertTrue("collect treasure should work for wrapping dungeon.",
            collectTreasure(wrapR));
  }

  @Test
  public void checkTreasureConsWrapD() {
    assertTrue("checking treasure constraints should work for wrapping dungeon.",
            checkTreasureCons(true));
  }

  @Test
  public void checkNumTreasLocWrapD() {
    assertTrue("checking treasure locations should work for wrapping dungeon.",
            checkNumTresCaves(true));
  }

  @Test(expected = IllegalStateException.class)
  public void getPlayerLocationInvalidWrap() {
    wrap.getPlayerLocation();
  }

  @Test(expected = IllegalStateException.class)
  public void getPlayerLocationInvalidNonWrap() {
    nonWrap.getPlayerLocation();
  }

  private boolean getPlayerLocation(Dungeon random) {
    random.enter();
    if (!(random.getPlayerLocation().matches("\\d+,\\d+"))) {
      fail("random dungeon - player location is incorrect");
    }

    Map<LocationDescription, List<String>> locationD = random.describeLocation();
    locationD = makeMove("F", locationD.get(LocationDescription.MOVES), random, null);

    String locationR = locationD.get(LocationDescription.ROW).get(0);
    String locationC = locationD.get(LocationDescription.COLUMN).get(0);

    String location = String.format("%s,%s", locationR, locationC);
    if (!(random.getPlayerLocation().equals(location))) {
      fail("random dungeon - player location is incorrect");
    }
    return true;
  }

  @Test
  public void getPlayerLocationWrap() {
    assertTrue("get player location should work for wrapping dungeon.",
            getPlayerLocation(wrapR));
  }

  @Test
  public void getPlayerLocationNonWrap() {
    assertTrue("get player location should work for non wrapping dungeon.",
            getPlayerLocation(nonWrapR));
  }

  @Test(expected = IllegalStateException.class)
  public void getInvalidPlayerLocWrapD() {
    wrap.getPlayerLocation();
  }

  @Test(expected = IllegalStateException.class)
  public void getInvalidPlayerLocNonWrapD() {
    nonWrap.getPlayerLocation();
  }

  private boolean gameStarted(Dungeon predictive, Dungeon random) {
    assertFalse("predictive dungeon: before player enters, game is not started.",
            predictive.gameStarted());
    assertFalse("random dungeon: before player enters, game is not started.",
            random.gameStarted());
    predictive.enter();
    random.enter();
    assertTrue("predictive dungeon: after player enters, game is started.",
            predictive.gameStarted());
    assertTrue("random dungeon: after player enters, game is started.",
            random.gameStarted());
    return true;
  }

  @Test
  public void gameStartedWrapD() {
    assertTrue("game started should work for wrapping dungeon.", gameStarted(wrap, wrapR));
  }

  @Test
  public void gameStartedNonWrapD() {
    assertTrue("game started should work for non wrapping dungeon.",
            gameStarted(nonWrap, nonWrapR));
  }

  private boolean gameEnded(Dungeon predictive, int row, int col) {
    assertFalse("predictive dungeon: before player enters, game has not ended.",
            predictive.gameEnded());

    predictive.enter();
    assertFalse("predictive dungeon: after player enters, game has not ended.",
            predictive.gameEnded());

    int moves = 0;
    Map<LocationDescription, List<String>> locationD;
    List<String> playerLocations = new ArrayList<>();

    playerLocations.add(predictive.getPlayerLocation());

    while (true) {
      if (predictive.gameEnded()) {
        break;
      } else {
        locationD = predictive.describeLocation();
        makeMove("R", locationD.get(LocationDescription.MOVES), predictive, null);
        if (!(playerLocations.contains(predictive.getPlayerLocation()))) {
          playerLocations.add(predictive.getPlayerLocation());
        }
        moves++;
      }
    }
    if (moves <= 5) {
      fail("distance from start to finish is less than 5.");
    }
    if (playerLocations.size() != (row * col)) {
      fail("should have visited every location.");
    }
    return true;
  }

  @Test
  public void gameEndedPredWrap() {
    assertTrue("game ended should work for wrapping dungeon.",
            gameEnded(wrap, 5, 6));
  }

  @Test
  public void gameEndedPredNonWrap() {
    assertTrue("game ended should work for non wrapping dungeon.",
            gameEnded(nonWrap, 5, 6));
  }

  @Test
  public void gameEndedTrueRWrap() {
    List<String> start = new ArrayList<>();
    List<String> end = new ArrayList<>();
    List<String> startLocType = new ArrayList<>();
    List<Direction> movedD = new ArrayList<>();

    Random r = new Random();
    for (int i = 0; i < 100; i++) {
      int row = 5;
      int col = 6;

      Dungeon randomW = new WrappingDungeon("player", row, col, 1, 15);

      randomW.enter();
      Map<LocationDescription, List<String>> locationD = randomW.describeLocation();

      if (!(start.contains(randomW.getPlayerLocation()))) {
        start.add(randomW.getPlayerLocation());
        String locT = locationD.get(LocationDescription.TYPE).get(0);
        if (!startLocType.contains(locT)) {
          startLocType.add(locT);
        }
      }

      int moves = 0;
      while (true) {
        if (randomW.gameEnded()) {
          if (!(end.contains(randomW.getPlayerLocation()))) {
            end.add(randomW.getPlayerLocation());
          }
          break;
        } else {
          locationD = randomW.describeLocation();
          makeMove("R", locationD.get(LocationDescription.MOVES), randomW, movedD);
          moves++;
        }
      }
      if (moves < 5) {
        fail("distance from start to finish is less than 5.");
      }
    }
    if (movedD.size() != 4) {
      fail("should have moved in all 4 directions.");
    }
    if (start.size() < 10) {
      fail("in 100 attempts of building random wrapped dungeon, the start is always same.");
    }
    if (end.size() < 10) {
      fail("in 100 attempts of building random wrapped dungeon, the end is always same.");
    }
    assertTrue("start must always be cave.",
            ((startLocType.size() == 1) && (startLocType.get(0).equals(LocationType.CAVE.name()))));

    List<String> paths = new ArrayList<>();
    Map<LocationDescription, List<String>> locationD;
    for (int i = 0; i < 100; i++) {
      StringBuffer path = new StringBuffer();
      wrapR.enter();
      path.append(wrapR.getPlayerLocation());
      while (true) {
        if (wrapR.gameEnded()) {
          break;
        } else {
          locationD = wrapR.describeLocation();
          makeMove("R", locationD.get(LocationDescription.MOVES), wrapR, null);
          path.append("-");
          path.append(wrapR.getPlayerLocation());
        }
      }
      if (!(paths.contains(path))) {
        paths.add(path.toString());
      }
      wrapR.reset();
    }
    if (paths.size() < 5) {
      fail("in 100 attempts playing the same grid randomly, the path is the same.");
    }
  }

  @Test
  public void gameEndedTrueRNonWrap() {
    List<String> start = new ArrayList<>();
    List<String> end = new ArrayList<>();
    List<String> startLocType = new ArrayList<>();

    Random r = new Random();
    for (int i = 0; i < 100; i++) {
      int row = 5;
      int col = 6;
      Dungeon randomNW = new NonWrappingDungeon("player", row, col, 0, 50);

      randomNW.enter();
      Map<LocationDescription, List<String>> locationD = randomNW.describeLocation();
      if (!(start.contains(randomNW.getPlayerLocation()))) {
        start.add(randomNW.getPlayerLocation());
        String locT = locationD.get(LocationDescription.TYPE).get(0);
        if (!startLocType.contains(locT)) {
          startLocType.add(locT);
        }
      }

      int moves = 0;
      while (true) {
        if (randomNW.gameEnded()) {
          if (!(end.contains(randomNW.getPlayerLocation()))) {
            end.add(randomNW.getPlayerLocation());
          }
          break;
        } else {
          locationD = randomNW.describeLocation();
          makeMove("R", locationD.get(LocationDescription.MOVES), randomNW, null);
          moves++;
        }
      }
      if (moves < 5) {
        fail("distance from start to finish is less than 5.");
      }
    }
    if (start.size() < 10) {
      fail("in 100 attempts of building random wrapped dungeon, the start is always same.");
    }
    if (end.size() < 10) {
      fail("in 100 attempts of building random wrapped dungeon, the end is always same.");
    }
    assertTrue("start must always be cave.",
            ((startLocType.size() == 1) && (startLocType.get(0).equals(LocationType.CAVE.name()))));

    List<String> paths = new ArrayList<>();
    Map<LocationDescription, List<String>> locationD;
    for (int i = 0; i < 100; i++) {
      StringBuffer path = new StringBuffer();
      nonWrapR.enter();
      path.append(nonWrapR.getPlayerLocation());
      while (true) {
        if (nonWrapR.gameEnded()) {
          break;
        } else {
          locationD = nonWrapR.describeLocation();
          makeMove("R", locationD.get(LocationDescription.MOVES), nonWrapR, null);
          path.append("-");
          path.append(nonWrapR.getPlayerLocation());
        }
      }
      if (!(paths.contains(path))) {
        paths.add(path.toString());
      }
      nonWrapR.reset();
    }
    if (paths.size() < 20) {
      fail("in 100 attempts playing the same grid randomly, the path is the same.");
    }
  }

  @Test(expected = IllegalStateException.class)
  public void resetInvalidWrap() {
    wrap.reset();
  }

  @Test(expected = IllegalStateException.class)
  public void resetInvalidNonWrap() {
    nonWrap.reset();
  }

  private boolean reset(Dungeon d) {
    d.enter();
    Map<PlayerDescription, List<String>> playerD = d.describePlayer();
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 0, 0,
                    playerD.get(PlayerDescription.TREASURE)));

    int finalD = 0;
    int finalR = 0;
    int finalS = 0;
    String start = d.getPlayerLocation();
    String end;

    Map<LocationDescription, List<String>> locationD;
    while (true) {
      if (d.gameEnded()) {
        end = d.getPlayerLocation();
        break;
      } else {
        d.collectTreasure();
        locationD = d.describeLocation();
        makeMove("R", locationD.get(LocationDescription.MOVES), d, null);
      }
    }
    playerD = d.describePlayer();
    List<String> t = playerD.get(PlayerDescription.TREASURE);
    if (!((t.size() == 3) && (t.get(0).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(1).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+"))
            && (t.get(2).matches("(DIAMONDS|RUBIES|SAPPHIRES)\\s\\d+")))) {
      fail("player's treasure doesn't match the expected format.");
    }

    finalD = Integer.parseInt(t.get(0).split("\\s")[1]);
    finalR = Integer.parseInt(t.get(1).split("\\s")[1]);
    finalS = Integer.parseInt(t.get(2).split("\\s")[1]);

    if (start.equals(end)) {
      fail("start and end are the same locations.");
    }
    assertTrue("game should have ended", d.gameEnded());
    d.reset();

    d.enter();
    playerD = d.describePlayer();
    if (!((start.equals(d.getPlayerLocation())
            && treasureMatch(0, 0, 0,
            playerD.get(PlayerDescription.TREASURE))
            && !(d.gameEnded())))) {
      fail("reset didn't work as expected.");
    }
    return true;
  }

  @Test
  public void resetWrapD() {
    assertTrue("reset should work for non wrapping dungeon.", reset(wrapR));
  }

  @Test
  public void resetNonWrapD() {
    assertTrue("reset should work for non wrapping dungeon.", reset(nonWrapR));
  }
}