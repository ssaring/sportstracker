package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import jakarta.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for reading / storing of the application data from / to a SQLite database. It manages the database connection
 * and provides repository instances for the specific entities.
 *
 * @author Stefan Saring
 */
@Singleton
public class DbStorage {

    private static final int SCHEMA_VERSION = 1;

    private Connection connection;

    private NoteRepository noteRepository;
    private WeightRepository weightRepository;
    private ExerciseRepository exerciseRepository;
    private SportTypeRepository sportTypeRepository;

    public void openDatabase(String dbFilename) throws STException {
        String jdbcUrl = "jdbc:sqlite:" + dbFilename;

        try {
            connection = DriverManager.getConnection(jdbcUrl);
            // AutoCommit is default for SQLite, use own TX management instead
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_OPEN_DATABASE,
                    "Failed to open SQLite database '" + jdbcUrl + "'!", e);
        }

        validateSchemaVersion();
        noteRepository = new NoteRepository(connection);
        weightRepository = new WeightRepository(connection);
        exerciseRepository = new ExerciseRepository(connection);
        sportTypeRepository = new SportTypeRepository(connection);
    }

    public void closeDatabase() throws STException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new STException(STExceptionID.DBSTORAGE_CLOSE_DATABASE,
                        "Failed to close opened SQLite database!", e);
            }
        }
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    public WeightRepository getWeightRepository() {
        return weightRepository;
    }

    public ExerciseRepository getExerciseRepository() {
        return exerciseRepository;
    }

    public SportTypeRepository getSportTypeRepository() {
        return sportTypeRepository;
    }

    private void validateSchemaVersion() throws STException {
        try(var statement = connection.prepareStatement("SELECT SCHEMA_VERSION FROM META")) {
            var rs = statement.executeQuery();
            rs.next();
            var schemaVersion = rs.getInt("SCHEMA_VERSION");
            if (schemaVersion != SCHEMA_VERSION) {
                throw new STException(STExceptionID.DBSTORAGE_INVALID_SCHEMA, "DB schema version is invalid! " +
                        "Expected version " + SCHEMA_VERSION + ", found version " + schemaVersion + ".");
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_INVALID_SCHEMA, "Failed to read DB schema version!", e);
        }
    }
}
