package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Note;
import de.saring.util.Date310Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Database repository for the Note data.
 *
 * @author Stefan Saring
 */
public class NoteRepository {

    private static final Logger LOGGER = Logger.getLogger(NoteRepository.class.getName());

    private final Connection connection;

    public NoteRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Note> readAllNotes() throws STException {
        LOGGER.info("Reading all Notes");
        var notes = new ArrayList<Note>();

        try (var statement = connection.prepareStatement("SELECT * FROM NOTE")) {
            var rs = statement.executeQuery();
            while (rs.next()) {
                notes.add(readNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_NOTES, "Failed to read all Notes!", e);
        }
        return notes;
    }

    public Note readNote(int noteId) throws STException {
        LOGGER.info("Reading Note with ID '" + noteId + "'");

        try (var statement = connection.prepareStatement("SELECT * FROM NOTE WHERE ID = ?")) {
            statement.setInt(1, noteId);
            var rs = statement.executeQuery();
            rs.next();
            return readNoteFromResultSet(rs);
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_ENTRY, "Failed to read Note with ID '" + noteId + "'!", e);
        }
    }

    public Note createNote(Note note) throws STException {
        LOGGER.info("Creating new Note");

        try (var statement = connection.prepareStatement("INSERT INTO NOTE " +
                "(DATE_TIME, COMMENT) " +
                "VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, RepositoryUtil.dateTimeToString(note.getDateTime()));
            statement.setString(2, note.getComment());
            statement.executeUpdate();

            var rs = statement.getGeneratedKeys();
            rs.next();
            var noteId = rs.getInt(1);
            var persistedNote = readNote(noteId);
            return persistedNote;
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_CREATE_ENTRY,
                    "Failed to update Note with ID '" + note.getId() + "'!", e);
        }
    }

    public void updateNote(Note note) throws STException {
        LOGGER.info("Updating Note with ID '" + note.getId() + "'");

        try (var statement = connection.prepareStatement("UPDATE NOTE SET " +
                "DATE_TIME = ?, " +
                "COMMENT = ? " +
                "WHERE ID = ?")) {
            statement.setString(1, RepositoryUtil.dateTimeToString(note.getDateTime()));
            statement.setString(2, note.getComment());
            statement.setInt(3, note.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_UPDATE_ENTRY,
                    "Failed to update Note with ID '" + note.getId() + "'!", e);
        }
    }

    public void deleteNote(Note note) throws STException {
        LOGGER.info("Deleting Note with ID '" + note.getId() + "'");

        try (var statement = connection.prepareStatement("DELETE FROM NOTE WHERE ID = ?")) {
            statement.setInt(1, note.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_DELETE_ENTRY,
                    "Failed to delete Note with ID '" + note.getId() + "'!", e);
        }
    }

    private Note readNoteFromResultSet(ResultSet rs) throws SQLException {
        var note = new Note(rs.getInt("ID"));
        note.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
        note.setComment(rs.getString("COMMENT"));
        return note;
    }
}
