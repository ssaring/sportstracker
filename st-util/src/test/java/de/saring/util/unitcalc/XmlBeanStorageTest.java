package de.saring.util.unitcalc;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;

import de.saring.util.XmlBeanStorage;

/**
 * This class contains all unit tests of class XmlBeanStorage.
 *
 * @author Stefan Saring
 */
public class XmlBeanStorageTest {

    private static final String BEAN_FILENAME = XmlBeanStorageTest.class.getName() + ".xml";

    /**
     * Removes the test files after test execution.
     */
    @After
    public void tearDown() throws Exception {
        Files.deleteIfExists(Paths.get(BEAN_FILENAME));
    }

    /**
     * Tests the methods saveBean() and loadBean(): It stores an Java Bean to XML and reads it
     * afterwards. The read objects must be identical to the initial Java Bean.
     */
    @Test
    public void testSaveAndLoadBeanSuccess() throws Exception {

        final TestBean testBean = new TestBean();
        testBean.unitSystem = FormatUtils.UnitSystem.English;
        testBean.message = "Bar";
        testBean.number = 123;

        XmlBeanStorage.saveBean(testBean, BEAN_FILENAME);

        final TestBean loadedBean = (TestBean) XmlBeanStorage.loadBean(BEAN_FILENAME);
        assertEquals(testBean.unitSystem, loadedBean.unitSystem);
        assertEquals(testBean.message, loadedBean.message);
        assertEquals(testBean.number, loadedBean.number);
    }

    /**
     * Tests the method loadBean(): Loading must fail with a FileNotFoundException because
     * the file does not exists.
     */
    @Test(expected = FileNotFoundException.class)
    public void testLoadBeanFailedFileNotFound() throws Exception {
        XmlBeanStorage.loadBean(BEAN_FILENAME);
    }

    /**
     * Tests the method saveBean(): Saving must fail with a IOException because the file
     * can't be created (there's a directory with the same name).
     */
    @Test(expected = IOException.class)
    public void testSaveBeanFailedCreateFile() throws Exception {
        Files.createDirectory(Paths.get(BEAN_FILENAME));
        XmlBeanStorage.saveBean(new TestBean(), BEAN_FILENAME);
    }

    /**
     * Simple Java Bean for testing.
     */
    public static class TestBean implements Serializable {
        private static final long serialVersionUID = 0L;

        private FormatUtils.UnitSystem unitSystem = FormatUtils.UnitSystem.Metric;
        private String message = "Foo";
        private Integer number = 42;

        public FormatUtils.UnitSystem getUnitSystem() {
            return unitSystem;
        }

        public void setUnitSystem(final FormatUtils.UnitSystem unitSystem) {
            this.unitSystem = unitSystem;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(final Integer number) {
            this.number = number;
        }
    }
}
