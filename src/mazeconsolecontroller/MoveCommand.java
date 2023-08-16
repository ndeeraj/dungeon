package mazeconsolecontroller;

import maze.Direction;
import maze.Dungeon;
import maze.LocationDescription;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents action to move from a {@link Dungeon} location.
 * reads input from a scanner about the direction to move,
 * prints to an appendable the prompt to move and the details of the moved location after the move.
 * Intentionally making packing private since it should not be available outside the package.
 */
class MoveCommand implements DungeonCommand {

  private final Appendable out;
  private final Scanner scan;
  private final Map<String, DungeonCommand> commands;

  /**
   * Initializes the action with the appendable, scanner and followup commands.
   *
   * @param scan the source to read from
   * @param out  the target to print to
   *
   * @throws IllegalArgumentException when scan/out is null.
   */
  public MoveCommand(Scanner scan, Appendable out) throws IllegalArgumentException {
    if (scan == null || out == null) {
      throw new IllegalArgumentException("Scanner / Appendable can't be null");
    }
    this.out = out;
    this.scan = scan;
    this.commands = new HashMap<>();
    commands.put("describeL", new DescribeLocationCommand(out));
    commands.put("playerS", new PlayerStatusCommand(out));
    commands.put("playerLoc", new PlayerLocationCommand(out));
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    boolean moveMade = false;
    do {
      String outString = generateOptionString(dungeon);
      if (outString == null) {
        return;
      }
      out.append(outString);
      String token = scan.next();
      out.append("\n");
      try {
        switch (token) {
          case "E":
            dungeon.move(Direction.EAST);
            moveMade = true;
            break;
          case "W":
            dungeon.move(Direction.WEST);
            moveMade = true;
            break;
          case "N":
            dungeon.move(Direction.NORTH);
            moveMade = true;
            break;
          case "S":
            dungeon.move(Direction.SOUTH);
            moveMade = true;
            break;
          default:
            break;
        }
      } catch (IllegalArgumentException | IllegalStateException exp) {
        out.append("invalid move.\n");
      }
    }
    while (!moveMade);
    commands.get("describeL").apply(dungeon);
    commands.get("playerLoc").apply(dungeon);
    commands.get("playerS").apply(dungeon);
  }

  private String generateOptionString(Dungeon dungeon) {
    StringBuffer sb = new StringBuffer();
    Map<LocationDescription, List<String>> locationD = dungeon.describeLocation();
    if (locationD != null) {
      List<String> moves = locationD.get(LocationDescription.MOVES);

      final boolean moveWExists = !moves.get(1).equals("null");
      final boolean moveEExists = !moves.get(2).equals("null");
      final boolean moveSExists = !moves.get(3).equals("null");
      final boolean moveNExists = !moves.get(0).equals("null");
      if (moveNExists) {
        sb.append("N, ");
      }
      if (moveWExists) {
        sb.append("W, ");
      }
      if (moveEExists) {
        sb.append("E, ");
      }
      if (moveSExists) {
        sb.append("S, ");
      }
      if (sb.length() != 0) {
        StringBuffer result = new StringBuffer();
        result.append("select one of the door (");
        result.append(sb.substring(0, sb.length() - 2));
        result.append("): ");
        return result.toString();
      } else {
        return null;
      }
    }
    return null;
  }
}
