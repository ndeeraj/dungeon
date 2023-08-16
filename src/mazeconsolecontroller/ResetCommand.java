package mazeconsolecontroller;

import maze.Dungeon;

import java.io.IOException;

/**
 * Represents the action of resetting the play in {@link Dungeon}.
 * prints to an appendable the reset message.
 * Intentionally making packing private since it should not be available outside the package.
 */
class ResetCommand implements DungeonCommand {

  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public ResetCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    out.append("\nresetting game...");
    dungeon.reset();
  }
}
