package maze;

import java.util.List;
import java.util.Map;

/**
 * Represents the operations that are possible on a location in the dungeon.
 * A location can have neighbors in any of the {@link Direction}.
 * location can also contain treasures, weapons, monsters.
 * Intentionally making this package private since it should not be available outside the package.
 */
interface Location {
  /**
   * Returns the row value of the location.
   *
   * @return row value.
   */
  int getRow();

  /**
   * Returns the column value of the location.
   *
   * @return column value.
   */
  int getColumn();

  /**
   * Fetches all possible moves from this location.
   *
   * @return {@link Map} where key will be {@link Direction} and
   *     values will be {@link Location} objects.
   *     If there is no move in a particular {@link Direction}
   *     it's value will be filled with null object.
   */
  Map<Direction, Location> getPossibleMoves();

  /**
   * Fetches the treasure found in the location.
   *
   * @return {@link Map} where key will be {@link Treasure} and
   *     values will be {@link Integer}.
   *     If no treasure of a particular {@link Treasure} is found,
   *     the value for the key will be 0.
   */
  Map<Treasure, Integer> getTreasure();

  /**
   * Places the treasure of given quantity in the location.
   *
   * @param tres     {@link Treasure} to be placed.
   * @param quantity quantity of treasure to place.
   * @throws IllegalArgumentException when treasure to place is null; when quantity is <= 0.
   * @throws IllegalStateException    when placing treasure in tunnels; when the location already
   *                                  has treasure of the provided type.
   */
  void placeTreasure(Treasure tres, int quantity)
          throws IllegalArgumentException, IllegalStateException;

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
   *     If no weapon of a particular {@link WeaponType} is found, it will be "CROOKEDARRAW 0"
   *     for {@link LocationDescription}.MONSTER, the list have 1 element. If there is no monster in
   *     the location the element will be a string "null". else, the string will be formatted as
   *     "[{@link Monster}.getType()] [{@link Monster}.getInitialHealth()]
   *     [{@link Monster}.getCurrentHealth()]" e.g., "OTYUGH 2 1".
   */
  Map<LocationDescription, List<String>> getLocationSign();

  /**
   * fetches the type of the location.
   *
   * @return one of {@link LocationType}, null object if the location's type doesn't match
   *     any values in {@link LocationType}.
   */
  LocationType getType();

  /**
   * sets the passed location as the neighbour in the given direction.
   *
   * @param dir {@link Direction}
   * @param loc {@link Location} that should be set as neighbour.
   * @throws IllegalArgumentException when {@link Direction} passed is null;
   *                                  when passes {@link Location} cannot be
   *                                  set as neighbour to this location.
   */
  void setNeighbour(Direction dir, Location loc) throws IllegalArgumentException;

  /**
   * Fetches the weapon found in the location.
   *
   * @return {@link Map} where key will be {@link WeaponType} and
   *     values will be {@link Integer}.
   *     If there is no weapon of a particular {@link WeaponType},
   *     the value for the key will be 0.
   */
  Map<WeaponType, Integer> getWeaponInfo();

  /**
   * Creates and assigns a monster to the location.
   *
   * @throws IllegalStateException if the location already has a monster;
   *                               if the location is a tunnel.
   */
  void placeMonster() throws IllegalStateException;

  /**
   * Places the weapon of given type and quantity in the location.
   *
   * @param weapon   {@link WeaponType} to be placed.
   * @param quantity quantity of weapon to place.
   * @throws IllegalArgumentException when weapon to place is null; quantity is 0.
   * @throws IllegalStateException    when the quantity to add, takes the weapon quantity below 0.
   */
  void placeWeapon(WeaponType weapon, int quantity)
          throws IllegalArgumentException, IllegalStateException;

  /**
   * Fetches the monster in the location.
   *
   * @return object of {@link Monster}
   */
  Monster getMonster();
}
