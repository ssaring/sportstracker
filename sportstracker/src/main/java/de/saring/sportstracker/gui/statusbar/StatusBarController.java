package de.saring.sportstracker.gui.statusbar;

import javafx.scene.control.Label;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.unitcalc.CalculationUtils;

/**
 * Controller (MVC)of the status bar of the SportsTracker application window.
 *
 * @author Stefan Saring
 */
@Singleton
public class StatusBarController {

    private final STContext context;
    private final STDocument document;

    private Label laStatusBar;

    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param document the document component
     */
    @Inject
    public StatusBarController(final STContext context, final STDocument document) {
        this.context = context;
        this.document = document;
    }

    /**
     * Sets the label of the status bar.
     *
     * @param laStatusBar label
     */
    public void setStatusBar(final Label laStatusBar) {
        this.laStatusBar = laStatusBar;
    }

    /**
     * Updates the content of the status bar depending on the current exercise selection.
     *
     * @param selectedExerciseIds array of currently selected exercise IDs (can be empty, not null)
     */
    public void updateStatusBar(final int[] selectedExerciseIds) {
        String statusText = "";

        // create status bar text only when exercises are selected
        if (selectedExerciseIds.length > 0) {

            float sumDistance = 0;
            float sumAvgSpeed = 0;
            int sumDuration = 0;

            // calculate summary distance, AVG speed and duration for all selected exercises
            for (int exerciseID : selectedExerciseIds) {
                final Exercise selExercise = document.getExerciseList().getByID(exerciseID);
                sumDistance += selExercise.getDistance();
                sumDuration += selExercise.getDuration();
                sumAvgSpeed = selExercise.getAvgSpeed();
            }

            if (selectedExerciseIds.length > 1) {
                sumAvgSpeed = CalculationUtils.calculateAvgSpeed(sumDistance, sumDuration);
            }

            // build status bar text
            final String strCount = String.valueOf(selectedExerciseIds.length);
            final String strDistance = context.getFormatUtils().distanceToString(sumDistance, 3);
            final String strAVGSpeed = context.getFormatUtils().speedToString(sumAvgSpeed, 3);
            final String strDuration = context.getFormatUtils().seconds2TimeString(sumDuration);
            statusText = context.getResources().getString("st.view.statusbar", //
                    strCount, strDistance, strAVGSpeed, strDuration);
        }

        laStatusBar.setText(statusText);
    }
}
