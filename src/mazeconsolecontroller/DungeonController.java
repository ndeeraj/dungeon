package mazeconsolecontroller;

import maze.Dungeon;

/**
 * Represents a Controller for {@link Dungeon}.
 * Handles user moves and actions by executing them using the model;
 * Also exposes game state when user makes actions.
 */
public interface DungeonController {

  /**
   * Execute a single game of dungeon given a dungeon model.
   * When the game is over, the playGame method ends.
   * @param dungeon {@link Dungeon}
   *
   * @throws IllegalArgumentException when dungeon is null.
   */
  void playGame(Dungeon dungeon) throws IllegalArgumentException;
}
