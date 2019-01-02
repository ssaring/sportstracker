package de.saring.sportstracker.gui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import de.saring.sportstracker.storage.IStorage;
import de.saring.util.XmlBeanStorage;
import de.saring.util.data.IdObject;
import de.saring.util.data.IdObjectListChangeListener;

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

    private final STContext context;

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
     * The data storage instance of the application.
     */
    private final IStorage storage;

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
     * @param storage the data storage instance to be used
     */
    @Inject
    public STDocumentImpl(final STContext context, final IStorage storage) {
        this.context = context;
        this.storage = storage;

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
        try {
            // read application data from XML files
            sportTypeList = storage.readSportTypeList(dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST,
                    options.getPreferredSpeedMode());
            exerciseList = storage.readExerciseList(dataDirectory + "/" + FILENAME_EXERCISE_LIST, sportTypeList);
            noteList = storage.readNoteList(dataDirectory + "/" + FILENAME_NOTE_LIST);
            weightList = storage.readWeightList(dataDirectory + "/" + FILENAME_WEIGHT_LIST);
        } finally {
            // register this document as a listener for list content changes
            // (also when reading data has failed)
            registerListChangeListener(this);
            dirtyData = false;
        }
    }

    @Override
    public void storeApplicationData() throws STException {
        // store application data in XML files
        storage.storeSportTypeList(sportTypeList, dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST);
        storage.storeExerciseList(exerciseList, dataDirectory + "/" + FILENAME_EXERCISE_LIST);
        storage.storeNoteList(noteList, dataDirectory + "/" + FILENAME_NOTE_LIST);
        storage.storeWeightList(weightList, dataDirectory + "/" + FILENAME_WEIGHT_LIST);
        dirtyData = false;
    }

    @Override
    public List<Exercise> checkExerciseFiles() {
        return exerciseList.stream()
                .filter(exercise -> exercise.getHrmFile() != null && !new File(exercise.getHrmFile()).exists())
                .collect(Collectors.toList());
    }

    @Override
    public void listChanged(IdObject changedObject) {
        // one of the data lists has been changed => set dirty data flag
        dirtyData = true;
    }

    @Override
    public void registerListChangeListener(IdObjectListChangeListener listener) {
        sportTypeList.addListChangeListener(listener);
        exerciseList.addListChangeListener(listener);
        noteList.addListChangeListener(listener);
        weightList.addListChangeListener(listener);
    }
}
