package mazecontrollertest;

import maze.ReadOnlyDungeon;
import mazegraphiccontroller.DungeonControllerFeatures;
import mazegraphiccontroller.DungeonView;

/**
 * Intentionally making the class package private since it should not be available
 * outside the package.
 */
class MockDungeonView implements DungeonView {
  private StringBuffer calledF;

  public MockDungeonView(StringBuffer calledF) {
    this.calledF = calledF;
  }

  @Override
  public void repaintDungeon() {
    calledF.append("repaintDungeon\n");
  }

  @Override
  public void showNewGameScreen() {
    calledF.append("showNewGameScreen\n");
  }

  @Override
  public void showShootFeedback(boolean result) {
    calledF.append("showShootFeedback\n");
  }

  @Override
  public void setListeners(DungeonControllerFeatures f) {
    calledF.append("setListeners\n");
  }

  @Override
  public void assignReadOnlyModel(ReadOnlyDungeon m) {
    calledF.append("assignReadOnlyModel\n");
  }

  @Override
  public void showGameEndedScreen() {
    calledF.append("showGameEndedScreen\n");
  }

  @Override
  public void setVisible() {
    calledF.append("setVisible\n");
  }
}
