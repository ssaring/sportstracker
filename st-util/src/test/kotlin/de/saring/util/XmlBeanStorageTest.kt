package de.saring.util

import de.saring.util.unitcalc.UnitSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException
import java.io.IOException
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This class contains all unit tests of class XmlBeanStorage.
 *
 * @author Stefan Saring
 */
class XmlBeanStorageTest {

    /**
     * Removes the test files after test execution.
     */
    @AfterEach
    fun tearDown() {
        Files.deleteIfExists(Paths.get(BEAN_FILENAME))
    }

    /**
     * Tests the methods saveBean() and loadBean(): It stores an Java Bean to XML and reads it afterwards. The read
     * objects must be identical to the initial Java Bean.
     */
    @Test
    fun testSaveAndLoadBeanSuccess() {

        val testBean = TestBean()
        testBean.unitSystem = UnitSystem.ENGLISH
        testBean.message = "Bar"
        testBean.number = 123

        XmlBeanStorage.saveBean(testBean, BEAN_FILENAME)

        val loadedBean = XmlBeanStorage.loadBean(BEAN_FILENAME) as TestBean
        assertEquals(testBean.unitSystem, loadedBean.unitSystem)
        assertEquals(testBean.message, loadedBean.message)
        assertEquals(testBean.number, loadedBean.number)
    }

    /**
     * Tests the method loadBean(): Loading must fail with a FileNotFoundException because the file does not exists.
     */
    @Test
    fun testLoadBeanFailedFileNotFound() {
        assertThrows(FileNotFoundException::class.java) { XmlBeanStorage.loadBean(BEAN_FILENAME) }
    }

    /**
     * Tests the method saveBean(): Saving must fail with a IOException because the file can't be created (there's a
     * directory with the same name).
     */
    @Test
    fun testSaveBeanFailedCreateFile() {
        Files.createDirectory(Paths.get(BEAN_FILENAME))

        assertThrows(IOException::class.java) { XmlBeanStorage.saveBean(TestBean(), BEAN_FILENAME) }
    }

    /**
     * Simple Java Bean for testing.
     */
    class TestBean : Serializable {

        var unitSystem = UnitSystem.METRIC
        var message = "Foo"
        var number: Int = 42

        companion object {
            private const val serialVersionUID = 0L
        }
    }

    companion object {
        private val BEAN_FILENAME = "${XmlBeanStorageTest::class.java.name}.xml"
    }
}
