package mazeconsolecontroller;

import maze.Dungeon;
import maze.LocationDescription;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents accessor to get information about a {@link Dungeon} location.
 * prints to an appendable the location information.
 * Intentionally making packing private since it should not be available outside the package.
 */
class DescribeLocationCommand implements DungeonCommand {

  private final Appendable out;
  private final DungeonCommand smellC;

  /**
   * Initializes the action with the appendable it can print results to and followup commands.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public DescribeLocationCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
    this.smellC = new LocationSmellCommand(out);
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    printLocationDescription(dungeon);
  }

  // helper to print location details.
  private void printLocationDescription(Dungeon dungeon) throws IOException {
    Map<LocationDescription, List<String>> locationD = dungeon.describeLocation();
    if (locationD != null) {
      try {
        out.append("location description...");
        out.append("\ntype: ").append(locationD.get(LocationDescription.TYPE).get(0));
        out.append("\ntreasure: ").append(String.valueOf(
                locationD.get(LocationDescription.TREASURE)));
        out.append("\npossible moves [NORTH, WEST, EAST, SOUTH]: ").append(
                String.valueOf(locationD.get(LocationDescription.MOVES)));
        out.append("\nweapon: ").append(String.valueOf(locationD.get(LocationDescription.WEAPON)));
        out.append("\nmonster: ").append(String.valueOf(
                locationD.get(LocationDescription.MONSTER)));
        smellC.apply(dungeon);
        out.append("\n");
      } catch (NullPointerException exp) {
        // suppress
      }
    }
  }
}
