package de.saring.util.gui.javafx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ToggleGroup related methods in class BindingUtils.
 *
 * @author Stefan Saring
 */
public class BindingUtilsToggleGroupTest {

    private enum Options {
        OptionA, OptionB
    }

    private RadioButton rbOptionA;
    private RadioButton rbOptionB;

    private ToggleGroup tgOptions;

    private ObjectProperty<Options> optionsProperty;

    /**
     * Initializes the JavaFX toolkit before test execution. Otherwise the test will fail with
     * "java.lang.IllegalStateException: Toolkit not initialized".
     */
    @BeforeAll
    static void initJavaFXToolKit() {

        new Thread(() -> {
            Application.launch(DummyFXApplication.class);
        }).start();
    }

    @BeforeEach
    void setUp() {
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
    @Test
    public void testBindToggleGroupToPropertyMissingUserData() {

        rbOptionB.setUserData(null);

        assertThrows(IllegalArgumentException.class, () ->
            BindingUtils.bindToggleGroupToProperty(tgOptions, optionsProperty));
    }

    /**
     * Dummy application for initializing the JavaFX toolkit.
     */
    public static class DummyFXApplication extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
        }
    }
}
