package de.saring.util.gui.javafx;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of class ColorConverter.
 *
 * @author Stefan Saring
 */
public class ColorConverterTest {

    /**
     * Tests of method toAwtColor().
     */
    @Test
    public void testToAwtColor() {
        javafx.scene.paint.Color fxColor = new javafx.scene.paint.Color(1.0d, 0.5d, 0d, 1d);
        assertEquals(new java.awt.Color(255, 128, 0), ColorConverter.toAwtColor(fxColor));
    }

    /**
     * Tests of method toFxColor().
     */
    @Test
    public void testToFxColor() {
        java.awt.Color awtColor = new java.awt.Color(255, 128, 0);
        // compare String representations, not double values
        assertEquals("0xff8000ff", ColorConverter.toFxColor(awtColor).toString());
    }
}
