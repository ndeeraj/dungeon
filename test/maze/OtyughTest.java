package maze;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testing class for {@link Monster}.
 */
public class OtyughTest {

  private Monster mons1;

  @Before
  public void setUp() throws Exception {
    mons1 = new Otyugh();
  }

  @Test
  public void getInitialHealth() {
    assertEquals("Initial health should be 2", mons1.getInitialHealth(), 2);
  }

  @Test
  public void getCurrentHealth() {
    assertEquals("Current health should be 2", mons1.getCurrentHealth(), 2);
  }

  @Test
  public void slay() {
    int attack = mons1.slay();
    assertEquals("Impact made should be 1.", attack, 1);
    assertEquals("Current health should be 1", mons1.getCurrentHealth(), 1);
    assertEquals("Initial health should be 2", mons1.getInitialHealth(), 2);
    attack = mons1.slay();
    assertEquals("Impact made should be 1.", attack, 1);
    assertEquals("Current health should be 0", mons1.getCurrentHealth(), 0);
    assertEquals("Initial health should be 2", mons1.getInitialHealth(), 2);
  }

  @Test(expected = IllegalStateException.class)
  public void slayDeadMons() {
    mons1.slay();
    mons1.slay();
    mons1.slay();
  }

  @Test
  public void getType() {
    assertEquals("type should be correct", mons1.getType(), MonsterType.OTYUGH);
  }
}