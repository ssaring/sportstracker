package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Equipment
import de.saring.sportstracker.data.Note
import de.saring.sportstracker.data.SportType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Unit tests of  the [NoteRepository] class. The tests are using this repository provided by the [DbStorage] class,
 * this handles also the database connection and schema setup. The create() method is tested automatically during the
 * test data setup.
 *
 * @author Stefan Saring
 */
class NoteRepositoryTest : DbStorageTestBase() {

    private lateinit var note1: Note
    private lateinit var note2: Note

    private lateinit var sportTypes: List<SportType>
    private lateinit var sportType1: SportType

    override fun setUpTestData() {
        // create a sport type first (needed for notes)
        creatSportType("Cycling")

        // reload of updated sportType must be done via readAll(), otherwise the sport subtypes and equipments are not loaded
        sportTypes = dbStorage.sportTypeRepository.readAll()
        sportType1 = sportTypes[0]

        createNote(sportType1,  sportType1.equipmentList.first(), "Note 1")
        createNote(null, null, "Note 2")

        // reload of created notes must be done via readAll(), otherwise the sport type and equipment are not loaded
        val notes = dbStorage.noteRepository.readAll(sportTypes)
        note1 = notes[0]
        note2 = notes[1]
    }

    /**
     * Test of readAll(): needs to provide all existing notes.
     */
    @Test
    fun testReadAll() {
        val notes = dbStorage.noteRepository.readAll(sportTypes)
        Assertions.assertEquals(2, notes.size)

        // note 1 must have the stored references to SportType and Equipment
        Assertions.assertEquals(sportType1.id, notes[0].sportType.id)
        Assertions.assertEquals(sportType1.equipmentList.first().id, notes[0].equipment.id)

        // note 2 must not have any references to SportType and Equipment
        Assertions.assertNull(notes[1].sportType)
        Assertions.assertNull(notes[1].equipment)
    }

    /**
     * Test of readById(): needs to provide a existing note with proper data.
     */
    @Test
    fun testReadById() {
        val note = dbStorage.noteRepository.readById(note1.id!!)

        Assertions.assertEquals(note1.id, note.id)
        Assertions.assertEquals(note1.dateTime, note.dateTime)
        // sportType and equipment are provided by readAll(List<SportType>) only
        Assertions.assertNull(note.sportType)
        Assertions.assertNull(note.equipment)
        Assertions.assertEquals(note1.comment, note.comment)
    }

    /**
     * Test of update(): needs to update an existing note, will be verified by reading the note.
     */
    @Test
    fun testUpdate() {
        note1.comment = "FooBar"
        dbStorage.noteRepository.update(note1)

        val note = dbStorage.noteRepository.readById(note1.id!!)
        Assertions.assertEquals("FooBar", note.comment)
    }

    /**
     * Test of delete(): needs to delete an existing note, will be verified by reading all notes.
     */
    @Test
    fun testDelete() {
        dbStorage.noteRepository.delete(note1.id!!)

        val notes = dbStorage.noteRepository.readAll(sportTypes)
        Assertions.assertEquals(1, notes.size)
        Assertions.assertEquals(note2.id, notes[0].id)
    }

    private fun createNote(sportType: SportType?, equipment: Equipment?, comment: String): Note {
        val note = Note(null)
        note.dateTime = LocalDateTime.now()
        note.sportType = sportType
        note.equipment = equipment
        note.comment = comment
        return dbStorage.noteRepository.create(note)
    }
}