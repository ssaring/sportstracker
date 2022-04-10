package de.saring.sportstracker.gui.dialogs;

import de.saring.exerciseviewer.gui.EVMain;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;

/**
 * Class for grouping all the Dialog controller providers. The goal is to reduce the dependency injection
 * complexity in the controller classes which need the dialog providers.
 *
 * @author Stefan Saring
 */
@Singleton
public class DialogProvider {

    /** Provider for the HRMFileOpenDialog */
    public Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
    
    /** Provider for the ExerciseViewer */
    public Provider<EVMain> prExerciseViewer;
    
    /** Provider for the ExerciseDialogController */
    public Provider<ExerciseDialogController> prExerciseDialogController;
    
    /** Provider for the NoteDialogController */
    public Provider<NoteDialogController> prNoteDialogController;
    
    /** Provider for the WeightDialogController */
    public Provider<WeightDialogController> prWeightDialogController;
    
    /** Provider for the SportTypeListDialogController */
    public Provider<SportTypeListDialogController> prSportTypeListDialogController;
    
    /** Provider for the StatisticDialogController */
    public Provider<StatisticDialogController> prStatisticDialogController;

    /** Provider for the OverviewDialogController */
    public Provider<OverviewDialogController> prOverviewDialogController;

    /** Provider for the EquipmentUsageDialogController */
    public Provider<EquipmentUsageDialogController> prEquipmentUsageDialogController;

    /** Provider for the PreferencesDialogController */
    public Provider<PreferencesDialogController> prPreferencesDialogController;
    
    /** Provider for the FilterDialogController */
    public Provider<FilterDialogController> prFilterDialogController;
    
    /** Provider for the AboutDialogController */
    public Provider<AboutDialogController> prAboutDialogController;

    /**
     * C'tor for dependency injection.
     *
     * @param prHRMFileOpenDialog provider for the HRMFileOpenDialog
     * @param prExerciseViewer provider for the ExerciseViewer
     * @param prExerciseDialogController provider for the ExerciseDialogController
     * @param prNoteDialogController provider for the NoteDialogController
     * @param prWeightDialogController provider for the WeightDialogController
     * @param prSportTypeListDialogController provider for the SportTypeListDialogController
     * @param prStatisticDialogController provider for the StatisticDialogController
     * @param prOverviewDialogController provider for the OverviewDialogController
     * @param prEquipmentUsageDialogController provider for the EquipmentUsageDialogController
     * @param prPreferencesDialogController provider for the PreferencesDialogController
     * @param prFilterDialogController provider for the FilterDialogController
     * @param prAboutDialogController provider for the AboutDialogController
     */
    @Inject
    public DialogProvider(Provider<HRMFileOpenDialog> prHRMFileOpenDialog,
                          Provider<EVMain> prExerciseViewer,
                          Provider<ExerciseDialogController> prExerciseDialogController,
                          Provider<NoteDialogController> prNoteDialogController,
                          Provider<WeightDialogController> prWeightDialogController,
                          Provider<SportTypeListDialogController> prSportTypeListDialogController,
                          Provider<StatisticDialogController> prStatisticDialogController,
                          Provider<OverviewDialogController> prOverviewDialogController,
                          Provider<EquipmentUsageDialogController> prEquipmentUsageDialogController,
                          Provider<PreferencesDialogController> prPreferencesDialogController,
                          Provider<FilterDialogController> prFilterDialogController,
                          Provider<AboutDialogController> prAboutDialogController) {
        this.prHRMFileOpenDialog = prHRMFileOpenDialog;
        this.prExerciseViewer = prExerciseViewer;
        this.prExerciseDialogController = prExerciseDialogController;
        this.prNoteDialogController = prNoteDialogController;
        this.prWeightDialogController = prWeightDialogController;
        this.prSportTypeListDialogController = prSportTypeListDialogController;
        this.prStatisticDialogController = prStatisticDialogController;
        this.prOverviewDialogController = prOverviewDialogController;
        this.prEquipmentUsageDialogController = prEquipmentUsageDialogController;
        this.prPreferencesDialogController = prPreferencesDialogController;
        this.prFilterDialogController = prFilterDialogController;
        this.prAboutDialogController = prAboutDialogController;
    }
}
