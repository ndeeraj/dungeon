package maze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Represents a location in the dungeon.
 * Once neighbours are set, they can be unset by passing null object along with the direction
 * that should be unset.
 * Does the following basic validation for {@link Direction}.EAST, SOUTH
 * before setting a location as neighbour.
 * for EAST, row of the potential neighbour must match this location's row,
 * column should be this location's column + 1.
 * for SOUTH, column of the potential neighbour must match this location's column,
 * row should be this location's row + 1.
 * A location can contain treasures, weapons, monsters.
 * A location should be constructed with a row and column values.
 * so, using default constructor will result in IllegalStateException.
 * Intentionally making the class package private since it should not be available outside
 * the package.
 */
final class LocationImpl implements Location {
  private final int row;
  private final int column;
  private Location moveNorth;
  private Location moveWest;
  private Location moveEast;
  private Location moveSouth;
  private final Map<Treasure, Integer> treasure;
  private final Map<WeaponType, Integer> weapons;
  private Monster monster;

  /**
   * Since a location should have row, column values,
   * calling default constructor will throw an exception.
   *
   * @throws IllegalStateException when calling default constructor.
   */
  public LocationImpl() throws IllegalArgumentException {
    throw new IllegalStateException(
            "location in dungeon cannot be created with negative values for row or column.");
  }

  /**
   * Initializes a location with the provided row and column values.
   *
   * @param row    row value for the location.
   * @param column column value for the location.
   * @throws IllegalArgumentException when row or column value is < 0.
   */
  public LocationImpl(int row, int column) throws IllegalArgumentException {
    if ((row < 0) || (column < 0)) {
      throw new IllegalArgumentException("row or column cannot be less than 0.");
    }
    this.row = row;
    this.column = column;
    this.treasure = new Hashtable<>();
    this.weapons = new Hashtable<>();
  }

  @Override
  public int getRow() {
    return this.row;
  }

  @Override
  public int getColumn() {
    return this.column;
  }

  @Override
  public Map<Direction, Location> getPossibleMoves() {
    Map<Direction, Location> possMoves = new HashMap<>();
    possMoves.put(Direction.NORTH, moveNorth);
    possMoves.put(Direction.WEST, moveWest);
    possMoves.put(Direction.EAST, moveEast);
    possMoves.put(Direction.SOUTH, moveSouth);
    return possMoves;
  }

  @Override
  public Map<Treasure, Integer> getTreasure() {
    Map<Treasure, Integer> treasureR = new Hashtable<>();
    int existingQ = 0;
    for (Treasure t : Treasure.values()) {
      existingQ = (treasure.get(t) == null) ? 0 : treasure.get(t);
      treasureR.put(t, existingQ);
    }
    return treasureR;
  }

  @Override
  public void placeTreasure(Treasure t, int quantity)
          throws IllegalArgumentException, IllegalStateException {
    if (t == null) {
      throw new IllegalArgumentException("treasure to place can't be null.");
    }
    if (quantity == 0) {
      throw new IllegalArgumentException("quantity to place cannot be 0.");
    }
    if (getType() == LocationType.TUNNEL) {
      throw new IllegalStateException("cannot place treasure in tunnels.");
    }
    if ((treasure.get(t) != null) && (quantity > 0)) {
      throw new IllegalStateException("given treasure already present in location.");
    } else {
      int existingQ = 0;
      if (!treasure.isEmpty()) {
        existingQ = (treasure.get(t) == null) ? 0 : treasure.get(t);
      }
      int newQ = existingQ + quantity;
      if (newQ < 0) {
        throw new IllegalStateException("treasure quantity cannot be set below 0.");
      }
      treasure.put(t, newQ);
    }
  }

  @Override
  public Map<LocationDescription, List<String>> getLocationSign() {
    Map<LocationDescription, List<String>> locationD = new Hashtable<>();
    for (LocationDescription item : LocationDescription.values()) {
      List<String> temp = new ArrayList<>();
      if (item == LocationDescription.ROW) {
        temp.add(String.valueOf(this.row));
      } else if (item == LocationDescription.COLUMN) {
        temp.add(String.valueOf(this.column));
      } else if (item == LocationDescription.TREASURE) {
        int existingQ = 0;
        for (Treasure t : Treasure.values()) {
          existingQ = (treasure.get(t) == null) ? 0 : treasure.get(t);
          temp.add(t.name() + " " + existingQ);
        }
      } else if (item == LocationDescription.MOVES) {
        if (moveNorth != null) {
          temp.add(moveNorth.toString());
        } else {
          temp.add("null");
        }
        if (moveWest != null) {
          temp.add(moveWest.toString());
        } else {
          temp.add("null");
        }
        if (moveEast != null) {
          temp.add(moveEast.toString());
        } else {
          temp.add("null");
        }
        if (moveSouth != null) {
          temp.add(moveSouth.toString());
        } else {
          temp.add("null");
        }
      } else if (item == LocationDescription.TYPE) {
        LocationType type = getType();
        if (type == null) {
          temp.add("null");
        } else {
          temp.add(type.name());
        }
      } else if (item == LocationDescription.WEAPON) {
        int existingQ = 0;
        for (WeaponType w : WeaponType.values()) {
          if (weapons.isEmpty()) {
            temp.add(w.name() + " " + 0);
          } else {
            existingQ = (weapons.get(w) == null) ? 0 : weapons.get(w);
            temp.add(w.name() + " " + existingQ);
          }
        }
      } else if (item == LocationDescription.MONSTER) {
        if (this.monster == null) {
          temp.add("null");
        } else {
          temp.add(String.format("%s %d %d", monster.getType().name(),
                  monster.getInitialHealth(),
                  monster.getCurrentHealth()));
        }
      }
      locationD.put(item, temp);
    }
    return locationD;
  }

  @Override
  public LocationType getType() {
    int possibleN = 0;
    if (moveNorth != null) {
      possibleN++;
    }
    if (moveWest != null) {
      possibleN++;
    }
    if (moveEast != null) {
      possibleN++;
    }
    if (moveSouth != null) {
      possibleN++;
    }
    if (possibleN == 0) {
      return null;
    } else if (possibleN == 2) {
      return LocationType.TUNNEL;
    } else {
      return LocationType.CAVE;
    }
  }

  @Override
  public void setNeighbour(Direction dir, Location loc) throws IllegalArgumentException {
    if (dir == null) {
      throw new IllegalArgumentException("direction to set neighbour cannot be null.");
    }
    if (loc != null) {
      if (loc.getRow() == this.row + 1) {
        if (loc.getColumn() != this.column) {
          throw new IllegalArgumentException(
                  "location passed cannot be neighbour to this location.");
        }
      }
      if (loc.getColumn() == this.column + 1) {
        if (loc.getRow() != this.row) {
          throw new IllegalArgumentException(
                  "location passed cannot be neighbour to this location.");
        }
      }
    }
    if (dir == Direction.NORTH) {
      moveNorth = loc;
    } else if (dir == Direction.WEST) {
      moveWest = loc;
    } else if (dir == Direction.EAST) {
      moveEast = loc;
    } else if (dir == Direction.SOUTH) {
      moveSouth = loc;
    }
  }

  @Override
  public Map<WeaponType, Integer> getWeaponInfo() {
    if (weapons.isEmpty()) {
      for (WeaponType w : WeaponType.values()) {
        weapons.put(w, 0);
      }
    }
    return Map.copyOf(weapons);
  }

  @Override
  public void placeMonster() throws IllegalStateException {
    if (monster != null) {
      throw new IllegalStateException("monster already exists in the location.");
    }
    if (getType() == LocationType.TUNNEL) {
      throw new IllegalStateException("cannot place monster in tunnels.");
    }
    Monster m = new Otyugh();
    monster = m;
  }

  @Override
  public void placeWeapon(WeaponType weapon, int quantity)
          throws IllegalArgumentException, IllegalStateException {
    if (weapon == null) {
      throw new IllegalArgumentException("weapon to place can't be null.");
    }
    if (quantity == 0) {
      throw new IllegalArgumentException("quantity to place cannot be 0.");
    }
    int existingQ = 0;
    if (!weapons.isEmpty()) {
      existingQ = (weapons.get(weapon) == null) ? 0 : weapons.get(weapon);
    }
    int newQ = existingQ + quantity;
    if (newQ < 0) {
      throw new IllegalStateException("weapon quantity cannot be set below 0.");
    }
    weapons.put(weapon, newQ);
  }

  @Override
  public Monster getMonster() {
    return monster;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.row);
    sb.append(",");
    sb.append(this.column);
    return sb.toString();
  }
}
