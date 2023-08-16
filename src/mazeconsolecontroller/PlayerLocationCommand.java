package mazeconsolecontroller;

import maze.Dungeon;

import java.io.IOException;

/**
 * Represents accessor to get information about the player location in the {@link Dungeon}.
 * prints to an appendable the player location information.
 * Intentionally making packing private since it should not be available outside the package.
 */
class PlayerLocationCommand implements DungeonCommand {

  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public PlayerLocationCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    String playerLoc = dungeon.getPlayerLocation();
    if (playerLoc != null) {
      try {
        out.append("\nplayer location: ").append(playerLoc).append("\n");
      } catch (IllegalArgumentException exp) {
        out.append(exp.getMessage());
      }
    }
  }
}
