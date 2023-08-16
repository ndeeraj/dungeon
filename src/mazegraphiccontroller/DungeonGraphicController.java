package mazegraphiccontroller;

import maze.Direction;
import maze.Dungeon;
import maze.NonWrappingDungeon;
import maze.ReadOnlyDungeon;
import maze.WrappingDungeon;

/**
 * Represents a {@link Dungeon} controller that is compatible with a graphical view which
 * supports operation in {@link DungeonView}.
 * An instance of the controller can either be constructed by passing just a {@link DungeonView} or
 * by passing a {@link DungeonView} and a {@link Dungeon}.
 * In both cases, playGame() method is the entry point to the controller which presents the game.
 */
public class DungeonGraphicController implements DungeonControllerFeatures {

  private Dungeon model;
  private final DungeonView view;

  /**
   * Initializes the controller with provided view and model.
   *
   * @param view  an instance of {@link DungeonView}
   * @param model an instance of {@link Dungeon}
   * @throws IllegalArgumentException when view is null; when model is null.
   */
  public DungeonGraphicController(DungeonView view, Dungeon model) throws IllegalArgumentException {
    this(view);
    if (model == null) {
      throw new IllegalArgumentException("model should not be null.");
    }
    this.model = model;
    ReadOnlyDungeon readOnlyModel = (ReadOnlyDungeon) model;
    view.assignReadOnlyModel(readOnlyModel);
    model.enter();
  }

  /**
   * Initializes the controller with the provided view.
   *
   * @param view an instance of {@link DungeonView}.
   * @throws IllegalArgumentException when view is null.
   */
  public DungeonGraphicController(DungeonView view) throws IllegalArgumentException {
    if (view == null) {
      throw new IllegalArgumentException("view should not be null.");
    }
    this.view = view;
  }

  @Override
  public void move(Direction dir) throws IllegalArgumentException, IllegalStateException {
    if (model == null) {
      throw new IllegalStateException("model is not initialized.");
    }
    if (dir == null) {
      throw new IllegalArgumentException("direction to shoot should not be null");
    }
    try {
      model.move(dir);
      view.repaintDungeon();
      if (model.gameEnded()) {
        view.showGameEndedScreen();
      }
    } catch (IllegalArgumentException | IllegalStateException exp) {
      /* Intentionally suppressing since why the exception occurs is obvious.
      The exceptions would occur when user inputs a direction that cannot be the next move or make
      a move when the game has ended.
      */
    }
  }

  @Override
  public void collectTreasure() throws IllegalStateException {
    if (model == null) {
      throw new IllegalStateException("model is not initialized.");
    }
    try {
      model.collectTreasure();
      view.repaintDungeon();
    } catch (IllegalStateException exp) {
      /* Intentionally suppressing since why the exception occurs is obvious.
      Only when the game ends and the user tries to collect treasure, the exception would occur.
      */
    }
  }

  @Override
  public void pickWeapon() throws IllegalStateException {
    if (model == null) {
      throw new IllegalStateException("model is not initialized.");
    }
    try {
      model.pickWeapon();
      view.repaintDungeon();
    } catch (IllegalStateException exp) {
      /* Intentionally suppressing since why the exception occurs is obvious.
      Only when the game ends and the user tries to collect arrows, the exception would occur.
       */
    }
  }

  @Override
  public void playGame() throws IllegalStateException {
    if (view == null) {
      throw new IllegalStateException("view is not initialized.");
    }
    view.setListeners(this);
    view.setVisible();
    if (model == null) {
      view.showNewGameScreen();
    }
  }

  @Override
  public void shootArrow(Direction dirToShoot, int distance)
          throws IllegalStateException, IllegalArgumentException {
    if (model == null) {
      throw new IllegalStateException("model is not initialized.");
    }
    if (dirToShoot == null) {
      throw new IllegalArgumentException("direction should not be null.");
    }
    try {
      boolean shootR = model.shootArrow(dirToShoot, distance);
      view.showShootFeedback(shootR);
      view.repaintDungeon();
    } catch (IllegalStateException exp) {
      throw new IllegalStateException(exp.getMessage());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(e.getMessage());
    }
  }

  @Override
  public void setUpGame(String name,
                        int row,
                        int column,
                        int interConn,
                        int treasureP,
                        int difficulty,
                        boolean wrapping)
          throws IllegalArgumentException {
    try {
      if (wrapping) {
        model = new WrappingDungeon(
                name, row, column, interConn, treasureP, difficulty, null);
      } else {
        model = new NonWrappingDungeon(
                name, row, column, interConn, treasureP, difficulty, null);
      }
      ReadOnlyDungeon readOnlyModel = (ReadOnlyDungeon) model;
      view.assignReadOnlyModel(readOnlyModel);
      model.enter();
      view.repaintDungeon();
    } catch (IllegalArgumentException | IllegalStateException exp) {
      throw new IllegalArgumentException(exp.getMessage());
    }
  }
}
