package de.saring.sportstracker.gui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.dialogs.AboutDialogController;
import de.saring.sportstracker.gui.dialogs.ExerciseDialogController;
import de.saring.sportstracker.gui.dialogs.FilterDialogController;
import de.saring.sportstracker.gui.dialogs.HRMFileOpenDialog;
import de.saring.sportstracker.gui.dialogs.NoteDialogController;
import de.saring.sportstracker.gui.dialogs.OverviewDialogController;
import de.saring.sportstracker.gui.dialogs.PreferencesDialogController;
import de.saring.sportstracker.gui.dialogs.SportTypeListDialogController;
import de.saring.sportstracker.gui.dialogs.StatisticDialogController;
import de.saring.sportstracker.gui.dialogs.WeightDialogController;
import de.saring.sportstracker.gui.statusbar.StatusBarController;
import de.saring.sportstracker.gui.views.EntryViewController;
import de.saring.sportstracker.gui.views.calendarview.CalendarViewController;
import de.saring.sportstracker.gui.views.listviews.ExerciseListViewController;
import de.saring.sportstracker.gui.views.listviews.NoteListViewController;
import de.saring.sportstracker.gui.views.listviews.WeightListViewController;
import de.saring.util.Date310Utils;
import de.saring.util.StringUtils;
import de.saring.util.SystemUtils;
import de.saring.util.data.IdDateObject;
import de.saring.util.data.IdDateObjectList;
import de.saring.util.gui.javafx.FxmlLoader;
import de.saring.util.gui.mac.PlatformUtils;
import de.saring.util.unitcalc.FormatUtils;

/**
 * This class provides all controller (MVC)functionality of the SportsTracker main application window.
 *
 * @author Stefan Saring
 */
@Singleton
public class STControllerImpl implements STController {

    private static final Logger LOGGER = Logger.getLogger(STControllerImpl.class.getName());

    private final STContext context;
    private final STDocument document;

    @Inject
    private CalendarViewController calendarViewController;
    @Inject
    private ExerciseListViewController exerciseListViewController;
    @Inject
    private NoteListViewController noteListViewController;
    @Inject
    private WeightListViewController weightListViewController;

    /** The controller of the currently displayed view. */
    private EntryViewController currentViewController;

    @Inject
    private StatusBarController statusBarController;

    @Inject
    private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
    @Inject
    private Provider<EVMain> prExerciseViewer;
    @Inject
    private Provider<ExerciseDialogController> prExerciseDialogController;
    @Inject
    private Provider<NoteDialogController> prNoteDialogController;
    @Inject
    private Provider<WeightDialogController> prWeightDialogController;
    @Inject
    private Provider<SportTypeListDialogController> prSportTypeListDialogController;
    @Inject
    private Provider<StatisticDialogController> prStatisticDialogController;
    @Inject
    private Provider<OverviewDialogController> prOverviewDialogController;
    @Inject
    private Provider<PreferencesDialogController> prPreferencesDialogController;
    @Inject
    private Provider<FilterDialogController> prFilterDialogController;
    @Inject
    private Provider<AboutDialogController> prAboutDialogController;

    // list of all menu items
    @FXML
    private MenuItem miSave;
    @FXML
    private MenuItem miQuit;
    @FXML
    private MenuItem miEditEntry;
    @FXML
    private MenuItem miCopyEntry;
    @FXML
    private MenuItem miDeleteEntry;
    @FXML
    private MenuItem miViewHrm;
    @FXML
    private MenuItem miCalendarView;
    @FXML
    private MenuItem miExerciseListView;
    @FXML
    private MenuItem miNoteListView;
    @FXML
    private MenuItem miWeightListView;
    @FXML
    private MenuItem miFilterDisable;

    // list of all toolbar buttons
    @FXML
    private Button btSave;
    @FXML
    private Button btEditEntry;
    @FXML
    private Button btCopyEntry;
    @FXML
    private Button btDeleteEntry;
    @FXML
    private Button btViewHrm;
    @FXML
    private Button btCalendarView;
    @FXML
    private Button btExerciseListView;
    @FXML
    private Button btNoteListView;
    @FXML
    private Button btWeightListView;
    @FXML
    private Button btFilterDisable;

    @FXML
    private StackPane spViews;

    @FXML
    private Label laStatusBar;

    /** Property for the disabled status of the 'Save' action. */
    private final BooleanProperty actionSaveDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Edit / Copy Entry' action. */
    private final BooleanProperty actionEditEntryDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Delete Entry' action. */
    private final BooleanProperty actionDeleteEntryDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'View HRM File' action. */
    private final BooleanProperty actionViewHrmDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Calendar View' action. */
    private final BooleanProperty actionCalendarViewDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Exercise List View' action. */
    private final BooleanProperty actionExerciseListViewDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Note List View' action. */
    private final BooleanProperty actionNoteListViewDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Weight List View' action. */
    private final BooleanProperty actionWeightListViewDisabled = new SimpleBooleanProperty(true);

    /** Property for the disabled status of the 'Disable Exercise Filter' action. */
    private final BooleanProperty actionFilterDisableDisabled = new SimpleBooleanProperty(true);

    /** The date to be set initially when the next entry will be added. */
    private LocalDate dateForNewEntries;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param document the document component
     */
    @Inject
    public STControllerImpl(final STContext context, final STDocument document) {
        this.context = context;
        this.document = document;
    }

    @Override
    public void initApplicationWindow() throws IOException {
        final Stage primaryStage = this.context.getPrimaryStage();

        final Parent root = FxmlLoader.load(STController.class.getResource("/fxml/SportsTracker.fxml"), //
                context.getResources().getResourceBundle(), this);

        primaryStage.setScene(new Scene(root));

        primaryStage.setTitle(MessageFormat.format("{0} {1}", //
                context.getResources().getString("application.title"), //
                context.getResources().getString("application.version")));

        primaryStage.getIcons().addAll( //
                new Image("icons/st-logo-512.png"), //
                new Image("icons/st-logo-256.png"), //
                new Image("icons/st-logo-128.png"), //
                new Image("icons/st-logo-64.png"), //
                new Image("icons/st-logo-48.png"), //
                new Image("icons/st-logo-32.png"), //
                new Image("icons/st-logo-24.png"));

        setupActionBindings();
        setupMacSpecificUI();

        // setup all views
        calendarViewController.loadAndSetupViewContent();
        exerciseListViewController.loadAndSetupViewContent();
        noteListViewController.loadAndSetupViewContent();
        weightListViewController.loadAndSetupViewContent();

        statusBarController.setStatusBar(laStatusBar);

        // set initial view
        if (document.getOptions().getInitialView() == STOptions.View.Calendar) {
            switchToView(EntryViewController.ViewType.CALENDAR);
        } else {
            switchToView(EntryViewController.ViewType.EXERCISE_LIST);
        }

        // register listener for window close event
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            saveChangesAndExitApplication();
        });
    }

    @Override
    public void loadApplicationData() {
        context.blockMainWindow(true);
        new Thread(new LoadTask()).start();
    }

    @Override
    public void onOpenHrmFile(final ActionEvent event) {
        // show file open dialog for HRM file selection
        final File selectedFile = prHRMFileOpenDialog.get().selectHRMFile(context.getPrimaryStage(),
                document.getOptions(), null);
        if (selectedFile != null) {

            // start ExerciseViewer
            LOGGER.info("Opening HRM file '" + selectedFile + "' in ExerciseViewer...");
            prExerciseViewer.get().showExercise(selectedFile.getAbsolutePath(), document.getOptions(),
                    context.getPrimaryStage(), false);
        }
    }

    @Override
    public void onSave(final ActionEvent event) {
        context.blockMainWindow(true);
        new Thread(new SaveTask(false)).start();
    }

    @Override
    public void onPrint(final ActionEvent event) {
        currentViewController.print();
    }

    @Override
    public void onQuit(final ActionEvent event) {
        saveChangesAndExitApplication();
    }

    @Override
    public void onAddExercise(final ActionEvent event) {
        if (!checkForExistingSportTypes()) {
            return;
        }

        // start exercise dialog for a new created exercise
        final Exercise newExercise = createNewExercise(dateForNewEntries);
        prExerciseDialogController.get().show(context.getPrimaryStage(), newExercise, false);
    }

    @Override
    public void onAddNote(final ActionEvent event) {
        // start Note dialog for a new created Note
        final Note newNote = new Note(document.getNoteList().getNewID());
        newNote.setDateTime(Date310Utils.getNoonDateTimeForDate(dateForNewEntries));

        prNoteDialogController.get().show(context.getPrimaryStage(), newNote);
    }

    @Override
    public void onAddWeight(final ActionEvent event) {
        // start Weight dialog for a new created Weight
        final Weight newWeight = new Weight(document.getWeightList().getNewID());
        newWeight.setDateTime(Date310Utils.getNoonDateTimeForDate(dateForNewEntries));

        // initialize with the weight value of previous entry (if there is some)
        int weightCount = document.getWeightList().size();
        if (weightCount > 0) {
            Weight lastWeight = document.getWeightList().getAt(weightCount - 1);
            newWeight.setValue(lastWeight.getValue());
        }

        prWeightDialogController.get().show(context.getPrimaryStage(), newWeight);
    }

    @Override
    public void setDateForNewEntries(final LocalDate date) {
        this.dateForNewEntries = date;
    }

    @Override
    public void onEditEntry(final ActionEvent event) {
        // start edit action depending on entry type
        if (currentViewController.getSelectedExerciseCount() == 1) {
            editExercise(currentViewController.getSelectedExerciseIDs()[0]);
        } else if (currentViewController.getSelectedNoteCount() == 1) {
            editNote(currentViewController.getSelectedNoteIDs()[0]);
        } else if (currentViewController.getSelectedWeightCount() == 1) {
            editWeight(currentViewController.getSelectedWeightIDs()[0]);
        }
    }

    @Override
    public void onCopyEntry(final ActionEvent event) {
        // start copy action depending on entry type
        if (currentViewController.getSelectedExerciseCount() == 1) {
            copyExercise(currentViewController.getSelectedExerciseIDs()[0]);
        } else if (currentViewController.getSelectedNoteCount() == 1) {
            copyNote(currentViewController.getSelectedNoteIDs()[0]);
        } else if (currentViewController.getSelectedWeightCount() == 1) {
            copyWeight(currentViewController.getSelectedWeightIDs()[0]);
        }
    }

    @Override
    public void onDeleteEntry(final ActionEvent event) {
        int[] selectedEntryIDs = null;
        IdDateObjectList<? extends IdDateObject> entryList = null;

        // get selected entry IDs and the type of their list
        if (currentViewController.getSelectedExerciseCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedExerciseIDs();
            entryList = document.getExerciseList();
        } else if (currentViewController.getSelectedNoteCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedNoteIDs();
            entryList = document.getNoteList();
        } else if (currentViewController.getSelectedWeightCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedWeightIDs();
            entryList = document.getWeightList();
        }

        if (selectedEntryIDs != null && selectedEntryIDs.length > 0) {

            // show confirmation dialog first
            final Optional<ButtonType> result = context.showConfirmationDialog(context.getPrimaryStage(), //
                    "st.view.confirm.delete.title", "st.view.confirm.delete.text");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // finally remove the entries
                for (int id : selectedEntryIDs) {
                    entryList.removeByID(id);
                }
            }
        }
    }

    @Override
    public void onViewHrmFile(final ActionEvent event) {
        // get selected exercise and start ExerciseViewer for it's HRM file
        // (special checks not needed here, done by action status property)
        final int exerciseID = currentViewController.getSelectedExerciseIDs()[0];
        final String hrmFile = document.getExerciseList().getByID(exerciseID).getHrmFile();

        LOGGER.info("Opening HRM file '" + hrmFile + "' in ExerciseViewer...");
        prExerciseViewer.get().showExercise(hrmFile, document.getOptions(), context.getPrimaryStage(), false);
    }

    @Override
    public void onPreferences(final ActionEvent event) {
        prPreferencesDialogController.get().show(context.getPrimaryStage());
        // update view after dialog was closed, preferences (e.g. unit system) might be changed
        updateView();
    }

    @Override
    public void onCalendarView(final ActionEvent event) {
        switchToView(EntryViewController.ViewType.CALENDAR);
    }

    @Override
    public void onExerciseListView(final ActionEvent event) {
        switchToView(EntryViewController.ViewType.EXERCISE_LIST);
    }

    @Override
    public void onNoteListView(final ActionEvent event) {
        switchToView(EntryViewController.ViewType.NOTE_LIST);
    }

    @Override
    public void onWeightListView(final ActionEvent event) {
        switchToView(EntryViewController.ViewType.WEIGHT_LIST);
    }

    @Override
    public void onFilterExercises(final ActionEvent event) {
        final FilterDialogController controller = prFilterDialogController.get();
        controller.show(context.getPrimaryStage(), document.getCurrentFilter());

        // set and enable filter when available after dialog has been closed
        controller.getSelectedFilter().ifPresent(selectedFilter -> {
            document.setCurrentFilter(selectedFilter);
            document.setFilterEnabled(true);
            updateView();
        });
    }

    @Override
    public void onFilterDisable(final ActionEvent event) {
        document.setFilterEnabled(false);
        updateView();
    }

    @Override
    public void onSportTypeEditor(final ActionEvent event) {
        prSportTypeListDialogController.get().show(context.getPrimaryStage());

        // sport type and subtype objects may have been changed => these will be new objects
        // => update all exercises and the current filter when the dialog closes, they need
        // to reference to these new objects
        final SportTypeList stList = document.getSportTypeList();
        document.getExerciseList().updateSportTypes(stList);
        document.getCurrentFilter().updateSportTypes(stList);
        updateView();
    }

    @Override
    public void onStatistics(final ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        prStatisticDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onOverviewDiagram(final ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        prOverviewDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onAbout(final ActionEvent event) {
        prAboutDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onAddExerciseForDroppedHrmFile(final String hrmFilePath) {
        if (checkForExistingSportTypes()) {

            // create a new exercise and assign the HRM file
            Exercise newExercise = createNewExercise(null);
            newExercise.setHrmFile(hrmFilePath);

            // start Exercise dialog for it and import the HRM data
            prExerciseDialogController.get().show(context.getPrimaryStage(), newExercise, true);
        }
    }

    @Override
    public void onAssignDroppedHrmFileToExercise(final String hrmFilePath, final Exercise exercise) {
        exercise.setHrmFile(hrmFilePath);
        document.getExerciseList().set(exercise);
        context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.INFORMATION, //
                "common.info", "st.calview.draganddrop.assigned");
    }

    @Override
    public boolean checkForExistingSportTypes() {
        if (document.getSportTypeList().size() == 0) {
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.no_sporttype");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkForExistingExercises() {
        if (document.getExerciseList().size() == 0) {
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.no_exercise");
            return false;
        }
        return true;
    }

    @Override
    public void updateView() {
        // update format utils in context (setting may have changed)
        final STOptions options = document.getOptions();
        context.setFormatUtils(new FormatUtils(options.getUnitSystem(), options.getSpeedView()));

        currentViewController.updateView();
        updateActionsAndStatusBar();
    }

    @Override
    public void updateActionsAndStatusBar() {
        updateActionStatus();
        statusBarController.updateStatusBar(currentViewController.getSelectedExerciseIDs());
    }

    /**
     * Setup of bindings for the menu items and toolbar buttons. There is an action*Disabled property
     * for each action which is bound to the disabled property of the appropriate controls. So the
     * status of all similar action controls can be controlled by one action*Disabled property.
     */
    private void setupActionBindings() {

        miSave.disableProperty().bind(actionSaveDisabled);
        btSave.disableProperty().bind(actionSaveDisabled);

        miEditEntry.disableProperty().bind(actionEditEntryDisabled);
        btEditEntry.disableProperty().bind(actionEditEntryDisabled);
        miCopyEntry.disableProperty().bind(actionEditEntryDisabled);
        btCopyEntry.disableProperty().bind(actionEditEntryDisabled);
        miDeleteEntry.disableProperty().bind(actionDeleteEntryDisabled);
        btDeleteEntry.disableProperty().bind(actionDeleteEntryDisabled);

        miViewHrm.disableProperty().bind(actionViewHrmDisabled);
        btViewHrm.disableProperty().bind(actionViewHrmDisabled);

        miCalendarView.disableProperty().bind(actionCalendarViewDisabled);
        btCalendarView.disableProperty().bind(actionCalendarViewDisabled);
        miExerciseListView.disableProperty().bind(actionExerciseListViewDisabled);
        btExerciseListView.disableProperty().bind(actionExerciseListViewDisabled);
        miNoteListView.disableProperty().bind(actionNoteListViewDisabled);
        btNoteListView.disableProperty().bind(actionNoteListViewDisabled);
        miWeightListView.disableProperty().bind(actionWeightListViewDisabled);
        btWeightListView.disableProperty().bind(actionWeightListViewDisabled);

        miFilterDisable.disableProperty().bind(actionFilterDisableDisabled);
        btFilterDisable.disableProperty().bind(actionFilterDisableDisabled);
    }

    private void setupMacSpecificUI() {
        if (PlatformUtils.isMacOSX()) {
            // remove File->Quit command from file menu (already displayed in the application menu)›
            miQuit.setVisible(false);
            // About and Preferences commands can't be move to the application menu in JavaFX
            // applications (works in Swing by using a hack with the proprietary Mac API)
        }
    }

    private void updateActionStatus() {
        actionSaveDisabled.set(!document.isDirtyData());

        // update status of entry actions depending on current entry selection
        final int selExerciseCount = currentViewController.getSelectedExerciseCount();
        final int selNoteCount = currentViewController.getSelectedNoteCount();
        final int selWeightCount = currentViewController.getSelectedWeightCount();

        final boolean fEditEnabled = selExerciseCount == 1 || selNoteCount == 1 || selWeightCount == 1;
        actionEditEntryDisabled.set(!fEditEnabled);

        final boolean fDeleteEnabled = selExerciseCount > 0 || selNoteCount > 0 || selWeightCount > 0;
        actionDeleteEntryDisabled.set(!fDeleteEnabled);

        // action 'View HRM File' is only enabled when the selected exercise contains a HRM file
        boolean fHRMEnabled = false;
        if (selExerciseCount == 1) {
            final int selExerciseID = currentViewController.getSelectedExerciseIDs()[0];
            final Exercise selExercise = document.getExerciseList().getByID(selExerciseID);
            fHRMEnabled = StringUtils.getTrimmedTextOrNull(selExercise.getHrmFile()) != null;
        }
        actionViewHrmDisabled.set(!fHRMEnabled);

        actionFilterDisableDisabled.set(!document.isFilterEnabled());

        // update status of view actions depending on the current view type
        final EntryViewController.ViewType currentViewType = currentViewController.getViewType();
        actionCalendarViewDisabled.set(currentViewType == EntryViewController.ViewType.CALENDAR);
        actionExerciseListViewDisabled.set(currentViewType == EntryViewController.ViewType.EXERCISE_LIST);
        actionNoteListViewDisabled.set(currentViewType == EntryViewController.ViewType.NOTE_LIST);
        actionWeightListViewDisabled.set(currentViewType == EntryViewController.ViewType.WEIGHT_LIST);
    }

    /**
     * Checks for existing sport types. When the list is empty, create a list of initial sport types
     * and display an information message (easier so new application users).
     */
    private void addInitialSportTypesIfMissing() {
        if (document.getSportTypeList().size() == 0) {

            addInitialSportType("st.initial_sporttypes.cycling", Color.DARKBLUE, //
                    "st.initial_sporttypes.cycling.mtb_tour", "st.initial_sporttypes.cycling.mtb_race", //
                    "st.initial_sporttypes.cycling.road_tour", "st.initial_sporttypes.cycling.road_race");

            addInitialSportType("st.initial_sporttypes.running", Color.FIREBRICK, //
                    "st.initial_sporttypes.running.street_run", "st.initial_sporttypes.running.street_race", //
                    "st.initial_sporttypes.running.trail_run", "st.initial_sporttypes.running.trail_race");

            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.INFORMATION, //
                    "common.info", "st.main.info.initial_sporttypes_added");
        }
    }

    /**
     * Creates the specified sport type and stores it in the document list.
     *
     * @param nameKey key of the sport type name
     * @param color sport type color
     * @param subtypeNameKeys list of keys for the sport subtype names
     */
    private void addInitialSportType(final String nameKey, final Color color, final String... subtypeNameKeys) {
        final SportType sportType = new SportType(document.getSportTypeList().getNewID());
        sportType.setName(context.getResources().getString(nameKey));
        sportType.setColor(color);

        for (String subtypeNameKey : subtypeNameKeys) {
            final SportSubType sportSubtype = new SportSubType(sportType.getSportSubTypeList().getNewID());
            sportSubtype.setName(context.getResources().getString(subtypeNameKey));
            sportType.getSportSubTypeList().set(sportSubtype);
        }
        document.getSportTypeList().set(sportType);
    }

    /**
     * Checks for unsaved application data and exits the application. When there is unsaved data then
     * the user will be asked to save the data before (can also be saved without user confirmation when
     * the 'save on exit' option is enabled in the preferences).<br/>
     * The application will not be exited when the save action fails or when the user cancels the
     * confirmation.<br/>
     * When there is no unsaved data, the application will be exited immediately.
     */
    private void saveChangesAndExitApplication() {

        if (document.isDirtyData()) {
            if (!document.getOptions().isSaveOnExit()) {

                final Optional<ButtonType> oResult = context.showConfirmationDialog(context.getPrimaryStage(), //
                        "st.main.confirm.save_exit.title", "st.main.confirm.save_exit.text", //
                        ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

                if (!oResult.isPresent() || oResult.get() == ButtonType.CANCEL) {
                    // cancel the application exit
                    return;
                } else if (oResult.get() == ButtonType.NO) {
                    // exit without saving unsaved changes
                    exitApplication();
                    return;
                }
            }

            // save unsaved changes and exit on success
            context.blockMainWindow(true);
            new Thread(new SaveTask(true)).start();
        } else {
            exitApplication();
        }
    }

    /**
     * Exits the SportsTracker application and releases the resources before.
     */
    private void exitApplication() {
        context.getPrimaryStage().close();
    }

    /**
     * Registers a listener which updates the view after each data change and selects the changed
     * object in the current view, if specified.
     */
    private void registerListenerForDataChanges() {
        document.registerListChangeListener(changedObject -> {
            updateView();
            if (changedObject != null) {
                currentViewController.selectEntry(changedObject);
            }
        });
    }

    /**
     * Switches the view to the specified exercise view type.
     *
     * @param viewType the exercise view type to display
     */
    private void switchToView(final EntryViewController.ViewType viewType) {

        // determine controller of specified view
        switch (viewType) {
            case CALENDAR:
                currentViewController = calendarViewController;
                break;
            case EXERCISE_LIST:
                currentViewController = exerciseListViewController;
                break;
            case NOTE_LIST:
                currentViewController = noteListViewController;
                break;
            case WEIGHT_LIST:
                currentViewController = weightListViewController;
                break;
            default:
                throw new IllegalArgumentException("Invalid ViewType " + viewType + "!");
        }

        // update and display the new view
        currentViewController.removeSelection();
        updateView();
        spViews.getChildren().setAll(currentViewController.getRootNode());

        // trigger a garbage collection after view change to avoid allocation of additional heap space
        SystemUtils.triggerGC();
    }

    /**
     * Creates a new Exercise. The exercise date will be the specified date at
     * 12:00:00 or if no date is specified the current day at 12:00.
     *
     * @param date the date to be set in the exercise (can be null for the current date)
     * @return the created Exercise
     */
    private Exercise createNewExercise(final LocalDate date) {
        final Exercise exercise = new Exercise(document.getExerciseList().getNewID());
        exercise.setDateTime(Date310Utils.getNoonDateTimeForDate(date));
        exercise.setIntensity(Exercise.IntensityType.NORMAL);
        return exercise;
    }

    /**
     * Starts Edit dialog for the specified exercise ID.
     *
     * @param exerciseID ID of the exercise entry
     */
    private void editExercise(int exerciseID) {
        final Exercise selExercise = document.getExerciseList().getByID(exerciseID);
        prExerciseDialogController.get().show(context.getPrimaryStage(), selExercise, false);
    }

    /**
     * Starts Edit dialog for the specified note ID.
     *
     * @param noteID ID of the note entry
     */
    private void editNote(int noteID) {
        final Note selectedNote = document.getNoteList().getByID(noteID);
        prNoteDialogController.get().show(context.getPrimaryStage(), selectedNote);
    }

    /**
     * Starts Edit dialog for the specified weight ID.
     *
     * @param weightID ID of the weight entry
     */
    private void editWeight(int weightID) {
        final Weight selectedWeight = document.getWeightList().getByID(weightID);
        prWeightDialogController.get().show(context.getPrimaryStage(), selectedWeight);
    }

    /**
     * Creates a copy of the specified Exercise and displays it in the Exercise dialog.
     *
     * @param exerciseID ID of the exercise entry to copy
     */
    private void copyExercise(final int exerciseID) {
        final Exercise selectedExercise = document.getExerciseList().getByID(exerciseID);

        final Exercise copiedExercise = selectedExercise.clone(document.getExerciseList().getNewID());
        copiedExercise.setDateTime(Date310Utils.getNoonDateTimeForDate(null));
        copiedExercise.setHrmFile(null);

        // start exercise dialog for the copied exercise
        prExerciseDialogController.get().show(context.getPrimaryStage(), copiedExercise, false);
    }

    /**
     * Creates a copy of the specified Note and displays it in the Note dialog.
     *
     * @param noteID ID of the note entry to copy
     */
    private void copyNote(final int noteID) {
        final Note selectedNote = document.getNoteList().getByID(noteID);

        final Note copiedNote = selectedNote.clone(document.getNoteList().getNewID());
        copiedNote.setDateTime(Date310Utils.getNoonDateTimeForDate(null));

        // start note dialog for the copied note
        prNoteDialogController.get().show(context.getPrimaryStage(), copiedNote);
    }

    /**
     * Creates a copy of the specified Weight and displays it in the Weight dialog.
     *
     * @param weightID ID of the weight entry to copy
     */
    private void copyWeight(final int weightID) {
        final Weight selectedWeight = document.getWeightList().getByID(weightID);

        final Weight copiedWeight = selectedWeight.clone(document.getWeightList().getNewID());
        copiedWeight.setDateTime(Date310Utils.getNoonDateTimeForDate(null));

        // start weight dialog for the copied weight
        prWeightDialogController.get().show(context.getPrimaryStage(), copiedWeight);
    }

    /**
     * This class executes the loading action inside a background task without blocking the UI thread.
     * It also checks the existence of all attached exercise files.
     */
    private class LoadTask extends Task<Void> {

        private List<Exercise> corruptExercises;

        @Override
        protected Void call() throws Exception {
            LOGGER.info("Loading application data...");
            document.readApplicationData();
            corruptExercises = document.checkExerciseFiles();
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            context.blockMainWindow(false);

            updateFinally();
            displayCorruptExercises();
            addInitialSportTypesIfMissing();
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);

            LOGGER.log(Level.SEVERE, "Failed to load application data!", getException());
            updateFinally();
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.load_data");
        }

        private void updateFinally() {
            updateView();
            // listener must be registered after loading data, because new lists are created
            registerListenerForDataChanges();
        }

        private void displayCorruptExercises() {
            if (corruptExercises != null && !corruptExercises.isEmpty()) {

                final StringBuilder sb = new StringBuilder();
                final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

                for (int i = 0; i < corruptExercises.size(); i++) {
                    if (i > 15) {
                        sb.append("...\n");
                        break;
                    }

                    sb.append(corruptExercises.get(i).getDateTime().format(dateTimeFormatter));
                    sb.append("\n");
                }

                context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.WARNING, //
                        "common.warning", "st.main.error.missing_exercise_files", sb.toString());
            }
        }
    }

    /**
     * This class executes the save action inside a background task without blocking the UI thread.
     */
    private class SaveTask extends Task<Void> {

        private boolean exitOnSuccess;

        /**
         * Standard c'tor.
         *
         * @param exitOnSuccess flag for exiting the application after successful save
         */
        public SaveTask(final boolean exitOnSuccess) {
            this.exitOnSuccess = exitOnSuccess;
        }

        @Override
        protected Void call() throws Exception {
            LOGGER.info("Saving application data...");
            document.storeApplicationData();
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            context.blockMainWindow(false);
            updateActionsAndStatusBar();

            if (exitOnSuccess) {
                exitApplication();
            }
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);

            LOGGER.log(Level.SEVERE, "Failed to store application data!", getException());
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.save_data");
        }
    }
}
