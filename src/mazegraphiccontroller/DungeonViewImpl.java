package mazegraphiccontroller;

import maze.Direction;
import maze.LocationDescription;
import maze.PlayerDescription;
import maze.PlayerStatus;
import maze.ReadOnlyDungeon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Represents a GUI for a {@link maze.Dungeon} compatible with {@link DungeonControllerFeatures}.
 * After initializing the view before calling other operations on the view,
 * a {@link ReadOnlyDungeon} should be initialized to the view using assignReadOnlyModel method.
 * listeners should be initialized using setListeners method.
 */
public class DungeonViewImpl extends JFrame implements DungeonView {
  private final DungeonPanel mainGamePane;
  private final PlayerDetailsPanel playerDPane;
  private DungeonControllerFeatures control;
  private ReadOnlyDungeon model;

  /**
   * Initializes the view with all the components that are part of the GUI.
   */
  public DungeonViewImpl() {
    super();

    this.setTitle("Dungeon");
    this.setSize(800, 600);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    setupMenuBar();

    // use a border layout with status panel in the north, drawing panel in center
    // and button panel in south.
    this.setLayout(new BorderLayout());

    playerDPane = new PlayerDetailsPanel();
    playerDPane.setPreferredSize(new Dimension(800, 50));
    this.add(playerDPane, BorderLayout.NORTH);

    mainGamePane = new DungeonPanel();
    JScrollPane scrollPane = new JScrollPane(mainGamePane);
    scrollPane.setPreferredSize(new Dimension(800, 450));
    this.add(scrollPane, BorderLayout.CENTER);

    //button panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.setPreferredSize(new Dimension(800, 50));
    this.add(buttonPanel, BorderLayout.SOUTH);

    //quit button
    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener((ActionEvent e) -> System.exit(0));
    buttonPanel.add(quitButton);

    this.setFocusable(true);
    this.requestFocus();
    this.setResizable(false);

    this.pack();
  }

  private void setupMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Options");
    menuBar.add(menu);

    JMenuItem newGameItem = new JMenuItem("New Game");
    menu.add(newGameItem);
    newGameItem.addActionListener(l -> showNewGameScreen());

    JMenuItem quitItem = new JMenuItem("Quit");
    menu.add(quitItem);
    quitItem.addActionListener((ActionEvent e) -> System.exit(0));

    this.setJMenuBar(menuBar);
  }

  @Override
  public void repaintDungeon() {
    this.repaint();
  }

  @Override
  public void showNewGameScreen() {
    // sets up the setting screen.
    JPanel newGScreen = new JPanel();
    newGScreen.setLayout(new GridLayout(0, 2, 2, 2));
    final JLabel name = new JLabel("player name ");
    final JTextField nameVal = new JTextField(20);
    final JLabel note = new JLabel("");
    JTextArea noteText = new JTextArea(
            "NOTE: for wrapping dungeon,\n row must be >= 5 and column must be >=6");
    noteText.setEditable(false);
    final JLabel row = new JLabel("row size (min 4)");
    final JTextField rowVal = new JTextField(5);
    final JLabel col = new JLabel("column size (min 5)");
    final JTextField colVal = new JTextField(5);
    final JLabel difficulty = new JLabel("difficulty (min 0)");
    final JTextField dVal = new JTextField(5);
    final JLabel interC = new JLabel("interconnectivity (min 0)");
    final JTextField interConnV = new JTextField(5);
    final JLabel treasureP = new JLabel("treasure percentage (0-100)");
    final JTextField treasureV = new JTextField(5);
    final JLabel wrapping = new JLabel("wrapping ");
    final JCheckBox wrappingS = new JCheckBox();

    rowVal.setText("4");
    colVal.setText("5");
    dVal.setText("1");

    dVal.setToolTipText("value will be used to \nset the number of otyughs in the dungeon.");
    treasureV.setToolTipText("percentage of location that should hold treasure / arrows.");
    interConnV.setToolTipText("higher value means more connectivity between locations.");
    wrappingS.setToolTipText(
            "enabling creates locations at the edges that may wrap to other side.");

    newGScreen.add(name);
    newGScreen.add(nameVal);
    newGScreen.add(noteText);
    newGScreen.add(note);
    newGScreen.add(row);
    newGScreen.add(rowVal);
    newGScreen.add(col);
    newGScreen.add(colVal);
    newGScreen.add(difficulty);
    newGScreen.add(dVal);
    newGScreen.add(interC);
    newGScreen.add(interConnV);
    newGScreen.add(treasureP);
    newGScreen.add(treasureV);
    newGScreen.add(wrapping);
    newGScreen.add(wrappingS);

    int result = JOptionPane.showConfirmDialog(this, newGScreen,
            "Game settings", JOptionPane.OK_CANCEL_OPTION);

    try {
      control.setUpGame(nameVal.getText(),
              Integer.parseInt(rowVal.getText()),
              Integer.parseInt(colVal.getText()),
              Integer.parseInt(interConnV.getText()),
              Integer.parseInt(treasureV.getText()),
              Integer.parseInt(dVal.getText()),
              wrappingS.isSelected());
    } catch (NumberFormatException e) {
      if (result == JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(this,
                "you have used a text value for a numeric field.");
        showNewGameScreen();
      }
    } catch (IllegalArgumentException e) {
      if (result == JOptionPane.OK_OPTION) {
        JOptionPane.showMessageDialog(this, e.getMessage());
        showNewGameScreen();
      }
    }

    this.repaint();
    this.requestFocus();
  }

  @Override
  public void showShootFeedback(boolean result) {
    String message = null;
    if (result) {
      message = "bullseye!!! otyugh was hit.";
    } else {
      message = "shot failed, you lost an arrow.";
    }
    JOptionPane.showMessageDialog(this, message);
  }

  @Override
  public void setListeners(DungeonControllerFeatures control) throws IllegalArgumentException {
    if (control == null) {
      throw new IllegalArgumentException("controller should not be null.");
    }
    this.control = control;

    this.addKeyListener(new KeyListener() {
      private boolean shoot = false;

      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 't') {
          control.collectTreasure();
        } else if (e.getKeyChar() == 'a') {
          control.pickWeapon();
        } else if (e.getKeyChar() == 'a') {
          control.pickWeapon();
        }
      }

      @Override
      public synchronized void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
          shoot = true;
        }

        Direction dir = null;
        if (e.getKeyCode() == KeyEvent.VK_UP) {
          dir = Direction.NORTH;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
          dir = Direction.SOUTH;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
          dir = Direction.WEST;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          dir = Direction.EAST;
        }

        if ((shoot) && (dir != null)) {
          String arrow = model.describePlayer()
                  .get(PlayerDescription.WEAPON).get(0).split("\\s")[1];
          int arrQ = Integer.parseInt(arrow);
          if (arrQ > 0) {
            int distance = showShootScreen(dir);
            if (distance != 0) {
              control.shootArrow(dir, distance);
            }
          } else {
            showOutOfArrows();
          }
          shoot = false;
        } else if (dir != null) {
          control.move(dir);
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
        shoot = false;
      }
    });
  }

  private void showOutOfArrows() {
    JOptionPane.showMessageDialog(this,
            "you don't have arrows to shoot, collect some from dungeon.");
  }

  // shows the prompt that lets user ot enter distance to shoot.
  private int showShootScreen(Direction dir) {
    String result = (String) JOptionPane.showInputDialog(
            this,
            String.format("shooting in %s,\nenter a distance to shoot : ", dir.name()),
            "shoot",
            JOptionPane.PLAIN_MESSAGE,
            null,
            new Object[]{"1", "2", "3", "4", "5"},
            null);
    if (result == null) {
      return 0;
    }
    return Integer.parseInt(result);
  }

  @Override
  public void assignReadOnlyModel(ReadOnlyDungeon m) {
    mainGamePane.assignReadOnlyModel(m);
    mainGamePane.setController(control);
    playerDPane.assignReadOnlyModel(m);
    this.model = m;
  }

  @Override
  public void showGameEndedScreen() {
    StringBuffer message = new StringBuffer();
    String monsterHealth = model.describeLocation()
            .get(LocationDescription.MONSTER).get(0).split("\\s")[2];

    int mHealth = Integer.parseInt(monsterHealth);
    if (model.getPlayerStatus() == PlayerStatus.ALIVE) {
      if (mHealth == 0) {
        message.append("congrats!!! you made it to the end alive.");
      } else {
        message.append("lucky escape!!! you made it to the end alive "
                + "though there was an injured otyugh in the end location.");
      }
    } else {
      message.append("oh no!!! you have been killed by the otyugh.");
    }
    message.append("\n\ngame ended. you can start a new game or quit.");
    JOptionPane.showMessageDialog(this, message);
  }

  @Override
  public void setVisible() {
    this.setVisible(true);
    this.requestFocus();
  }
}
