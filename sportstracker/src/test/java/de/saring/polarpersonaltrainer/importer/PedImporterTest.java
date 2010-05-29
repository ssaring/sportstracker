package de.saring.polarpersonaltrainer.importer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class PedImporter.
 * 
 * @author Philippe Marzouk
 */
public class PedImporterTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {
        System.setOut(null);
    }

    /**
     * Test of main method, of class PedImporter.
     */
    @Test
    public void testRequiredArgs() {
        String[] args = null;
        PedImporter.main(args);
        assertTrue(outContent.toString().startsWith("Usage error:"));
    }

    /**
     * Test of main method, of class PedImporter.
     */
    @Test
    public void testWrongSportType() {
        String[] args = {"-n", "-f", "../st-exerciseviewer/misc/testdata/polarpersonaltrainer/polar-ped-sample.ped", "-d", "misc/polarpersonaltrainer-importer/testdata", "-sportType", "5"};
        PedImporter.main(args);
        assertTrue(outContent.toString().startsWith("sport-type id 5 not found"));
    }

    /**
     * Test of main method, of class PedImporter.
     */
    @Test
    public void testWrongSportSubType() {
        String[] args = {"-n", "-f", "../st-exerciseviewer/misc/testdata/polarpersonaltrainer/polar-ped-sample.ped", "-d", "misc/polarpersonaltrainer-importer/testdata", "-sportSubType", "6"};
        PedImporter.main(args);
        assertTrue(outContent.toString().startsWith("sport-subtype id 6 not found"));
    }
    
    /**
     * Test of main method, of class PedImporter.
     */
    @Test
    public void testAdded() {
        String[] args = {"-n", "-f", "../st-exerciseviewer/misc/testdata/polarpersonaltrainer/polar-ped-sample.ped", "-d", "misc/polarpersonaltrainer-importer/testdata"};
        PedImporter.main(args);
        assertTrue(outContent.toString().contains("added 1"));
    }
}
