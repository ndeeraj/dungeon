package mazegraphiccontroller;

import maze.Direction;
import maze.Dungeon;

/**
 * Represents a {@link Dungeon} controller that is compatible with a graphical view which
 * supports operation in {@link DungeonView}.
 * Entry to the controller should be through the playGame() method.
 */
public interface DungeonControllerFeatures {

  /**
   * makes a call to the model to make move in the specified direction.
   *
   * @param dir {@link Direction} to move.
   * @throws IllegalArgumentException when dir is null.
   * @throws IllegalStateException    when the controller is not initialized with a model.
   */
  void move(Direction dir) throws IllegalArgumentException, IllegalStateException;

  /**
   * makes a call to the model to collect treasure from the player's location.
   *
   * @throws IllegalStateException when the controller is not initialized with a model.
   */
  void collectTreasure() throws IllegalStateException;

  /**
   * makes a call to the model to pick up weapon from the player's location.
   *
   * @throws IllegalStateException when the controller is not initialized with a model.
   */
  void pickWeapon() throws IllegalStateException;

  /**
   * entry point to the controller, makes the view visible and launches the game.
   *
   * @throws IllegalStateException when the controller is not initialized with the view.
   */
  void playGame() throws IllegalStateException;

  /**
   * make a call to the model to shoot an arrow in the specified direction and distance.
   *
   * @param dirToShoot {@link Direction} to shoot.
   * @param distance   integer representing the distance to shoot.
   * @throws IllegalStateException    when the controller is not initialized with the model.
   * @throws IllegalArgumentException when dirToShoot is null.
   */
  void shootArrow(Direction dirToShoot, int distance)
          throws IllegalStateException, IllegalArgumentException;

  /**
   * creates an instance of {@link Dungeon} and sets a new instance of the game.
   *
   * @param name       name of the player.
   * @param row        row size of the dungeon.
   * @param column     column size of the dungeon.
   * @param interConn  interconnectivity between the dungeon locations.
   * @param treasureP  percentage of locations that should contain treasure / arrows.
   * @param difficulty number of otyughs that are present in the dungeon.
   * @param wrapping   whether to create a wrapping / non-wrapping instance of the {@link Dungeon}.
   * @throws IllegalArgumentException when dungeon creation fails for the provided parameters.
   */
  void setUpGame(
          String name,
          int row,
          int column,
          int interConn,
          int treasureP,
          int difficulty,
          boolean wrapping) throws IllegalArgumentException;
}
