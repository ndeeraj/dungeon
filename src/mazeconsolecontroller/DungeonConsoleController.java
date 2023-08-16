package mazeconsolecontroller;

import maze.Dungeon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents a console based controller for {@link Dungeon} game that reads input
 * and appends output via the given sources.
 * Control returns when the game ends.
 */

public class DungeonConsoleController implements DungeonController {

  private final Appendable out;
  private final Scanner scan;
  private final Map<String, DungeonCommand> commands;

  /**
   * Constructor for the console controller.
   * Initializes input, output for the controller and sets up the {@link DungeonCommand} commands.
   *
   * @param in  the source to read from
   * @param out the target to print to
   */
  public DungeonConsoleController(Readable in, Appendable out) {
    if (in == null || out == null) {
      throw new IllegalArgumentException("Readable and Appendable can't be null");
    }
    this.out = out;
    scan = new Scanner(in);
    commands = new HashMap<>();
  }

  @Override
  public void playGame(Dungeon dungeon) throws IllegalArgumentException {
    if (dungeon == null) {
      throw new IllegalArgumentException("model cannot be empty.");
    }
    commands.put("enter", new EnterCommand(out));
    commands.put("describeLoc", new DescribeLocationCommand(out));
    commands.put("describePlayer", new DescribePlayerCommand(out));
    commands.put("playerLoc", new PlayerLocationCommand(out));
    commands.put("reset", new ResetCommand(out));
    commands.put("nextAction", new TakeNextActionCommand(scan, out));
    commands.put("playerS", new PlayerStatusCommand(out));

    boolean rematch = false;
    try {
      do {
        while (!dungeon.gameStarted()) {
          out.append("\nenter or display player (E-D): ");
          String token = scan.next();
          if (token.equals("E")) {
            commands.get("enter").apply(dungeon);
          } else if (token.equals("D")) {
            commands.get("describePlayer").apply(dungeon);
          } else {
            out.append("\nUnsupported operation: ").append(token);
          }
        }
        commands.get("describeLoc").apply(dungeon);
        commands.get("playerLoc").apply(dungeon);
        commands.get("describePlayer").apply(dungeon);
        while (!dungeon.gameEnded()) {
          commands.get("nextAction").apply(dungeon);
        }
        out.append("\ngame ended.\n");
        boolean cont = true;
        while (cont) {
          out.append("\nreset or display player or describe location or quit (R-P-L-Q): ");
          String token = scan.next();
          out.append("\n");
          switch (token) {
            case "R":
              commands.get("reset").apply(dungeon);
              rematch = true;
              cont = false;
              break;
            case "P":
              commands.get("describePlayer").apply(dungeon);
              break;
            case "L":
              commands.get("describeLoc").apply(dungeon);
              break;
            case "Q":
              rematch = false;
              cont = false;
              break;
            default:
              break;
          }
        }
      }
      while (rematch);
    } catch (IOException exp) {
      throw new IllegalStateException("could not write to the output.");
    }
  }
}
