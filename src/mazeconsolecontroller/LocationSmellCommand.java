package mazeconsolecontroller;

import maze.Dungeon;
import maze.SmellIntensity;

import java.io.IOException;

/**
 * Represents accessor to get information about the smell in a {@link Dungeon} location.
 * prints to an appendable the smell information in the location.
 * Intentionally making packing private since it should not be available outside the package.
 */
class LocationSmellCommand implements DungeonCommand {
  private final Appendable out;

  /**
   * Initializes the action with the appendable to print results.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public LocationSmellCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    SmellIntensity smell = dungeon.getLocationSmell();
    if (smell != null) {
      if (smell == SmellIntensity.HIGH) {
        out.append("\nyou smell something terrible nearby.");
      } else {
        out.append("\nthere is a light pungent smell.");
      }
    }
  }
}
