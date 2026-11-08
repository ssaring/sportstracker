package de.saring.sportstracker.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/**
 * This class contains all unit tests for the SportSubType class.
 *
 * @author Stefan Saring
 */
public class SportSubTypeTest {

  /** Tests of method clone(). */
  @Test
  public void testClone() {
    SportSubType sstOrg = new SportSubType(123L);
    sstOrg.setName("Sstype");

    SportSubType sstClone = sstOrg.clone();
    assertFalse(sstOrg == sstClone);
    assertEquals(123, sstClone.getId());
    assertEquals("Sstype", sstClone.getName());
  }
}
