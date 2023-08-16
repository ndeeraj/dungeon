package maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract dungeon implementation that captures common functionality in both wrapping and
 * non-wrapping dungeon.
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
 * selecting start and end locations of the dungeon.
 * placing the treasure in the caves.
 * Intentionally making the class package private so that it is not available outside the package.
 */
abstract class AbstractDungeon implements Dungeon {
  private Player player;
  private Location playerLocation;
  protected List<List<Integer>> allEdges;
  private final List<List<Integer>> selectedEdges;
  private final List<List<Integer>> leftOverEdges;
  private Location start;
  private Location end;
  private boolean started;
  private boolean ended;
  private final RandomInteger rand;
  private final RandomInteger trueRandom;
  protected final int row;
  protected final int col;
  private final Map<String, Map<String, Integer>> shortestPath;
  private final int treasureP;
  protected final int numMonsters;
  protected Location[][] dungeon;
  private List<Location> treasureCollected;
  private List<Location> arrowsCollected;
  private static final int MIN_START_END_DIST = 5;
  private static final int INJURED_OTYUGH_HEALTH = 1;
  private static final int HEALTH_OTYUGH_HEALTH = 2;

  /**
   * operation not permitted.
   *
   * @throws IllegalStateException when calling default constructor.
   */
  protected AbstractDungeon() throws IllegalStateException {
    throw new IllegalStateException("dungeon cannot be created without any arguments.");
  }

  /**
   * initializes the dungeon with provided row, col, treasure configuration.
   *
   * @param pName      name of the player.
   * @param treasureP  percentage of caves that should hold the treasure.
   * @param rand       {@link RandomInteger} to use for random generation.
   * @param row        number of rows that should be in the dungeon.
   * @param col        number of columns that should be in the dungeon.
   * @param difficulty number of monsters to be configured in the dungeon.
   * @throws IllegalArgumentException when player name is null or empty;
   *                                  when percentage of caves to place treasure in is less than
   *                                  or equal to 0 / greater than 100.
   *                                  when percentage of caves to place treasure is too small to
   *                                  select caves in the dungeon.
   */
  protected AbstractDungeon(
          String pName, int treasureP, RandomInteger rand, int row, int col, int difficulty)
          throws IllegalArgumentException {
    if ((pName == null) || (pName.length() == 0)) {
      throw new IllegalArgumentException("player name cannot be null or empty.");
    }
    if (treasureP <= 0) {
      throw new IllegalArgumentException(
              "percentage of caves to place treasure in, cannot be negative or 0.");
    }
    if (treasureP > 100) {
      throw new IllegalArgumentException(
              "percentage of caves to place treasure in, cannot be > 100.");
    }
    if (Math.round((treasureP / 100f) * (row * col)) < 1) {
      throw new IllegalArgumentException(
              "cannot configure treasure of given percentage with this size of dungeon.");
    }
    if (difficulty < 0) {
      throw new IllegalArgumentException(
              "difficulty cannot be less than 0");
    }
    this.player = new PlayerImpl(pName);
    if (rand == null) {
      rand = new CustomRandomInteger();
    }
    this.rand = rand;
    this.allEdges = new ArrayList<>();
    this.selectedEdges = new ArrayList<>();
    this.leftOverEdges = new ArrayList<>();
    this.row = row;
    this.col = col;
    this.dungeon = new Location[row][col];
    this.shortestPath = new HashMap<>();
    this.trueRandom = new CustomRandomInteger();
    this.treasureP = treasureP;
    this.treasureCollected = new ArrayList<>();
    this.numMonsters = difficulty;
    this.arrowsCollected = new ArrayList<>();
  }

  @Override
  public String getStart() {
    return String.format("%d,%d", start.getRow(), start.getColumn());
  }

  @Override
  public String getEnd() {
    return String.format("%d,%d", end.getRow(), end.getColumn());
  }

  @Override
  public void enter() throws IllegalStateException {
    try {
      checkGameStatus(false, false);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while entering the dungeon:" + ill.getMessage());
    }
    playerLocation = start;
    started = true;
    if (numMonsters != 0) {
      try {
        player.addWeapon(WeaponType.CROOKEDARROW, 3);
      } catch (IllegalStateException | IllegalArgumentException exp) {
        throw new IllegalStateException("error while initializing player with weapons");
      }
    }
  }

  @Override
  public int getRow() {
    return this.row;
  }

  @Override
  public int getCol() {
    return this.col;
  }

  @Override
  public Map<PlayerDescription, List<String>> describePlayer() {
    return player.getPlayerSign();
  }

  @Override
  public Map<LocationDescription, List<String>> describeLocation() throws IllegalStateException {
    try {
      checkGameStatus(true, null);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while describing player location:" + ill.getMessage());
    }
    Map<LocationDescription, List<String>> result = playerLocation.getLocationSign();

    SmellIntensity smell = getLocationSmell();
    List<String> smellVal = new ArrayList<>();
    if (smell != null) {
      smellVal.add(smell.name());
    } else {
      smellVal.add("null");
    }
    result.put(LocationDescription.SMELL, smellVal);
    return result;
  }

  @Override
  public void move(Direction dir) throws IllegalArgumentException, IllegalStateException {
    try {
      checkGameStatus(true, false);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while making a move:" + ill.getMessage());
    }
    if (dir == null) {
      throw new IllegalArgumentException("direction to move cannot be null.");
    }
    Map<Direction, Location> pMoves = playerLocation.getPossibleMoves();
    Location reqLoc = pMoves.get(dir);
    if (reqLoc != null) {
      playerLocation = reqLoc;
      Monster m = playerLocation.getMonster();
      if (m != null) {
        if (m.getCurrentHealth() == HEALTH_OTYUGH_HEALTH) {
          player.setPlayerStatus(PlayerStatus.DECEASED);
          ended = true;
        } else if (m.getCurrentHealth() == INJURED_OTYUGH_HEALTH) {
          int randomNum = rand.nextInt(0, 2);
          if (randomNum == 0) {
            // survives
            if (reqLoc == end) {
              ended = true;
            }
          } else {
            player.setPlayerStatus(PlayerStatus.DECEASED);
            ended = true;
          }
        } else {
          if (reqLoc == end) {
            collectTreasure();
            ended = true;
          }
        }
      } else {
        if (reqLoc == end) {
          collectTreasure();
          ended = true;
        }
      }
    } else {
      throw new IllegalStateException("invalid move.");
    }
  }

  /*
  helper to check game status.
  throws exception when the states provided doesn't match.
  isEnded can be null, in which case, checking end status is skipped.
  */
  private void checkGameStatus(boolean isStarted, Boolean isEnded) throws IllegalStateException {
    if (started != isStarted) {
      throw new IllegalStateException("player has not entered dungeon.");
    }
    if (isEnded != null) {
      if (ended != isEnded) {
        throw new IllegalStateException("player reached end, reset to play again.");
      }
    }
  }

  @Override
  public Map<Treasure, Integer> collectTreasure() throws IllegalStateException {
    try {
      checkGameStatus(true, false);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while collecting treasure:" + ill.getMessage());
    }
    if (treasureCollected.contains(playerLocation)) {
      return null;
    }

    Map<Treasure, Integer> treasureL = playerLocation.getTreasure();
    try {
      for (Treasure t : Treasure.values()) {
        int treasureQ = treasureL.get(t);
        if (treasureQ != 0) {
          player.addTreasure(t, treasureQ);
          treasureCollected.add(playerLocation);
          playerLocation.placeTreasure(t, -treasureQ);
        }
      }
    } catch (IllegalArgumentException | IllegalStateException exp) {
      throw new IllegalStateException("error while collecting treasure." + exp.getMessage());
    }
    return treasureL;
  }

  private int countCaves() {
    int caves = 0;
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        if (dungeon[i][j].getType() == LocationType.CAVE) {
          caves++;
        }
      }
    }
    return caves;
  }

  /*
  configure treasure in the dungeon.
  throws exception when there aren't enough caves to place treasure.
   */
  protected void configureTreasure() throws IllegalStateException, IllegalArgumentException {
    int caves = countCaves();

    int treasureRooms = Math.round((treasureP / 100f) * (caves));
    if (treasureRooms < 1) {
      throw new IllegalArgumentException(
              "cannot configure treasure of given percentage with this size of dungeon.");
    }
    List<Location> treasureLoc = new ArrayList<>();

    List<List<Integer>> allNodes = new ArrayList<>();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        List<Integer> edge = new ArrayList<>();
        edge.add(i);
        edge.add(j);
        allNodes.add(edge);
      }
    }
    int consecutiveM = 0;
    RandomInteger rGen = trueRandom;
    while (treasureLoc.size() != treasureRooms) {
      int randomN = rGen.nextInt(0, allNodes.size());
      Location locToPlace = dungeon[allNodes.get(randomN).get(0)][allNodes.get(randomN).get(1)];
      int randomT = rand.nextInt(0, Treasure.values().length);
      int randomQ = rand.nextInt(20, 100);

      try {
        if (randomT == 0) {
          configureTreasureHelper(Treasure.DIAMONDS, randomQ, locToPlace, rand, treasureLoc);
          if (consecutiveM > 0) {
            consecutiveM = 0;
          }
        }
        if (randomT == 1) {
          configureTreasureHelper(Treasure.RUBIES, randomQ, locToPlace, rand, treasureLoc);
          if (consecutiveM > 0) {
            consecutiveM = 0;
          }
        }
        if (randomT == 2) {
          configureTreasureHelper(Treasure.SAPPHIRES, randomQ, locToPlace, rand, treasureLoc);
          if (consecutiveM > 0) {
            consecutiveM = 0;
          }
        }
      } catch (IllegalStateException ill) {
        consecutiveM++;
        if (consecutiveM > 1000) {
          throw new IllegalStateException("unable to place treasure." + ill.getMessage());
        }
      } catch (IllegalArgumentException illArg) {
        throw new IllegalStateException("invalid argument while placing treasure."
                + illArg.getMessage());
      }
    }
  }

  /*
  helper to configure treasure.
  validates whether the given location can be placed with treasure and places it if true.
   */
  private void configureTreasureHelper(Treasure treasure, int treasureQ,
                                       Location locToPlace, RandomInteger rGen,
                                       List<Location> treasureLoc)
          throws IllegalArgumentException, IllegalStateException {
    if (locToPlace.getType() != LocationType.TUNNEL) {
      locToPlace.placeTreasure(treasure, treasureQ);
      if (!treasureLoc.contains(locToPlace)) {
        treasureLoc.add(locToPlace);
      }
    }
  }

  /*
  configure arrows in the dungeon.
  throws exception when there aren't enough caves to place arrows.
   */
  protected void configureArrows() throws IllegalStateException {
    int numLocations = row * col;

    int arrowRooms = Math.round((treasureP / 100f) * (numLocations));
    if (arrowRooms < 1) {
      throw new IllegalArgumentException(
              "cannot configure treasure of given percentage with this size of dungeon.");
    }
    List<Location> arrowLoc = new ArrayList<>();

    List<List<Integer>> allNodes = new ArrayList<>();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        List<Integer> edge = new ArrayList<>();
        edge.add(i);
        edge.add(j);
        allNodes.add(edge);
      }
    }

    int consecutiveM = 0;
    RandomInteger rGen = trueRandom;
    while (arrowLoc.size() != arrowRooms) {
      int randomN = rGen.nextInt(0, allNodes.size());
      Location locToPlace = dungeon[allNodes.get(randomN).get(0)][allNodes.get(randomN).get(1)];
      int randomQ = rand.nextInt(1, 3);

      try {
        if (!arrowLoc.contains(locToPlace)) {
          locToPlace.placeWeapon(WeaponType.CROOKEDARROW, randomQ);
          arrowLoc.add(locToPlace);
          if (consecutiveM > 0) {
            consecutiveM = 0;
          }
        }
      } catch (IllegalStateException ill) {
        consecutiveM++;
        if (consecutiveM > 1000) {
          throw new IllegalStateException("unable to place arrows." + ill.getMessage());
        }
      }
    }
  }

  /*
  configure monsters in the dungeon.
  throws exception when there aren't enough caves to place arrows.
   */
  protected void configureMonsters() throws IllegalStateException {
    if (numMonsters == 0) {
      return;
    }
    int caves = countCaves();

    if (numMonsters > (caves - 1)) {
      throw new IllegalArgumentException(
              "cannot configure monsters of required quantity with this size of dungeon.");
    }

    List<Location> monsterLoc = new ArrayList<>();

    List<List<Integer>> allNodes = new ArrayList<>();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        List<Integer> edge = new ArrayList<>();
        edge.add(i);
        edge.add(j);
        allNodes.add(edge);
      }
    }

    end.placeMonster();
    monsterLoc.add(end);

    int consecutiveM = 0;
    RandomInteger rGen = trueRandom;
    while (monsterLoc.size() != numMonsters) {
      int randomN = rGen.nextInt(0, allNodes.size());
      Location locToPlace = dungeon[allNodes.get(randomN).get(0)][allNodes.get(randomN).get(1)];

      try {
        if (!monsterLoc.contains(locToPlace)) {
          if (configureMonsterHelper(locToPlace)) {
            monsterLoc.add(locToPlace);
            if (consecutiveM > 0) {
              consecutiveM = 0;
            }
          }
        }
      } catch (IllegalStateException ill) {
        consecutiveM++;
        if (consecutiveM > 1000) {
          throw new IllegalStateException("unable to place monsters." + ill.getMessage());
        }
      }
    }
  }

  /*
  helper to configure monster.
  validates whether the given location can be placed with monster and places it if true.
   */
  private boolean configureMonsterHelper(Location locToPlace)
          throws IllegalArgumentException, IllegalStateException {
    if ((locToPlace.getType() != LocationType.TUNNEL)
            && (locToPlace != start)) {
      locToPlace.placeMonster();
      return true;
    }
    return false;
  }

  @Override
  public String getPlayerLocation() throws IllegalStateException {
    try {
      checkGameStatus(true, null);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while getting player location:" + ill.getMessage());
    }
    return String.format("%d,%d", playerLocation.getRow(), playerLocation.getColumn());
  }

  @Override
  public boolean gameStarted() {
    return started;
  }

  @Override
  public boolean gameEnded() {
    return ended;
  }

  @Override
  public void reset() throws IllegalStateException {
    try {
      checkGameStatus(true, null);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("game hasn't started yet:" + ill.getMessage());
    }
    player = new PlayerImpl(player.getName());
    playerLocation = null;
    started = false;
    ended = false;
    treasureCollected = new ArrayList<>();
    arrowsCollected = new ArrayList<>();
  }

  @Override
  public Map<WeaponType, Integer> pickWeapon() {
    try {
      checkGameStatus(true, false);
    } catch (IllegalStateException ill) {
      throw new IllegalStateException("error while collecting weapon:" + ill.getMessage());
    }
    if (arrowsCollected.contains(playerLocation)) {
      return null;
    }

    Map<WeaponType, Integer> weaponAtLoc = playerLocation.getWeaponInfo();
    try {
      for (WeaponType t : WeaponType.values()) {
        int weaponQ = weaponAtLoc.get(t);
        if (weaponQ != 0) {
          player.addWeapon(t, weaponQ);
          arrowsCollected.add(playerLocation);
          playerLocation.placeWeapon(t, -weaponQ);
        }
      }
    } catch (IllegalArgumentException | IllegalStateException exp) {
      throw new IllegalStateException("error while collecting weapon." + exp.getMessage());
    }
    return weaponAtLoc;
  }

  @Override
  public boolean shootArrow(Direction dir, int distance)
          throws IllegalArgumentException, IllegalStateException {
    if (dir == null) {
      throw new IllegalArgumentException("direction to shoot cannot be null.");
    }
    if (distance <= 0) {
      throw new IllegalArgumentException("distance to shoot cannot be <= 0.");
    }
    int playerArr = player.getWeaponInfo().get(WeaponType.CROOKEDARROW);
    if (playerArr <= 0) {
      throw new IllegalStateException("player does not have arrows to shoot.");
    }
    boolean result = false;
    int distToTravel = distance;
    Location tempLocation = playerLocation;
    Direction tempDir = dir;

    while (distToTravel != 0) {
      Map<Location, Direction> nextPossible = getNextPossibleLoc(tempLocation, tempDir);
      if (nextPossible == null) {
        break;
      }
      for (Map.Entry<Location, Direction> mapEntry : nextPossible.entrySet()) {
        if (mapEntry.getKey() != null) {
          tempLocation = mapEntry.getKey();
          tempDir = mapEntry.getValue();
          if (tempLocation.getLocationSign().get(LocationDescription.TYPE).get(0)
                  .equals(LocationType.CAVE.name())) {
            distToTravel--;
          }
        } else {
          break;
        }
      }
    }
    if (distToTravel == 0) {
      // check if temp location has monster, if so, slay it.
      Monster m = tempLocation.getMonster();
      if ((m != null) && (m.getCurrentHealth() != 0)) {
        m.slay();
        result = true;
      }
    }
    // loose the arrow from the player.
    player.addWeapon(WeaponType.CROOKEDARROW, -1);
    return result;
  }

  private Map<Location, Direction> getNextPossibleLoc(Location location, Direction dir) {

    Map<Direction, Direction> compDir = new HashMap<>();
    compDir.put(Direction.NORTH, Direction.SOUTH);
    compDir.put(Direction.SOUTH, Direction.NORTH);
    compDir.put(Direction.EAST, Direction.WEST);
    compDir.put(Direction.WEST, Direction.EAST);

    Location nextLocation;
    Map<Direction, Location> possMoves = location.getPossibleMoves();
    if (possMoves.get(dir) != null) {
      nextLocation = possMoves.get(dir);
    } else {
      return null;
    }

    possMoves = nextLocation.getPossibleMoves();
    Direction nextDir = null;
    if (nextLocation.getType() == LocationType.TUNNEL) {
      for (Map.Entry<Direction, Location> entry : possMoves.entrySet()) {
        if (entry.getKey() != compDir.get(dir)) {
          if (entry.getValue() != null) {
            nextDir = entry.getKey();
          }
        }
      }
    } else {
      nextDir = dir;
    }
    Map<Location, Direction> result = new HashMap<>();
    result.put(nextLocation, nextDir);

    return result;
  }

  @Override
  public PlayerStatus getPlayerStatus() {
    return player.getPlayerStatus();
  }

  @Override
  public SmellIntensity getLocationSmell() {
    Map<Direction, Location> possMoves = playerLocation.getPossibleMoves();
    Map<Direction, Direction> compDir = new HashMap<>();
    compDir.put(Direction.NORTH, Direction.SOUTH);
    compDir.put(Direction.SOUTH, Direction.NORTH);
    compDir.put(Direction.EAST, Direction.WEST);
    compDir.put(Direction.WEST, Direction.EAST);

    if (gameStarted()) {
      Monster m = playerLocation.getMonster();
      if ((m != null) && (m.getCurrentHealth() != 0)) {
        return SmellIntensity.HIGH;
      }
    }

    int monsAtNextL = 0;
    List<Location> nextNeighbor = new ArrayList<>();

    // immediate neighbors
    for (Direction d : Direction.values()) {
      if (possMoves.get(d) != null) {
        Monster m = possMoves.get(d).getMonster();
        if ((m != null) && (m.getCurrentHealth() > 0)) {
          return SmellIntensity.HIGH;
        }
        Map<Direction, Location> tempMoves = possMoves.get(d).getPossibleMoves();
        for (Direction di : Direction.values()) {
          Location neigh = tempMoves.get(di);
          if ((di != compDir.get(d)) && (neigh != null) && (!nextNeighbor.contains(neigh))) {
            Monster nextLevel = neigh.getMonster();
            if ((nextLevel != null) && (nextLevel.getCurrentHealth() > 0)) {
              monsAtNextL++;
              nextNeighbor.add(neigh);
              if (monsAtNextL == 2) {
                return SmellIntensity.HIGH;
              }
            }
          }
        }
      }
    }

    if (monsAtNextL == 0) {
      return null;
    } else if (monsAtNextL == 1) {
      return SmellIntensity.LOW;
    }
    return null;
  }

  /*
  creates bare location objects in the dungeon.
   */
  protected void createLocations() {
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        Location loc = new LocationImpl(i, j);
        dungeon[i][j] = loc;
      }
    }
  }

  /*
  runs the kruskal algorithm to generate the grid.
  also sets neighbours between locations in dungeon.
  interconnectivity is handled by adding leftover edges after kruskal algorithm, the number of
  leftover edges added is the same as the value of interconnectivity.
   */
  protected void runKruskal(int interConn) {
    List<List<Integer>> allEdgesCopy = new ArrayList<>(allEdges);
    List<String> selectedE = new ArrayList<>();
    int random = 0;
    while (selectedEdges.size() < (row * col) - 1) {
      random = rand.nextInt(0, allEdgesCopy.size());
      List<Integer> edge = allEdgesCopy.get(random);
      String source = String.format("%d,%d", edge.get(0), edge.get(1));
      String dest = String.format("%d,%d", edge.get(2), edge.get(3));
      boolean edgeSelected = false;
      int sourceInd = -1;
      int destInd = -1;

      // union-find for kruskal is done based on string comparison.
      if (selectedEdges.size() == 0) {
        edgeSelected = true;
      } else {
        for (int i = 0; i < selectedE.size(); i++) {
          if (selectedE.get(i).contains(source.subSequence(0, source.length()))) {
            sourceInd = i;
          }
          if (selectedE.get(i).contains(dest.subSequence(0, dest.length()))) {
            destInd = i;
          }
        }

        if (sourceInd != destInd) {
          edgeSelected = true;
        } else {
          if (sourceInd == -1) {
            edgeSelected = true;
          } else {
            edgeSelected = false;
          }
        }
      }
      if (edgeSelected) {
        StringBuffer sb = new StringBuffer();
        sb.append(source);
        sb.append("-");
        sb.append(dest);
        StringBuffer temp = new StringBuffer();
        if (selectedEdges.size() != 0) {
          if (sourceInd != -1) {
            temp.append(selectedE.get(sourceInd));
            temp.append("->");
          }
          temp.append(sb.toString());
          if (destInd != -1) {
            temp.append("->");
            temp.append(selectedE.get(destInd));
          }
          if ((sourceInd == destInd) && (sourceInd != -1)) {
            selectedE.remove(sourceInd);
          } else {
            if (sourceInd != -1) {
              selectedE.remove(sourceInd);
              if (sourceInd < destInd) {
                destInd--;
              }
            }
            if (destInd != -1) {
              selectedE.remove(destInd);
            }
          }
          selectedE.add(temp.toString());
        } else {
          selectedE.add(sb.toString());
        }
        setNeighbours(edge.get(0), edge.get(1), edge.get(2), edge.get(3));
        allEdgesCopy.remove(random);
        selectedEdges.add(edge);
      } else {
        allEdgesCopy.remove(random);
        leftOverEdges.add(edge);
      }
    }

    // handling interconnectivity.
    leftOverEdges.addAll(allEdgesCopy);
    interConn = Math.min(interConn, leftOverEdges.size());
    for (int i = 0; i < interConn; i++) {
      if (countCaves() <= 2) {
        break;
      }
      random = rand.nextInt(0, leftOverEdges.size());
      List<Integer> edge = leftOverEdges.get(random);
      setNeighbours(edge.get(0), edge.get(1), edge.get(2), edge.get(3));
      leftOverEdges.remove(random);
      selectedEdges.add(edge);
    }
  }

  /*
   set neighbours between two location objects represented by their row,column position.
   @param sourceR source location row.
   @param sourceC source location column.
   @param destR destination location row.
   @param destC destination location column.
   */
  protected abstract void setNeighbours(int sourceR, int sourceC, int destR, int destC);

  /**
   * Fills up non wrapping edges in this.allEdges
   * To support testing using predictable random numbers, sets up few edges to
   * the front of the list.
   */
  protected void setupNonWrappingEdges() {
    int i = 0;
    int j = 0;
    int a = 0;
    int b = 1;
    // sets up few edges towards the front of the allEdges data structure so that we know how the
    // grid will be formed when using predictable random numbers to construct the dungeon.
    // the pattern that is setup is a mirror image of 5,
    // e.g., 0,0-0,1 -> 0,1-0,2 -> ... -> 0,(column-2)-0,(column-1) -> 0,(column-1)-1,(column-1) ...
    List<String> selectedEdges = new ArrayList<>();
    for (int k = 0; k < row; k++) {
      for (int m = 0; m < col; m++) {
        List<Integer> edge = new ArrayList<>();
        if (!((k == row - 1) && (m == col - 1))) {
          if ((i <= a) && (j <= b)) {
            edge.add(i);
            edge.add(j);
            edge.add(a);
            edge.add(b);
            allEdges.add(edge);
            selectedEdges.add(String.format("%d%d%d%d", i, j, a, b));
          } else {
            edge.add(a);
            edge.add(b);
            edge.add(i);
            edge.add(j);
            allEdges.add(edge);
            selectedEdges.add(String.format("%d%d%d%d", a, b, i, j));
          }
        }
        i = a;
        j = b;
        if ((k % 2) == 0) {
          if (m < (col - 2)) {
            b++;
          } else {
            if (m == col - 2) {
              b = col - 1;
            } else {
              b--;
            }
            if (k != row - 1) {
              a = k + 1;
            } else {
              //do nothing.
            }
          }
        } else {
          if (m < (col - 2)) {
            b--;
          } else {
            if (m == col - 2) {
              b = 0;
            } else {
              b++;
            }
            if (k != row - 1) {
              a = k + 1;
            } else {
              //do nothing.
            }
          }
        }
      }
    }
    // fills remaining non-wrapping edges.
    for (int k = 0; k < row; k++) {
      for (int l = 0; l < col; l++) {
        List<Integer> edge = new ArrayList<>();
        String pEdge1 = "";
        String pEdge2 = "";
        if (l != col - 1) {
          pEdge1 = String.format("%d%d%d%d", k, l, k, l + 1);
        }
        if (k != row - 1) {
          pEdge2 = String.format("%d%d%d%d", k, l, k + 1, l);
        }
        if ((pEdge1.length() > 0) && (!(selectedEdges.contains(pEdge1)))) {
          edge = new ArrayList<>();
          edge.add(k);
          edge.add(l);
          edge.add(k);
          edge.add(l + 1);
          allEdges.add(edge);
        }
        if ((pEdge2.length() > 0) && (!(selectedEdges.contains(pEdge2)))) {
          edge = new ArrayList<>();
          edge.add(k);
          edge.add(l);
          edge.add(k + 1);
          edge.add(l);
          allEdges.add(edge);
        }
      }
    }
  }

  /*
  selects start and end location in the dungeon.
  uses the all pair shortest path algorithm between randomly selected source and end to
  ensure that the minimum distance between them is at least MIN_START_END_DIST.
   */
  protected void selectStartEnd() {
    List<String> allNodes = new ArrayList<>();
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        allNodes.add(String.format("%d,%d", i, j));
      }
    }

    RandomInteger rGen = trueRandom;

    int random = rGen.nextInt(0, allNodes.size());

    String possStart = allNodes.get(random);
    int pStartI = Integer.parseInt(possStart.split(",")[0]);
    int pStartJ = Integer.parseInt(possStart.split(",")[1]);
    start = dungeon[pStartI][pStartJ];

    List<String> possEndsChecked = new ArrayList<>();

    while (start.getType() != LocationType.CAVE) {
      random = rGen.nextInt(0, allNodes.size());
      possStart = allNodes.get(random);
      pStartI = Integer.parseInt(possStart.split(",")[0]);
      pStartJ = Integer.parseInt(possStart.split(",")[1]);
      start = dungeon[pStartI][pStartJ];
    }

    while (end == null) {

      if (possEndsChecked.size() == allNodes.size()) {
        throw new IllegalStateException("start and end location cannot be selected "
                + "for the provided row and column values.");
      }

      random = rGen.nextInt(0, allNodes.size());
      String possEnd = allNodes.get(random);
      int pEndI = Integer.parseInt(possEnd.split(",")[0]);
      int pEndJ = Integer.parseInt(possEnd.split(",")[1]);
      end = dungeon[pEndI][pEndJ];
      while (end.getType() != LocationType.CAVE) {
        random = rGen.nextInt(0, allNodes.size());
        possEnd = allNodes.get(random);
        pEndI = Integer.parseInt(possEnd.split(",")[0]);
        pEndJ = Integer.parseInt(possEnd.split(",")[1]);
        end = dungeon[pEndI][pEndJ];
      }

      if (!possEndsChecked.contains(possEnd)) {
        possEndsChecked.add(possEnd);
      }

      Map<String, Integer> pathFromEnd;
      if ((!shortestPath.isEmpty()) && (shortestPath.get(possEnd) != null)) {
        pathFromEnd = shortestPath.get(possEnd);
        if (pathFromEnd.get(possStart) < MIN_START_END_DIST) {
          end = null;
        }
      } else {
        runAllPairShortestPath(possEnd, allNodes);
        pathFromEnd = shortestPath.get(possEnd);
        if (pathFromEnd.get(possStart) != null) {
          if (pathFromEnd.get(possStart) < MIN_START_END_DIST) {
            end = null;
          }
        } else {
          throw new IllegalStateException("cannot determine distance between start to end.");
        }
      }
    }
  }

  /*
  all pair shortest path algorithm implementation.
  for the given target node "possEnd" finds distance from all other location.
  results are stored in shortestPath map.
   */
  protected void runAllPairShortestPath(String possEnd, List<String> allNodes) {
    int[][] opt = new int[selectedEdges.size()][allNodes.size()];
    for (int i = 0; i < allNodes.size(); i++) {
      opt[0][i] = Integer.MAX_VALUE - 100000;
    }
    int endIndex = allNodes.indexOf(possEnd);
    opt[0][endIndex] = 0;

    Location temp;
    Map<Direction, Location> p;

    for (int i = 1; i < selectedEdges.size(); i++) {
      for (int j = 0; j < allNodes.size(); j++) {
        opt[i][j] = opt[i - 1][j];
        String[] iJ = allNodes.get(j).split(",");
        temp = dungeon[Integer.parseInt(iJ[0])][Integer.parseInt(iJ[1])];
        p = temp.getPossibleMoves();
        List<String> moves = new ArrayList<>();
        List<Integer> neighbour = new ArrayList<>();
        for (Direction dir : Direction.values()) {
          if (p.get(dir) != null) {
            moves.add(String.format("%d,%d", p.get(dir).getRow(), p.get(dir).getColumn()));
            neighbour.add(allNodes.indexOf(
                    String.format("%d,%d", p.get(dir).getRow(), p.get(dir).getColumn())));
          }
        }
        for (int neigh : neighbour) {
          opt[i][j] = Math.min(opt[i][j], opt[i - 1][neigh] + 1);
        }
      }
    }
    Map<String, Integer> shortP = new HashMap<>();
    for (int j = 0; j < allNodes.size(); j++) {
      shortP.put(allNodes.get(j), opt[selectedEdges.size() - 1][j]);
    }
    shortestPath.put(allNodes.get(endIndex), shortP);
  }

  /**
   * String representation of the dungeon comprised of:
   * the location details of each cell will have neighbours represented with = (or) ||.,
   * treasure information at each location detailed separately.
   * location which has the player will be denoted by "P".
   *
   * @return String with dungeon information as explained above.
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < row; i++) {
      for (int k = 0; k < 4; k++) {
        for (int j = 0; j < col; j++) {
          Map<Direction, Location> p = dungeon[i][j].getPossibleMoves();
          /*
          case 0: information about neighbours to north.
          case 1: information about neighbours to west and east.
          case 2: information about neighbours to south.
           */
          switch (k) {
            case 0: {
              if (p.get(Direction.NORTH) != null) {
                sb.append("   | |       ");
              } else {
                sb.append("             ");
              }
            }
            break;
            case 1: {
              if (p.get(Direction.WEST) != null) {
                sb.append(" = ");
              } else {
                sb.append("   ");
              }
              sb.append(dungeon[i][j].toString());
              if (end == dungeon[i][j]) {
                sb.append("E");
              } else if (start == dungeon[i][j]) {
                sb.append("S");
              } else {
                sb.append(" ");
              }

              int monsterHealth;
              if (dungeon[i][j].getMonster() != null) {
                monsterHealth = dungeon[i][j].getMonster().getCurrentHealth();
                if (monsterHealth == 2) {
                  sb.append("M*");
                } else if (monsterHealth == 1) {
                  sb.append("M+");
                } else if (monsterHealth == 0) {
                  sb.append("M-");
                }
              } else {
                sb.append("  ");
              }

              if (playerLocation == dungeon[i][j]) {
                sb.append("P");
              } else {
                sb.append(" ");
              }
              if (p.get(Direction.EAST) != null) {
                sb.append(" = ");
              } else {
                sb.append("   ");
              }
            }
            break;
            case 2: {
              if (p.get(Direction.SOUTH) != null) {
                sb.append("   | |       ");
              } else {
                sb.append("             ");
              }
            }
            break;
            default:
              // do nothing;
              break;
          }
        }
        sb.append("\n");
      }
      sb.append("\n");
    }
    StringBuffer strB = new StringBuffer();
    strB.append("\n\nTreasure map\n");
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        strB.append(dungeon[i][j].toString());
        strB.append(": ");
        String treasureS = getTreasureString(dungeon[i][j]);
        if (treasureS.length() > 0) {
          strB.append(treasureS);
        }
        strB.append("\n");
      }
      strB.append("\n");
    }
    sb.append(strB);

    if (numMonsters > 0) {
      strB = new StringBuffer();
      strB.append("\n\nArrows map\n");
      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          strB.append(dungeon[i][j].toString());
          strB.append(": ");
          String arrowS = getWeaponString(dungeon[i][j]);
          if (arrowS.length() > 0) {
            strB.append(arrowS);
          }
          strB.append("\n");
        }
        strB.append("\n");
      }
      sb.append(strB);
    }

    return sb.toString();
  }

  private String getWeaponString(Location location) {
    StringBuffer sb = new StringBuffer();
    Map<WeaponType, Integer> weaponInfo = location.getWeaponInfo();
    int crookQ = weaponInfo.get(WeaponType.CROOKEDARROW);
    if (crookQ > 0) {
      sb.append(String.format("%S %d ", WeaponType.CROOKEDARROW, crookQ));
    }
    return sb.toString();
  }

  /*
  helper for toSting() to get the treasure information at the location.
  each treasure will be represented by its first letter.
   */
  private String getTreasureString(Location location) {
    StringBuffer sb = new StringBuffer();
    Map<Treasure, Integer> treasureL = location.getTreasure();
    int diaQ = treasureL.get(Treasure.DIAMONDS);
    if (diaQ > 0) {
      sb.append(String.format("D %d ", diaQ));
    }
    int rubyQ = treasureL.get(Treasure.RUBIES);
    if (rubyQ > 0) {
      sb.append(String.format("R %d ", rubyQ));
    }
    int sapQ = treasureL.get(Treasure.SAPPHIRES);
    if (sapQ > 0) {
      sb.append(String.format("S %d ", sapQ));
    }
    return sb.toString();
  }
}
