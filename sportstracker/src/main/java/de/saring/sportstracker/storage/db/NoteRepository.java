package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Note;
import de.saring.util.Date310Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database repository for the Note data.
 *
 * @author Stefan Saring
 */
public class NoteRepository {

    private final Connection connection;

    public NoteRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Note> readAllNotes() throws STException {
        var notes = new ArrayList<Note>();

        try(var statement = connection.prepareStatement("SELECT * FROM NOTE")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var note = new Note(rs.getInt("ID"));
                note.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                note.setComment(rs.getString("COMMENT"));
                notes.add(note);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_NOTES, "Failed to read all Notes!", e);
        }
        return notes;
    }
}
