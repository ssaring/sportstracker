package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Note
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Basic unit tests of  the [DbStorage] class (open and close database, basic access, commit).
 * More detailed tests are done implicitly in the Repository tests.
 *
 * @author Stefan Saring
 */
class DbStorageTest : DbStorageTestBase() {

    /**
     * Test database schema version, which also ensures that all schema update files have been executed properly.
     */
    @Test
    fun testGetSchemaVersion() {
        Assertions.assertEquals(DbStorage.SCHEMA_VERSION, dbStorage.getSchemaVersion())
    }

    /**
     * Basic storage tests for creating a new Note entity, commit the change and read from database.
     */
    @Test
    fun testStorage() {

        val note = Note(null);
        note.dateTime = LocalDateTime.now()
        note.comment = "FooBar"

        dbStorage.noteRepository.create(note)
        dbStorage.commitChanges()

        val notes = dbStorage.noteRepository.readAll()
        Assertions.assertEquals(1, notes.size)
        Assertions.assertEquals("FooBar", notes[0].comment)
    }
}