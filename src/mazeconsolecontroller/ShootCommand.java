package mazeconsolecontroller;

import maze.Direction;
import maze.Dungeon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents action to shoot from a {@link Dungeon} location.
 * reads input from a scanner about the direction, distance to shoot,
 * prints to an appendable the prompt for shoot.
 * Intentionally making packing private since it should not be available outside the package.
 */
class ShootCommand implements DungeonCommand {
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
  public ShootCommand(Scanner scan, Appendable out) throws IllegalArgumentException {
    if (scan == null || out == null) {
      throw new IllegalArgumentException("Scanner / Appendable can't be null");
    }
    this.out = out;
    this.scan = scan;
    this.commands = new HashMap<>();
    commands.put("smell", new LocationSmellCommand(out));
    commands.put("describeP", new DescribePlayerCommand(out));
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    out.append("direction to shoot (N-E-W-S): ");
    String dir = scan.next();
    out.append("\n");
    boolean valid = false;
    do {
      out.append("distance (1-5): ");
      String token = scan.next();
      out.append("\n");
      try {
        int dist = Integer.parseInt(token);
        if ((dist >= 1) && (dist <= 5)) {
          valid = true;
          switch (dir) {
            case "N": {
              dungeon.shootArrow(Direction.NORTH, dist);
            }
            break;
            case "W": {
              dungeon.shootArrow(Direction.WEST, dist);
            }
            break;
            case "E": {
              dungeon.shootArrow(Direction.EAST, dist);
            }
            break;
            case "S": {
              dungeon.shootArrow(Direction.SOUTH, dist);
            }
            break;
            default: {
              out.append("invalid direction: ").append(dir);
              out.append("\n");
            }
          }
        } else {
          out.append("invalid range: ").append(token).append("\n");
          valid = false;
        }
      } catch (NumberFormatException exp) {
        out.append("\ninvalid input: ").append(token).append("\n");
        valid = false;
      } catch (IllegalStateException ex) {
        out.append("\ninvalid direction: ").append(dir).append("\n");
      }
    }
    while (!valid);
    commands.get("describeP").apply(dungeon);
    commands.get("smell").apply(dungeon);
  }
}
