package de.saring.sportstracker.storage.db

import de.saring.sportstracker.core.STException
import de.saring.sportstracker.core.STExceptionID
import de.saring.sportstracker.data.Note
import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.storage.db.RepositoryUtil.dateTimeToString
import de.saring.sportstracker.storage.db.RepositoryUtil.getEquipmentById
import de.saring.sportstracker.storage.db.RepositoryUtil.getLongOrNull
import de.saring.sportstracker.storage.db.RepositoryUtil.getSportTypeById
import de.saring.util.Date310Utils.dateToLocalDateTime
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
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

    @Throws(STException::class)
    fun readAll(sportTypes: List<SportType>): List<Note> {
        logger.info("Reading all Notes")
        val notes = ArrayList<Note>()

        try {
            connection.prepareStatement("SELECT * FROM NOTE").use { statement ->
                val rs = statement.executeQuery()
                while (rs.next()) {
                    val note = readFromResultSet(rs)

                    val sportTypeId = getLongOrNull(rs, "SPORT_TYPE_ID")
                    if (sportTypeId != null) {
                        val sportType = getSportTypeById(sportTypes, sportTypeId)
                        note.sportType = sportType

                        val equipmentID = getLongOrNull(rs, "EQUIPMENT_ID")
                        note.equipment = if (equipmentID == null) null else getEquipmentById(sportType, equipmentID)
                    }
                    notes.add(note)
                }
            }
        } catch (e: SQLException) {
            throw STException(STExceptionID.DBSTORAGE_READ_ALL, "Failed to read all Notes!", e)
        }
        return notes
    }

    override fun readAll(): List<Note> {
        throw UnsupportedOperationException("Use readAll(List<SportType>) for reading all Notes!")
    }

    override fun readFromResultSet(rs: ResultSet): Note {
        val note = Note(rs.getLong("ID"))
        note.dateTime = dateToLocalDateTime(rs.getDate("DATE_TIME"))
        note.comment = rs.getString("COMMENT")
        return note
    }

    override fun executeCreate(entry: Note): Note {
        connection.prepareStatement("INSERT INTO NOTE (DATE_TIME, SPORT_TYPE_ID, EQUIPMENT_ID, COMMENT) VALUES (?, ?, ?, ?) RETURNING ID",
            Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, RepositoryUtil.dateTimeToString(entry.dateTime))
            statement.setObject(2, entry.sportType?.id, Types.INTEGER);
            statement.setObject(3, entry.equipment?.id, Types.INTEGER);
            statement.setString(4, entry.comment)
            statement.execute()

            val noteId = statement.resultSet.getLong(1)
            return readById(noteId)
        }
    }

    override fun executeUpdate(entry: Note) {
        connection.prepareStatement("UPDATE NOTE SET DATE_TIME = ?, SPORT_TYPE_ID = ?, EQUIPMENT_ID = ?, COMMENT = ? WHERE ID = ?"
        ).use { statement ->
            statement.setString(1, dateTimeToString(entry.dateTime))
            statement.setObject(2, entry.sportType?.id, Types.INTEGER);
            statement.setObject(3, entry.equipment?.id, Types.INTEGER);
            statement.setString(4, entry.comment)
            statement.setLong(5, entry.id!!)
            statement.executeUpdate()
        }
    }
}