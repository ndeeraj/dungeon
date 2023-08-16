package demo;

import maze.CustomRandomInteger;
import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;
import maze.NonWrappingDungeon;
import maze.PlayerDescription;
import maze.RandomInteger;
import maze.Treasure;
import maze.WrappingDungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Driver for {@link WrappingDungeon}, {@link NonWrappingDungeon}.
 */
public class DungeonDriver {
  /**
   * entry point for the driver.
   *
   * @param args parameters to construct dungeon.
   */
  public static void main(String[] args) {
    int row = 0;
    int col = 0;
    String wrapOrNot;
    int interConn = 0;
    int treasureP = 0;
    boolean predictable = false;
    RandomInteger trueR = new CustomRandomInteger();
    RandomInteger pred = new CustomRandomInteger(true);

    Dungeon maze;

    if (args.length == 5) {
      row = Integer.parseInt(args[0]);
      col = Integer.parseInt(args[1]);
      wrapOrNot = args[2];
      interConn = Integer.parseInt(args[3]);
      treasureP = Integer.parseInt(args[4]);
      if (wrapOrNot.equals("W")) {
        maze = new WrappingDungeon("player", row, col, interConn, treasureP);
      } else {
        maze = new NonWrappingDungeon("player", row, col, interConn, treasureP);
      }
    } else if (args.length == 0) {
      while (true) {
        maze = new NonWrappingDungeon(
                "player", 4, 5, 0, 100, pred);
        maze.enter();
        if (maze.getPlayerLocation().equals("0,0")) {
          maze.reset();
          break;
        }
      }
      predictable = true;
    } else {
      throw new IllegalArgumentException("parameters missing.");
    }
    System.out.println("\nentering dungeon...");
    maze.enter();

    System.out.println("\nconstructed dungeon...\n" + maze.toString());

    System.out.println("initial player and starting location description...");
    printPlayerLocation(maze);
    printPlayerDescription(maze);
    printLocationDescription(maze);

    List<String> path = new ArrayList<>();

    if (predictable) {
      playPredictive(maze, path);
    } else {
      playRandom(maze, path, trueR);
    }

    System.out.println("\nplayer reached end, game finished.");
    printPlayerLocation(maze);
    printPlayerDescription(maze);

    StringBuffer sb = new StringBuffer();
    int countTwenty = 0;
    for (int i = 0; i < path.size(); i++) {
      String loc = path.get(i);
      sb.append(loc);
      countTwenty++;
      if (i != path.size() - 1) {
        sb.append(" -> ");
      }
      if (countTwenty == 20) {
        sb.append("\n");
        countTwenty = 0;
      }
    }
    System.out.println(String.format("\n%s:\n%s\n", "path followed by the player to reach end",
            sb.toString()));
  }

  // helper to play a predictable game.
  private static void playPredictive(Dungeon maze, List<String> path) {
    Direction[] moves = {Direction.EAST, Direction.EAST, Direction.EAST, Direction.EAST,
                         Direction.SOUTH, Direction.WEST, Direction.WEST, Direction.WEST,
                         Direction.WEST, Direction.SOUTH, Direction.EAST, Direction.EAST,
                         Direction.EAST, Direction.EAST, Direction.SOUTH, Direction.WEST,
                         Direction.WEST, Direction.WEST, Direction.WEST};
    for (int i = 0; i < moves.length; i++) {
      Direction dir = moves[i];
      if (!maze.gameEnded()) {
        Map<Treasure, Integer> treasureD = maze.collectTreasure();
        printTreasureInfo(treasureD);
        maze.move(moves[i]);
        System.out.println("\nmoving " + moves[i].name() + "...");
        path.add(maze.getPlayerLocation());
        printPlayerLocation(maze);
        printPlayerDescription(maze);
        printLocationDescription(maze);
      }
    }
  }

  // helper to print treasure information.
  private static void printTreasureInfo(Map<Treasure, Integer> treasureD) {
    StringBuffer sb = new StringBuffer();
    if ((treasureD != null) && !treasureD.isEmpty()) {
      for (Map.Entry<Treasure, Integer> e : treasureD.entrySet()) {
        if (e.getValue() > 0) {
          if (sb.length() != 0) {
            sb.append(", ");
          }
          sb.append(e.getKey().name());
          sb.append(" ");
          sb.append(e.getValue());
        }
      }
      if (sb.length() > 0) {
        System.out.println("\ncollecting treasure ...");
        System.out.println(String.format("[%s]", sb.toString()));
      }
    }
  }

  // helper to make a move.
  private static Map<LocationDescription, List<String>> makeMove(
          String dir, Dungeon d, List<String> path, RandomInteger rand) {

    Direction direction = null;
    Map<LocationDescription, List<String>> locationD = d.describeLocation();
    List<String> possibleMoves = locationD.get(LocationDescription.MOVES);

    if (dir.equals("R")) {
      List<Integer> m = new ArrayList<>();
      m.add(0);
      m.add(1);
      m.add(2);
      m.add(3);
      while (true) {
        int random = rand.nextInt(0, m.size());
        if (possibleMoves.get(m.get(random)).equals("null")) {
          m.remove(random);
        } else {
          if (m.get(random) == 0) {
            d.move(Direction.NORTH);
            direction = Direction.NORTH;
            break;
          } else if (m.get(random) == 1) {
            d.move(Direction.WEST);
            direction = Direction.WEST;
            break;
          } else if (m.get(random) == 2) {
            d.move(Direction.EAST);
            direction = Direction.EAST;
            break;
          } else if (m.get(random) == 3) {
            d.move(Direction.SOUTH);
            direction = Direction.SOUTH;
            break;
          }
        }
      }
    }
    if (direction != null) {
      System.out.println("\nmoving " + direction.name() + "...");
      path.add(d.getPlayerLocation());
    }
    return d.describeLocation();
  }

  // helper to print location details.
  private static void printLocationDescription(Dungeon maze) {
    Map<LocationDescription, List<String>> locationD = maze.describeLocation();
    System.out.println("\nlocation type: " + locationD.get(LocationDescription.TYPE).get(0));
    System.out.println("treasure at location:" + locationD.get(LocationDescription.TREASURE));
    System.out.println("possible moves [NORTH, WEST, EAST, SOUTH]: "
            + locationD.get(LocationDescription.MOVES));
  }

  // helper to print player description.
  private static void printPlayerDescription(Dungeon maze) {
    Map<PlayerDescription, List<String>> playerD = maze.describePlayer();
    System.out.println("player treasure: " + playerD.get(PlayerDescription.TREASURE).toString());
  }

  // helper that prints the player location.
  private static void printPlayerLocation(Dungeon d) {
    System.out.println("\nplayer location: " + d.getPlayerLocation());
  }

  // helper to play a random game.
  private static void playRandom(Dungeon d, List<String> path, RandomInteger trueR) {
    while (!d.gameEnded()) {
      Map<Treasure, Integer> treasureD = d.collectTreasure();
      printTreasureInfo(treasureD);
      makeMove("R", d, path, trueR);
      printPlayerLocation(d);
      printPlayerDescription(d);
      printLocationDescription(d);
    }
  }
}
