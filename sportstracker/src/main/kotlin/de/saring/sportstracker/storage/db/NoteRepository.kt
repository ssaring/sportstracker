package de.saring.sportstracker.storage.db

import de.saring.sportstracker.data.Note
import de.saring.sportstracker.storage.db.RepositoryUtil.dateTimeToString
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
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
    connection: Connection
) : AbstractRepository<Note>(connection) {

    override val entityName = "Note"

    override val tableName = "NOTE"

    override val logger: Logger = Logger.getLogger(NoteRepository::class.java.name)

    override fun readFromResultSet(rs: ResultSet): Note {
        val note = Note(rs.getInt("ID"))
        note.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        note.comment = rs.getString("COMMENT")
        return note
    }

    override fun executeCreate(note: Note): Note {
        connection.prepareStatement("INSERT INTO NOTE (DATE_TIME, COMMENT) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(note.dateTime))
            statement.setString(2, note.comment)
            statement.executeUpdate()

            val rs = statement.generatedKeys
            rs.next()
            val noteId = rs.getInt(1)
            return readById(noteId)
        }
    }

    override fun executeUpdate(note: Note) {
        connection.prepareStatement("UPDATE NOTE SET DATE_TIME = ?, COMMENT = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, dateTimeToString(note.dateTime))
            statement.setString(2, note.comment)
            statement.setInt(3, note.id!!)
            statement.executeUpdate()
        }
    }
}