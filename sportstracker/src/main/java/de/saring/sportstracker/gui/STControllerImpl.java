package de.saring.sportstracker.gui;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import de.saring.exerciseviewer.gui.EVMain;
import de.saring.sportstracker.gui.dialogs.AboutDialogController;
import javafx.event.ActionEvent;
import javafx.stage.WindowEvent;

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
    private Provider<AboutDialogController> prAboutDialogController;

    // @Inject
    // private Provider<ExerciseDialogController> prExerciseDialogController;
    // @Inject
    // private Provider<NoteDialogController> prNoteDialogController;
    // @Inject
    // private Provider<WeightDialogController> prWeightDialogController;
    // @Inject
    // private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
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

    @Inject
    private Provider<EVMain> prExerciseViewer;

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
    public void onWindowCloseRequest(final WindowEvent event) {
        event.consume();
        // TODO

        LOGGER.info("Exiting application");
        System.exit(0);
    }

    @Override
    public void onOpenHrmFile(ActionEvent event) {
        // TODO
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
        // TODO
    }
}
