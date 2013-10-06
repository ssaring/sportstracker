package de.saring.sportstracker.gui.dialogs;

import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.gui.EVMain;
import de.saring.exerciseviewer.parser.ExerciseParser;
import de.saring.exerciseviewer.parser.ExerciseParserFactory;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.GuiCreateUtils;
import de.saring.util.unitcalc.CalculationUtils;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;
import org.jdesktop.application.Action;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the dialog for editing / adding exercises.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class ExerciseDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(ExerciseDialog.class.getName());

    private STContext context;
    private STDocument document;

    @Inject
    private Provider<HRMFileOpenDialog> prHRMFileOpenDialog;
    @Inject
    private Provider<EVMain> prExerciseViewer;

    /**
     * This is the exercises object edited in this dialog.
     */
    private Exercise exercise;

    private FormatUtils formatUtils;

    /**
     * Index of the previous similar exercise in the exercise list from which
     * the last comment has been copied. It's null when it was not used yet.
     */
    private Integer previousExerciseIndex = null;

    /**
     * This flag is true when a sport type is selected for which the distance
     * must be entered or when no sport type is selected.
     */
    private boolean distanceActive = true;

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_AUTO_CALC_DISTANCE = "st.dlg.exercise.auto_calc_distance";
    private static final String ACTION_AUTO_CALC_AVG_SPEED = "st.dlg.exercise.auto_calc_avg_speed";
    private static final String ACTION_AUTO_CALC_DURATION = "st.dlg.exercise.auto_calc_duration";
    private static final String ACTION_HRM_BROWSE = "st.dlg.exercise.hrm_browse";
    private static final String ACTION_HRM_VIEW = "st.dlg.exercise.hrm_view";
    private static final String ACTION_HRM_IMPORT = "st.dlg.exercise.hrm_import";
    private static final String ACTION_COPY_COMMENT = "st.dlg.exercise.copy_comment";
    private static final String ACTION_OK = "st.dlg.exercise.ok";
    private static final String ACTION_CANCEL = "st.dlg.exercise.cancel";

    /**
     * Constants for the tab indices of the tabbed pane content.
     */
    private static final int TABINDEX_MAIN = 0;
    private static final int TABINDEX_OPTIONAL = 1;
    private static final int TABINDEX_COMMENT = 2;


    /**
     * Standard c'tor. The method setExercise() needs to be called before
     * showing the dialog.
     *
     * @param context the SportsTracker context
     * @param document the application document component
     */
    @Inject
    public ExerciseDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        formatUtils = context.getFormatUtils();

        initComponents();
        setLocationRelativeTo(getParent());
        setTextTranslations();
        this.getRootPane().setDefaultButton(btOK);
        // use same font in textarea as in textfield (not default on Win32)
        taComment.setFont(tfDistance.getFont());

        dpDate.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));
        // don't show the link panel in date pickers ("today is ...")
        dpDate.setLinkPanel(null);

        GuiCreateUtils.addMouseWheelSupportToSpinner(spHour);
        GuiCreateUtils.addMouseWheelSupportToSpinner(spMinute);

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        cbAutoCalcDistance.setAction(actionMap.get(ACTION_AUTO_CALC_DISTANCE));
        cbAutoCalcAvgSpeed.setAction(actionMap.get(ACTION_AUTO_CALC_AVG_SPEED));
        cbAutoCalcDuration.setAction(actionMap.get(ACTION_AUTO_CALC_DURATION));
        btHRMBrowse.setAction(actionMap.get(ACTION_HRM_BROWSE));
        btHRMView.setAction(actionMap.get(ACTION_HRM_VIEW));
        btHRMImport.setAction(actionMap.get(ACTION_HRM_IMPORT));
        btCopyComment.setAction(actionMap.get(ACTION_COPY_COMMENT));
        btOK.setAction(actionMap.get(ACTION_OK));

        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);

        // fill the comoboxes with all sport types and intensity types
        cbSportType.removeAllItems();
        for (SportType sportType : document.getSportTypeList()) {
            cbSportType.addItem(sportType.getName());
        }
        cbSportType.setSelectedIndex(-1);

        cbIntensity.removeAllItems();
        for (Exercise.IntensityType intensity : Exercise.IntensityType.values()) {
            cbIntensity.addItem(intensity);
        }
    }

    /**
     * Initializes the ExerciseDialog with the specified exercise.
     *
     * @param exercise the Exercise object to be edited
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        setInitialValues();
        setupListeners();
    }

    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {
        // display "Add New ..." title when it's a new exercise
        if (document.getExerciseList().getByID(exercise.getId()) == null) {
            super.setTitle(context.getResReader().getString("st.dlg.exercise.title.add"));
        } else {
            super.setTitle(title);
        }
    }

    /**
     * Sets the text translations for all widgets where it's not done automatically.
     */
    private void setTextTranslations() {

        // set tab titles (i18n is not done by the Application Framework automatically)
        tabbedPane.setTitleAt(TABINDEX_MAIN, context.getResReader().getString("st.dlg.exercise.main.title"));
        tabbedPane.setTitleAt(TABINDEX_OPTIONAL, context.getResReader().getString("st.dlg.exercise.optional.title"));
        tabbedPane.setTitleAt(TABINDEX_COMMENT, context.getResReader().getString("st.dlg.exercise.comment.title"));

        // append unit names for current unit system to the label texts
        laDistance.setText(context.getResReader().getString("st.dlg.exercise.distance.text", formatUtils.getDistanceUnitName()));
        laAvgSpeed.setText(context.getResReader().getString("st.dlg.exercise.avg_speed.text", formatUtils.getSpeedUnitName()));
        laAscent.setText(context.getResReader().getString("st.dlg.exercise.ascent.text", formatUtils.getAltitudeUnitName()));
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues() {

        // set date (formating is done by the textfield) and time
        dpDate.setDate(exercise.getDate());

        Calendar calTemp = Calendar.getInstance();
        calTemp.setTime(exercise.getDate());
        spHour.setValue(calTemp.get(Calendar.HOUR_OF_DAY));
        spMinute.setValue(calTemp.get(Calendar.MINUTE));

        // select the exercise sport type in the combobox (same order as in sport type list)
        // => if there is only one select it as default
        int sportTypeIndex = document.getSportTypeList().indexOf(exercise.getSportType());
        cbSportType.setSelectedIndex(cbSportType.getItemCount() == 1 ? 0 : sportTypeIndex);

        // force the refill of sport subtype option menu
        updateForSelectedSportType();

        // select the sport subtype of this exercise in the combobox (same order as in sport type)
        // => if there is only one select it as default
        int subTypeIndex = -1;
        if (exercise.getSportSubType() != null) {
            subTypeIndex = exercise.getSportType().getSportSubTypeList().indexOf(exercise.getSportSubType());
        }
        cbSportSubType.setSelectedIndex(cbSportSubType.getItemCount() == 1 ? 0 : subTypeIndex);

        // select intensity or use 'normal' for new exercises
        if (exercise.getIntensity() != null) {
            cbIntensity.setSelectedItem(exercise.getIntensity());
        } else {
            cbIntensity.setSelectedItem(Exercise.IntensityType.NORMAL);
        }

        // set distance, AVG speed and duration values
        tfDistance.setText(formatUtils.distanceToStringWithoutUnitName(exercise.getDistance(), 3));
        tfAvgSpeed.setText(formatUtils.speedToStringWithoutUnitName(exercise.getAvgSpeed(), 3));
        tfDuration.setText(formatUtils.seconds2TimeString(exercise.getDuration()));

        // set current automatic calculation type, disable the appropriate textfield
        switch (document.getOptions().getDefaultAutoCalcuation()) {
            case Distance:
                cbAutoCalcDistance.setSelected(true);
                break;
            case AvgSpeed:
                cbAutoCalcAvgSpeed.setSelected(true);
                break;
            default:
                cbAutoCalcDuration.setSelected(true);
        }
        updateInputs();

        // select the equipment of this exercise in the combobox (same order as in sport type)
        // => is optional, select the default "none" item when there is no equipment 
        int equipmentIndex = 0;
        if (exercise.getEquipment() != null) {
            equipmentIndex = exercise.getSportType().getEquipmentList().indexOf(
                    exercise.getEquipment()) + 1;
        }
        cbEquipment.setSelectedIndex(equipmentIndex);

        // set all the optional values
        if (exercise.getAscent() > 0) {
            tfAscent.setText(formatUtils.heightToStringWithoutUnitName(exercise.getAscent()));
        }

        NumberFormat nf = NumberFormat.getIntegerInstance();
        if (exercise.getAvgHeartRate() > 0) {
            tfAvgHeartrate.setText(nf.format(exercise.getAvgHeartRate()));
        }

        if (exercise.getCalories() > 0) {
            tfCalories.setText(nf.format(exercise.getCalories()));
        }

        tfHRMFile.setText(exercise.getHrmFile());

        taComment.setText(exercise.getComment());
        taComment.setCaretPosition(0);
    }

    /**
     * Setup of the dialog listeners.
     */
    private void setupListeners() {

        // Add listener for sport subtype updates when sport type was selected.
        // Also delete the previous exercise index (for copying the comment) on
        // sport type and subtype selection changes.
        cbSportType.addActionListener(event -> {
            updateForSelectedSportType();
            previousExerciseIndex = null;
        });
        cbSportSubType.addActionListener(event -> previousExerciseIndex = null);

        // add focus lost listener for changes on inputs for distance, avg speed
        // and duration => the automatic value must be calculated then        
        FocusAdapter focusListenerAutoCalc = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculateAutomaticValue();
            }
        };
        tfDistance.addFocusListener(focusListenerAutoCalc);
        tfAvgSpeed.addFocusListener(focusListenerAutoCalc);
        tfDuration.addFocusListener(focusListenerAutoCalc);

        // add focus listener for hour and minute spinners, it needs to select
        // the whole spinner text when focus is gained (due to some internal
        // JSpinner problems we need to use invokeLater here :-( )
        FocusAdapter focusListenerSelectAll = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                final JTextField tf = (JTextField) e.getSource();
                SwingUtilities.invokeLater(tf::selectAll);
            }
        };
        ((JSpinner.DefaultEditor) spHour.getEditor()).getTextField().addFocusListener(focusListenerSelectAll);
        ((JSpinner.DefaultEditor) spMinute.getEditor()).getTextField().addFocusListener(focusListenerSelectAll);
    }

    /**
     * Updates the sport subtype and equipment list and the distance-related
     * inputs depending on the current selected sport type.
     */
    private void updateForSelectedSportType() {

        // subtype and equipment comboboxes are only enabled when a sport type is selected
        int selSportTypeIndex = cbSportType.getSelectedIndex();
        cbSportSubType.setEnabled(selSportTypeIndex >= 0);
        cbSportSubType.removeAllItems();
        cbEquipment.setEnabled(selSportTypeIndex >= 0);
        cbEquipment.removeAllItems();

        // make sure that an sport type is selected
        if (selSportTypeIndex < 0) {
            // workaround: add one dummy item, otherwise the height is wrong
            cbSportSubType.addItem(" ");
            cbEquipment.addItem(" ");
            return;
        }

        // append all sport subtypes of the selected sport type
        SportType selSportType = document.getSportTypeList().getAt(selSportTypeIndex);
        for (SportSubType sportSubType : selSportType.getSportSubTypeList()) {
            cbSportSubType.addItem(sportSubType.getName());
        }

        // append default "none (equipment)" item and all eqipment's of the selected sport type
        cbEquipment.addItem(context.getResReader().getString("st.dlg.exercise.equipment.none.text"));
        for (Equipment equipment : selSportType.getEquipmentList()) {
            cbEquipment.addItem(equipment.getName());
        }
        cbEquipment.setSelectedIndex(0);

        // reset sport subtype selection
        // => if there is only one subtype use this as default
        cbSportSubType.setSelectedIndex(cbSportSubType.getItemCount() == 1 ? 0 : -1);

        // store whether the distance muste be recorded for the current sport type
        // and update these inputs
        distanceActive = selSportType.isRecordDistance();
        updateInputs();
    }

    /**
     * Action for automatic calculation of the distance.
     */
    @Action(name = ACTION_AUTO_CALC_DISTANCE)
    public void autoCalculdateDistance() {
        updateInputs();
    }

    /**
     * Action for automatic calculation of the average speed.
     */
    @Action(name = ACTION_AUTO_CALC_AVG_SPEED)
    public void autoCalculdateAvgSpeed() {
        updateInputs();
    }

    /**
     * Action for automatic calculation of the duration.
     */
    @Action(name = ACTION_AUTO_CALC_DURATION)
    public void autoCalculdateDuration() {
        updateInputs();
    }

    /**
     * Action for browsing the HRM file.
     */
    @Action(name = ACTION_HRM_BROWSE)
    public void browseHRMFile() {

        // do we have an initial HRM file for the dialog?
        File initialFile = null;
        String strHRMFile = tfHRMFile.getText().trim();
        if (strHRMFile.length() > 0) {
            initialFile = new File(strHRMFile);
        }

        // show file open dialog and display selected filename
        File selectedFile = prHRMFileOpenDialog.get().selectHRMFile(document.getOptions(), initialFile);
        if (selectedFile != null) {
            tfHRMFile.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Action for viewing the HRM file.
     */
    @Action(name = ACTION_HRM_VIEW)
    public void viewHRMFile() {

        // start ExerciseViewer sub-application when the filename is available
        String hrmFile = getHRMFile();
        if (hrmFile != null) {
            EVMain pv = prExerciseViewer.get();
            pv.showExercise(hrmFile, document.getOptions(), true);
        }
    }

    /**
     * Action for importing the exercise data from the HRM file.
     */
    @Action(name = ACTION_HRM_IMPORT)
    public void importHRMFile() {

        // get required HRM filename
        String hrmFile = getHRMFile();
        if (hrmFile == null) {
            return;
        }

        // parse exercise file
        EVExercise pvExercise = null;
        try {
            ExerciseParser parser = ExerciseParserFactory.getParser(hrmFile);
            pvExercise = parser.parseExercise(hrmFile);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse exercise file!", e);
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.import_console", hrmFile);
            return;
        }

        // fill dialog widgets with values from parsed HRM exercise
        if (pvExercise.getDate() != null) {
            dpDate.setDate(pvExercise.getDate());
            Calendar calTemp = Calendar.getInstance();
            calTemp.setTime(pvExercise.getDate());
            spHour.setValue(calTemp.get(Calendar.HOUR_OF_DAY));
            spMinute.setValue(calTemp.get(Calendar.MINUTE));
        }

        tfDuration.setText(formatUtils.seconds2TimeString(pvExercise.getDuration() / 10));
        tfAvgHeartrate.setText(String.valueOf(pvExercise.getHeartRateAVG()));
        tfCalories.setText(String.valueOf(pvExercise.getEnergy()));

        // fill speed-related values
        if (pvExercise.getSpeed() != null) {

            tfAvgSpeed.setText(formatUtils.speedToStringWithoutUnitName(
                    pvExercise.getSpeed().getSpeedAVG(), 3));
            tfDistance.setText(formatUtils.distanceToStringWithoutUnitName(
                    pvExercise.getSpeed().getDistance() / 1000f, 3));

            // imported distance, AVG and duration are often not in correct relation
            // => calculate the selected value automatically
            calculateAutomaticValue();
        }

        // fill ascent-related values
        if (pvExercise.getAltitude() != null) {
            tfAscent.setText(formatUtils.heightToStringWithoutUnitName(pvExercise.getAltitude().getAscent()));
        }
    }

    /**
     * Action for copying the comment from the previous similar exercise.
     */
    @Action(name = ACTION_COPY_COMMENT)
    public void copyComment() {

        // get selected sport type and subtype (must be selected)
        if ((cbSportType.getSelectedIndex() == -1) ||
                (cbSportSubType.getSelectedIndex() == -1)) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.no_sport_and_subtype");
            return;
        }

        SportType selSportType =
                document.getSportTypeList().getAt(cbSportType.getSelectedIndex());
        SportSubType selSportSubType =
                selSportType.getSportSubTypeList().getAt(cbSportSubType.getSelectedIndex());

        // find the start index for searching the previous exercise when not done yet
        // => get index of current exercise or use the last index when not found
        if (previousExerciseIndex == null) {
            int indexCurrentExercise =
                    document.getExerciseList().indexOf(exercise);
            if (indexCurrentExercise == -1) {
                indexCurrentExercise = document.getExerciseList().size();
            }
            previousExerciseIndex = indexCurrentExercise;
        }

        // check all exercises before the previous exercise
        int currentSearchIndex = previousExerciseIndex - 1;
        while (currentSearchIndex >= 0) {

            Exercise tempExercise =
                    document.getExerciseList().getAt(currentSearchIndex);

            // has the current checked exercise the same sport type and subtype?
            if ((tempExercise.getSportType().getId() == selSportType.getId()) &&
                    (tempExercise.getSportSubType().getId() == selSportSubType.getId())) {

                // the current checked exercise is similar => copy comment when there is one
                if (tempExercise.getComment() != null && tempExercise.getComment().length() > 0) {
                    taComment.setText(tempExercise.getComment());
                    previousExerciseIndex = currentSearchIndex;
                    return;
                }
            }
            currentSearchIndex--;
        }

        // nothing found => delete previous index, so searching will start from the end again
        previousExerciseIndex = null;
        context.showMessageDialog(this, JOptionPane.INFORMATION_MESSAGE,
                "common.info", "st.dlg.exercise.error.no_previous_exercise");
    }

    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name = ACTION_OK)
    public void ok() {

        // create a new Exercise, because user can cancel after validation errors
        // => so we don't modify the original exercise
        Exercise newExercise = new Exercise(exercise.getId());

        // check date input
        if (dpDate.getDate() == null) {
            tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.date");
            dpDate.requestFocus();
            return;
        }

        // store date and time of exercise
        Calendar calTemp = Calendar.getInstance();
        calTemp.setTime(dpDate.getDate());
        calTemp.set(Calendar.HOUR_OF_DAY, (Integer) spHour.getValue());
        calTemp.set(Calendar.MINUTE, (Integer) spMinute.getValue());
        calTemp.set(Calendar.SECOND, 0);
        newExercise.setDate(calTemp.getTime());

        // check and get sport type
        int selSportTypeIndex = cbSportType.getSelectedIndex();
        if (selSportTypeIndex < 0) {
            tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.no_sport_type");
            cbSportType.requestFocus();
            return;
        }

        newExercise.setSportType(
                document.getSportTypeList().getAt(selSportTypeIndex));

        // do we need to record distance data for this sport type ?
        boolean recordDistance = newExercise.getSportType().isRecordDistance();

        // check and get sport subtype
        int selSportSubTypeIndex = cbSportSubType.getSelectedIndex();
        if (selSportSubTypeIndex < 0) {
            tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.no_sport_subtype");
            cbSportSubType.requestFocus();
            return;
        }

        newExercise.setSportSubType(
                newExercise.getSportType().getSportSubTypeList().getAt(selSportSubTypeIndex));


        // check and get intensity
        if (cbIntensity.getSelectedIndex() < 0) {
            tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.no_intensity");
            cbIntensity.requestFocus();
            return;
        }
        newExercise.setIntensity((Exercise.IntensityType) cbIntensity.getSelectedItem());

        // get distance, AVG speed and duration
        try {
            // get distance and AVG only when these are recorded for the current sport type
            if (recordDistance) {
                newExercise.setDistance(getDistanceEntry(true));
                newExercise.setAvgSpeed(getAvgSpeedEntry(true));
            } else {
                newExercise.setDistance(0);
                newExercise.setAvgSpeed(0);
            }

            newExercise.setDuration(getDurationEntry(true));
        } catch (STException se) {
            // nothing to do anymore :-)
            return;
        }

        // Only when distance is recorded for this sport type => check the correct relation
        // of distance, AVG speed and duration. Tolerate a difference of 60 seconds, because
        // the values in the dialog are rounded. This difference is even bigger when speed
        // is entered in minutes/distance.
        // Actually this check is not needed anymore, because the relation is computed
        // automatically when entering it into the dialog - so it's just an additonal
        // protection.
        if (recordDistance) {
            int durationCheck = CalculationUtils.calculateDuration(newExercise.getDistance(), newExercise.getAvgSpeed());
            if ((durationCheck < newExercise.getDuration() - 60) || (durationCheck > newExercise.getDuration() + 60)) {
                tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.exercise.error.wrong_relation");
                return;
            }
        }

        // get ascent (optional)
        Integer iAscent = getOptionalIntValueFromTextField(tfAscent, "st.dlg.exercise.error.ascent");
        if (iAscent == null) {
            return;
        }
        newExercise.setAscent(iAscent);

        // get AVG heartrate (optional)
        Integer iAvgHeartrate = getOptionalIntValueFromTextField(tfAvgHeartrate, "st.dlg.exercise.error.avg_heartrate");
        if (iAvgHeartrate == null) {
            return;
        }
        newExercise.setAvgHeartRate(iAvgHeartrate);

        // get calorie consumption (optional)
        Integer iCalories = getOptionalIntValueFromTextField(tfCalories, "st.dlg.exercise.error.calories");
        if (iCalories == null) {
            return;
        }
        newExercise.setCalories(iCalories);

        // get HRM filename (optional)
        String strHRMFile = tfHRMFile.getText().trim();
        if (strHRMFile.length() > 0) {
            newExercise.setHrmFile(strHRMFile);
        }

        // get equipment (optional, first item is "none")
        int selEquipmentIndex = cbEquipment.getSelectedIndex() - 1;
        if (selEquipmentIndex >= 0) {
            newExercise.setEquipment(
                    newExercise.getSportType().getEquipmentList().getAt(selEquipmentIndex));
        }

        // get comment (optional)
        String strComment = taComment.getText().trim();
        if (strComment.length() > 0) {
            newExercise.setComment(strComment);
        }

        // convert to metric units when english units were entered
        if (document.getOptions().getUnitSystem() == FormatUtils.UnitSystem.English) {
            newExercise.setDistance((float) ConvertUtils.convertMiles2Kilometer(newExercise.getDistance()));
            newExercise.setAvgSpeed((float) ConvertUtils.convertMiles2Kilometer(newExercise.getAvgSpeed()));
            newExercise.setAscent(ConvertUtils.convertFeet2Meter(newExercise.getAscent()));
        }

        // finally store the new exercise and close dialog
        document.getExerciseList().set(newExercise);
        this.dispose();
    }

    /**
     * Action for closing the dialog with the Cancel button.
     */
    @Action(name = ACTION_CANCEL)
    public void cancel() {
        this.dispose();
    }

    /**
     * Updates the inputs for distance, avg speed and duration for the current
     * automatic calculation selection. When the current selected sport type
     * does not stores distance values then the distance and avg speed inputs
     * will be disabled and set to 0.
     */
    private void updateInputs() {
        cbAutoCalcDistance.setEnabled(distanceActive);
        cbAutoCalcAvgSpeed.setEnabled(distanceActive);
        cbAutoCalcDuration.setEnabled(distanceActive);

        if (!distanceActive) {
            tfDistance.setText("0");
            tfAvgSpeed.setText("0");
            tfDistance.setEnabled(false);
            tfAvgSpeed.setEnabled(false);
            tfDuration.setEnabled(true);
        } else {
            tfDistance.setEnabled(!cbAutoCalcDistance.isSelected());
            tfAvgSpeed.setEnabled(!cbAutoCalcAvgSpeed.isSelected());
            tfDuration.setEnabled(!cbAutoCalcDuration.isSelected());
        }
    }

    /**
     * Calculates the automatic value (distance, avg speed or duration) depending
     * on the current automatic calculation setting. There's no calculation when
     * the distance is not recorded for the current sport type.
     */
    private void calculateAutomaticValue() {
        if (distanceActive) {
            if (cbAutoCalcDistance.isSelected()) {
                autoCalculateDistance();
            } else if (cbAutoCalcAvgSpeed.isSelected()) {
                autoCalculateAvgSpeed();
            } else {
                autoCalculateDuration();
            }
        }
    }

    /**
     * Automatic calculation of the distance based on the other values.
     */
    private void autoCalculateDistance() {
        float avgSpeed;
        int duration;

        // get AVG speed and duration
        try {
            avgSpeed = getAvgSpeedEntry(false);
            duration = getDurationEntry(false);
        } catch (STException se) {
            return;
        }

        float distance = CalculationUtils.calculateDistance(avgSpeed, duration);
        // convert distance to metric unit when currently in english unit mode
        if (document.getOptions().getUnitSystem() == FormatUtils.UnitSystem.English) {
            distance = (float) ConvertUtils.convertMiles2Kilometer(distance);
        }
        tfDistance.setText(formatUtils.distanceToStringWithoutUnitName(distance, 3));
    }

    /**
     * Automatic calculation of the avg speed based on the other values.
     */
    private void autoCalculateAvgSpeed() {
        float distance;
        int duration;

        // get distance and duration
        try {
            distance = getDistanceEntry(false);
            duration = getDurationEntry(false);
        } catch (STException se) {
            return;
        }

        // convert distance to metric unit when currently in english unit mode
        if (document.getOptions().getUnitSystem() == FormatUtils.UnitSystem.English) {
            distance = (float) ConvertUtils.convertMiles2Kilometer(distance);
        }

        float avgSpeed = CalculationUtils.calculateAvgSpeed(distance, duration);
        tfAvgSpeed.setText(formatUtils.speedToStringWithoutUnitName(avgSpeed, 3));
    }

    /**
     * Automatic calculation of the duration based on the other values.
     */
    private void autoCalculateDuration() {
        float distance;
        float avgSpeed;

        // get distance and AVG speed 
        try {
            distance = getDistanceEntry(false);
            avgSpeed = getAvgSpeedEntry(false);
        } catch (STException se) {
            return;
        }

        int duration = CalculationUtils.calculateDuration(distance, avgSpeed);
        tfDuration.setText(formatUtils.seconds2TimeString(duration));
    }

    /**
     * This methods parses the distance entry and returns it. On parsing problems
     * the widget will be selected and focused, a message box will be displayed
     * and a STExcpetion will be thrown.
     *
     * @param displayError error message will not be displayed when false (but exception will be thrown)
     * @return the distance value
     * @throws STException on parsing problems
     */
    private float getDistanceEntry(boolean displayError) throws STException {

        try {
            float value = NumberFormat.getInstance().parse(tfDistance.getText()).floatValue();
            if (value <= 0) {
                throw new Exception("The value must be greater than 0...");
            }
            return value;
        } catch (Exception e) {
            if (displayError) {
                tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
                tfDistance.selectAll();
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.exercise.error.distance");
                tfDistance.requestFocus();
            }
            throw new STException(STExceptionID.GUI_EXERCISEDIALOG_INVALID_DISTANCE, "Failed to parse distance entry ...", e);
        }
    }

    /**
     * This methods parses the AVG speed entry and returns it. On parsing problems
     * the widget will be selected and focused, a message box will be displayed
     * and a STExcpetion will be thrown.
     *
     * @param displayError error message will not be displayed when false (but exception will be thrown)
     * @return the AVG speed value
     * @throws STException on parsing problems
     */
    private float getAvgSpeedEntry(boolean displayError) throws STException {

        try {
            if (document.getOptions().getSpeedView() == FormatUtils.SpeedView.DistancePerHour) {
                float value = NumberFormat.getInstance().parse(tfAvgSpeed.getText()).floatValue();
                if (value <= 0) {
                    throw new Exception("The value must be greater than 0...");
                }
                return value;
            } else {
                // SpeedView.MinutesPerDistance
                Date value = new SimpleDateFormat("mm:ss").parse(tfAvgSpeed.getText());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);
                return 3600 / (float) (minutes * 60 + seconds);
            }
        } catch (Exception e) {
            if (displayError) {
                tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
                tfAvgSpeed.selectAll();
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.exercise.error.avg_speed");
                tfAvgSpeed.requestFocus();
            }
            throw new STException(STExceptionID.GUI_EXERCISEDIALOG_INVALID_AVGSPEED, "Failed to parse average speed entry ...", e);
        }
    }

    /**
     * This methods parses the duration entry and returns it. On parsing problems
     * the widget will be selected and focused, a message box will be displayed
     * and a STExcpetion will be thrown.
     *
     * @param displayError error message will not be displayed when false (but exception will be thrown)
     * @return the duration value
     * @throws STException on parsing problems
     */
    private int getDurationEntry(boolean displayError) throws STException {

        int duration = formatUtils.timeString2TotalSeconds(tfDuration.getText());
        if (duration <= 0) {
            if (displayError) {
                tabbedPane.getModel().setSelectedIndex(TABINDEX_MAIN);
                tfDuration.selectAll();
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.exercise.error.duration");
                tfDuration.requestFocus();
            }
            throw new STException(STExceptionID.GUI_EXERCISEDIALOG_INVALID_DURATION, "Failed to parse duration entry ...");
        }
        return duration;
    }

    /**
     * Returns the integer value of the specified textfield. It returns 0 when
     * the textfield is empty. When there's an invalid value it displayes the
     * specified error message, selects the textfield and returns null.
     *
     * @param textField textfield to get the value from
     * @param errorKey error message key for invalid values
     * @return the integer value (0 for no value) or null for invalid values
     */
    private Integer getOptionalIntValueFromTextField(JTextField textField, String errorKey) {

        String strValue = textField.getText().trim();
        if (strValue.length() > 0) {
            try {
                return NumberFormat.getIntegerInstance().parse(strValue).intValue();
            } catch (Exception e) {
                tabbedPane.getModel().setSelectedIndex(TABINDEX_OPTIONAL);
                textField.selectAll();
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE, "common.error", errorKey);
                textField.requestFocus();
                return null;
            }
        }
        return 0;
    }

    /**
     * Returns the HRM filename specified in the textfield or displays an error
     * message when there's no value and returns null.
     *
     * @return the HRM filename or null when not specified
     */
    private String getHRMFile() {

        String hrmFile = tfHRMFile.getText().trim();
        if (hrmFile.length() == 0) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.exercise.error.no_hrm_file");
            return null;
        }
        return hrmFile;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgAutoCalculation = new javax.swing.ButtonGroup();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        pExerciseData = new javax.swing.JPanel();
        laDate = new javax.swing.JLabel();
        dpDate = de.saring.util.gui.GuiCreateUtils.createDatePicker();
        laTime = new javax.swing.JLabel();
        spHour = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        spMinute = new javax.swing.JSpinner();
        laDistance = new javax.swing.JLabel();
        tfDistance = new javax.swing.JTextField();
        laAvgSpeed = new javax.swing.JLabel();
        tfAvgSpeed = new javax.swing.JTextField();
        laDuration = new javax.swing.JLabel();
        tfDuration = new javax.swing.JTextField();
        cbAutoCalcDistance = new javax.swing.JRadioButton();
        cbAutoCalcAvgSpeed = new javax.swing.JRadioButton();
        cbAutoCalcDuration = new javax.swing.JRadioButton();
        laSportType = new javax.swing.JLabel();
        cbSportType = new javax.swing.JComboBox<>();
        laSportSubType = new javax.swing.JLabel();
        cbSportSubType = new javax.swing.JComboBox<>();
        laIntensity = new javax.swing.JLabel();
        cbIntensity = new javax.swing.JComboBox<>();
        pOptionalData = new javax.swing.JPanel();
        laHRMFile = new javax.swing.JLabel();
        tfHRMFile = new javax.swing.JTextField();
        btHRMBrowse = new javax.swing.JButton();
        btHRMView = new javax.swing.JButton();
        btHRMImport = new javax.swing.JButton();
        laEquipment = new javax.swing.JLabel();
        cbEquipment = new javax.swing.JComboBox<>();
        laAscent = new javax.swing.JLabel();
        tfAscent = new javax.swing.JTextField();
        laAvgHeartrate = new javax.swing.JLabel();
        tfAvgHeartrate = new javax.swing.JTextField();
        laCalories = new javax.swing.JLabel();
        tfCalories = new javax.swing.JTextField();
        pComment = new javax.swing.JPanel();
        spComment = new javax.swing.JScrollPane();
        taComment = new javax.swing.JTextArea();
        btCopyComment = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("st.dlg.exercise"); // NOI18N
        setResizable(false);

        btOK.setText("_OK");

        btCancel.setText("_Cancel");

        laDate.setText("_Date:");
        laDate.setName("st.dlg.exercise.date"); // NOI18N

        laTime.setText("_Time (hh:mm):");
        laTime.setName("st.dlg.exercise.time"); // NOI18N

        spHour.setModel(new javax.swing.SpinnerNumberModel(12, 0, 23, 1));

        jLabel2.setText(":");

        spMinute.setModel(new javax.swing.SpinnerNumberModel(0, 0, 59, 1));

        laDistance.setText("_Distance (km):");

        laAvgSpeed.setText("_Avg. speed (km/h):");

        laDuration.setText("_Duration (hh:mm:ss):");
        laDuration.setName("st.dlg.exercise.duration"); // NOI18N

        bgAutoCalculation.add(cbAutoCalcDistance);
        cbAutoCalcDistance.setText("_Calculate automatically");

        bgAutoCalculation.add(cbAutoCalcAvgSpeed);
        cbAutoCalcAvgSpeed.setText("_Calculate automatically");

        bgAutoCalculation.add(cbAutoCalcDuration);
        cbAutoCalcDuration.setText("_Calculate automatically");

        laSportType.setText("_Sport type:");
        laSportType.setName("st.dlg.exercise.sport_type"); // NOI18N

        laSportSubType.setText("_Sport subtype:");
        laSportSubType.setName("st.dlg.exercise.sport_subtype"); // NOI18N

        laIntensity.setText("_Intensity:");
        laIntensity.setName("st.dlg.exercise.intensity"); // NOI18N

        javax.swing.GroupLayout pExerciseDataLayout = new javax.swing.GroupLayout(pExerciseData);
        pExerciseData.setLayout(pExerciseDataLayout);
        pExerciseDataLayout.setHorizontalGroup(
                pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pExerciseDataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pExerciseDataLayout.createSequentialGroup()
                                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laSportType)
                                                        .addComponent(laSportSubType)
                                                        .addComponent(laIntensity))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cbSportType, 0, 391, Short.MAX_VALUE)
                                                        .addComponent(cbSportSubType, 0, 391, Short.MAX_VALUE)
                                                        .addComponent(cbIntensity, 0, 391, Short.MAX_VALUE)))
                                        .addGroup(pExerciseDataLayout.createSequentialGroup()
                                                .addComponent(laDate)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(dpDate, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(25, 25, 25)
                                                .addComponent(laTime)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(spHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(5, 5, 5)
                                                .addComponent(jLabel2)
                                                .addGap(5, 5, 5)
                                                .addComponent(spMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(pExerciseDataLayout.createSequentialGroup()
                                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laAvgSpeed)
                                                        .addComponent(laDuration)
                                                        .addComponent(laDistance))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(tfDistance, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                                        .addComponent(tfAvgSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                                                        .addComponent(tfDuration, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cbAutoCalcDistance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(cbAutoCalcAvgSpeed, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                                        .addComponent(cbAutoCalcDuration, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        pExerciseDataLayout.setVerticalGroup(
                pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pExerciseDataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(dpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(laTime)
                                        .addComponent(spHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(spMinute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(laDate))
                                .addGap(30, 30, 30)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laSportType)
                                        .addComponent(cbSportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laSportSubType)
                                        .addComponent(cbSportSubType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laIntensity)
                                        .addComponent(cbIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tfDistance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbAutoCalcDistance)
                                        .addComponent(laDistance))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laAvgSpeed)
                                        .addComponent(tfAvgSpeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbAutoCalcAvgSpeed))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pExerciseDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laDuration)
                                        .addComponent(tfDuration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbAutoCalcDuration))
                                .addContainerGap())
        );

        pExerciseDataLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[]{dpDate, spHour, spMinute});

        tabbedPane.addTab("_Exercise Data", pExerciseData);

        laHRMFile.setText("_HRM file:");
        laHRMFile.setName("st.dlg.exercise.hrm_file"); // NOI18N

        btHRMBrowse.setText("...");

        btHRMView.setText("_View HRM file");

        btHRMImport.setText("_Import from HRM file");

        laEquipment.setText("_Equipment:");
        laEquipment.setName("st.dlg.exercise.equipment"); // NOI18N

        laAscent.setText("_Ascent (m):");

        tfAscent.setColumns(10);

        laAvgHeartrate.setText("_Avg. heart rate (bpm):");
        laAvgHeartrate.setName("st.dlg.exercise.avg_heartrate"); // NOI18N

        tfAvgHeartrate.setColumns(10);

        laCalories.setText("_Calorie consumed (kCal):");
        laCalories.setName("st.dlg.exercise.calories"); // NOI18N

        tfCalories.setColumns(10);

        javax.swing.GroupLayout pOptionalDataLayout = new javax.swing.GroupLayout(pOptionalData);
        pOptionalData.setLayout(pOptionalDataLayout);
        pOptionalDataLayout.setHorizontalGroup(
                pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pOptionalDataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(pOptionalDataLayout.createSequentialGroup()
                                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laAscent)
                                                        .addComponent(laAvgHeartrate)
                                                        .addComponent(laCalories))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(tfCalories)
                                                        .addComponent(tfAvgHeartrate)
                                                        .addComponent(tfAscent)))
                                        .addGroup(pOptionalDataLayout.createSequentialGroup()
                                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laEquipment)
                                                        .addComponent(laHRMFile))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(pOptionalDataLayout.createSequentialGroup()
                                                                .addComponent(btHRMView)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btHRMImport))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pOptionalDataLayout.createSequentialGroup()
                                                                .addComponent(tfHRMFile, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btHRMBrowse))
                                                        .addComponent(cbEquipment, 0, 412, Short.MAX_VALUE))))
                                .addContainerGap())
        );

        pOptionalDataLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btHRMImport, btHRMView});

        pOptionalDataLayout.setVerticalGroup(
                pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pOptionalDataLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laAscent)
                                        .addComponent(tfAscent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laAvgHeartrate)
                                        .addComponent(tfAvgHeartrate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laCalories)
                                        .addComponent(tfCalories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laEquipment)
                                        .addComponent(cbEquipment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laHRMFile)
                                        .addComponent(btHRMBrowse)
                                        .addComponent(tfHRMFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pOptionalDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btHRMView)
                                        .addComponent(btHRMImport))
                                .addContainerGap())
        );

        tabbedPane.addTab("_Optional Data", pOptionalData);

        taComment.setColumns(20);
        taComment.setLineWrap(true);
        taComment.setRows(4);
        taComment.setWrapStyleWord(true);
        spComment.setViewportView(taComment);

        btCopyComment.setText("_Copy from previous similar Exercise");

        javax.swing.GroupLayout pCommentLayout = new javax.swing.GroupLayout(pComment);
        pComment.setLayout(pCommentLayout);
        pCommentLayout.setHorizontalGroup(
                pCommentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pCommentLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pCommentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spComment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                                        .addComponent(btCopyComment, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE))
                                .addContainerGap())
        );
        pCommentLayout.setVerticalGroup(
                pCommentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pCommentLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(spComment, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btCopyComment)
                                .addContainerGap())
        );

        tabbedPane.addTab("_Comment", pComment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btCancel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btOK)))
                                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btCancel, btOK});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btOK)
                                        .addComponent(btCancel))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgAutoCalculation;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btCopyComment;
    private javax.swing.JButton btHRMBrowse;
    private javax.swing.JButton btHRMImport;
    private javax.swing.JButton btHRMView;
    private javax.swing.JButton btOK;
    private javax.swing.JRadioButton cbAutoCalcAvgSpeed;
    private javax.swing.JRadioButton cbAutoCalcDistance;
    private javax.swing.JRadioButton cbAutoCalcDuration;
    private javax.swing.JComboBox<String> cbEquipment;
    private javax.swing.JComboBox<Exercise.IntensityType> cbIntensity;
    private javax.swing.JComboBox<String> cbSportSubType;
    private javax.swing.JComboBox<String> cbSportType;
    private org.jdesktop.swingx.JXDatePicker dpDate;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel laAscent;
    private javax.swing.JLabel laAvgHeartrate;
    private javax.swing.JLabel laAvgSpeed;
    private javax.swing.JLabel laCalories;
    private javax.swing.JLabel laDate;
    private javax.swing.JLabel laDistance;
    private javax.swing.JLabel laDuration;
    private javax.swing.JLabel laEquipment;
    private javax.swing.JLabel laHRMFile;
    private javax.swing.JLabel laIntensity;
    private javax.swing.JLabel laSportSubType;
    private javax.swing.JLabel laSportType;
    private javax.swing.JLabel laTime;
    private javax.swing.JPanel pComment;
    private javax.swing.JPanel pExerciseData;
    private javax.swing.JPanel pOptionalData;
    private javax.swing.JScrollPane spComment;
    private javax.swing.JSpinner spHour;
    private javax.swing.JSpinner spMinute;
    private javax.swing.JTextArea taComment;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField tfAscent;
    private javax.swing.JTextField tfAvgHeartrate;
    private javax.swing.JTextField tfAvgSpeed;
    private javax.swing.JTextField tfCalories;
    private javax.swing.JTextField tfDistance;
    private javax.swing.JTextField tfDuration;
    private javax.swing.JTextField tfHRMFile;
    // End of variables declaration//GEN-END:variables

}
