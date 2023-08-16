package maze;

/**
 * Represents operation possible on a Monster in the dungeon.
 * Intentionally making the class package private so that it is not available outside the package.
 */
interface Monster {

  /**
   * Fetches the initial health of the monster.
   * @return health value as int.
   */
  int getInitialHealth();

  /**
   * Fetches the current health of the monster.
   * @return health value as int.
   */
  int getCurrentHealth();

  /**
   * slays the monster.
   * At each attack the health is reduced.
   * @return the amount by which the health was reduced.
   * @throws IllegalStateException when attempting to slay a Monster that has 0 as current health.
   */
  int slay() throws IllegalStateException;

  /**
   * fetches the {@link MonsterType} of the monster.
   * @return {@link MonsterType}.
   */
  MonsterType getType();
}
