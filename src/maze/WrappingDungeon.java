package maze;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a wrapping dungeon.
 * The locations at the border of the dungeons can wrap to the other end.
 * All the rules and constraints of a non wrapping dungeon remain the same to a wrapping dungeon.
 */
public class WrappingDungeon extends AbstractDungeon {

  /**
   * operation not permitted.
   *
   * @throws IllegalStateException when calling default constructor.
   */
  public WrappingDungeon() throws IllegalStateException {
    throw new IllegalStateException("dungeon cannot be created without arguments.");
  }

  /**
   * initializes dungeon with a true random generator.
   * dungeon construction takes place in constructor which accepts a random generator
   * by passing null for the generator.
   */
  public WrappingDungeon(String pName, int row, int column, int interConn, int treasureP) {
    this(pName, row, column, interConn, treasureP, null);
  }

  /**
   * constructs the dungeon with the requested parameters.
   * will use the constructor that takes in the difficulty parameter, for exceptions and parameter
   * definition, refer to that constructor documentation.
   */
  public WrappingDungeon(
          String pName, int row, int column, int interConn, int treasureP, RandomInteger rand) {
    this(pName, row, column, interConn, treasureP, 0, rand);
  }

  /**
   * constructs the dungeon with the requested parameters.
   * construction will fail:
   * if row / column value is too small to generate a wrapping dungeon.
   * if treasure percentage requested is too small / above 100.
   * when interconnectivity provided is higher than maximum achievable interconnectivity possible
   * in the dungeon, will stop at max interconnectivity.
   *
   * @param pName     player name.
   * @param row       number of rows for the dungeon.
   * @param column    number of columns for the dungeon.
   * @param interConn degree of interconnectivity needed in the dungeon.
   * @param treasureP percentage of cave that should have the treasure.
   * @param rand      {@link RandomInteger}, can be null, in which case true random number generator
   *                  {@link RandomInteger} will be used.
   */
  public WrappingDungeon(
          String pName,
          int row,
          int column,
          int interConn,
          int treasureP,
          int difficulty,
          RandomInteger rand) throws IllegalArgumentException {
    super(pName, treasureP, rand, row, column, difficulty);

    if ((row < 5) || (column < 6)) {
      throw new IllegalArgumentException(
              "row, column value provided is small for creating a wrapping dungeon with "
                      + "distance between start and end as 5. "
                      + "row should be >= 5 and column should be >= 6.");
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
    setupWrappingEdges();
    createLocations();
    runKruskal(interConn);
    selectStartEnd();
    configureTreasure();
    if (numMonsters != 0) {
      configureArrows();
      configureMonsters();
    }
  }

  /*
  adds all the wrapping edges to the dungeon.
   */
  private void setupWrappingEdges() {
    List<Integer> edge;
    for (int i = 0; i < row; i++) {
      edge = new ArrayList<>();
      edge.add(i);
      edge.add(0);
      edge.add(i);
      edge.add(col - 1);
      allEdges.add(edge);
    }
    for (int i = 0; i < col; i++) {
      edge = new ArrayList<>();
      edge.add(0);
      edge.add(i);
      edge.add(row - 1);
      edge.add(i);
      allEdges.add(edge);
    }
  }

  @Override
  protected void setNeighbours(int sourceR, int sourceC, int destR, int destC) {
    Location source = dungeon[sourceR][sourceC];
    Location dest = dungeon[destR][destC];

    if (sourceR < destR) {
      if ((sourceR == 0) && (destR == (row - 1))) {
        source.setNeighbour(Direction.NORTH, dest);
        dest.setNeighbour(Direction.SOUTH, source);
      } else {
        source.setNeighbour(Direction.SOUTH, dest);
        dest.setNeighbour(Direction.NORTH, source);
      }
    } else if (sourceR > destR) {
      if ((sourceR == (row - 1)) && (destR == 0)) {
        source.setNeighbour(Direction.SOUTH, dest);
        dest.setNeighbour(Direction.NORTH, source);
      } else {
        source.setNeighbour(Direction.NORTH, dest);
        dest.setNeighbour(Direction.SOUTH, source);
      }
    } else if (sourceR == destR) {
      if (sourceC < destC) {
        if ((sourceC == 0) && (destC == (col - 1))) {
          source.setNeighbour(Direction.WEST, dest);
          dest.setNeighbour(Direction.EAST, source);
        } else {
          source.setNeighbour(Direction.EAST, dest);
          dest.setNeighbour(Direction.WEST, source);
        }
      } else if (sourceC > destC) {
        if ((sourceC == (col - 1)) && (destR == 0)) {
          source.setNeighbour(Direction.EAST, dest);
          dest.setNeighbour(Direction.WEST, source);
        } else {
          source.setNeighbour(Direction.WEST, dest);
          dest.setNeighbour(Direction.EAST, source);
        }
      }
    }
  }
}
