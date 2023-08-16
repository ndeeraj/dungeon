package mazetest;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maze.CustomRandomInteger;
import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;
import maze.LocationType;
import maze.NonWrappingDungeon;
import maze.PlayerDescription;
import maze.PlayerStatus;
import maze.RandomInteger;
import maze.SmellIntensity;
import maze.Treasure;
import maze.WeaponType;
import maze.WrappingDungeon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testing class specifically for tests concentrating on dungeon with monsters.
 */
public class MonsterDungeonTest {
  private RandomInteger rand;
  private RandomInteger randP;
  private Dungeon wrap;
  private static final int DONT_SHOOT = 0;
  private static final int SHOOT_AT_START_AND_BEFORE_END = 1;
  private static final int SHOOT_AT_TWO_LOC_BEFORE_END = 2;
  private static final int SHOOT_ONCE_BEFORE_END = 3;
  private static final int SHOOT_TWICE_LOC_BEFORE_END = 4;

  @Before
  public void setUp() throws Exception {
    rand = new CustomRandomInteger();
    randP = new CustomRandomInteger(true);
    // must have 1 as interconnectivity, dependency for playerDescription test.
    wrap = new WrappingDungeon("player1", 5, 6,
            1, 50, 1, randP);
  }

  @Test
  public void checkPlayerInitialArrow() {
    wrap.enter();
    Map<PlayerDescription, List<String>> playerD = wrap.describePlayer();
    boolean check = checkWeapon(3, playerD.get(PlayerDescription.WEAPON));
    assertTrue("player should initially have 3 arrows.", check);
  }

  private boolean checkWeapon(int crookedArrQ, List<String> weaponS) {
    List<String> weaponInfo = weaponS;
    for (String w : weaponInfo) {
      String[] weapQ = w.split("\\s");
      if (weapQ[0].equals(WeaponType.CROOKEDARROW.name())) {
        if (Integer.parseInt(weapQ[1]) != crookedArrQ) {
          return false;
        }
      }
    }
    return true;
  }

  @Test
  public void nonWrapPReachingEndSmellCheck() {
    assertTrue("checking smell at all the locations in the dungeon",
            nonWrapPReachingEndSlayMHelper(DONT_SHOOT));
  }

  @Test
  public void nonWrapPReachingEndSlayStrat1() {
    assertTrue("should slay monster at the end after going through"
                    + " all the locations in the dungeon",
            nonWrapPReachingEndSlayMHelper(SHOOT_AT_START_AND_BEFORE_END));
  }

  @Test
  public void nonWrapPReachingEndSlayStrat2() {
    assertTrue("should slay monster at the end after going through"
                    + " all the locations in the dungeon",
            nonWrapPReachingEndSlayMHelper(SHOOT_AT_TWO_LOC_BEFORE_END));
  }

  @Test
  public void nonWrapPReachingEndSlayStrat3() {
    assertTrue("should slay monster once and go through"
                    + " all the locations in the dungeon",
            nonWrapPReachingEndSlayMHelper(SHOOT_ONCE_BEFORE_END));
  }

  @Test
  public void nonWrapPReachingEndSlayStrat4() {
    assertTrue("should slay monster once and go through"
                    + " all the locations in the dungeon",
            nonWrapPReachingEndSlayMHelper(SHOOT_TWICE_LOC_BEFORE_END));
  }

  private boolean nonWrapPReachingEndSlayMHelper(int shootStrategy) {
    Dungeon nonWrapT;
    do {
      nonWrapT = new NonWrappingDungeon(
              "player1", 5, 6, 1, 50, 1, randP);
      nonWrapT.enter();
    }
    while (!nonWrapT.getPlayerLocation().equals("0,0"));

    boolean monsterInTunnels = false;
    boolean monsterAtStart = true;
    boolean monsterAtEnd = false;
    Map<LocationDescription, List<String>> locationD;
    Map<PlayerDescription, List<String>> playerD;
    List<Integer> arrowsAtLocQ = new ArrayList<>();
    boolean checkW;
    int numMonster = 0;

    boolean checkSmell;
    boolean shootResult = false;

    if (shootStrategy == SHOOT_AT_START_AND_BEFORE_END) {
      // wrong direction.
      shootResult = nonWrapT.shootArrow(Direction.WEST, 2);
      playerD = nonWrapT.describePlayer();
      checkW = checkWeapon(2, playerD.get(PlayerDescription.WEAPON));
      assertTrue("player should initially have 2 arrow.", checkW);
      assertFalse("shoot feedback should be correct.", shootResult);

      shootResult = nonWrapT.shootArrow(Direction.EAST, 1);
      assertTrue("shoot feedback should be correct.", shootResult);
    }

    assertFalse("predictive dungeon: after player enters, game has not ended.",
            nonWrapT.gameEnded());

    // since the game is not ended, player is not eaten so monster is not at start.
    monsterAtStart = false;
    int moves = 0;
    List<String> arrowLoc = new ArrayList<>();

    List<String> playerLocations = new ArrayList<>();

    playerLocations.add(nonWrapT.getPlayerLocation());

    while (true) {
      if (nonWrapT.gameEnded()) {
        locationD = nonWrapT.describeLocation();
        arrowsAtLocQ = parseArrAtLoc(locationD.get(LocationDescription.WEAPON));
        int tempArrQ = arrowsAtLocQ.get(0);
        if (tempArrQ > 0) {
          if (!arrowLoc.contains(nonWrapT.getPlayerLocation())) {
            arrowLoc.add(nonWrapT.getPlayerLocation());
          }
        }
        break;
      } else {
        locationD = nonWrapT.describeLocation();

        if (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.TUNNEL.name())) {
          if (!locationD.get(LocationDescription.MONSTER).get(0).equals("null")) {
            monsterInTunnels = true;
          }
        }

        arrowsAtLocQ = parseArrAtLoc(locationD.get(LocationDescription.WEAPON));
        int tempArrQ = arrowsAtLocQ.get(0);
        if (tempArrQ > 0) {
          if (!arrowLoc.contains(nonWrapT.getPlayerLocation())) {
            arrowLoc.add(nonWrapT.getPlayerLocation());
          }
        }

        if (nonWrapT.getPlayerLocation().equals("4,3")) {
          checkSmell = checkSmellIntensity(
                  locationD.get(LocationDescription.SMELL), SmellIntensity.LOW);

          if (shootStrategy == SHOOT_AT_TWO_LOC_BEFORE_END) {
            assertTrue("smell should match the location setting.", checkSmell);
            shootResult = nonWrapT.shootArrow(Direction.EAST, 1);
            locationD = nonWrapT.describeLocation();
            checkSmell = checkSmellIntensity(
                    locationD.get(LocationDescription.SMELL), SmellIntensity.LOW);
            assertTrue("smell should match the location setting.", checkSmell);
            assertTrue("shoot feedback should be correct.", shootResult);
          }
        } else if (nonWrapT.getPlayerLocation().equals("4,4")) {
          checkSmell = checkSmellIntensity(
                  locationD.get(LocationDescription.SMELL), SmellIntensity.HIGH);
          assertTrue("smell should match the location setting.", checkSmell);
          // cave before end, smell intensity is HIGH so there is a monster at end.
          monsterAtEnd = true;
          if (shootStrategy != DONT_SHOOT) {
            shootResult = nonWrapT.shootArrow(Direction.EAST, 1);
            locationD = nonWrapT.describeLocation();
            assertTrue("shoot feedback should be correct.", shootResult);
            if ((shootStrategy == SHOOT_AT_START_AND_BEFORE_END)
                    || (shootStrategy == SHOOT_AT_TWO_LOC_BEFORE_END)) {
              checkSmell = checkSmellIntensity(
                      locationD.get(LocationDescription.SMELL), null);
            } else {
              checkSmell = checkSmellIntensity(
                      locationD.get(LocationDescription.SMELL), SmellIntensity.HIGH);
            }
            assertTrue("smell should match expectation.", checkSmell);
            if (shootStrategy == SHOOT_TWICE_LOC_BEFORE_END) {
              // wrong shot with non-exact distance.
              nonWrapT.shootArrow(Direction.EAST, 2);
              locationD = nonWrapT.describeLocation();
              checkSmell = checkSmellIntensity(
                      locationD.get(LocationDescription.SMELL), SmellIntensity.HIGH);
              assertTrue("after shooting wrong dist, monster should not be hit.",
                      checkSmell);

              playerD = nonWrapT.describePlayer();
              checkW = checkWeapon(1, playerD.get(PlayerDescription.WEAPON));
              assertTrue("player should initially have 1 arrow.", checkW);

              nonWrapT.shootArrow(Direction.EAST, 1);
              locationD = nonWrapT.describeLocation();
              checkSmell = checkSmellIntensity(
                      locationD.get(LocationDescription.SMELL), null);
              assertTrue("after shooting injured monster, smell should die.", checkSmell);

              playerD = nonWrapT.describePlayer();
              checkW = checkWeapon(0, playerD.get(PlayerDescription.WEAPON));
              assertTrue("player should initially have 0 arrow.", checkW);
            }
          } else {
            // location before end, I find a smell of high intensity, so a monster is present.
            // opt to not shoot
            numMonster++;
          }
        } else {
          checkSmell = checkSmellIntensity(
                  locationD.get(LocationDescription.SMELL), null);
        }

        assertTrue("smell should match the location setting.", checkSmell);
        if (nonWrapT.getPlayerLocation().equals("4,4")
                || nonWrapT.getPlayerLocation().equals("4,3")) {
          makeMove("E", locationD.get(LocationDescription.MOVES), nonWrapT, null);
        } else {
          makeMove("R", locationD.get(LocationDescription.MOVES), nonWrapT, null);
        }
        if (!(playerLocations.contains(nonWrapT.getPlayerLocation()))) {
          playerLocations.add(nonWrapT.getPlayerLocation());
        }
        moves++;
      }
    }
    if (shootStrategy == SHOOT_ONCE_BEFORE_END) {
      assertEquals("since player didn't slay, should be dead", nonWrapT.getPlayerStatus(),
              PlayerStatus.ALIVE);
    }
    if (shootStrategy == DONT_SHOOT) {
      assertEquals("since player didn't slay, should be dead", nonWrapT.getPlayerStatus(),
              PlayerStatus.DECEASED);
    }
    if (moves <= 5) {
      fail("distance from start to finish is less than 5.");
    }
    if (playerLocations.size() != (5 * 6)) {
      fail("should have visited every location.");
    }
    if ((shootStrategy == DONT_SHOOT) && (numMonster != 1)) {
      fail("number of monster should meet difficulty set.");
    }
    if (arrowLoc.size() != 15) {
      // 50 % of 30 caves
      fail("arrows not configured correctly");
    }

    assertFalse("monster should not be at start.", monsterAtStart);
    assertTrue("monster should be at the end.", monsterAtEnd);
    assertFalse("monster should not be in tunnels", monsterInTunnels);

    return true;
  }

  private boolean checkSmellIntensity(List<String> smellD, SmellIntensity intensity) {
    boolean result;
    if (intensity == null) {
      result = smellD.get(0).equals("null");
    } else {
      result = smellD.get(0).equals(intensity.name());
    }

    return result;
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
  public void wrapPReachingEndDontShoot() {
    Dungeon wrapT = new WrappingDungeon("player1", 5, 6, 3, 50,
            1, null);

    wrapT.enter();

    Map<PlayerDescription, List<String>> playerD;

    int arrQ = 3;
    int onlyArrowsFound = 0;
    int arrowLoc = 0;

    playerD = wrapT.describePlayer();
    boolean checkW = checkWeapon(arrQ, playerD.get(PlayerDescription.WEAPON));
    assertTrue("player should initially have 3 arrows.", checkW);

    Map<LocationDescription, List<String>> locationD;
    List<String> possibleMoves;
    List<Integer> treasureAtLocQ = new ArrayList<>();
    List<Integer> arrowsAtLocQ = new ArrayList<>();
    Map<Treasure, Integer> nonZeroTreasure = new HashMap<>();

    while (true) {
      locationD = wrapT.describeLocation();
      treasureAtLocQ = parseTValAtLoc(locationD.get(LocationDescription.TREASURE));
      nonZeroTreasure = nonZeroT(treasureAtLocQ);
      boolean nonZTres = false;
      if (!nonZeroTreasure.isEmpty()) {
        nonZTres = true;
      }
      arrowsAtLocQ = parseArrAtLoc(locationD.get(LocationDescription.WEAPON));
      int tempArrQ = arrowsAtLocQ.get(0);
      if (tempArrQ > 0) {
        arrowLoc++;
      }
      if (!nonZTres && (tempArrQ > 0)) {
        onlyArrowsFound++;
      }
      if (wrapT.gameEnded()) {
        break;
      } else {
        if (tempArrQ > 0) {
          Map<WeaponType, Integer> collectedT = wrapT.pickWeapon();
          assertEquals("weapon picked quantity should match.",
                  collectedT.get(WeaponType.CROOKEDARROW), (Integer) tempArrQ);
          assertTrue("weapon type should have 1 weapon for now.", (collectedT.size() == 1));
          playerD = wrapT.describePlayer();
          checkW = checkWeapon(arrQ + tempArrQ, playerD.get(PlayerDescription.WEAPON));
          assertTrue("player should match.", checkW);
          arrQ = arrQ + tempArrQ;
        }
      }
      possibleMoves = locationD.get(LocationDescription.MOVES);
      makeMove("R", possibleMoves, wrapT, null);
    }

    if (onlyArrowsFound == 0) {
      fail("arrows should be found in location other than those with treasure.");
    }
  }

  @Test
  public void wrapRReachingEndSlayOnce() {
    List<PlayerStatus> statusAtEnd = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      Dungeon wrapT = new WrappingDungeon("player1", 5, 6, 3, 50,
              1, null);

      wrapT.enter();

      Map<PlayerDescription, List<String>> playerD;

      int arrQ = 3;
      int onlyArrowsFound = 0;

      playerD = wrapT.describePlayer();
      boolean checkW = checkWeapon(arrQ, playerD.get(PlayerDescription.WEAPON));
      assertTrue("player should initially have 3 arrows.", checkW);

      Map<LocationDescription, List<String>> locationD;
      List<String> possibleMoves;
      List<Integer> treasureAtLocQ = new ArrayList<>();
      List<Integer> arrowsAtLocQ = new ArrayList<>();
      Map<Treasure, Integer> nonZeroTreasure = new HashMap<>();

      while (true) {
        locationD = wrapT.describeLocation();
        treasureAtLocQ = parseTValAtLoc(locationD.get(LocationDescription.TREASURE));
        nonZeroTreasure = nonZeroT(treasureAtLocQ);
        boolean nonZTres = false;
        if (!nonZeroTreasure.isEmpty()) {
          nonZTres = true;
        }
        arrowsAtLocQ = parseArrAtLoc(locationD.get(LocationDescription.WEAPON));
        int tempArrQ = arrowsAtLocQ.get(0);
        if (!nonZTres && (tempArrQ > 0)) {
          onlyArrowsFound++;
        }
        if (wrapT.gameEnded()) {
          if (!statusAtEnd.contains(wrapT.getPlayerStatus())) {
            statusAtEnd.add(wrapT.getPlayerStatus());
          }
          break;
        } else {
          if (tempArrQ > 0) {
            Map<WeaponType, Integer> collectedT = wrapT.pickWeapon();
            assertEquals("weapon picked quantity should match.",
                    collectedT.get(WeaponType.CROOKEDARROW), (Integer) tempArrQ);
            assertTrue("weapon type should have 1 weapon for now.", (collectedT.size() == 1));
            playerD = wrapT.describePlayer();
            checkW = checkWeapon(arrQ + tempArrQ, playerD.get(PlayerDescription.WEAPON));
            assertTrue("player should match.", checkW);
            arrQ = arrQ + tempArrQ;
          }
        }
        if (wrapT.getLocationSmell() == SmellIntensity.HIGH) {
          wrapT.shootArrow(Direction.EAST, 1);
          arrQ--;
        }
        possibleMoves = locationD.get(LocationDescription.MOVES);
        makeMove("R", possibleMoves, wrapT, null);
      }

      if (onlyArrowsFound == 0) {
        fail("arrows should be found in location other than those with treasure.");
      }
    }
    if (statusAtEnd.size() != 2) {
      fail("In a 50-50 chance player should have survived once in 20 random games.");
    }
  }

  private List<Integer> parseArrAtLoc(List<String> arrowAtLoc) {
    List<Integer> result = new ArrayList<>();

    String weapons = arrowAtLoc.get(0);
    String[] t = weapons.split("\\s");
    result.add(Integer.parseInt(t[1]));

    return result;
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
}