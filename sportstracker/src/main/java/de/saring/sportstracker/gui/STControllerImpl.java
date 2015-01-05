package de.saring.sportstracker.gui;

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
import de.saring.util.gui.javafx.FxmlLoader;
import de.saring.util.unitcalc.FormatUtils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Provider<AboutDialogController> prAboutDialogController;

    // @Inject
    // private Provider<ExerciseDialogController> prExerciseDialogController;
    // @Inject
    // private Provider<NoteDialogController> prNoteDialogController;
    // @Inject
    // private Provider<WeightDialogController> prWeightDialogController;
    // @Inject
    // private Provider<FilterDialogController> prFilterDialogController;

    @FXML
    private Label laStatusBar;

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

        primaryStage.setTitle(MessageFormat.format(
                "{0} {1}", //
                context.getResources().getString("application.title"),
                context.getResources().getString("application.version")));

        primaryStage.getIcons().addAll( //
                new Image("icons/st-logo-512.png"), //
                new Image("icons/st-logo-256.png"), //
                new Image("icons/st-logo-128.png"), //
                new Image("icons/st-logo-64.png"), //
                new Image("icons/st-logo-48.png"), //
                new Image("icons/st-logo-32.png"), //
                new Image("icons/st-logo-24.png"));

        // register listener for window close / application exit
        primaryStage.setOnCloseRequest(event -> onWindowCloseRequest(event));
    }

    @Override
    public void loadApplicationData() {
        showWaitCursor(true);
        new Thread(new LoadTask()).start();
    }

    @Override
    public void onOpenHrmFile(ActionEvent event) {
        // show file open dialog for HRM file selection
        final File selectedFile = prHRMFileOpenDialog.get().selectHRMFile(
                context.getPrimaryStage(), document.getOptions(), null);
        if (selectedFile != null) {

            // start ExerciseViewer
            prExerciseViewer.get().showExercise(selectedFile.getAbsolutePath(), document.getOptions(),
                    context.getPrimaryStage(), false);
        }
    }

    @Override
    public void onSave(ActionEvent event) {
        // TODO
    }

    @Override
    public void onPrint(ActionEvent event) {
        // TODO
    }

    @Override
    public void onQuit(ActionEvent event) {
        // TODO
        System.exit(0);
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
        // TODO
    }

    @Override
    public void onExerciseListView(ActionEvent event) {
        // TODO
    }

    @Override
    public void onNoteListView(ActionEvent event) {
        // TODO
    }

    @Override
    public void onWeightListView(ActionEvent event) {
        // TODO
    }

    @Override
    public void onFilterExercises(ActionEvent event) {
        // TODO
    }

    @Override
    public void onFilterDisable(ActionEvent event) {
        // TODO
    }

    @Override
    public void onSportTypeEditor(ActionEvent event) {
        prSportTypeListDialogController.get().show(context.getPrimaryStage());

        // sport type and subtype objects may have been changed  => these will be new objects
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
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, "common.error",
                    "st.main.error.no_sporttype");
            return false;
        }
        return true;
    }

    @Override
    public boolean checkForExistingExercises() {
        if (document.getExerciseList().size() == 0) {
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, "common.error",
                    "st.main.error.no_exercise");
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
        // currentView.updateView();
        // updateEntryActions();
    }

    /**
     * The event handler is called when the user wants to close the main application window.
     *
     * @param event event
     */
    private void onWindowCloseRequest(final WindowEvent event) {
        event.consume();
        onQuit(null);
    }

    // TODO test on OS X, works fine on Windows
    private void showWaitCursor(final boolean waitCursor) {
        // TODO block application window while showing wait cursor
        // can be done by displaying a modal transparent dialog of size 0x0
        context.getPrimaryStage().getScene().setCursor(waitCursor ? Cursor.WAIT : Cursor.DEFAULT);
    }

    /**
     * This class executes the loading action inside a background task without blocking the UI thread.
     * It also checks the existence of all attached exercise files.
     */
    private class LoadTask extends Task<Void> {

        private List<Exercise> corruptExercises;

        @Override
        protected Void call() throws Exception {
            document.readApplicationData();
            corruptExercises = document.checkExerciseFiles();
            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            showWaitCursor(false);

            updateView();
            // TODO view.registerViewForDataChanges();

            displayCorruptExercises();
            // TODO askForDefiningSportTypes();
        }

        @Override
        protected void failed() {
            super.failed();
            showWaitCursor(false);

            LOGGER.log(Level.SEVERE, "Failed to load application data!", getException());
            context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.ERROR, "common.error",
                    "st.main.error.load_data");
        }

        private void displayCorruptExercises() {
            if (corruptExercises != null && !corruptExercises.isEmpty()) {

                StringBuilder sb = new StringBuilder();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

                for (int i = 0; i < corruptExercises.size(); i++) {
                    if (i > 15) {
                        sb.append("...\n");
                        break;
                    }

                    sb.append(corruptExercises.get(i).getDateTime().format(dateTimeFormatter));
                    sb.append("\n");
                }

                context.showMessageDialog(context.getPrimaryStage(), Alert.AlertType.WARNING, "common.warning",
                        "st.main.error.missing_exercise_files", sb.toString());
            }
        }
    }
}
