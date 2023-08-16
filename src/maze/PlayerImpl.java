package maze;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Represents the player in the dungeon.
 * player has a treasure chest, armory of weapons and a status indicating one of
 * {@link PlayerStatus} based on the player's state in the dungeon.
 * A player should be constructed with a name so, using default constructor will result in
 * IllegalStateException.
 * Intentionally making the class package private since it should not be available outside
 * the package.
 */
final class PlayerImpl implements Player {
  private final String name;
  private final Map<Treasure, Integer> treasure;
  private final Map<WeaponType, Integer> armory;
  private PlayerStatus status;

  /**
   * Since a player should have a name, calling default constructor will throw an exception.
   * @throws IllegalStateException when calling default constructor.
   */
  public PlayerImpl() throws IllegalStateException {
    throw new IllegalStateException("player cannot be created without a name.");
  }

  /**
   * Initializes the player with given name and set the player status as {@link PlayerStatus}.ALIVE.
   * @param name name for the player.
   * @throws IllegalArgumentException when name is null or empty.
   */
  public PlayerImpl(String name) throws IllegalArgumentException {
    if ((name == null) || (name.length() == 0)) {
      throw new IllegalArgumentException("name of the player cannot be null or empty.");
    }
    this.name = name;
    this.treasure = new Hashtable<>();
    this.armory = new Hashtable<>();
    this.status = PlayerStatus.ALIVE;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void addTreasure(Treasure tres, int quantity) throws IllegalArgumentException {
    if (tres == null) {
      throw new IllegalArgumentException("treasure to add cannot be null.");
    }
    if (quantity <= 0) {
      throw new IllegalArgumentException("quantity to add cannot be zero or negative.");
    }
    int existingQ = (treasure.get(tres) == null) ? 0 : treasure.get(tres);
    treasure.put(tres, existingQ + quantity);
  }

  @Override
  public Map<PlayerDescription, List<String>> getPlayerSign() {
    Map<PlayerDescription, List<String>> playerD = new Hashtable<>();
    for (PlayerDescription item : PlayerDescription.values()) {
      List<String> temp = new ArrayList<>();
      if (item == PlayerDescription.NAME) {
        temp.add(this.name);
      } else if (item == PlayerDescription.TREASURE) {
        int existingQ = 0;
        for (Treasure t: Treasure.values()) {
          existingQ = (treasure.get(t) == null) ? 0 : treasure.get(t);
          temp.add(t.name() + " " + existingQ);
        }
      } else if (item == PlayerDescription.WEAPON) {
        int existingQ = 0;
        for (WeaponType t: WeaponType.values()) {
          existingQ = (armory.get(t) == null) ? 0 : armory.get(t);
          temp.add(t.name() + " " + existingQ);
        }
      }
      playerD.put(item, temp);
    }
    return playerD;
  }

  @Override
  public void addWeapon(WeaponType weapon, int quantity)
          throws IllegalArgumentException, IllegalStateException {
    if (weapon == null) {
      throw new IllegalArgumentException("weapon to add cannot be null.");
    }
    if (quantity == 0) {
      throw new IllegalArgumentException("quantity to add cannot be zero.");
    }
    int existingQ = (armory.get(weapon) == null) ? 0 : armory.get(weapon);
    int newQ = existingQ + quantity;
    if (newQ < 0) {
      throw new IllegalStateException(
              "number of weapon in the armory goes below 0: invalid operation");
    }
    armory.put(weapon, newQ);
  }

  @Override
  public PlayerStatus getPlayerStatus() {
    return this.status;
  }

  @Override
  public void setPlayerStatus(PlayerStatus status)
          throws IllegalArgumentException, IllegalStateException {
    if (status == null) {
      throw new IllegalArgumentException("status to set cannot be null.");
    }
    if ((this.status == PlayerStatus.DECEASED) && (status == PlayerStatus.ALIVE)) {
      throw new IllegalStateException("player cannot be brought back from death.");
    }
    this.status = status;
  }

  @Override
  public Map<WeaponType, Integer> getWeaponInfo() {
    Map<WeaponType, Integer> weaponInfo = new Hashtable<>();
    if (armory.isEmpty()) {
      for (WeaponType item : WeaponType.values()) {
        armory.put(item, 0);
      }
    }
    return Map.copyOf(armory);
  }
}
