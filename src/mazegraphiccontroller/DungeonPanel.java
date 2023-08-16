package mazegraphiccontroller;

import maze.Direction;
import maze.LocationDescription;
import maze.LocationType;
import maze.PlayerStatus;
import maze.ReadOnlyDungeon;
import maze.SmellIntensity;
import maze.Treasure;
import maze.WeaponType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Represents the panel that shows all the locations in the {@link maze.Dungeon} to the user.
 * This is the main game area.
 * After constructing the panel, for this panel to work, a {@link ReadOnlyDungeon} should be
 * assigned using assignReadOnlyModel method.
 * Intentionally making the class package private since it should not be available
 * outside the package.
 */
final class DungeonPanel extends JPanel {
  private final Map<String, LocationPanel> locPanels;

  /**
   * Initializes the panel.
   */
  public DungeonPanel() {
    locPanels = new HashMap<>();
    this.setFocusable(true);
  }

  /**
   * assigns the provided model to the panel.
   *
   * @param readModel {@link ReadOnlyDungeon}.
   * @throws IllegalArgumentException when readModel is null.
   */
  public void assignReadOnlyModel(ReadOnlyDungeon readModel) throws IllegalArgumentException {
    if (readModel == null) {
      throw new IllegalArgumentException("readModel should not be null.");
    }
    if (!locPanels.isEmpty()) {
      for (LocationPanel p : locPanels.values()) {
        this.remove(p);
      }
      locPanels.clear();
    }
    int dungeonR = readModel.getRow();
    int dungeonC = readModel.getCol();
    this.setLayout(new GridLayout(dungeonR, dungeonC));
    for (int i = 0; i < dungeonR; i++) {
      for (int j = 0; j < dungeonC; j++) {
        LocationPanel temp = new LocationPanel(i, j, readModel);
        temp.setPreferredSize(new Dimension(160, 112));
        locPanels.put(String.format("%s,%s", i, j), temp);
        this.add(temp);
      }
    }
    this.revalidate();
  }

  /**
   * sets the panel with the provided controller.
   *
   * @param control {@link DungeonControllerFeatures}.
   * @throws IllegalArgumentException when control is null.
   */
  public void setController(DungeonControllerFeatures control) throws IllegalArgumentException {
    if (control == null) {
      throw new IllegalArgumentException("control should not be null.");
    }
    for (LocationPanel p : locPanels.values()) {
      p.setController(control);
    }
  }

  /*
   * Represents each location in the dungeon.
   * All the main drawing is done here.
   */
  class LocationPanel extends JPanel {
    private final String location;
    private final ReadOnlyDungeon readModel;
    private boolean visited;
    private DungeonControllerFeatures control;
    private List<String> possibleNeigh;
    private Map<LocationDescription, List<String>> locInfo;

    /**
     * Initializes the location with provided row, column positions and the read only model.
     * Also sets the mouse adapter for each location panel.
     *
     * @param rowP      row position of the location.
     * @param colP      column position of the location.
     * @param readModel {@link ReadOnlyDungeon}.
     * @throws IllegalArgumentException when rowP/colP <= 0; when readModel is null.
     */
    public LocationPanel(int rowP,
                         int colP,
                         ReadOnlyDungeon readModel) throws IllegalArgumentException {
      if ((rowP < 0) || (colP < 0)) {
        throw new IllegalArgumentException("row / column value cannot be negative.");
      }
      if (readModel == null) {
        throw new IllegalArgumentException("read only model cannot be null.");
      }
      possibleNeigh = new ArrayList<>();
      locInfo = new HashMap<>();
      location = String.format("%s,%s", rowP, colP);
      this.readModel = readModel;
      visited = false;

      this.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

          String playerLoc = LocationPanel.this.readModel.getPlayerLocation();
          String[] rowCol = playerLoc.split(",");
          int playerR = Integer.parseInt(rowCol[0]);
          int playerC = Integer.parseInt(rowCol[1]);

          if ((playerR == rowP) && (playerC == (colP - 1))) {
            //move west
            if (control != null) {
              control.move(Direction.EAST);
            }
          } else if ((playerR == rowP) && (playerC == (colP + 1))) {
            //move east
            if (control != null) {
              control.move(Direction.WEST);
            }
          } else if ((playerR == rowP - 1) && (playerC == (colP))) {
            // move north
            if (control != null) {
              control.move(Direction.SOUTH);
            }
          } else if ((playerR == rowP + 1) && (playerC == (colP))) {
            // move south
            if (control != null) {
              control.move(Direction.NORTH);
            }
          }
        }
      });

      this.setVisible(true);
    }

    public void setController(DungeonControllerFeatures control) {
      this.control = control;
    }

    @Override
    protected void paintComponent(Graphics g)
            throws IllegalArgumentException, IllegalStateException {
      if (g == null) {
        throw new IllegalArgumentException("graphics should not be null");
      }
      super.paintComponent(g);
      if (readModel == null) {
        return;
      }

      String playerLoc = readModel.getPlayerLocation();
      if (!playerLoc.equals(location)) {
        if (!visited) {
          return;
        }
      } else {
        locInfo = readModel.describeLocation();
      }
      visited = true;

      Graphics2D g2d = (Graphics2D) g;
      g2d.setColor(Color.WHITE);

      drawBaseLocation(g2d, locInfo);

      String locType = locInfo.get(LocationDescription.TYPE).get(0);
      if (locType.equals(LocationType.CAVE.name())) {
        drawTreasure(g2d, locInfo);
        drawWeapons(g2d, locInfo);
        drawMonster(g2d, locInfo);
        drawStench(g2d, locInfo);
        drawPlayer(g2d, true);
      } else {
        drawWeapons(g2d, locInfo);
        drawStench(g2d, locInfo);
        drawPlayer(g2d, false);
      }
    }

    private void drawPlayer(Graphics2D g2d, boolean cave) throws IllegalStateException {
      try {
        if (readModel.getPlayerLocation().equals(location)) {
          InputStream imageStream = getClass().getResourceAsStream("/img/player.png");
          BufferedImage image = ImageIO.read(imageStream);
          if (cave) {
            if (!readModel.gameEnded()) {
              g2d.drawImage(image, 80, 36, 30, 30, null);
            } else {
              if (readModel.getPlayerStatus() == PlayerStatus.ALIVE) {
                g2d.drawImage(image, 80, 36, 30, 30, null);
              } else if (readModel.getPlayerStatus() == PlayerStatus.DECEASED) {
                imageStream = getClass().getResourceAsStream("/img/player-dead.png");
                image = ImageIO.read(imageStream);
                g2d.drawImage(image, 80, 36, 30, 30, null);
              }
            }
          } else {
            g2d.drawImage(image, 60, 36, 50, 50, null);
          }
        }
      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw player."
                + e.getMessage());
      }
    }

    private void drawStench(Graphics2D g2d, Map<LocationDescription, List<String>> locInfo)
            throws IllegalStateException {
      try {
        String smellInfo = locInfo.get(LocationDescription.SMELL).get(0);
        if (smellInfo.equals("null") || !readModel.getPlayerLocation().equals(location)) {
          return;
        }
        InputStream imageStream = null;
        BufferedImage image = null;
        for (SmellIntensity smell : SmellIntensity.values()) {
          if (smellInfo.equals(smell.name())) {
            if (smell == SmellIntensity.HIGH) {
              imageStream = getClass().getResourceAsStream("/img/stench02.png");
              image = ImageIO.read(imageStream);
            } else {
              imageStream = getClass().getResourceAsStream("/img/stench01.png");
              image = ImageIO.read(imageStream);
            }
          }
        }
        g2d.drawImage(image, 50, 26, 60, 60, null);

      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw stench."
                + e.getMessage());
      }
    }

    private void drawMonster(Graphics2D g2d, Map<LocationDescription, List<String>> locInfo)
            throws IllegalStateException {
      try {
        String monsterInfo = locInfo.get(LocationDescription.MONSTER).get(0);
        if (monsterInfo.equals("null")) {
          return;
        }
        String[] monsterD = monsterInfo.split("\\s");
        int currH = Integer.parseInt(monsterD[2]);
        if (currH == 0) {
          return;
        }
        InputStream imageStream = getClass().getResourceAsStream("/img/otyugh.png");
        BufferedImage image = ImageIO.read(imageStream);
        g2d.drawImage(image, 40, 36, 30, 30, null);

      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw monster."
                + e.getMessage());
      }
    }

    private void drawWeapons(Graphics2D g2d, Map<LocationDescription, List<String>> locInfo)
            throws IllegalStateException {
      try {
        List<String> weaponQ = locInfo.get(LocationDescription.WEAPON);
        int arrQ = 0;
        int i = 0;
        for (WeaponType w : WeaponType.values()) {
          String temp;
          temp = weaponQ.get(i);
          String[] weaponD = temp.split("\\s");
          int tempQ = Integer.parseInt(weaponD[1]);
          if (w == WeaponType.CROOKEDARROW) {
            if (tempQ > 0) {
              arrQ = tempQ;
            }
          }
          i++;
        }
        BufferedImage image = null;
        if (arrQ > 0) {
          InputStream imageStream = getClass().getResourceAsStream("/img/arrow-white.png");
          image = ImageIO.read(imageStream);
          g2d.drawImage(image, 0, 15, 20, 10, null);
        }
      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw weapons."
                + e.getMessage());
      }
    }

    private void drawTreasure(Graphics2D g2d, Map<LocationDescription, List<String>> locationD)
            throws IllegalStateException {
      try {
        List<String> possT = locationD.get(LocationDescription.TREASURE);
        int diaQ = 0;
        int rubyQ = 0;
        int sapQ = 0;
        int i = 0;
        for (Treasure t : Treasure.values()) {
          String temp;
          temp = possT.get(i);
          String[] treasureD = temp.split("\\s");
          int tempQ = Integer.parseInt(treasureD[1]);
          if (t == Treasure.DIAMONDS) {
            if (tempQ > 0) {
              diaQ = tempQ;
            }
          } else if (t == Treasure.RUBIES) {
            if (tempQ > 0) {
              rubyQ = tempQ;
            }
          } else if (t == Treasure.SAPPHIRES) {
            if (tempQ > 0) {
              sapQ = tempQ;
            }
          }
          i++;
        }
        InputStream imageStream = null;
        BufferedImage image = null;
        if (diaQ > 0) {
          imageStream = getClass().getResourceAsStream("/img/diamond.png");
          image = ImageIO.read(imageStream);
          g2d.drawImage(image, 0, 0, 10, 10, null);
        }
        if (rubyQ > 0) {
          imageStream = getClass().getResourceAsStream("/img/ruby.png");
          image = ImageIO.read(imageStream);
          g2d.drawImage(image, 10, 0, 10, 10, null);
        }
        if (sapQ > 0) {
          imageStream = getClass().getResourceAsStream("/img/emerald.png");
          image = ImageIO.read(imageStream);
          g2d.drawImage(image, 20, 0, 10, 10, null);
        }
      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw treasure."
                + e.getMessage());
      }
    }

    private void drawBaseLocation(Graphics2D g2d, Map<LocationDescription, List<String>> locationD)
            throws IllegalStateException {
      try {
        Map<Direction, String> possibleM = new HashMap<>();
        boolean north = false;
        boolean west = false;
        boolean east = false;
        boolean south = false;

        if (possibleNeigh.size() == 0) {
          possibleNeigh = locationD.get(LocationDescription.MOVES);
        }
        int i = 0;
        for (String poss : possibleNeigh) {
          if (!poss.equals("null")) {
            switch (i) {
              case 0: {
                possibleM.put(Direction.NORTH, poss);
                north = true;
              }
              break;
              case 1: {
                possibleM.put(Direction.WEST, poss);
                west = true;
              }
              break;
              case 2: {
                possibleM.put(Direction.EAST, poss);
                east = true;
              }
              break;
              case 3: {
                possibleM.put(Direction.SOUTH, poss);
                south = true;
              }
              break;
              default: // no action required.
            }
          }
          i++;
        }

        InputStream imageStream = null;
        BufferedImage image = null;
        int numMoves = possibleM.values().size();
        if (numMoves == 2) {
          // tunnels
          if (north && west) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/NW.png");
          } else if (north && east) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/NE.png");
          } else if (north && south) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/NS.png");
          } else if (west && east) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/EW.png");
          } else if (east && south) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/SE.png");
          } else if (west && south) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/SW.png");
          }
        } else {
          // caves
          if (numMoves == 4) {
            imageStream = getClass().getResourceAsStream("/img/color-cells/NSEW.png");
          } else if (numMoves == 3) {
            if (north && south && east) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/NSE.png");
            } else if (north && south && west) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/NSW.png");
            } else if (north && east && west) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/NEW.png");
            } else if (south && east && west) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/SEW.png");
            }
          } else {
            if (north) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/N.png");
            } else if (west) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/W.png");
            } else if (east) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/E.png");
            } else if (south) {
              imageStream = getClass().getResourceAsStream("/img/color-cells/S.png");
            }
          }
        }
        image = ImageIO.read(imageStream);
        g2d.drawImage(image, 0, 0, 160, 112, null);
      } catch (IOException e) {
        throw new IllegalStateException("error while loading image to draw base images."
                + e.getMessage());
      }
    }
  }
}
