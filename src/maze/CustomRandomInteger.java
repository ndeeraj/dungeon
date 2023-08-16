package maze;

import java.util.Random;

/**
 * This class represent a random number generator.
 * Can be used as a predictable generator by providing predictable value as {@link Boolean}.True
 * In predictable mode, for successive calls to nextInt(),
 * will always select the lower bound.
 * If not used as a predictable generator, {@link Random}.nextInt() is used to generate the values.
 */
public final class CustomRandomInteger implements RandomInteger {
  private Random rand;

  /**
   * Used when the generation should be based on {@link Random}.
   */
  public CustomRandomInteger() {
    rand = new Random();
  }

  /**
   * Used when the generation should be predictable.
   *
   * @param predictable boolean, True when generation should be predictable.
   */
  public CustomRandomInteger(boolean predictable) {
    if (predictable) {
      rand = null;
    }
  }

  @Override
  public int nextInt(int lowerBound, int upperBound) {
    if (rand == null) {
      return lowerBound;
    } else {
      int boundToCheck = upperBound - lowerBound;
      int random = rand.nextInt(boundToCheck);
      return lowerBound + random;
    }
  }
}
