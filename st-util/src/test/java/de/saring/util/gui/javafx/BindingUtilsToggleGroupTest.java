package de.saring.util.gui.javafx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.saxsys.javafx.test.JfxRunner;

/**
 * Unit tests for ToggleGroup related methods in class BindingUtils.
 *
 * @author Stefan Saring
 */
@RunWith(JfxRunner.class)
public class BindingUtilsToggleGroupTest {

    private enum Options {
        OptionA, OptionB
    }

    private RadioButton rbOptionA;
    private RadioButton rbOptionB;

    private ToggleGroup tgOptions;

    private ObjectProperty<Options> optionsProperty;

    @Before
    public void setUp() {
        rbOptionA = new RadioButton("Option A");
        rbOptionA.setUserData(Options.OptionA);

        rbOptionB = new RadioButton("Option B");
        rbOptionB.setUserData(Options.OptionB);

        tgOptions = new ToggleGroup();
        tgOptions.getToggles().add(rbOptionA);
        tgOptions.getToggles().add(rbOptionB);

        optionsProperty = new SimpleObjectProperty<>(Options.OptionB);
    }

    /**
     * Test of bindToggleGroupToProperty(): It binds a group of radio buttons to an ObjectProperty
     * which wraps an enum value. It tests the initial radio button selection and the update of
     * the property value after selection changes.
     */
    @Test
    public void testBindToggleGroupToPropertySuccess() {

        BindingUtils.bindToggleGroupToProperty(tgOptions, optionsProperty);

        // check initial selection of radio buttons
        assertFalse(rbOptionA.isSelected());
        assertTrue(rbOptionB.isSelected());
        assertEquals(Options.OptionB, optionsProperty.get());

        // modify selection => make sure the property contains the selected value
        rbOptionA.setSelected(true);
        assertTrue(rbOptionA.isSelected());
        assertFalse(rbOptionB.isSelected());
        assertEquals(Options.OptionA, optionsProperty.get());
    }

    /**
     * Test of bindToggleGroupToProperty(): One radio button has no user data set,
     * the binding must throw an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBindToggleGroupToPropertyMissingUserData() {

        rbOptionB.setUserData(null);
        BindingUtils.bindToggleGroupToProperty(tgOptions, optionsProperty);
    }
}
