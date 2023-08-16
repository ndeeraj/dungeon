package mazeconsolecontroller;

import maze.Dungeon;

import java.io.IOException;

/**
 * Represents actions that can be in the dungeon / accessors to get {@link Dungeon} model's state.
 * Intentionally making package private since it should not be available outside the package.
 */
interface DungeonCommand {

  /**
   * executes the command represented by the type using the model.
   * @param dungeon {@link Dungeon}
   * @throws IOException when it encounters error while getting inputs / writing output.
   */
  void apply(Dungeon dungeon) throws IOException;
}
