package maze;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link PlayerImpl}.
 */
public class PlayerImplTest {
  private Player pl;

  @Before
  public void setUp() throws Exception {
    pl = new PlayerImpl("player1");
  }

  @Test(expected = IllegalStateException.class)
  public void playerNoArgs() {
    new PlayerImpl();
  }

  @Test(expected = IllegalArgumentException.class)
  public void playerNameNull() {
    new PlayerImpl(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void playerNameEmpty() {
    new PlayerImpl("");
  }

  @Test
  public void getName() {
    assertEquals("name should be correct.", pl.getName(), "player1");
  }

  private boolean treasureMatch(int diamondQ, int rubyQ, int sapphireQ, Player p) {
    Map<PlayerDescription, List<String>> playerD = p.getPlayerSign();
    List<String> treasure = playerD.get(PlayerDescription.TREASURE);
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

  @Test
  public void addTreasure() {
    pl.addTreasure(Treasure.DIAMONDS, 20);
    assertTrue("player treasure should match.",
            treasureMatch(20, 0, 0, pl));
    pl.addTreasure(Treasure.DIAMONDS, 21);
    assertTrue("player treasure should match.",
            treasureMatch(41, 0, 0, pl));
    pl.addTreasure(Treasure.RUBIES, 21);
    assertTrue("player treasure should match.",
            treasureMatch(41, 21, 0, pl));
    pl.addTreasure(Treasure.RUBIES, 21);
    assertTrue("player treasure should match.",
            treasureMatch(41, 42, 0, pl));
    pl.addTreasure(Treasure.SAPPHIRES, 21);
    assertTrue("player treasure should match.",
            treasureMatch(41, 42, 21, pl));
    pl.addTreasure(Treasure.SAPPHIRES, 21);
    assertTrue("player treasure should match.",
            treasureMatch(41, 42, 42, pl));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addTreasureNull() {
    pl.addTreasure(null, 21);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addTreasureZero() {
    pl.addTreasure(Treasure.DIAMONDS, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addTreasureNegative() {
    pl.addTreasure(Treasure.DIAMONDS, -1);
  }

  private boolean weaponMatch(int crookedArrQ, Player p) {
    Map<PlayerDescription, List<String>> playerD = p.getPlayerSign();
    List<String> weaponInfo = playerD.get(PlayerDescription.WEAPON);
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
  public void getPlayerSign() {
    Map<PlayerDescription, List<String>> playerD = pl.getPlayerSign();
    assertEquals("player name should match",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 0, 0, pl));
    assertTrue("initial weapon quantity should match.",
            weaponMatch(0, pl));

    pl.addTreasure(Treasure.RUBIES, 21);
    pl.addWeapon(WeaponType.CROOKEDARROW, 2);

    playerD = pl.getPlayerSign();
    assertEquals("player name should match",
            playerD.get(PlayerDescription.NAME).get(0), "player1");
    assertTrue("initial player treasure should match.",
            treasureMatch(0, 21, 0, pl));
    assertTrue("weapon quantity should match.",
            weaponMatch(2, pl));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addWeaponNull() {
    pl.addWeapon(null, 21);
  }

  @Test(expected = IllegalArgumentException.class)
  public void addWeaponZero() {
    pl.addWeapon(WeaponType.CROOKEDARROW, 0);
  }


  @Test(expected = IllegalStateException.class)
  public void dropWeaponWhenZeroW() {
    pl.addWeapon(WeaponType.CROOKEDARROW, -1);
  }

  @Test
  public void addWeapon() {
    Map<WeaponType, Integer> weaponD = pl.getWeaponInfo();
    assertEquals("player should start with 0 weapon",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 0);

    pl.addWeapon(WeaponType.CROOKEDARROW, 2);
    weaponD = pl.getWeaponInfo();
    assertEquals("player picks a weapon - should be 2",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 2);

    pl.addWeapon(WeaponType.CROOKEDARROW, 10);
    weaponD = pl.getWeaponInfo();
    assertEquals("player picks a weapon - should be 12",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 12);

    pl.addWeapon(WeaponType.CROOKEDARROW, -1);
    weaponD = pl.getWeaponInfo();
    assertEquals("player loses a weapon - should be 11",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 11);

    pl.addWeapon(WeaponType.CROOKEDARROW, -10);
    weaponD = pl.getWeaponInfo();
    assertEquals("player loses a weapon - should be 1",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 1);

    pl.addWeapon(WeaponType.CROOKEDARROW, 2);
    weaponD = pl.getWeaponInfo();
    assertEquals("player picks a weapon - should be 3",
            weaponD.get(WeaponType.CROOKEDARROW), (Integer) 3);
  }

  @Test
  public void testPlayerStatus() {
    assertEquals("status should be correct.", pl.getPlayerStatus(), PlayerStatus.ALIVE);
    pl.setPlayerStatus(PlayerStatus.ALIVE);
    assertEquals("status should be correct, when alive is set while the player is alive.",
            pl.getPlayerStatus(), PlayerStatus.ALIVE);
    pl.setPlayerStatus(PlayerStatus.DECEASED);
    assertEquals("status should be correct.", pl.getPlayerStatus(), PlayerStatus.DECEASED);
    pl.setPlayerStatus(PlayerStatus.DECEASED);
    assertEquals(
            "status should be correct, when deceased is set while the player is deceased.",
            pl.getPlayerStatus(), PlayerStatus.DECEASED);
  }

  @Test(expected = IllegalStateException.class)
  public void setIllegalStatus() {
    pl.setPlayerStatus(PlayerStatus.DECEASED);
    pl.setPlayerStatus(PlayerStatus.ALIVE);
  }
}