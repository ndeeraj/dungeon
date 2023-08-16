package mazeconsolecontroller;

import maze.Dungeon;

import java.io.IOException;

/**
 * Represents the action of entering the {@link Dungeon}.
 * prints to an appendable the introduction message.
 * Intentionally making packing private since it should not be available outside the package.
 */
class EnterCommand implements DungeonCommand {

  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public EnterCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    out.append("\nentering dungeon...\n");
    dungeon.enter();
    out.append("\n");
  }
}
