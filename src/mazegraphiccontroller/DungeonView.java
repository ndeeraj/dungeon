package mazegraphiccontroller;

import maze.ReadOnlyDungeon;

/**
 * Represents operations that should be possible in a GUI for {@link maze.Dungeon} game.
 * Depends on a controller that supports operations defined in {@link DungeonControllerFeatures}.
 */
public interface DungeonView {

  /**
   * repaints the main game area.
   */
  void repaintDungeon();

  /**
   * shows the configuration settings for a dungeon to the user and sends the values entered to the
   * controller to create a {@link maze.Dungeon} instance.
   */
  void showNewGameScreen();

  /**
   * shows the feedback of a shot to the user.
   *
   * @param result whether the result of a shot was a success / failure.
   */
  void showShootFeedback(boolean result);

  /**
   * sets the passed controller as a callback object.
   *
   * @param control {@link DungeonControllerFeatures} used as a callback object.
   * @throws IllegalArgumentException when control is null.
   */
  void setListeners(DungeonControllerFeatures control) throws IllegalArgumentException;

  /**
   * sets the view with the provided read model so that the view can use it to get data directly
   * from the model.
   *
   * @param readModel {@link ReadOnlyDungeon}.
   */
  void assignReadOnlyModel(ReadOnlyDungeon readModel);

  /**
   * shows the game ended screen to the user.
   */
  void showGameEndedScreen();

  /**
   * makes the view visible.
   */
  void setVisible();
}
