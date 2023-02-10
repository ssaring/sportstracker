package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Note
import de.saring.sportstracker.storage.db.RepositoryUtil.dateTimeToString
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.logging.Logger

/**
 * Database repository for the Note data.
 *
 * @property connection database connection
 *
 * @author Stefan Saring
 */
class NoteRepository(
    private val connection: Connection
) {

    @Throws(STException::class)
    fun readAllNotes(): List<Note> {
        LOGGER.info("Reading all Notes")
        val notes = ArrayList<Note>()

        try {
            connection.prepareStatement("SELECT * FROM NOTE").use { statement ->
                val rs: ResultSet = statement.executeQuery()
                while (rs.next()) {
                    notes.add(readNoteFromResultSet(rs))
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_NOTES, "Failed to read all Notes!", e)
        }
        return notes
    }

    @Throws(STException::class)
    fun readNote(noteId: Int): Note {
        LOGGER.info("Reading Note with ID '$noteId'")

        try {
            connection.prepareStatement("SELECT * FROM NOTE WHERE ID = ?").use { statement ->
                statement.setInt(1, noteId)
                val rs: ResultSet = statement.executeQuery()
                rs.next()
                return readNoteFromResultSet(rs)
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ENTRY, "Failed to read Note with ID '$noteId'!", e)
        }
    }

    @Throws(STException::class)
    fun createNote(note: Note): Note {
        LOGGER.info("Creating new Note")

        try {
            connection.prepareStatement("INSERT INTO NOTE (DATE_TIME, COMMENT) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            ).use { statement ->
                statement.setString(1, dateTimeToString(note.dateTime))
                statement.setString(2, note.comment)
                statement.executeUpdate()
                val rs: ResultSet = statement.generatedKeys
                rs.next()
                val noteId: Int = rs.getInt(1)
                return readNote(noteId)
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_CREATE_ENTRY, "Failed to update Note with ID '${note.id}'!", e)
        }
    }

    @Throws(STException::class)
    fun updateNote(note: Note) {
        LOGGER.info("Updating Note with ID '${note.id}'")

        try {
            connection.prepareStatement("UPDATE NOTE SET DATE_TIME = ?, COMMENT = ? WHERE ID = ?"
            ).use { statement ->
                statement.setString(1, dateTimeToString(note.dateTime))
                statement.setString(2, note.comment)
                statement.setInt(3, (note.id)!!)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_UPDATE_ENTRY, "Failed to update Note with ID '${note.id}'!", e)
        }
    }

    @Throws(STException::class)
    fun deleteNote(note: Note) {
        LOGGER.info("Deleting Note with ID '${note.id}'")

        try {
            connection.prepareStatement("DELETE FROM NOTE WHERE ID = ?").use { statement ->
                statement.setInt(1, (note.id)!!)
                statement.executeUpdate()
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_DELETE_ENTRY, "Failed to delete Note with ID '${note.id}'!", e)
        }
    }

    @Throws(SQLException::class)
    private fun readNoteFromResultSet(rs: ResultSet): Note {
        val note = Note(rs.getInt("ID"))
        note.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        note.comment = rs.getString("COMMENT")
        return note
    }

    companion object {
        private val LOGGER = Logger.getLogger(NoteRepository::class.java.name)
    }
}