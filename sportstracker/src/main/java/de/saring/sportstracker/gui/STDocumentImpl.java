package de.saring.sportstracker.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.LongStream;

import de.saring.sportstracker.core.ApplicationDataChangeListener;
import de.saring.sportstracker.storage.db.DbStorage;
import de.saring.sportstracker.storage.xml.XMLStorage;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.EntryFilter;
import de.saring.sportstracker.data.EntryList;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.NoteList;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.data.WeightList;
import de.saring.util.XmlBeanStorage;
import de.saring.util.data.IdObject;
import de.saring.util.unitcalc.SpeedMode;

/**
 * This class contains all document (MVC) related data and functionality of the
 * SportsTracker application.
 *
 * @author Stefan Saring
 */
@Singleton
public class STDocumentImpl implements STDocument {
    private static final Logger LOGGER = Logger.getLogger(STDocumentImpl.class.getName());

    private static final String FILENAME_SPORT_TYPE_LIST = "sport-types.xml";
    private static final String FILENAME_EXERCISE_LIST = "exercises.xml";
    private static final String FILENAME_NOTE_LIST = "notes.xml";
    private static final String FILENAME_WEIGHT_LIST = "weights.xml";
    private static final String FILENAME_OPTIONS = "st-options.xml";
    private static final String FILENAME_ST_DATABASE = "sportstracker.sqlite";

    private final STContext context;

    private final DbStorage dbStorage;

    /**
     * The sport type list of the user.
     */
    private SportTypeList sportTypeList;

    /**
     * This list contains all the exercises of the user. For displaying the
     * list in GUI the method GetFilterableExerciseList() should be used instead.
     */
    private ExerciseList exerciseList;

    /**
     * The note entry list of the user.
     */
    private NoteList noteList;

    /**
     * The body weight entry list of the user.
     */
    private WeightList weightList;

    /**
     * List of listeners which will be notified on each application data change.
     */
    private List<ApplicationDataChangeListener> changeListeners = new ArrayList<>();

    /**
     * The directory where the application data of the user is stored.
     */
    private String dataDirectory;

    /** This flag is true when data has been modified but not saved yet. */
    private boolean dirtyData;

    /**
     * The application settings.
     */
    private STOptions options;

    /**
     * This flag is true when the view displays only exercises for the current filter.
     */
    private boolean filterEnabled;
    /**
     * The current exercise filter in the view.
     */
    private EntryFilter currentFilter;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     */
    @Inject
    public STDocumentImpl(final STContext context, final DbStorage dbStorage) {
        this.context = context;
        this.dbStorage = dbStorage;

        // create name of directory where the data is stored
        dataDirectory = System.getProperty("user.home") + "/.sportstracker";

        // start with empty lists
        sportTypeList = new SportTypeList();
        exerciseList = new ExerciseList();
        noteList = new NoteList();
        weightList = new WeightList();
        dirtyData = false;

        // create default filter for current month, but it is disabled
        filterEnabled = false;
        currentFilter = EntryFilter.createDefaultExerciseFilter();
    }

    @Override
    public SportTypeList getSportTypeList() {
        return sportTypeList;
    }

    @Override
    public ExerciseList getExerciseList() {
        return exerciseList;
    }

    @Override
    public NoteList getNoteList() {
        return noteList;
    }

    @Override
    public WeightList getWeightList() {
        return weightList;
    }

    @Override
    public DbStorage getStorage() {
        return dbStorage;
    }

    @Override
    public STOptions getOptions() {
        return options;
    }

    @Override
    public boolean isDirtyData() {
        return dirtyData;
    }

    @Override
    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    @Override
    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    @Override
    public EntryFilter getCurrentFilter() {
        return currentFilter;
    }

    @Override
    public void setCurrentFilter(EntryFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    @Override
    public String getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public void evaluateCommandLineParameters(final List<String> parameters) {

        // check for a custom data directory (optional)
        for (String parameter : parameters) {
            if (parameter.startsWith(PARAMETER_DATA_DIR)) {
                final String dataDir = parameter.substring(PARAMETER_DATA_DIR.length()).trim();
                if (dataDir.length() > 0) {
                    dataDirectory = dataDir;
                }
            }
        }
    }

    @Override
    public void loadOptions() {
        final String optionsPath = dataDirectory + File.separator + FILENAME_OPTIONS;
        if (Files.exists(Paths.get(optionsPath))) {

            LOGGER.info("Loading application options...");
            try {
                options = (STOptions) XmlBeanStorage.loadBean(optionsPath);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to load application options from '" + optionsPath
                        + "', using default values ...", e);
            }
        }

        // use default options at first start or on load errors
        if (options == null) {
            LOGGER.log(Level.WARNING, "Using default application options...");
            options = new STOptions();
        }
    }

    @Override
    public void storeOptions() {
        LOGGER.info("Storing application options...");
        final String optionsPath = dataDirectory + File.separator + FILENAME_OPTIONS;

        try {
            XmlBeanStorage.saveBean(options, optionsPath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to store application options to '" + optionsPath + "' ...", e);
        }
    }

    @Override
    public void createApplicationDirectory() throws STException {
        File fDataDirectory = new File(dataDirectory);
        if (!fDataDirectory.exists() || !fDataDirectory.isDirectory()) {
            if (!fDataDirectory.mkdir()) {
                throw new STException(STExceptionID.DOCUMENT_CREATE_APP_DIRECTORY,
                        "Failed to create application data directory '" + dataDirectory + "' ...");
            }
        }
    }

    @Override
    public EntryList<Exercise> getFilterableExerciseList() {

        if ((filterEnabled) && (currentFilter != null)) {
            // use current filter to get list
            return exerciseList.getEntriesForFilter(currentFilter);
        } else {
            // no filter: return list of all exercises
            return exerciseList;
        }
    }

    @Override
    public EntryList<Note> getFilterableNoteList() {

        if ((filterEnabled) && (currentFilter != null)) {
            // use current filter to get list
            return noteList.getEntriesForFilter(currentFilter);
        } else {
            // no filter: return list of all notes
            return noteList;
        }
    }

    @Override
    public EntryList<Weight> getFilterableWeightList() {

        if ((filterEnabled) && (currentFilter != null)) {
            // use current filter to get list
            return weightList.getEntriesForFilter(currentFilter);
        } else {
            // no filter: return list of all weights
            return weightList;
        }
    }

    @Override
    public void readApplicationData() throws STException {
        LOGGER.info("Reading application data");

        sportTypeList = new SportTypeList();
        exerciseList = new ExerciseList();
        noteList = new NoteList();
        weightList = new WeightList();

        var msStart = System.currentTimeMillis();
        dbStorage.openDatabase(dataDirectory + "/" + FILENAME_ST_DATABASE);
        var msOpened = System.currentTimeMillis();
        LOGGER.info("Opened SQLite database in " + (msOpened - msStart) + " msec");

        // read application data from SQLite database
        readListsFromStorage();
        var msDataRead = System.currentTimeMillis();
        LOGGER.info("Loaded all data in " + (msDataRead - msOpened) + " msec");
        dirtyData = false;
    }

    private void readListsFromStorage() throws STException {
        var dbSportTypes = dbStorage.getSportTypeRepository().readAll();
        sportTypeList.clearAndAddAll(dbSportTypes);

        var dbExercises = dbStorage.getExerciseRepository().readAll(dbSportTypes);
        exerciseList.clearAndAddAll(dbExercises);

        var dbNotes = dbStorage.getNoteRepository().readAll(dbSportTypes);
        noteList.clearAndAddAll(dbNotes);

        var dbWeights = dbStorage.getWeightRepository().readAll();
        weightList.clearAndAddAll(dbWeights);
    }

    @Override
    public void storeApplicationData() throws STException {
        LOGGER.info("Storing application data");
        dbStorage.commitChanges();
        dirtyData = false;
    }

    @Override
    public boolean importApplicationDataFromXml() throws STException {
        LOGGER.info("Importing application data from XML");

        if (!isApplicationDataInXmlAvailable()) {
            LOGGER.info("No application data from XML available for import");
            return false;
        }

        try {
            // read application data from XML files
            var xmlStorage = new XMLStorage();
            var importedSportTypes = xmlStorage.readSportTypeList(dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST,
                    options.getPreferredSpeedMode());
            var importedExercises = xmlStorage.readExerciseList(dataDirectory + "/" + FILENAME_EXERCISE_LIST,
                    importedSportTypes);
            var importedNotes = xmlStorage.readNoteList(dataDirectory + "/" + FILENAME_NOTE_LIST);
            var importedWeights = xmlStorage.readWeightList(dataDirectory + "/" + FILENAME_WEIGHT_LIST);

            // import data to database and persist
            dbStorage.importExistingApplicationData(
                    importedSportTypes, importedExercises, importedNotes, importedWeights);
            dbStorage.commitChanges();

            readListsFromStorage();
            dirtyData = false;
            return true;
        } catch (STException e) {
            throw new STException(STExceptionID.DBSTORAGE_COMMIT_CHANGES, "Failed to import application data from XML!'", e);
        }
    }

    private boolean isApplicationDataInXmlAvailable() {
        return Files.exists(Paths.get(dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST)) &&
                Files.exists(Paths.get(dataDirectory + "/" + FILENAME_EXERCISE_LIST)) &&
                Files.exists(Paths.get(dataDirectory + "/" + FILENAME_NOTE_LIST)) &&
                Files.exists(Paths.get(dataDirectory + "/" + FILENAME_WEIGHT_LIST));
    }

    @Override
    public void updateApplicationData(IdObject changedObject) throws STException {
        LOGGER.info("Updating application data");
        dirtyData = true;
        readListsFromStorage();

        // notify all listeners of application data changes
        changeListeners.forEach(listener -> listener.applicationDataChanged(changedObject));
    }

    @Override
    public List<Exercise> checkExerciseFiles() {
        return exerciseList.stream()
                .filter(exercise -> exercise.getHrmFile() != null && !new File(exercise.getHrmFile()).exists())
                .toList();
    }

    @Override
    public void registerChangeListener(ApplicationDataChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public SpeedMode getSpeedModeForExercises(final long[] exerciseIds) {

        if (exerciseIds == null || exerciseIds.length == 0) {
            throw new IllegalArgumentException("Empty exerciseIds!");
        }

        final List<SpeedMode> speedModes = LongStream.of(exerciseIds)
                .mapToObj(exerciseId -> getExerciseList().getByID(exerciseId))
                .map(exercise -> exercise.getSportType().getSpeedMode())
                .distinct()
                .toList();

        return speedModes.size() == 1 ? speedModes.get(0) : getOptions().getPreferredSpeedMode();
    }
}
