package mazeconsolecontroller;

import maze.Dungeon;
import maze.PlayerDescription;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents accessor to get information about the player in the {@link Dungeon}.
 * prints to an appendable the player information.
 * Intentionally making packing private since it should not be available outside the package.
 */
class DescribePlayerCommand implements DungeonCommand {

  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public DescribePlayerCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    Map<PlayerDescription, List<String>> playerD = dungeon.describePlayer();
    if (playerD != null) {
      out.append("\nplayer details...");
      try {
        out.append("\ntreasure: ").append(playerD.get(PlayerDescription.TREASURE).toString());
        out.append("\nweapon: ").append(playerD.get(PlayerDescription.WEAPON).toString());
        out.append("\n");
      } catch (NullPointerException exp) {
        // suppress
      }
    }
  }
}
