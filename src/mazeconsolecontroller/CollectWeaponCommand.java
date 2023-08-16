package mazeconsolecontroller;

import maze.Dungeon;
import maze.WeaponType;

import java.io.IOException;
import java.util.Map;

/**
 * Represents action to collect weapon from a {@link Dungeon} location.
 * prints to an appendable the weapon collected.
 * Intentionally making packing private since it should not be available outside the package.
 */
class CollectWeaponCommand implements DungeonCommand {

  private final Appendable out;
  private final DungeonCommand describePlayer;

  /**
   * Initializes the action with the appendable it can print results to and followup commands.
   *
   * @param out {@link Appendable}
   * @throws IllegalArgumentException when out is empty.
   */
  public CollectWeaponCommand(Appendable out) throws IllegalArgumentException {
    if (out == null) {
      throw new IllegalArgumentException("an appendable is required");
    }
    this.out = out;
    this.describePlayer = new DescribePlayerCommand(out);
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    try {
      Map<WeaponType, Integer> collectedW = dungeon.pickWeapon();
      if (collectedW == null) {
        out.append("weapon already collected from location.\n");
        return;
      }
      StringBuffer sb = new StringBuffer();
      for (WeaponType w : WeaponType.values()) {
        int collectedQ = collectedW.get(w);
        if (collectedQ > 0) {
          sb.append(collectedQ).append(" ").append(w.name()).append(", ");
        }
      }
      if (sb.length() > 0) {
        StringBuffer result = new StringBuffer();
        result.append("picked up ");
        result.append(sb.substring(0, sb.length() - 2));
        result.append(".");
        out.append(result.toString()).append("\n");
        describePlayer.apply(dungeon);
      }
    } catch (IllegalStateException exp) {
      out.append(exp.getMessage());
    }
  }
}
