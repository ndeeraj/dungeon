package maze;

/**
 * Abstract class for monsters that capture all common state and behaviors between monsters.
 * Intentionally making the class package private so that it is not available outside the package.
 */
abstract class AbstractMonster implements Monster {
  protected int initialHealth;
  protected int currentHealth;
  protected MonsterType type;

  protected AbstractMonster(int initialHealth, MonsterType type) throws IllegalArgumentException {
    if (initialHealth <= 0) {
      throw new IllegalArgumentException("initial health should be greater than 0.");
    }
    if (type == null) {
      throw new IllegalArgumentException("monster type cannot be null.");
    }
    this.initialHealth = initialHealth;
    this.currentHealth = initialHealth;
    this.type = type;
  }

  @Override
  public int getInitialHealth() {
    return this.initialHealth;
  }

  @Override
  public int getCurrentHealth() {
    return this.currentHealth;
  }

  @Override
  public MonsterType getType() {
    return this.type;
  }
}
