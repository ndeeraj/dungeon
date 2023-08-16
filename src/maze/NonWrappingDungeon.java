package maze;

/**
 * Represents a non wrapping dungeon.
 * Each location in the dungeon is represented in row * column format, with 0,0 indicating
 * first location and row-1,col-1 indicating the last location.
 * Uses kruskal's algorithm to generate the grid.
 * If Interconnectivity specified is too large for the dungeon, will stop at maximum
 * interconnectivity possible with the dungeon configuration.
 * dungeon creation fails when it cannot be configured with given percentage of treasure locations.
 * The minimum distance between start to end is at least 5.
 * Game ends when the player reaches the end location.
 * Treasure at the end location will be picked automatically when the player reaches end.
 * for other caves, player must pick the treasure before leaving the location (it is not automatic).
 * uses {@link RandomInteger} to make random choices while constructing dungeon.
 * even when predictable random generator is used, will switch to a true random generation for:
 *     selecting start and end locations of the dungeon.
 *     placing the treasure in the caves.
 */
public class NonWrappingDungeon extends AbstractDungeon {

  /**
   * operation not permitted.
   *
   * @throws IllegalStateException when calling default constructor.
   */
  public NonWrappingDungeon() throws IllegalStateException {
    throw new IllegalStateException("dungeon cannot be created without arguments.");
  }

  /**
   * initializes dungeon with a true random generator.
   * dungeon construction takes place in constructor which accepts a random generator
   * by passing null for the generator.
   */
  public NonWrappingDungeon(String pName, int row, int column, int interConn, int treasureP) {
    this(pName, row, column, interConn, treasureP, null);
  }

  /**
   * constructs the dungeon with the requested parameters.
   * will use the constructor that takes in the difficulty parameter, for exceptions and parameter
   * definition, refer to that constructor documentation.
   */
  public NonWrappingDungeon(
          String pName, int row, int column, int interConn, int treasureP, RandomInteger rand)
          throws IllegalArgumentException {
    this(pName, row, column, interConn, treasureP, 0, rand);
  }

  /**
   * constructs the dungeon with the requested parameters.
   * construction will fail:
   * if row / column value is too small to generate a wrapping dungeon.
   * if treasure percentage requested is too small / above 100.
   * if difficulty is < 0
   * when interconnectivity provided is higher than maximum achievable interconnectivity possible
   * in the dungeon, will stop at max interconnectivity.
   *
   * @param pName     player name.
   * @param row       number of rows for the dungeon.
   * @param column    number of columns for the dungeon.
   * @param interConn degree of interconnectivity needed in the dungeon.
   * @param treasureP percentage of cave that should have the treasure.
   * @param difficulty number of monsters that should be configured in the dungeon.
   * @param rand      {@link RandomInteger}, can be null, in which case true random number generator
   *                  {@link RandomInteger} will be used.
   */
  public NonWrappingDungeon(
          String pName,
          int row,
          int column,
          int interConn,
          int treasureP,
          int difficulty,
          RandomInteger rand) throws IllegalArgumentException {
    super(pName, treasureP, rand, row, column, difficulty);

    if ((row < 4) || (column < 5)) {
      throw new IllegalArgumentException(
              "row, column value provided is small for creating a non wrapping dungeon with "
                      + "distance between start and end as 5. "
                      + "row should be >= 4, column should be >= 5");
    }

    if (interConn < 0) {
      throw new IllegalArgumentException(
              "inter connectivity value cannot be less than or equal to 0.");
    }
    createDungeon(interConn);
  }

  /*
  creates the dungeon.
   */
  private void createDungeon(int interConn) {
    setupNonWrappingEdges();
    createLocations();
    runKruskal(interConn);
    selectStartEnd();
    configureTreasure();
    if (numMonsters != 0) {
      configureArrows();
      configureMonsters();
    }
  }

  @Override
  protected void setNeighbours(int sourceR, int sourceC, int destR, int destC) {
    Location source = dungeon[sourceR][sourceC];
    Location dest = dungeon[destR][destC];
    if (sourceR < destR) {
      source.setNeighbour(Direction.SOUTH, dest);
      dest.setNeighbour(Direction.NORTH, source);
    } else if (sourceR > destR) {
      source.setNeighbour(Direction.NORTH, dest);
      dest.setNeighbour(Direction.SOUTH, source);
    } else if (sourceR == destR) {
      if (sourceC < destC) {
        source.setNeighbour(Direction.EAST, dest);
        dest.setNeighbour(Direction.WEST, source);
      } else if (sourceC > destC) {
        source.setNeighbour(Direction.WEST, dest);
        dest.setNeighbour(Direction.EAST, source);
      }
    }
  }
}
