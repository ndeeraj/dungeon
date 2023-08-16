package demo;

import maze.CustomRandomInteger;
import maze.Dungeon;
import maze.NonWrappingDungeon;
import maze.WrappingDungeon;
import mazeconsolecontroller.DungeonConsoleController;
import mazeconsolecontroller.DungeonController;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;

/**
 * Driver for the console controller.
 */
public class ConsoleController {
  /**
   * main method that drives the controller.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("\nInvalid number of parameters."
              + "\nShould be on of ('predictable', 'random', 'custom').\n");
      return;
    }
    switch (args[0]) {
      case "predictable": {
        runPredictableDungeon();
      }
      break;
      case "random": {
        runRandomDungeon();
      }
      break;
      case "custom": {
        runCustomDungeon();
      }
      break;
      default: {
        System.out.println("\nUnsupported mode."
                + "\nShould be on of ('predictable', 'random', 'custom').\n");
        return;
      }
    }
  }

  private static void runCustomDungeon() {
    Scanner scan = new Scanner(System.in);
    String name;
    do {
      System.out.println("\nEnter name for the player: ");
      name = scan.next();
    }
    while (name.length() == 0);

    int row = 0;
    do {
      try {
        System.out.println("\nNumber of rows for the dungeon: ");
        row = scan.nextInt();
      }
      catch (NumberFormatException exp) {
        System.out.println("\ninvalid value for row.");
        row = 0;
      }
    }
    while (row == 0);

    int col = 0;
    do {
      try {
        System.out.println("\nNumber of columns for the dungeon: ");
        col = scan.nextInt();
      }
      catch (NumberFormatException exp) {
        System.out.println("\ninvalid value for column.");
        col = 0;
      }
    }
    while (col == 0);

    boolean wrapping = false;
    boolean validVal = false;
    String token;

    while (!validVal) {
      System.out.println("\nWould you like to create a wrapping / non-wrapping dungeon W-N: ");
      token = scan.next();
      switch (token) {
        case "W" : {
          wrapping = true;
          validVal = true;
        }
        break;
        case "N" : {
          wrapping = false;
          validVal = true;
        }
        break;
        default : {
          System.out.println("\nInvalid option: " + token);
        }
        break;
      }
    }

    int interConn;
    do {
      try {
        System.out.println("\nLevel of interconnectivity for the dungeon: ");
        interConn = scan.nextInt();
      }
      catch (NumberFormatException exp) {
        System.out.println("\ninvalid value for treasure percentage.");
        interConn = 0;
      }
    }
    while (interConn == 0);

    int treasureP;
    do {
      try {
        System.out.println("\nPercentage of caves that should hold treasure and weapons "
                + "in the dungeon (enter whole number without percentage sign): ");
        treasureP = scan.nextInt();
      }
      catch (NumberFormatException exp) {
        System.out.println("\ninvalid value for treasure percentage.");
        treasureP = 0;
      }
    }
    while (treasureP == 0);

    int difficulty = 0;
    do {
      try {
        System.out.println("\nDifficulty for the dungeon: ");
        difficulty = scan.nextInt();
      }
      catch (NumberFormatException exp) {
        System.out.println("\ninvalid value for difficulty.");
        difficulty = 0;
      }
    }
    while (difficulty == 0);

    Dungeon dungeon;
    if (wrapping) {
      dungeon = new WrappingDungeon(name, row, col, interConn, treasureP, difficulty, null);
    }
    else {
      dungeon = new NonWrappingDungeon(name, row, col, interConn, treasureP, difficulty, null);
    }
    InputStreamReader input = new InputStreamReader(System.in);
    DungeonController control = new DungeonConsoleController(input, System.out);
    control.playGame(dungeon);
  }

  private static void runRandomDungeon() {
    Dungeon dungeon = new WrappingDungeon("player1", 5, 6, 3,
            50, 4, null);
    System.out.println(dungeon);
    InputStreamReader input = new InputStreamReader(System.in);
    DungeonController control = new DungeonConsoleController(input, System.out);
    control.playGame(dungeon);
  }

  private static void runPredictableDungeon() {
    Dungeon dungeon;
    do {
      dungeon = new NonWrappingDungeon("player1", 4, 5,
              1, 100, 1, new CustomRandomInteger(true));
      dungeon.enter();
    }
    while (!dungeon.getPlayerLocation().equals("0,0"));

    Scanner scan = new Scanner(System.in);
    String token;
    StringBuilder movesDef = new StringBuilder("E T M E M E W S N 7 2 M E M E M E e S M W s M W M "
            + "W S W 4 M W M S M E M E M E M S E N S M 1 M S M W M E M W M W M E");
    boolean validIn = false;

    while (!validIn) {
      System.out.println("\nwould you like to shoot and escape / don't shoot "
              + "/ injure monster once (S-D-I)? ");
      token = scan.next();
      switch (token) {
        case "S" : {
          movesDef.append("  M W S W 1 M W S W 1 M W P L Q");
          validIn = true;
        }
        break;
        case "D" : {
          movesDef.append(" M W M W M W P L Q");
          validIn = true;
        }
        break;
        case "I" : {
          movesDef.append("  M W S W 1 M W M W P L Q");
          validIn = true;
        }
        break;
        default: {
          System.out.println("\nUnsupported option.\n");
        }
      }
    }
    StringReader input = new StringReader(movesDef.toString());
    DungeonController control = new DungeonConsoleController(input, System.out);
    control.playGame(dungeon);
  }
}
