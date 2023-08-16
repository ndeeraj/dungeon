package maze;

/**
 * Represents operations possible with a random integer generator.
 */
public interface RandomInteger {
  /**
   * generates an integer between the provided upper bound and lower bound (both inclusive).
   * @param lowerBound lower bound
   * @param upperBound upper bound
   * @return Integer between upper bound and lower bound.
   */
  int nextInt(int lowerBound, int upperBound);
}
