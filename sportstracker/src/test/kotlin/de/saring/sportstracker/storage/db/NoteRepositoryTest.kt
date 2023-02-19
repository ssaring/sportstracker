package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Note
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Unit tests of  the [NoteRepository] class. The tests are using this repository provided by the [DbStorage] class,
 * this handles also the database connection and schema setup. The create() method is tested automatically during the
 * test data setup.
 *
 * @author Stefan Saring
 */
class NoteRepositoryTest {

    private val dbStorage = DbStorage()

    private lateinit var note1: Note
    private lateinit var note2: Note

    @BeforeEach
    fun setUp() {
        dbStorage.openDatabase(DbStorage.IN_MEMORY_FILENAME)

        note1 = createNote("Note 1")
        note2 = createNote("Note 2")
    }

    @AfterEach
    fun tearDown() {
        dbStorage.closeDatabase()
    }

    /**
     * Test of readAll(): needs to provide all existing notes.
     */
    @Test
    fun testReadAll() {
        val notes = dbStorage.noteRepository.readAll()
        Assertions.assertEquals(2, notes.size)
    }

    /**
     * Test of readById(): needs to provide a existing note with proper data.
     */
    @Test
    fun testReadById() {
        val note = dbStorage.noteRepository.readById(note1.id!!)

        Assertions.assertEquals(note1.id, note.id)
        Assertions.assertEquals(note1.dateTime, note.dateTime)
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

        val notes = dbStorage.noteRepository.readAll()
        Assertions.assertEquals(1, notes.size)
        Assertions.assertEquals(note2.id, notes[0].id)
    }

    private fun createNote(comment: String): Note {
        val note = Note(null)
        note.dateTime = LocalDateTime.now()
        note.comment = comment
        return dbStorage.noteRepository.create(note)
    }
}