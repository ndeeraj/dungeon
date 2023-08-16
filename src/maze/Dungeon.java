package maze;

import java.util.Map;

/**
 * Represents the operations that can be done in a dungeon.
 * There should be a path from every cave in the dungeon to every other cave in the dungeon.
 * One cave should be randomly selected as the start and one cave is randomly
 * selected to be the end.
 * The path between the start and the end locations should be at least of length 5.
 * Should support following operations:
 *     placing at least three types of treasure: diamonds, rubies, and sapphires.
 *     treasure to be added can be provided as a percentage of caves.
 *     provide a description of the player that, at a minimum, includes a description of what
 *     treasure the player has collected.
 *     provide a description of the player's location that at the minimum includes a description
 *     of treasure in the room and the possible moves (north, east, south, west) that the
 *     player can make from their current location.
 *     player to move from their current location.
 *     player to pick up treasure that is located in their same location.
 *     picking up weapons from the current location.
 *     shooting arrows in the given direction.
 */
public interface Dungeon extends ReadOnlyDungeon {

  /**
   * returns the start location of the dungeon.
   * @return start location formatted as "row,column"
   */
  String getStart();

  /**
   * returns the end location of the dungeon.
   * @return end location formatted as "row,column"
   */
  String getEnd();

  /**
   * places the player in dungeon at the start location and starts the game.
   *
   * @throws IllegalStateException when the player is already in the dungeon;
   *     when the play has ended.
   */
  void enter() throws IllegalStateException;



  /**
   * moves the player in the specified direction.
   * @param dir direction to move the player {@link Direction}.
   * @throws IllegalArgumentException when dir is null.
   * @throws IllegalStateException when the player has not entered the dungeon;
   *     when the game has ended; when the direction specified has no neighbouring tunnel or cave.
   */
  void move(Direction dir) throws IllegalArgumentException, IllegalStateException;

  /**
   * collects the treasure from the player location.
   *
   * @return {@link Map} with key as all possible {@link Treasure} and
   *     value as {@link Integer} indicating the quantity of treasure.
   *     If a particular {@link Treasure} is not found in the player location, the value for the
   *     treasure would be 0.
   *     returns null object if attempting to get treasure from the same location more than once.
   *
   * @throws IllegalStateException when the player has not entered the dungeon;
   *     when the game has ended;
   */
  Map<Treasure, Integer> collectTreasure() throws IllegalStateException;

  /**
   * resets the game, the dungeon constructed will remain the same.
   * After reset, player's chest will be cleared of all the treasure.
   * player will be placed outside the dungeon.
   * user should enter the dungeon to play again.
   *
   * @throws IllegalStateException when the hasn't been started.
   */
  void reset() throws IllegalStateException;

  /**
   * picks the weapon from the player location.
   *
   * @return {@link Map} with key as all possible {@link WeaponType} and
   *     value as {@link Integer} indicating the quantity of weapon.
   *     If a particular {@link WeaponType} is not found in the player location, the value for the
   *     weapon would be 0.
   *     returns null object if attempting to get weapon from the same location more than once.
   *
   * @throws IllegalStateException when the player has not entered the dungeon;
   *     when the game has ended;
   */
  Map<WeaponType, Integer> pickWeapon();

  /**
   * Shoots the arrow in the specified direction and distance.
   * Arrows travel freely down tunnels (even crooked ones),
   * but only travel in a straight line through a cave.
   * distance includes both tunnels and caves in the direction.
   * Monster is hit only if the distance is exact.
   *
   * @param dir {@link Direction} to shoot.
   * @param distance number of locations the arrow should travel.
   *
   * @return boolean indicating whether the shot is a hit or a miss.
   *
   * @throws IllegalArgumentException when dir is null; when distance <= 0.
   * @throws IllegalStateException when the player has no arrows left to shoot.
   */
  boolean shootArrow(Direction dir, int distance)
          throws IllegalArgumentException, IllegalStateException;
}
