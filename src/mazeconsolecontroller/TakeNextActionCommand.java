package mazeconsolecontroller;

import maze.Dungeon;
import maze.LocationDescription;
import maze.PlayerDescription;
import maze.Treasure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Represents a set of actions applicable from a {@link Dungeon} location.
 * reads input from a scanner about which action to perform,
 * prints to an appendable the prompt for available actions.
 * Intentionally making packing private since it should not be available outside the package.
 */
class TakeNextActionCommand implements DungeonCommand {

  private final Appendable out;
  private final Scanner scan;
  private final Map<String, DungeonCommand> commands;

  /**
   * Initializes the action with the appendable, scanner and followup commands.
   *
   * @param scan the source to read from
   * @param out  the target to print to
   * @throws IllegalArgumentException when scan/out is null.
   */
  public TakeNextActionCommand(Scanner scan, Appendable out) throws IllegalArgumentException {
    if (scan == null || out == null) {
      throw new IllegalArgumentException("Scanner / Appendable can't be null");
    }
    this.out = out;
    this.scan = scan;
    this.commands = new HashMap<>();
    commands.put("move", new MoveCommand(scan, out));
    commands.put("collectT", new CollectTreasureCommand(out));
    commands.put("collectW", new CollectWeaponCommand(out));
    commands.put("shoot", new ShootCommand(scan, out));
  }

  @Override
  public void apply(Dungeon dungeon) throws IOException {
    String outString = generateOptionString(dungeon);
    if (outString == null) {
      return;
    }
    out.append("\n").append(outString);
    String token = scan.next();
    out.append("\n");
    switch (token) {
      case "M":
        commands.get("move").apply(dungeon);
        break;
      case "T":
        commands.get("collectT").apply(dungeon);
        break;
      case "W":
        commands.get("collectW").apply(dungeon);
        break;
      case "S":
        commands.get("shoot").apply(dungeon);
        break;
      default:
        break;
    }
  }

  private String generateOptionString(Dungeon dungeon) {
    StringBuffer sb = new StringBuffer();
    final boolean moveExists = !dungeon.gameEnded();
    final boolean treasExists = treasExists(dungeon);
    final boolean weaponExists = weapExists(dungeon);
    final boolean playerWeapon = playerWeapon(dungeon);
    if (moveExists) {
      sb.append("move ");
    }
    if (treasExists) {
      sb.append("or pickup treasure ");
    }
    if (weaponExists) {
      sb.append("or pickup weapon ");
    }
    if (playerWeapon) {
      sb.append("or shoot ");
    }
    if (sb.length() != 0) {
      if (moveExists) {
        sb.append("(M");
      }
      if (treasExists) {
        sb.append("-T");
      }
      if (weaponExists) {
        sb.append("-W");
      }
      if (playerWeapon) {
        sb.append("-S");
      }
      sb.append("): ");
      return sb.toString();
    } else {
      return null;
    }
  }

  private boolean playerWeapon(Dungeon dungeon) {
    Map<PlayerDescription, List<String>> playerD = dungeon.describePlayer();
    if (playerD != null) {
      List<String> weaponInfo = playerD.get(PlayerDescription.WEAPON);
      for (String w : weaponInfo) {
        String[] weapQ = w.split("\\s");
        if (Integer.parseInt(weapQ[1]) > 0) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean weapExists(Dungeon dungeon) {
    Map<LocationDescription, List<String>> locationD = dungeon.describeLocation();
    if (locationD != null) {
      List<Integer> arrowsAtLocQ = parseArrAtLoc(locationD.get(LocationDescription.WEAPON));
      if (!arrowsAtLocQ.isEmpty()) {
        int tempArrQ = arrowsAtLocQ.get(0);
        if (tempArrQ > 0) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean treasExists(Dungeon dungeon) {
    List<Integer> treasureAtLocQ;
    Map<Treasure, Integer> nonZeroTreasure;
    Map<LocationDescription, List<String>> locationD = dungeon.describeLocation();

    if (locationD != null) {
      treasureAtLocQ = parseTValAtLoc(locationD.get(LocationDescription.TREASURE));
      nonZeroTreasure = nonZeroT(treasureAtLocQ);
      boolean nonZTres = false;
      if (!nonZeroTreasure.isEmpty()) {
        nonZTres = true;
      }
      return nonZTres;
    }
    return false;
  }

  private List<Integer> parseTValAtLoc(List<String> treasureAtLoc) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      String treas = treasureAtLoc.get(i);
      String[] t = treas.split("\\s");
      result.add(Integer.parseInt(t[1]));
    }
    return result;
  }

  private Map<Treasure, Integer> nonZeroT(List<Integer> treasureAtLocQ) {
    Map<Treasure, Integer> result = new HashMap<>();
    for (int i = 0; i < 3; i++) {
      int tresQ = treasureAtLocQ.get(i);
      if (tresQ != 0) {
        if (i == 0) {
          result.put(Treasure.DIAMONDS, tresQ);
        } else if (i == 1) {
          result.put(Treasure.RUBIES, tresQ);
        } else if (i == 2) {
          result.put(Treasure.SAPPHIRES, tresQ);
        }
      }
    }
    return result;
  }

  private List<Integer> parseArrAtLoc(List<String> arrowAtLoc) {
    List<Integer> result = new ArrayList<>();

    String weapons = arrowAtLoc.get(0);
    String[] t = weapons.split("\\s");
    result.add(Integer.parseInt(t[1]));

    return result;
  }
}
