package maze;

import java.util.List;
import java.util.Map;

/**
 * Exposes all the read only queries that are available in a dungeon.
 */
public interface ReadOnlyDungeon {

  /**
   * fetches the number of rows in the dungeon.
   *
   * @return number of rows.
   */
  int getRow();

  /**
   * fetches the number of columns in the dungeon.
   *
   * @return number of columns.
   */
  int getCol();

  /**
   * generates the player description.
   * @return {@link Map} with key as {@link PlayerDescription} and value as {@link List} of strings.
   *     for {@link PlayerDescription}.NAME, the list will have 1 element with name.
   *     for {@link PlayerDescription}.TREASURE, the list will have elements equal to number of
   *     elements in {@link Treasure} and the order of elements will be same as the order of
   *     treasures in {@link Treasure}. Each element will be formatted as
   *     "[{@link Treasure}] [quantity]" where quantity is an integer. e.g., "DIAMONDS 20".
   *     If no treasure of a particular {@link Treasure} is found, it will be "RUBIES 0".
   *     for {@link PlayerDescription}.WEAPON, the list will have elements equal to number of
   *     elements in {@link WeaponType} and the order of elements will be same as the order of
   *     weapons in {@link WeaponType}. Each element will be formatted as
   *     "[{@link WeaponType}] [quantity]" where quantity is an integer. e.g., "CROOKEDARROW 20".
   *     If no weapon of a particular {@link WeaponType} is found, it will be "CROOKEDARROW 0".
   */
  Map<PlayerDescription, List<String>> describePlayer();

  /**
   * generates the location description.
   *
   * @return {@link Map} with key as {@link LocationDescription} and
   *     value as {@link List} of strings.
   *     for {@link LocationDescription}.ROW, the list will have 1 element with row value.
   *     for {@link LocationDescription}.COLUMN, the list will have 1 element with column value.
   *     for {@link LocationDescription}.TREASURE, the list will have elements equal to number of
   *     elements in {@link Treasure} and the order of elements will be same as the order of
   *     treasures in {@link Treasure}. Each element will be formatted as
   *     "[{@link Treasure}] [quantity]" where quantity is an integer. e.g., "DIAMONDS 20".
   *     If no treasure of a particular {@link Treasure} is found, it will be "RUBIES 0"
   *     for {@link LocationDescription}.MOVES, the list will have elements equal to number of
   *     elements in {@link Direction} and the order of elements will be same as the order of
   *     treasures in {@link Direction}. Each element will be {@link Location}.toString()
   *     If there is no possible move in a direction, the element corresponding to the direction
   *     will be formatted as "null"
   *     for {@link LocationDescription}.TYPE, the list will have 1 element with value
   *     same as one of {@link LocationType}.
   *     If the type can't be determined it will be represented as string "null".
   *     for {@link LocationDescription}.WEAPON, the list will have elements equal to number of
   *     elements in {@link WeaponType} and the order of elements will be same as the order of
   *     weapons in {@link WeaponType}. Each element will be formatted as
   *     "[{@link WeaponType}] [quantity]" where quantity is an integer. e.g., "CROOKEDARROW 20".
   *     If no weapon of a particular {@link WeaponType} is found, it will be "CROOKEDARR0W 0"
   *     for {@link LocationDescription}.MONSTER, the list have 1 element. If there is no monster in
   *     the location the element will be a string "null". else, the string will be formatted as
   *     "[{@link Monster}.getType()] [{@link Monster}.getInitialHealth()]
   *     [{@link Monster}.getCurrentHealth()]" e.g., "OTYUGH 2 1".
   *     for {@link LocationDescription}.SMELL, the list will have 1 element corresponding to
   *     {@link SmellIntensity}.name(). If the location has no smell, the String will be formatted
   *     as "null".
   *
   * @throws IllegalStateException when player has not entered the dungeon.
   */
  Map<LocationDescription, List<String>> describeLocation() throws IllegalStateException;

  /**
   * returns the location of the player as a string.
   * @return players location formatted as "row,column"
   * @throws IllegalStateException when player has not entered the dungeon.
   */
  String getPlayerLocation() throws IllegalStateException;

  /**
   * fetches the start status of the game.
   * entering the dungeon starts the game.
   *
   * @return boolean about the game start status.
   */
  boolean gameStarted();

  /**
   * fetches the end status of the game.
   * entering the end location of the dungeon ends the game.
   *
   * @return boolean about the game end status.
   */
  boolean gameEnded();

  /**
   * fetches the {@link PlayerStatus} of the player.
   * @return {@link PlayerStatus}.
   */
  PlayerStatus getPlayerStatus();

  /**
   * Fetches the {@link SmellIntensity} at the player's location.
   * @return {@link SmellIntensity}. If the player's location has no smell, will return null.
   */
  SmellIntensity getLocationSmell();
}
