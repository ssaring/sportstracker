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
        val note = Note(rs.getLong("ID"))
        note.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        note.comment = rs.getString("COMMENT")
        return note
    }

    override fun executeCreate(entry: Note): Note {
        connection.prepareStatement("INSERT INTO NOTE (DATE_TIME, COMMENT) VALUES (?, ?) RETURNING ID",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setString(2, entry.comment)
            statement.execute()

            val noteId = statement.resultSet.getLong(1)
            return readById(noteId)
        }
    }

    override fun executeUpdate(entry: Note) {
        connection.prepareStatement("UPDATE NOTE SET DATE_TIME = ?, COMMENT = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, dateTimeToString(entry.dateTime))
            statement.setString(2, entry.comment)
            statement.setLong(3, entry.id!!)
            statement.executeUpdate()
        }
    }
}