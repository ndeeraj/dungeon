package maze;

import java.util.List;
import java.util.Map;

/**
 * Represents the operations that is possible on a player in the dungeon.
 * Intentionally making this package private since it should not be available outside the package.
 */
interface Player {

  /**
   * Returns the name of the player.
   *
   * @return player's name.
   */
  String getName();

  /**
   * Adds the treasure of given quantity to the player's chest.
   * @param treas {@link Treasure} indicating the treasure to be added.
   * @param quantity quantity to be added.
   *
   * @throws IllegalArgumentException when tres is null; when quantity is <= 0.
   */
  void addTreasure(Treasure treas, int quantity) throws IllegalArgumentException;

  /**
   * generates the player description.
   * @return {@link Map} with key as {@link PlayerDescription} and value as {@link List} of strings.
   *     for {@link PlayerDescription}.NAME, the list will have 1 element with name.
   *     for {@link PlayerDescription}.TREASURE, the list will have elements equal to number of
   *     elements in {@link Treasure} and the order of elements will be same as the order of
   *     treasures in {@link Treasure}. Each element will be formatted as
   *     "[{@link Treasure}] [quantity]" where quantity is an integer. e.g., "DIAMONDS 20".
   *     If no treasure of a particular {@link Treasure} is found, it will be "RUBIES 0"
   *     for {@link PlayerDescription}.WEAPON, the list will have elements equal to number of
   *     elements in {@link WeaponType} and the order of elements will be same as the order of
   *     weapons in {@link WeaponType}. Each element will be formatted as
   *     "[{@link WeaponType}] [quantity]" where quantity is an integer. e.g., "CROOKEDARROW 20".
   *     If no weapon of a particular {@link WeaponType} is found, it will be "CROOKEDARROW 0"
   */
  Map<PlayerDescription, List<String>> getPlayerSign();

  /**
   * Adds the weapon of given quantity to the player's armory.
   * @param weapon {@link WeaponType} indicating the weapon to be added.
   * @param quantity quantity to be added.
   *
   * @throws IllegalArgumentException when weapon is null; when quantity is == 0.
   * @throws IllegalStateException when given quantity takes the total quantity < 0.
   */
  void addWeapon(WeaponType weapon, int quantity)
          throws IllegalArgumentException, IllegalStateException;

  /**
   * Fetches the status of the player in the dungeon.
   * @return one of {@link PlayerStatus}
   */
  PlayerStatus getPlayerStatus();

  /**
   * sets the status of the player to the provided {@link PlayerStatus}.
   * @param status {@link PlayerStatus}
   * @throws IllegalArgumentException when status is null.
   * @throws IllegalStateException when setting {@link PlayerStatus}.ALIVE when player is DECEASED
   *     state.
   */
  void setPlayerStatus(PlayerStatus status) throws IllegalArgumentException, IllegalStateException;

  /**
   * generates the player's weapon info.
   * @return {@link Map} with key as {@link WeaponType} and value as {@link Integer}.
   *     If no treasure of a particular {@link WeaponType} is found, it will be 0
   */
  Map<WeaponType, Integer> getWeaponInfo();
}
