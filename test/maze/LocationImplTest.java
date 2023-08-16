package maze;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * test class for {@link LocationImpl}.
 */
public class LocationImplTest {
  private Location cave;
  private Location tunnel;
  private Location temp1;

  @Before
  public void setUp() throws Exception {
    cave = new LocationImpl(0, 0);
    tunnel = new LocationImpl(0, 1);
    temp1 = new LocationImpl(1, 1);

    cave.setNeighbour(Direction.EAST, tunnel);
    tunnel.setNeighbour(Direction.WEST, cave);
    tunnel.setNeighbour(Direction.SOUTH, temp1);
  }

  @Test(expected = IllegalStateException.class)
  public void locationDefaultCons() {
    new LocationImpl();
  }

  @Test(expected = IllegalArgumentException.class)
  public void locationNegativeRow() {
    new LocationImpl(-1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void locationNegativeColumn() {
    new LocationImpl(1, -2);
  }

  @Test
  public void getRow() {
    assertEquals("row should match.", cave.getRow(), 0);
    assertEquals("row should match.", tunnel.getRow(), 0);
  }

  @Test
  public void getColumn() {
    assertEquals("column should match.", cave.getColumn(), 0);
    assertEquals("column should match.", tunnel.getColumn(), 1);
  }

  @Test
  public void getPossibleMoves() {
    assertTrue("locations should match - cave.",
            checkLocations(null, null, tunnel, null, cave));
    assertTrue("locations should match - tunnel.",
            checkLocations(null, cave, null, temp1, tunnel));
  }

  private boolean checkLocations(Location north, Location west, Location east,
                                 Location south, Location loc) {
    boolean result = true;
    Map<Direction, Location> p = loc.getPossibleMoves();
    if (p.get(Direction.NORTH) != north) {
      return false;
    }
    if (p.get(Direction.WEST) != west) {
      return false;
    }
    if (p.get(Direction.EAST) != east) {
      return false;
    }
    if (p.get(Direction.SOUTH) != south) {
      return false;
    }
    return result;
  }

  @Test
  public void getTreasure() {
    cave.placeTreasure(Treasure.DIAMONDS, 20);
    assertTrue("locations should match - cave.",
            treasureMatch(20, 0, 0, cave));
    assertTrue("locations should match - tunnel.",
            treasureMatch(0, 0, 0, tunnel));
  }

  private boolean treasureMatch(int diamondQ, int rubyQ, int sapphireQ, Location loc) {
    boolean result = true;
    Map<Treasure, Integer> treasureL = loc.getTreasure();
    if (treasureL.get(Treasure.DIAMONDS) != diamondQ) {
      return false;
    }
    if (treasureL.get(Treasure.RUBIES) != rubyQ) {
      return false;
    }
    if (treasureL.get(Treasure.SAPPHIRES) != sapphireQ) {
      return false;
    }
    return result;
  }

  @Test
  public void placeTreasure() {
    cave.placeTreasure(Treasure.DIAMONDS, 20);
    assertTrue("locations should match.",
            treasureMatch(20, 0, 0, cave));
    cave.placeTreasure(Treasure.RUBIES, 21);
    assertTrue("locations should match.",
            treasureMatch(20, 21, 0, cave));
    cave.placeTreasure(Treasure.SAPPHIRES, 1);
    assertTrue("locations should match.",
            treasureMatch(20, 21, 1, cave));
  }

  @Test(expected = IllegalStateException.class)
  public void placeRepeatTreasure() {
    cave.placeTreasure(Treasure.DIAMONDS, 20);
    cave.placeTreasure(Treasure.DIAMONDS, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void placeNullTreasure() {
    cave.placeTreasure(null, 20);
  }

  @Test(expected = IllegalArgumentException.class)
  public void placeZeroTreasure() {
    cave.placeTreasure(Treasure.RUBIES, 0);
  }

  @Test(expected = IllegalStateException.class)
  public void placeNegativeTreasureAtStart() {
    cave.placeTreasure(Treasure.RUBIES, -10);
  }

  @Test
  public void placeNegativeTreasure() {
    cave.placeTreasure(Treasure.RUBIES, 10);
    assertTrue("location treasure should match - cave.",
            treasureMatch(0, 10, 0, cave));
    cave.placeTreasure(Treasure.RUBIES, -10);
    assertTrue("location treasure should match - cave.",
            treasureMatch(0, 0, 0, cave));
  }

  @Test(expected = IllegalStateException.class)
  public void placeTreasureTunnel() {
    tunnel.placeTreasure(Treasure.RUBIES, 10);
  }

  @Test
  public void getLocationSignCave() {
    List<String> expected;
    List<String> result;
    cave.placeTreasure(Treasure.DIAMONDS, 20);
    Map<LocationDescription, List<String>> locationD = cave.getLocationSign();
    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) == 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) == 0)
            && (locationD.get(LocationDescription.TYPE).get(0).equals(LocationType.CAVE.name()))) {
      expected = new ArrayList<>();
      expected.add("null");
      expected.add("null");
      expected.add(tunnel.toString());
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
      expected = new ArrayList<>();
      expected.add("DIAMONDS 20");
      expected.add("RUBIES 0");
      expected.add("SAPPHIRES 0");
      result = locationD.get(LocationDescription.TREASURE);
      if (expected.size() == result.size()) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).equals(expected.get(i)))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.WEAPON);
      boolean check = checkWeaponInfo(0, result);

      assertTrue("weapon should be 0", check);

      result = locationD.get(LocationDescription.MONSTER);
      check = checkMonsterInfo(null, result);

      assertTrue("monster details should be correct", check);

      cave.placeWeapon(WeaponType.CROOKEDARROW, 2);
      cave.placeMonster();

      Monster m = new Otyugh();
      locationD = cave.getLocationSign();

      result = locationD.get(LocationDescription.WEAPON);
      check = checkWeaponInfo(2, result);

      assertTrue("weapon should be 2", check);

      result = locationD.get(LocationDescription.MONSTER);
      check = checkMonsterInfo(m, result);
      assertTrue("monster details should be correct", check);

    } else {
      fail("location signature fetched incorrectly.");
    }
  }

  @Test
  public void getLocationSignTunnel() {
    List<String> expected;
    List<String> result;
    Map<LocationDescription, List<String>> locationD = tunnel.getLocationSign();
    if ((Integer.parseInt(locationD.get(LocationDescription.ROW).get(0)) == 0)
            && (Integer.parseInt(locationD.get(LocationDescription.COLUMN).get(0)) == 1)
            && (locationD.get(LocationDescription.TYPE).get(0)
            .equals(LocationType.TUNNEL.name()))) {
      expected = new ArrayList<>();
      expected.add("null");
      expected.add(cave.toString());
      expected.add("null");
      expected.add(temp1.toString());
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
      expected = new ArrayList<>();
      expected.add("DIAMONDS 0");
      expected.add("RUBIES 0");
      expected.add("SAPPHIRES 0");
      result = locationD.get(LocationDescription.TREASURE);
      if (expected.size() == result.size()) {
        for (int i = 0; i < result.size(); i++) {
          if (!(result.get(i).equals(expected.get(i)))) {
            fail("location signature fetched incorrectly.");
          }
        }
      } else {
        fail("location signature fetched incorrectly.");
      }

      result = locationD.get(LocationDescription.WEAPON);
      boolean check = checkWeaponInfo(0, result);

      assertTrue("weapon should be 0", check);

      result = locationD.get(LocationDescription.MONSTER);
      check = checkMonsterInfo(null, result);

      assertTrue("monster details should be correct", check);

      tunnel.placeWeapon(WeaponType.CROOKEDARROW, 2);

      locationD = tunnel.getLocationSign();
      result = locationD.get(LocationDescription.WEAPON);
      check = checkWeaponInfo(2, result);

      assertTrue("weapon should be 2", check);

      result = locationD.get(LocationDescription.MONSTER);
      check = checkMonsterInfo(null, result);

      assertTrue("monster details should be correct", check);
    } else {
      fail("location signature fetched incorrectly.");
    }
  }

  private boolean checkMonsterInfo(Monster mons, List<String> result) {
    if (result.size() != 1) {
      return false;
    }
    if (mons == null) {
      return result.get(0).equals("null");
    } else {
      return result.get(0).equals(String.format("%s %d %d", mons.getType().name(),
              mons.getInitialHealth(),
              mons.getCurrentHealth()));
    }
  }

  private boolean checkWeaponInfo(int crookedArrQ, List<String> result) {
    int match = 0;
    for (String s : result) {
      if (s.equals(WeaponType.CROOKEDARROW.name() + " " + crookedArrQ)) {
        match++;
      }
    }
    return (match == result.size());
  }

  @Test
  public void getType() {
    assertEquals("type should match correctly - cave.", cave.getType(), LocationType.CAVE);
    assertEquals("type should match correctly - tunnel.",
            tunnel.getType(), LocationType.TUNNEL);
    assertEquals("type should match correctly.",
            temp1.getType(), null);
  }

  @Test
  public void setNeighbour() {
    Location neigh1 = new LocationImpl(1, 0);
    Location neigh2 = new LocationImpl(2, 1);
    Location neigh3 = new LocationImpl(1, 2);
    assertTrue(checkLocations(null, null, null, null, temp1));
    temp1.setNeighbour(Direction.NORTH, tunnel);
    assertTrue(checkLocations(tunnel, null, null, null, temp1));
    temp1.setNeighbour(Direction.WEST, neigh1);
    assertTrue(checkLocations(tunnel, neigh1, null, null, temp1));
    temp1.setNeighbour(Direction.EAST, neigh3);
    assertTrue(checkLocations(tunnel, neigh1, neigh3, null, temp1));
    temp1.setNeighbour(Direction.SOUTH, neigh2);
    assertTrue(checkLocations(tunnel, neigh1, neigh3, neigh2, temp1));
    temp1.setNeighbour(Direction.SOUTH, null);
    assertTrue(checkLocations(tunnel, neigh1, neigh3, null, temp1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNeighbourNullDir() {
    Location neigh1 = new LocationImpl(1, 0);
    cave.setNeighbour(null, neigh1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setNeighbourIncorrectLoc() {
    Location neigh1 = new LocationImpl(1, 5);
    cave.setNeighbour(Direction.SOUTH, neigh1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void placeNullWeapon() {
    cave.placeWeapon(null, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void placeZeroWeapon() {
    cave.placeWeapon(WeaponType.CROOKEDARROW, 0);
  }

  @Test
  public void placeWeaponTunnel() {
    tunnel.placeWeapon(WeaponType.CROOKEDARROW, 2);
    Map<WeaponType, Integer> weapons = tunnel.getWeaponInfo();
    int nonZeroWeapon = 0;
    for (WeaponType w : WeaponType.values()) {
      if (weapons.get(w) > 0) {
        nonZeroWeapon++;
      }
    }
    assertTrue("after placing weapon, should have weapon.", (nonZeroWeapon > 0));
  }

  private boolean weaponMatch(int crookedArrQ, Location loc) {
    Map<WeaponType, Integer> weaponL = loc.getWeaponInfo();
    int match = 0;
    for (WeaponType w : WeaponType.values()) {
      if (w == WeaponType.CROOKEDARROW) {
        if (weaponL.get(WeaponType.CROOKEDARROW) == crookedArrQ) {
          match++;
        }
      }
    }
    return (match == weaponL.size());
  }

  @Test
  public void placeWeapon() {
    cave.placeWeapon(WeaponType.CROOKEDARROW, 2);
    assertTrue("weapon placed should match",
            weaponMatch(2, cave));
    cave.placeWeapon(WeaponType.CROOKEDARROW, -1);
    assertTrue("weapon placed should match",
            weaponMatch(1, cave));
    cave.placeWeapon(WeaponType.CROOKEDARROW, -1);
    assertTrue("weapon placed should match",
            weaponMatch(0, cave));
    tunnel.placeWeapon(WeaponType.CROOKEDARROW, 10);
    assertTrue("weapon placed should match",
            weaponMatch(10, tunnel));
    tunnel.placeWeapon(WeaponType.CROOKEDARROW, -10);
    assertTrue("weapon placed should match",
            weaponMatch(0, tunnel));
  }

  @Test(expected = IllegalStateException.class)
  public void placeRepeatMonster() {
    cave.placeMonster();
    cave.placeMonster();
  }

  @Test(expected = IllegalStateException.class)
  public void placeMonsterTunnel() {
    tunnel.placeMonster();
  }

  @Test
  public void placeMonsterCave() {
    cave.placeMonster();
    Monster m = new Otyugh();
    Monster res = cave.getMonster();
    assertEquals("monster placed should match",
            res.getInitialHealth(), m.getInitialHealth());
    assertEquals("monster placed should match",
            res.getCurrentHealth(), m.getCurrentHealth());
    m.slay();
    res = cave.getMonster();
    res.slay();
    assertEquals("monster placed should match",
            res.getInitialHealth(), m.getInitialHealth());
    assertEquals("monster placed should match",
            res.getCurrentHealth(), m.getCurrentHealth());
  }
}
