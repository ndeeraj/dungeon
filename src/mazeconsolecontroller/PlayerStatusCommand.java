package mazeconsolecontroller;

import maze.Dungeon;
import maze.PlayerStatus;

import java.io.IOException;

/**
 * Represents accessor to get information about the player status in the {@link Dungeon}.
 * prints to an appendable the player status.
 * Intentionally making packing private since it should not be available outside the package.
 */
class PlayerStatusCommand implements DungeonCommand {
  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public PlayerStatusCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    PlayerStatus p = dungeon.getPlayerStatus();
    if (p != null) {
      out.append("\nplayer is ").append(p.name().toLowerCase()).append(".\n");
    }
  }
}
