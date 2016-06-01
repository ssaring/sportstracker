package de.saring.sportstracker.storage;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import javafx.scene.paint.Color;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.STDocumentImpl;

/**
 * Basic unit tests of the SQLiteExporter class.
 *
 * @author Stefan Saring
 */
public class SQLiteExporterTest {

    private STDocument document;
    private SQLiteExporter exporter;

    @Before
    public void setUp() throws IOException {

        STContext contextMock = mock(STContext.class);
        document = new STDocumentImpl(contextMock, null);
        fillDocumentWithTestData();

        exporter = new SQLiteExporter(document);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(exporter.getDatabasePath());
    }

    /**
     * Test of method exportToSqlite(): The document filled with some test data must be exported successfully to a
     * SQLite database. The test verifies the creation of the database without errors, the content will not be checked.
     *
     * @throws STException
     */
    @Test
    public void testExportToSqlite() throws STException {

        exporter.exportToSqlite();

        assertTrue(Files.exists(exporter.getDatabasePath()));
    }

    private void fillDocumentWithTestData() {

        SportSubType sportSubType = new SportSubType(1);
        sportSubType.setName("MTB");

        Equipment equipment = new Equipment(1);
        equipment.setName("Bike 1");

        SportType sportType = new SportType(1);
        sportType.setName("Cycling");
        sportType.setColor(Color.BLUE);
        sportType.getSportSubTypeList().set(sportSubType);
        sportType.getEquipmentList().set(equipment);

        Exercise exercise = new Exercise(1);
        exercise.setDateTime(LocalDateTime.now());
        exercise.setSportType(sportType);
        exercise.setSportSubType(sportSubType);
        exercise.setIntensity(Exercise.IntensityType.HIGH);
        exercise.setDistance(42);
        exercise.setAvgSpeed(20);
        exercise.setDuration(7600);
        exercise.setAscent(321);
        exercise.setHrmFile("FooBar.fit");
        exercise.setComment("Foo Bar");

        Note note = new Note(1);
        note.setDateTime(LocalDateTime.now());
        note.setText("Some comment...");

        Weight weight = new Weight(1);
        weight.setValue(123.4f);
        weight.setDateTime(LocalDateTime.now());
        weight.setComment("Some other comment...");

        document.getSportTypeList().set(sportType);
        document.getExerciseList().set(exercise);
        document.getNoteList().set(note);
        document.getWeightList().set(weight);
    }
}
