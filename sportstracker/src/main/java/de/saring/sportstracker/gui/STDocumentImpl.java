package de.saring.sportstracker.gui;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.*;
import de.saring.sportstracker.storage.IStorage;
import de.saring.util.data.IdDateObjectList;
import de.saring.util.data.IdObject;
import de.saring.util.data.IdObjectListChangeListener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class contains all document (MVC) related data and functionality of the
 * SportsTracker application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class STDocumentImpl implements STDocument {
    private static final Logger LOGGER = Logger.getLogger(STDocumentImpl.class.getName());

    private static final String FILENAME_SPORT_TYPE_LIST = "sport-types.xml";
    private static final String FILENAME_EXERCISE_LIST = "exercises.xml";
    private static final String FILENAME_NOTE_LIST = "notes.xml";
    private static final String FILENAME_WEIGHT_LIST = "weights.xml";
    private static final String FILENAME_OPTIONS = "st-options.xml";

    private STContext context;

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
    private IStorage storage;

    /**
     * The directory where the application data of the user is stored.
     */
    private String dataDirectory;

    /**
     * This flag is true when data has been modified but not saved yet.
     */
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
    private ExerciseFilter currentFilter;


    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param storage the data storage instance to be used
     */
    @Inject
    public STDocumentImpl(STContext context, IStorage storage) {
        this.context = context;
        this.storage = storage;

        // init enumerations (they need ResourceReader for string creation)
        Exercise.IntensityType.setResReader(context.getResReader());

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
        currentFilter = ExerciseFilter.createDefaultExerciseFilter();
    }

    public SportTypeList getSportTypeList() {
        return sportTypeList;
    }

    public ExerciseList getExerciseList() {
        return exerciseList;
    }

    public NoteList getNoteList() {
        return noteList;
    }

    public WeightList getWeightList() {
        return weightList;
    }

    public STOptions getOptions() {
        return options;
    }

    public boolean isDirtyData() {
        return dirtyData;
    }

    public boolean isFilterEnabled() {
        return filterEnabled;
    }

    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    public ExerciseFilter getCurrentFilter() {
        return currentFilter;
    }

    public void setCurrentFilter(ExerciseFilter currentFilter) {
        this.currentFilter = currentFilter;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    @Override
    public void evaluateCommandLineParameters(String[] parameters) {
        if (parameters != null) {
            for (String parameter : parameters) {

                // check for a custom data directory (optional)
                if (parameter.startsWith(PARAMETER_DATA_DIR)) {
                    String dataDir = parameter.substring(PARAMETER_DATA_DIR.length()).trim();
                    if (dataDir != null && dataDir.length() > 0) {
                        dataDirectory = dataDir;
                    }
                }
            }
        }
    }

    @Override
    public void loadOptions() {
        try {
            options = (STOptions) context.getSAFContext().getLocalStorage().load(FILENAME_OPTIONS);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to load application options from '" + FILENAME_OPTIONS + "', using default values ...", e);
        }

        // use default options at first start or on load errors
        if (options == null) {
            options = STOptions.createDefaultInstance();
        }
    }

    @Override
    public void storeOptions() {
        try {
            context.getSAFContext().getLocalStorage().save(options, FILENAME_OPTIONS);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Failed to write application options to '" +
                    FILENAME_OPTIONS + "' ...", ioe);
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
    public IdDateObjectList<Exercise> getFilterableExerciseList() {

        if ((filterEnabled) && (currentFilter != null)) {
            // use current filter to get list
            return exerciseList.getExercisesForFilter(currentFilter);
        } else {
            // no filter: return list of all exercises
            return exerciseList;
        }
    }

    @Override
    public void readApplicationData() throws STException {
        try {
            // read application data from XML files
            sportTypeList = storage.readSportTypeList(dataDirectory + "/" + FILENAME_SPORT_TYPE_LIST);
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
