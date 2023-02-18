package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.*
import javafx.scene.paint.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Basic unit tests of the data application import feature of the DbStorage and DbApplicationDataImporter class.
 *
 * @author Stefan Saring
 */
class DbStorageImportApplicationDataTest {

    private val dbStorage = DbStorage()
    private val sportTypes = SportTypeList()
    private val exercises = ExerciseList()
    private val notes = NoteList()
    private val weights = WeightList()

    @BeforeEach
    fun setUp() {
        dbStorage.openDatabase(DbStorage.IN_MEMORY_FILENAME)
        createTestData()
    }

    /**
     * Test of method importExistingApplicationData(): All specified application data must be imported successfully to
     * the SQLite database storage. The test verifies the import by checking the database content afterwards.
     */
    @Test
    fun testImportApplicationData() {
        dbStorage.importExistingApplicationData(sportTypes, exercises, notes, weights)

        val sportTypes = dbStorage.sportTypeRepository.readAll()
        assertEquals(1, sportTypes.size)
        assertEquals("Cycling", sportTypes[0].getName())
        assertEquals(Color.BLUE, sportTypes[0].color)

        assertEquals(1, sportTypes[0].sportSubTypeList.size())
        assertEquals(1, sportTypes[0].sportSubTypeList.getAt(0).id)
        assertEquals("MTB", sportTypes[0].sportSubTypeList.getAt(0).getName())

        assertEquals(1, sportTypes[0].equipmentList.size())
        assertEquals(1, sportTypes[0].equipmentList.getAt(0).id)
        assertEquals("Bike 1", sportTypes[0].equipmentList.getAt(0).getName())

        val exercises = dbStorage.exerciseRepository.readAll(sportTypes)
        assertEquals(1, exercises.size)
        assertEquals(1, exercises[0].sportType.id)
        assertEquals(1, exercises[0].sportSubType.id)
        assertEquals(42.0, exercises[0].distance)
        assertEquals(20.0, exercises[0].avgSpeed)

        val notes = dbStorage.noteRepository.readAll()
        assertEquals(1, notes.size)
        assertEquals("Some comment...", notes[0].comment)

        val weights = dbStorage.weightRepository.readAll()
        assertEquals(1, weights.size)
        assertEquals(123.4, weights[0].value)
        assertEquals("Some other comment...", weights[0].comment)
    }

    private fun createTestData() {
        val sportSubType = SportSubType(1)
        sportSubType.setName("MTB")
        sportSubType.fitId = 8

        val equipment = Equipment(1)
        equipment.setName("Bike 1")

        val sportType = SportType(1)
        sportType.setName("Cycling")
        sportType.color = Color.BLUE
        sportType.fitId = 2
        sportType.sportSubTypeList.set(sportSubType)
        sportType.equipmentList.set(equipment)

        val exercise = Exercise(1)
        exercise.dateTime = LocalDateTime.now()
        exercise.sportType = sportType
        exercise.sportSubType = sportSubType
        exercise.intensity = Exercise.IntensityType.HIGH
        exercise.distance = 42.0
        exercise.avgSpeed = 20.0
        exercise.duration = 7600
        exercise.ascent = 321
        exercise.descent = 333
        exercise.hrmFile = "FooBar.fit"
        exercise.comment = "Foo Bar"

        val note = Note(1)
        note.dateTime = LocalDateTime.now()
        note.comment = "Some comment..."

        val weight = Weight(1)
        weight.value = 123.4
        weight.dateTime = LocalDateTime.now()
        weight.comment = "Some other comment..."

        sportTypes.set(sportType)
        exercises.set(exercise)
        notes.set(note)
        weights.set(weight)
    }
}
