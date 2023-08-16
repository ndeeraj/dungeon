package mazecontrollertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;
import maze.PlayerDescription;
import maze.PlayerStatus;
import maze.SmellIntensity;
import maze.Treasure;
import maze.WeaponType;

/**
 * Mocks {@link Dungeon} to test the controller independent of the model.
 * Intentionally making the class package private since it should not be available
 * outside the package.
 */
class MockDungeonModel implements Dungeon {

  private StringBuffer log;
  private int numMoves;
  private final int numMovesAllowed;
  private boolean gameStarted;
  private boolean gameEnded;

  /**
   * Initializes with number of moves provided.
   *
   * @param log {@link StringBuffer} to print inputs to mock.
   * @param numMoves number of moves to mock.
   * @throws IllegalArgumentException when log is empty; when numMoves <= 0
   */
  public MockDungeonModel(StringBuffer log, int numMoves) throws IllegalArgumentException {
    if (log == null) {
      throw new IllegalArgumentException("log cannot be null.");
    }
    if (numMoves <= 0) {
      throw new IllegalArgumentException("number of moves cannot be <= 0.");
    }
    this.log = log;
    this.numMovesAllowed = numMoves;
    this.numMoves = numMoves;
  }

  @Override
  public String getStart() {
    return null;
  }

  @Override
  public String getEnd() {
    return null;
  }

  @Override
  public void enter() throws IllegalStateException {
    gameStarted = true;
  }

  @Override
  public int getRow() {
    return 0;
  }

  @Override
  public int getCol() {
    return 0;
  }

  @Override
  public Map<PlayerDescription, List<String>> describePlayer() {
    return null;
  }

  @Override
  public Map<LocationDescription, List<String>> describeLocation() throws IllegalStateException {
    Map<LocationDescription, List<String>> result = new HashMap<>();
    List<String> defaultVal = new ArrayList<>();
    defaultVal.add("null");
    defaultVal.add("null");
    defaultVal.add("available");
    defaultVal.add("null");
    result.put(LocationDescription.MOVES, defaultVal);

    for (LocationDescription l : LocationDescription.values()) {
      if (l != LocationDescription.MOVES) {
        defaultVal = new ArrayList<>();
        if (l == LocationDescription.TREASURE) {
          defaultVal.add("D 0");
          defaultVal.add("R 0");
          defaultVal.add("S 10");
        } else if (l == LocationDescription.WEAPON) {
          defaultVal.add("CARR 1");
        }
        else {
          defaultVal.add("null");
        }
        result.put(l, defaultVal);
      }
    }
    return result;
  }

  @Override
  public void move(Direction dir) throws IllegalArgumentException, IllegalStateException {
    if (dir == null) {
      throw new IllegalArgumentException("direction to move cannot be null.");
    }
    if (!gameStarted) {
      throw new IllegalArgumentException("cannot make move, game not started.");
    }
    if (gameEnded) {
      throw new IllegalArgumentException("cannot make move, game ended.");
    }
    numMoves--;
    if (numMoves == 0) {
      gameEnded = true;
    }
    log.append("Direction: ").append(dir.name()).append("\n");
  }

  @Override
  public Map<Treasure, Integer> collectTreasure() throws IllegalStateException {
    return null;
  }

  @Override
  public String getPlayerLocation() throws IllegalStateException {
    return null;
  }

  @Override
  public boolean gameStarted() {
    return gameStarted;
  }

  @Override
  public boolean gameEnded() {
    return gameEnded;
  }

  @Override
  public void reset() throws IllegalStateException {
    numMoves = numMovesAllowed;
  }

  @Override
  public Map<WeaponType, Integer> pickWeapon() {
    return null;
  }

  @Override
  public boolean shootArrow(Direction dir, int distance)
          throws IllegalArgumentException, IllegalStateException {
    if (dir == null) {
      throw new IllegalArgumentException("direction to move cannot be null.");
    }
    if (distance <= 0) {
      throw new IllegalArgumentException("distance to shoot cannot be <= 0.");
    }
    log.append("Direction: ").append(dir.name()).append(", ").append("Distance: ")
            .append(distance).append("\n");
    return false;
  }

  @Override
  public PlayerStatus getPlayerStatus() {
    return null;
  }

  @Override
  public SmellIntensity getLocationSmell() {
    return null;
  }
}
