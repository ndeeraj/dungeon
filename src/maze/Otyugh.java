package maze;

/**
 * Represent state and operations possible on an Otyugh.
 * Otyugh has an initial health of 2, after each attack the health reduces by 1.
 * Cannot slay a Otyugh with current health 0.
 * Intentionally making the class package private so that it is not available outside the package.
 */
final class Otyugh extends AbstractMonster {

  /**
   * Initializes an Otyugh with initial health as 2.
   */
  public Otyugh() {
    super(2, MonsterType.OTYUGH);
  }

  @Override
  public int slay() throws IllegalStateException {
    if ((currentHealth - 1) < 0) {
      throw new IllegalStateException("can't slay a dead Otyugh.");
    } else {
      currentHealth--;
    }
    return 1;
  }
}
