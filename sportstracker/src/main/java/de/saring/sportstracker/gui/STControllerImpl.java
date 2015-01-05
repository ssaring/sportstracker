package de.saring.sportstracker.gui;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.gui.dialogs.AboutDialogController;
import de.saring.sportstracker.gui.dialogs.HRMFileOpenDialog;
import de.saring.util.gui.javafx.FxmlLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
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
    private Provider<AboutDialogController> prAboutDialogController;

    // @Inject
    // private Provider<ExerciseDialogController> prExerciseDialogController;
    // @Inject
    // private Provider<NoteDialogController> prNoteDialogController;
    // @Inject
    // private Provider<WeightDialogController> prWeightDialogController;
    // @Inject
    // private Provider<SportTypeListDialogController> prSportTypeListDialogController;
    // @Inject
    // private Provider<OverviewDialogController> prOverviewDialogController;
    // @Inject
    // private Provider<FilterDialogController> prFilterDialogController;
    // @Inject
    // private Provider<StatisticDialogController> prStatisticDialogController;
    // @Inject
    // private Provider<PreferencesDialogController> prPreferencesDialogController;


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
        // TODO
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
        // TODO
    }

    @Override
    public void onStatistics(ActionEvent event) {
        // TODO
    }

    @Override
    public void onOverviewDiagram(ActionEvent event) {
        // TODO
    }

    @Override
    public void onAbout(ActionEvent event) {
        prAboutDialogController.get().show(context.getPrimaryStage());
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
}
