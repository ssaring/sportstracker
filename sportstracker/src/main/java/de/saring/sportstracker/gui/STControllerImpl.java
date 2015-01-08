package de.saring.sportstracker.gui;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.saring.sportstracker.gui.dialogs.FilterDialogController;
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
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.gui.dialogs.AboutDialogController;
import de.saring.sportstracker.gui.dialogs.HRMFileOpenDialog;
import de.saring.sportstracker.gui.dialogs.OverviewDialogController;
import de.saring.sportstracker.gui.dialogs.PreferencesDialogController;
import de.saring.sportstracker.gui.dialogs.SportTypeListDialogController;
import de.saring.sportstracker.gui.dialogs.StatisticDialogController;
import de.saring.sportstracker.gui.views.EntryViewController;
import de.saring.sportstracker.gui.views.listview.ExerciseListViewController;
import de.saring.util.gui.javafx.FxmlLoader;
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

    // @Inject
    // TODO private CalendarViewController calendarViewController;
    @Inject
    private ExerciseListViewController exerciseListViewController;
    // @Inject
    // TODO private NoteListViewController noteListViewController;
    // @Inject
    // TODO private WeightListViewController weightListViewController;

    /** The controller of the currently displayed view. */
    private EntryViewController currentViewController;

    @Inject
    private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
    @Inject
    private Provider<EVMain> prExerciseViewer;
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

    // @Inject
    // private Provider<ExerciseDialogController> prExerciseDialogController;
    // @Inject
    // private Provider<NoteDialogController> prNoteDialogController;
    // @Inject
    // private Provider<WeightDialogController> prWeightDialogController;

    // list of all menu items
    @FXML
    private MenuItem miSave;
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

        // setup all views
        exerciseListViewController.loadAndSetupViewContent();

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
    public void onOpenHrmFile(ActionEvent event) {
        // show file open dialog for HRM file selection
        final File selectedFile = prHRMFileOpenDialog.get().selectHRMFile(context.getPrimaryStage(),
                document.getOptions(), null);
        if (selectedFile != null) {

            // start ExerciseViewer
            prExerciseViewer.get().showExercise(selectedFile.getAbsolutePath(), document.getOptions(),
                    context.getPrimaryStage(), false);
        }
    }

    @Override
    public void onSave(ActionEvent event) {
        context.blockMainWindow(true);
        new Thread(new SaveTask(false)).start();
    }

    @Override
    public void onPrint(ActionEvent event) {
        // TODO
    }

    @Override
    public void onQuit(ActionEvent event) {
        saveChangesAndExitApplication();
    }

    @Override
    public void onAddExercise(ActionEvent event) {
        // TODO
    }

    @Override
    public void onAddNote(ActionEvent event) {
        // TODO
    }

    @Override
    public void onAddWeight(ActionEvent event) {
        // TODO
    }

    @Override
    public void onEditEntry(ActionEvent event) {
        // TODO
    }

    @Override
    public void onCopyEntry(ActionEvent event) {
        // TODO
    }

    @Override
    public void onDeleteEntry(ActionEvent event) {
        // TODO
    }

    @Override
    public void onViewHrmFile(ActionEvent event) {
        // TODO
    }

    @Override
    public void onPreferences(ActionEvent event) {
        prPreferencesDialogController.get().show(context.getPrimaryStage());
        // update view after dialog was closed, preferences (e.g. unit system) might be changed
        updateView();
    }

    @Override
    public void onCalendarView(ActionEvent event) {
        switchToView(EntryViewController.ViewType.CALENDAR);
    }

    @Override
    public void onExerciseListView(ActionEvent event) {
        switchToView(EntryViewController.ViewType.EXERCISE_LIST);
    }

    @Override
    public void onNoteListView(ActionEvent event) {
        switchToView(EntryViewController.ViewType.NOTE_LIST);
    }

    @Override
    public void onWeightListView(ActionEvent event) {
        switchToView(EntryViewController.ViewType.WEIGHT_LIST);
    }

    @Override
    public void onFilterExercises(ActionEvent event) {
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
    public void onFilterDisable(ActionEvent event) {
        document.setFilterEnabled(false);
        updateView();
    }

    @Override
    public void onSportTypeEditor(ActionEvent event) {
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
    public void onStatistics(ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        prStatisticDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onOverviewDiagram(ActionEvent event) {
        if (!checkForExistingExercises()) {
            return;
        }

        prOverviewDialogController.get().show(context.getPrimaryStage());
    }

    @Override
    public void onAbout(ActionEvent event) {
        prAboutDialogController.get().show(context.getPrimaryStage());
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

        // TODO update list of exercises to be displayed and update current view
        // displayedExercises = document.getFilterableExerciseList();
        currentViewController.updateView();

        updateActionStatus();
    }

    /**
     * Setup of bindings for the menu items and toolbar buttons. There is an action*Disabled property
     * for each action which is bound to the disabled property of the appropriate controls. So the
     * status of all similar action controls can be controlled by one action*Disabled property.
     */
    private void setupActionBindings() {

        miSave.disableProperty().bind(actionSaveDisabled);
        btSave.disableProperty().bind(actionSaveDisabled);

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

    /**
     * Updates the action status properties. The actions are enabled or disabled depending on
     * the current entry selection, document state and displayed view.
     */
    private void updateActionStatus() {
        actionSaveDisabled.set(!document.isDirtyData());
        actionFilterDisableDisabled.set(!document.isFilterEnabled());

        // TODO update status of view actions depending on the current view controller
        // actionCalendarViewDisabled.set(currentViewController == calendarViewController);
        actionExerciseListViewDisabled.set(currentViewController == exerciseListViewController);
        // actionNoteListViewDisabled.set(currentViewController == noteListViewController);
        // actionWeightListViewDisabled.set(currentViewController == weightListViewController);
    }

    /**
     * Checks for existing sport types. When there are no sport types yet, then the user will be
     * asked if he wants to define a sport type. If yes, then the sport type editor will be opened.
     */
    private void askForDefiningSportTypes() {
        if (document.getSportTypeList().size() == 0) {
            final Optional<ButtonType> oResult = context.showConfirmationDialog(context.getPrimaryStage(),
                    "common.info", "st.main.confirm.define_first_sporttype", ButtonType.YES, ButtonType.NO);

            if (oResult.isPresent() && oResult.get() == ButtonType.YES) {
                onSportTypeEditor(null);
            }
        }
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
        System.exit(0);
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
                // TODO currentView = calendarViewController;
                currentViewController = exerciseListViewController;
                break;
            case EXERCISE_LIST:
                currentViewController = exerciseListViewController;
                break;
            case NOTE_LIST:
                // TODO currentViewController = noteListViewController;
                currentViewController = exerciseListViewController;
                break;
            case WEIGHT_LIST:
                // TODO currentViewController = weightListViewController;
                currentViewController = exerciseListViewController;
                break;
            default:
                throw new IllegalArgumentException("Invalid ViewType " + viewType + "!");
        }

        // update and display the new view
        currentViewController.removeSelection();
        updateView();
        spViews.getChildren().setAll(currentViewController.getRootNode());
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
            // TODO view.registerViewForDataChanges();

            displayCorruptExercises();
            askForDefiningSportTypes();
        }

        @Override
        protected void failed() {
            super.failed();
            context.blockMainWindow(false);

            LOGGER.log(Level.SEVERE, "Failed to load application data!", getException());
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, //
                    "common.error", "st.main.error.load_data");
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
            updateActionStatus();

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
