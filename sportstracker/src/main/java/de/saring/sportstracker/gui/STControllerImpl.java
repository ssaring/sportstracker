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

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.storage.SQLiteExporter;
import de.saring.sportstracker.storage.db.AbstractRepository;
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

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.Note;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.dialogs.DialogProvider;
import de.saring.sportstracker.gui.dialogs.FilterDialogController;
import de.saring.sportstracker.gui.statusbar.StatusBarController;
import de.saring.sportstracker.gui.views.EntryViewEventHandler;
import de.saring.sportstracker.gui.views.EntryViewController;
import de.saring.sportstracker.gui.views.calendarview.CalendarViewController;
import de.saring.sportstracker.gui.views.listviews.ExerciseListViewController;
import de.saring.sportstracker.gui.views.listviews.NoteListViewController;
import de.saring.sportstracker.gui.views.listviews.WeightListViewController;
import de.saring.util.Date310Utils;
import de.saring.util.StringUtils;
import de.saring.util.SystemUtils;
import de.saring.util.gui.javafx.FxmlLoader;
import de.saring.util.PlatformUtils;
import de.saring.util.unitcalc.FormatUtils;
import de.saring.util.unitcalc.SpeedMode;

/**
 * This class provides all controller (MVC)functionality of the SportsTracker main application window.
 *
 * @author Stefan Saring
 */
@Singleton
public class STControllerImpl implements STController, EntryViewEventHandler {

    private static final Logger LOGGER = Logger.getLogger(STControllerImpl.class.getName());

    private static final String URL_PROJECT_WEBSITE = "https://www.saring.de/sportstracker";

    private final STContext context;
    private final STDocument document;
    private final SQLiteExporter exporter;

    private CalendarViewController calendarViewController;
    private ExerciseListViewController exerciseListViewController;
    private NoteListViewController noteListViewController;
    private WeightListViewController weightListViewController;
    private StatusBarController statusBarController;
    private DialogProvider dialogProvider;

    /** The controller of the currently displayed view. */
    private EntryViewController currentViewController;

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
     * Standard c'tor for DI.
     *
     * @param context the SportsTracker context
     * @param document the document component
     * @param exporter the SQLite exporter
     * @param calendarViewController controller of the calendar view
     * @param exerciseListViewController controller of the exercise list view
     * @param noteListViewController controller of the note list view
     * @param weightListViewController controller of the weight list view
     * @param statusBarController controller of the status bar
     * @param dialogProvider provider of all dialogs
     */
    @Inject
    public STControllerImpl(final STContext context, final STDocument document, final SQLiteExporter exporter,
                            final CalendarViewController calendarViewController,
                            final ExerciseListViewController exerciseListViewController,
                            final NoteListViewController noteListViewController,
                            final WeightListViewController weightListViewController,
                            final StatusBarController statusBarController,
                            final DialogProvider dialogProvider) {
        this.context = context;
        this.document = document;
        this.exporter = exporter;
        this.calendarViewController = calendarViewController;
        this.exerciseListViewController = exerciseListViewController;
        this.noteListViewController = noteListViewController;
        this.weightListViewController = weightListViewController;
        this.statusBarController = statusBarController;
        this.dialogProvider = dialogProvider;
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
                new Image("icons/st-logo_512x512.png"), //
                new Image("icons/st-logo_256x256.png"), //
                new Image("icons/st-logo_128x128.png"), //
                new Image("icons/st-logo_64x64.png"), //
                new Image("icons/st-logo_32x32.png"), //
                new Image("icons/st-logo_16x16.png"));

        setupActionBindings();
        setupMacSpecificUI();

        // setup all views
        calendarViewController.initAndSetupViewContent(this);
        exerciseListViewController.initAndSetupViewContent(this);
        noteListViewController.initAndSetupViewContent(this);
        weightListViewController.initAndSetupViewContent(this);

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
        final File selectedFile = dialogProvider.prHRMFileOpenDialog.get().selectHRMFile(context.getPrimaryStage(),
                document.getOptions(), null);
        if (selectedFile != null) {

            // start ExerciseViewer
            LOGGER.info("Opening HRM file '" + selectedFile + "' in ExerciseViewer...");
            final SpeedMode speedMode = document.getOptions().getPreferredSpeedMode();
            dialogProvider.prExerciseViewer.get().showExercise(selectedFile.getAbsolutePath(),
                    context.getPrimaryStage(), false, speedMode);
        }
    }

    @Override
    public void onSave(final ActionEvent event) {
        context.blockMainWindow(true);
        new Thread(new SaveTask(false)).start();
    }

    @Override
    public void onExportSqlite(final ActionEvent event) {
        context.blockMainWindow(true);
        new Thread(new ExportSqliteTask()).start();
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
        dialogProvider.prExerciseDialogController.get().show(context.getPrimaryStage(), newExercise, false);
    }

    @Override
    public void onAddNote(final ActionEvent event) {
        // start Note dialog for a new created Note
        final Note newNote = new Note(null);
        newNote.setDateTime(Date310Utils.getNoonDateTimeForDate(dateForNewEntries));

        dialogProvider.prNoteDialogController.get().show(context.getPrimaryStage(), newNote);
    }

    @Override
    public void onAddWeight(final ActionEvent event) {
        // start Weight dialog for a new created Weight
        final Weight newWeight = new Weight(null);
        newWeight.setDateTime(Date310Utils.getNoonDateTimeForDate(dateForNewEntries));

        // initialize with the weight value of previous entry (if there is some)
        int weightCount = document.getWeightList().size();
        if (weightCount > 0) {
            Weight lastWeight = document.getWeightList().getAt(weightCount - 1);
            newWeight.setValue(lastWeight.getValue());
        }

        dialogProvider.prWeightDialogController.get().show(context.getPrimaryStage(), newWeight);
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
        long[] selectedEntryIDs = null;
        AbstractRepository repository = null;

        // get selected entry IDs and the type of their list
        if (currentViewController.getSelectedExerciseCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedExerciseIDs();
            repository = document.getStorage().getExerciseRepository();
        } else if (currentViewController.getSelectedNoteCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedNoteIDs();
            repository = document.getStorage().getNoteRepository();
        } else if (currentViewController.getSelectedWeightCount() > 0) {
            selectedEntryIDs = currentViewController.getSelectedWeightIDs();
            repository = document.getStorage().getWeightRepository();
        }

        if (selectedEntryIDs != null && selectedEntryIDs.length > 0 && repository != null) {

            // show confirmation dialog first
            final Optional<ButtonType> result = context.showConfirmationDialog(context.getPrimaryStage(), //
                    "st.view.confirm.delete.title", "st.view.confirm.delete.text");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                // finally remove the entries
                try {
                    for (long id : selectedEntryIDs) {
                        repository.delete(id);
                    }
                    document.updateApplicationData(null);
                } catch (STException e) {
                    LOGGER.log(Level.SEVERE, "Failed to delete the selected entries!", e);
                }
            }
        }
    }

    @Override
    public void onViewHrmFile(final ActionEvent event) {
        // get selected exercise and start ExerciseViewer for it's HRM file
        // (special checks not needed here, done by action status property)
        final long exerciseID = currentViewController.getSelectedExerciseIDs()[0];
        final Exercise exercise = document.getExerciseList().getByID(exerciseID);
        final String hrmFile = exercise.getHrmFile();
        final SpeedMode speedMode = exercise.getSportType().getSpeedMode();

        LOGGER.info("Opening HRM file '" + hrmFile + "' in ExerciseViewer...");
        dialogProvider.prExerciseViewer.get().showExercise(hrmFile, context.getPrimaryStage(), false, speedMode);
    }

    @Override
    public void onPreferences(final ActionEvent event) {
        dialogProvider.prPreferencesDialogController.get().show(context.getPrimaryStage());
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
    public void onFilterEntries(final ActionEvent event) {
        final FilterDialogController controller = dialogProvider.prFilterDialogController.get();
        controller.show(context.getPrimaryStage(), document.getCurrentFilter(), true);

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
        dialogProvider.prSportTypeListDialogController.get().show(context.getPrimaryStage());

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

        dialogProvider.prStatisticDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onOverviewDiagram(final ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        dialogProvider.prOverviewDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onEquipmentUsage(final ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        dialogProvider.prEquipmentUsageDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onWebsite(final ActionEvent event) {
        context.getHostServices().showDocument(URL_PROJECT_WEBSITE);
    }

    @Override
    public void onAbout(final ActionEvent event) {
        dialogProvider.prAboutDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onAddExerciseForDroppedHrmFile(final String hrmFilePath) {
        if (checkForExistingSportTypes()) {

            // create a new exercise and assign the HRM file
            Exercise newExercise = createNewExercise(null);
            newExercise.setHrmFile(hrmFilePath);

            // start Exercise dialog for it and import the HRM data
            dialogProvider.prExerciseDialogController.get().show(context.getPrimaryStage(), newExercise, true);
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
        context.setFormatUtils(new FormatUtils(options.getUnitSystem()));

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
        if (PlatformUtils.isMacOS()) {
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
            final long selExerciseID = currentViewController.getSelectedExerciseIDs()[0];
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
            try {
                addInitialSportType("st.initial_sporttypes.cycling", SpeedMode.SPEED, Color.DARKBLUE, //
                        "st.initial_sporttypes.cycling.mtb_tour", "st.initial_sporttypes.cycling.mtb_race", //
                        "st.initial_sporttypes.cycling.road_tour", "st.initial_sporttypes.cycling.road_race");

                addInitialSportType("st.initial_sporttypes.running", SpeedMode.PACE, Color.FIREBRICK, //
                        "st.initial_sporttypes.running.street_run", "st.initial_sporttypes.running.street_race", //
                        "st.initial_sporttypes.running.trail_run", "st.initial_sporttypes.running.trail_race");

                document.updateApplicationData(null);
                context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.INFORMATION, //
                        "common.info", "st.main.info.initial_sporttypes_added");
            } catch (STException e) {
                LOGGER.log(Level.SEVERE, "Failed to create initial sport types!", e);
            }
        }
    }

    /**
     * Creates the specified sport type and stores it in the repository.
     *
     * @param nameKey key of the sport type name
     * @param speedMode speed mode
     * @param color sport type color
     * @param subtypeNameKeys list of keys for the sport subtype names
     */
    private void addInitialSportType(final String nameKey, final SpeedMode speedMode, final Color color,
                                     final String... subtypeNameKeys) throws STException {
        final SportType sportType = new SportType(null);
        sportType.setName(context.getResources().getString(nameKey));
        sportType.setSpeedMode(speedMode);
        sportType.setColor(color);

        for (String subtypeNameKey : subtypeNameKeys) {
            final SportSubType sportSubtype = new SportSubType(null);
            sportSubtype.setName(context.getResources().getString(subtypeNameKey));
            sportType.getSportSubTypeList().set(sportSubtype);
        }
        document.getStorage().getSportTypeRepository().create(sportType);
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
        document.registerChangeListener(changedObject -> {
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
        currentViewController = switch (viewType) {
            case CALENDAR -> calendarViewController;
            case EXERCISE_LIST -> exerciseListViewController;
            case NOTE_LIST -> noteListViewController;
            case WEIGHT_LIST -> weightListViewController;
            default -> throw new IllegalArgumentException("Invalid ViewType " + viewType + "!");
        };

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
        final Exercise exercise = new Exercise(null);
        exercise.setDateTime(Date310Utils.getNoonDateTimeForDate(date));
        exercise.setIntensity(Exercise.IntensityType.NORMAL);

        // pre-select sport type and sport subtype when there is only one choice
        if (document.getSportTypeList().size() == 1) {
            final SportType sportType = document.getSportTypeList().getAt(0);
            exercise.setSportType(sportType);

            if (sportType.getSportSubTypeList().size() == 1) {
                final SportSubType sportSubType = sportType.getSportSubTypeList().getAt(0);
                exercise.setSportSubType(sportSubType);
            }
        }

        return exercise;
    }

    /**
     * Starts Edit dialog for the specified exercise ID.
     *
     * @param exerciseID ID of the exercise entry
     */
    private void editExercise(long exerciseID) {
        final var selExercise = document.getExerciseList().getByID(exerciseID);
        dialogProvider.prExerciseDialogController.get().show(context.getPrimaryStage(), selExercise, false);
    }

    /**
     * Starts Edit dialog for the specified note ID.
     *
     * @param noteID ID of the note entry
     */
    private void editNote(long noteID) {
        final var selectedNote = document.getNoteList().getByID(noteID);
        dialogProvider.prNoteDialogController.get().show(context.getPrimaryStage(), selectedNote);
    }

    /**
     * Starts Edit dialog for the specified weight ID.
     *
     * @param weightID ID of the weight entry
     */
    private void editWeight(long weightID) {
        final var selectedWeight = document.getWeightList().getByID(weightID);
        dialogProvider.prWeightDialogController.get().show(context.getPrimaryStage(), selectedWeight);
    }

    /**
     * Creates a copy of the specified Exercise and displays it in the Exercise dialog.
     *
     * @param exerciseID ID of the exercise entry to copy
     */
    private void copyExercise(final long exerciseID) {
        final var selectedExercise = document.getExerciseList().getByID(exerciseID);

        final var copiedExercise = selectedExercise.clone(null);
        copiedExercise.setDateTime(Date310Utils.getNoonDateTimeForDate(null));
        copiedExercise.setHrmFile(null);

        // start exercise dialog for the copied exercise
        dialogProvider.prExerciseDialogController.get().show(context.getPrimaryStage(), copiedExercise, false);
    }

    /**
     * Creates a copy of the specified Note and displays it in the Note dialog.
     *
     * @param noteID ID of the note entry to copy
     */
    private void copyNote(final long noteID) {
        final var selectedNote = document.getNoteList().getByID(noteID);

        final var copiedNote = selectedNote.clone(null);
        copiedNote.setDateTime(Date310Utils.getNoonDateTimeForDate(null));

        // start note dialog for the copied note
        dialogProvider.prNoteDialogController.get().show(context.getPrimaryStage(), copiedNote);
    }

    /**
     * Creates a copy of the specified Weight and displays it in the Weight dialog.
     *
     * @param weightID ID of the weight entry to copy
     */
    private void copyWeight(final long weightID) {
        final var selectedWeight = document.getWeightList().getByID(weightID);

        final var copiedWeight = selectedWeight.clone(null);
        copiedWeight.setDateTime(Date310Utils.getNoonDateTimeForDate(null));

        // start weight dialog for the copied weight
        dialogProvider.prWeightDialogController.get().show(context.getPrimaryStage(), copiedWeight);
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

            updateView();
            // listener must be registered after loading data, because new lists are created
            registerListenerForDataChanges();
            displayCorruptExercises();
            addInitialSportTypesIfMissing();
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);

            LOGGER.log(Level.SEVERE, "Failed to load application data!", getException());
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.open_load_data");
            exitApplication();
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

    /**
     * This class executes the Export to SQLite action inside a background task without blocking the UI thread.
     */
    private class ExportSqliteTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {
            LOGGER.info("Exporting application data to SQLite...");
            exporter.exportToSqlite();
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            context.blockMainWindow(false);

            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.INFORMATION, //
                    "common.info", "st.main.info.export_sqlite_success", exporter.getDatabasePath().toString());
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);

            LOGGER.log(Level.SEVERE, "Failed to export application data to SQLite!", getException());
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.export_sqlite");
        }
    }
}
