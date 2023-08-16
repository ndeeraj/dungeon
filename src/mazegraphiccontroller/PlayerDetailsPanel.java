package mazegraphiccontroller;

import maze.PlayerDescription;
import maze.ReadOnlyDungeon;
import maze.Treasure;
import maze.WeaponType;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Represents the panel that shows the player's treasure and weapon details.
 * For the panel to display the player's details properly, a {@link ReadOnlyDungeon}
 * should be assigned using the assignReadOnlyModel method.
 * Intentionally making the class package private since it should not be available
 * outside the package.
 */
final class PlayerDetailsPanel extends JPanel {
  private ReadOnlyDungeon readModel;
  private final Font boldItalicFont;

  /**
   * Initializes the player details panel.
   */
  public PlayerDetailsPanel() {
    boldItalicFont = new Font("Serif", Font.BOLD + Font.ITALIC, 20);
  }

  /**
   * sets the provided read model to the panel instance.
   *
   * @param readModel {@link ReadOnlyDungeon}.
   */
  public void assignReadOnlyModel(ReadOnlyDungeon readModel) {
    this.readModel = readModel;
  }

  @Override
  protected void paintComponent(Graphics g) throws IllegalArgumentException, IllegalStateException {
    if (g == null) {
      throw new IllegalArgumentException("graphics should not be null");
    }
    super.paintComponent(g);
    if (readModel == null) {
      return;
    }
    this.setBackground(Color.black);
    Map<PlayerDescription, List<String>> playerD = readModel.describePlayer();
    List<String> treasureDesc = playerD.get(PlayerDescription.TREASURE);
    List<String> weaponDesc = playerD.get(PlayerDescription.WEAPON);

    // computing the current treasure / arrow quantities of the user.
    int diaQ = 0;
    int rubyQ = 0;
    int sapQ = 0;
    int i = 0;
    for (Treasure t : Treasure.values()) {
      String temp;
      temp = treasureDesc.get(i);
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

    int arrQ = 0;
    i = 0;
    for (WeaponType w : WeaponType.values()) {
      String temp;
      temp = weaponDesc.get(i);
      String[] weaponD = temp.split("\\s");
      int tempQ = Integer.parseInt(weaponD[1]);
      if (w == WeaponType.CROOKEDARROW) {
        if (tempQ > 0) {
          arrQ = tempQ;
        }
      }
      i++;
    }
    Graphics2D g2d = (Graphics2D) g;
    g2d.setColor(Color.WHITE);
    g.setFont(boldItalicFont);

    // drawing the treasure / arrows along with their quantities.
    try {
      InputStream imageStream = getClass().getResourceAsStream("/img/diamond.png");
      BufferedImage image = ImageIO.read(imageStream);
      g2d.drawImage(image, 0, 0, 35, 35, null);

      g2d.drawString(Integer.toString(diaQ), 40, 30);

      imageStream = getClass().getResourceAsStream("/img/ruby.png");
      image = ImageIO.read(imageStream);
      g2d.drawImage(image, 120, 0, 35, 35, null);
      g2d.drawString(Integer.toString(rubyQ), 160, 30);

      imageStream = getClass().getResourceAsStream("/img/emerald.png");
      image = ImageIO.read(imageStream);
      g2d.drawImage(image, 240, 0, 35, 35, null);
      g2d.drawString(Integer.toString(sapQ), 280, 30);

      imageStream = getClass().getResourceAsStream("/img/arrow-white.png");
      image = ImageIO.read(imageStream);
      g2d.drawImage(image, 360, 12, 100, 18, null);
      g2d.drawString(Integer.toString(arrQ), 500, 30);
    } catch (IOException e) {
      throw new IllegalStateException("error while reading image" + e);
    }
  }
}

