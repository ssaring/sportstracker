package de.saring.exerciseviewer.gui.panels;

import com.google.inject.Inject;
import de.saring.exerciseviewer.data.HeartRateLimit;
import de.saring.exerciseviewer.data.PVExercise;
import de.saring.exerciseviewer.gui.PVContext;
import de.saring.util.unitcalc.FormatUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javax.swing.ActionMap;
import org.jdesktop.application.Action;

/**
 * This class is the implementation of the "Main" panel, which displays all
 * the main exercise data.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class MainPanel extends BasePanel {
    
    private static final String ACTION_UPDATE_RANGE_TIMES = "pv.main.update_range_times";
    
    private DiagramPanel diagramPanel;    

    
    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     */
    @Inject
    public MainPanel (PVContext context) {
        super (context);
        initComponents ();
        
        // setup actions
        ActionMap actionMap = getContext ().getSAFContext ().getActionMap (getClass (), this);
        cbRange.setAction (actionMap.get (ACTION_UPDATE_RANGE_TIMES));
    }

    /** {@inheritDoc} */
    @Override
    public void displayExercise () {
        PVExercise exercise = getDocument ().getExercise ();
        FormatUtils formatUtils = getContext ().getFormatUtils ();

        // set default text for all value labels first
        laTypeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laUserValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laDateValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laDurationValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laEnergyValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        
        laAverageValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laMaximumValue.setText (getContext ().getResReader ().getString ("common.n_a_"));

        laTimeBelowValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTimeWithinValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTimeAboveValue.setText (getContext ().getResReader ().getString ("common.n_a_"));

        laAltitudeValue.setText (getContext ().getResReader ().getString ("common.disabled"));
        laSpeedValue.setText (getContext ().getResReader ().getString ("common.disabled"));
        laCadenceValue.setText (getContext ().getResReader ().getString ("common.disabled"));
        laPowerValue.setText (getContext ().getResReader ().getString ("common.disabled"));
        
        laTotalExerciseTimeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTotalRidingTimeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTotalEnergyValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laOdometerValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        
        // fill general data 
        laTypeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laUserValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laEnergyValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTotalExerciseTimeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTotalRidingTimeValue.setText (getContext ().getResReader ().getString ("common.n_a_"));
        laTotalEnergyValue.setText (getContext ().getResReader ().getString ("common.n_a_"));

        if (exercise.getFileType () != PVExercise.ExerciseFileType.HRM) {
            
            // fill type and user data
            laTypeValue.setText ("" + exercise.getType () + " (" + exercise.getTypeLabel () + ")");
            laUserValue.setText ("" + exercise.getUserID ());
            
            // fill energy data
            laEnergyValue.setText (formatUtils.caloriesToString (exercise.getEnergy ()));
            
            // fill statistics data
            laTotalExerciseTimeValue.setText (formatUtils.minutes2TimeString (exercise.getSumExerciseTime ()));
            laTotalEnergyValue.setText (formatUtils.caloriesToString (exercise.getEnergyTotal ()));
            
            // fill total riding time (available only in S710 exercises)
            if (exercise.getFileType () == PVExercise.ExerciseFileType.S710RAW) {
                laTotalRidingTimeValue.setText (formatUtils.minutes2TimeString (exercise.getSumRideTime ()));
            }
        }
        
        // fill time data
        DateFormat sdFormat = SimpleDateFormat.getDateTimeInstance (
            SimpleDateFormat.MEDIUM, SimpleDateFormat.MEDIUM);
        laDateValue.setText (sdFormat.format (exercise.getDate ()));
        laDurationValue.setText (formatUtils.tenthSeconds2TimeString (exercise.getDuration ()));
        
        // fill heartrate data
        laAverageValue.setText (formatUtils.heartRateToString (exercise.getHeartRateAVG ()));
        laMaximumValue.setText (formatUtils.heartRateToString (exercise.getHeartRateMax ()));
        
        // fill recording mode data
        laAltitudeValue.setText (boolean2EnabledString (exercise.getRecordingMode ().isAltitude ()));
        laSpeedValue.setText (boolean2EnabledString (exercise.getRecordingMode ().isSpeed ()));
        laCadenceValue.setText (boolean2EnabledString (exercise.getRecordingMode ().isCadence ()));
        laPowerValue.setText (boolean2EnabledString (exercise.getRecordingMode ().isPower ()));
        
        // fill odometer data (if available, e.g. not on Polar S410 or S610)
        if (exercise.getOdometer () != 0) {
            laOdometerValue.setText (formatUtils.distanceToString (exercise.getOdometer (), 2));
        }

        fillHeartRateRanges ();
    }
    
    /**
     * Sets the diagram panel of ExerciseViewer (needed for hertrage range updates).
     * @param diagramPanel the diagram panel
     */
    public void setDiagramPanel (DiagramPanel diagramPanel) {
        this.diagramPanel = diagramPanel;
    }
    
    /**
     * Converts the specified boolean value to string.
     * @param fEnabled boolean value to convert
     * @return "enabled" if fEnabled is true, "disabled" otherwise
     */
    private String boolean2EnabledString (boolean fEnabled) {
        if (fEnabled)
            return getContext ().getResReader ().getString ("common.enabled");
        else
            return getContext ().getResReader ().getString ("common.disabled");
    }
    
    /**
     * Fills the heartrate limit selection combobox with exercise values.
     */
    private void fillHeartRateRanges () 
    {        
        PVExercise exercise = getDocument ().getExercise ();
        
        // add all ranges (if there are some) to the combobox
        if ((exercise.getHeartRateLimits () != null) &&
            (exercise.getHeartRateLimits ().length > 0)) 
        {
            for (HeartRateLimit limit : exercise.getHeartRateLimits ()) {                
                String unitName = limit.isAbsoluteRange () ? "bpm" :"%";                
                cbRange.addItem  ("" + limit.getLowerHeartRate () + " - " 
                    + limit.getUpperHeartRate () + " " + unitName);
            }
            cbRange.setSelectedIndex (0);
            
            updateHeartRateRangeTimes ();
        } 
        else {
            cbRange.setEnabled (false);
        }
    }
        
    /**
     * Updates the heartrate time controls with the values of the current 
     * selected range.
     */
    @Action(name=ACTION_UPDATE_RANGE_TIMES)
    public void updateHeartRateRangeTimes ()
    {
        int index = cbRange.getSelectedIndex ();
        PVExercise exercise = getDocument ().getExercise ();
        FormatUtils formatUtils = getContext ().getFormatUtils ();
        
        HeartRateLimit limit = exercise.getHeartRateLimits ()[index];

        // calculate percentages of times below, within and above
        int percentsBelow = 0, percentsWithin = 0, percentsAbove = 0;
        if (exercise.getDuration () > 0)
        {
            percentsBelow  = (int) Math.round (limit.getTimeBelow  () / (double) exercise.getDuration () * 10 * 100);
            percentsWithin = (int) Math.round (limit.getTimeWithin () / (double) exercise.getDuration () * 10 * 100);
            percentsAbove  = (int) Math.round (limit.getTimeAbove  () / (double) exercise.getDuration () * 10 * 100);
        }

        laTimeBelowValue.setText (formatUtils.seconds2TimeString (limit.getTimeBelow ()) + "   (" + percentsBelow  + " %)");
        laTimeWithinValue.setText (formatUtils.seconds2TimeString (limit.getTimeWithin ()) + "   (" + percentsWithin + " %)");
        laTimeAboveValue.setText  (formatUtils.seconds2TimeString (limit.getTimeAbove  ()) + "   (" + percentsAbove  + " %)");

        // update heartrate range in diagram
        diagramPanel.displayDiagramForHeartrateRange (index);
    }
        
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pLeft = new javax.swing.JPanel();
        laHeartRateLimits = new javax.swing.JLabel();
        laHeartRateData = new javax.swing.JLabel();
        laDuration = new javax.swing.JLabel();
        laDate = new javax.swing.JLabel();
        laEnergy = new javax.swing.JLabel();
        laUser = new javax.swing.JLabel();
        laType = new javax.swing.JLabel();
        laMaximum = new javax.swing.JLabel();
        laAverage = new javax.swing.JLabel();
        laTimeBelow = new javax.swing.JLabel();
        laRange = new javax.swing.JLabel();
        laTimeWithin = new javax.swing.JLabel();
        laTimeAbove = new javax.swing.JLabel();
        laTypeValue = new javax.swing.JLabel();
        laUserValue = new javax.swing.JLabel();
        laDateValue = new javax.swing.JLabel();
        laDurationValue = new javax.swing.JLabel();
        laEnergyValue = new javax.swing.JLabel();
        laMaximumValue = new javax.swing.JLabel();
        laAverageValue = new javax.swing.JLabel();
        laTimeBelowValue = new javax.swing.JLabel();
        laTimeAboveValue = new javax.swing.JLabel();
        laTimeWithinValue = new javax.swing.JLabel();
        cbRange = new javax.swing.JComboBox();
        laGeneralData = new javax.swing.JLabel();
        pRight = new javax.swing.JPanel();
        laRecordingMode = new javax.swing.JLabel();
        laAltitude = new javax.swing.JLabel();
        laSpeed = new javax.swing.JLabel();
        laCadence = new javax.swing.JLabel();
        laPower = new javax.swing.JLabel();
        laStatistics = new javax.swing.JLabel();
        laTotalExerciseTime = new javax.swing.JLabel();
        laTotalRidingTime = new javax.swing.JLabel();
        laTotalEnergy = new javax.swing.JLabel();
        laOdometer = new javax.swing.JLabel();
        laAltitudeValue = new javax.swing.JLabel();
        laSpeedValue = new javax.swing.JLabel();
        laCadenceValue = new javax.swing.JLabel();
        laPowerValue = new javax.swing.JLabel();
        laTotalExerciseTimeValue = new javax.swing.JLabel();
        laTotalRidingTimeValue = new javax.swing.JLabel();
        laTotalEnergyValue = new javax.swing.JLabel();
        laOdometerValue = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.GridLayout(1, 2, 20, 20));

        laHeartRateLimits.setFont(laHeartRateLimits.getFont().deriveFont(laHeartRateLimits.getFont().getStyle() | java.awt.Font.BOLD));
        laHeartRateLimits.setText("_Heart Rate Limits");
        laHeartRateLimits.setName("pv.main.heartrate_limits"); // NOI18N

        laHeartRateData.setFont(laHeartRateData.getFont().deriveFont(laHeartRateData.getFont().getStyle() | java.awt.Font.BOLD));
        laHeartRateData.setText("_Heart Rate Data");
        laHeartRateData.setName("pv.main.heartrate_data"); // NOI18N

        laDuration.setText("_Duration:");
        laDuration.setName("pv.main.duration"); // NOI18N

        laDate.setText("_Date:");
        laDate.setName("pv.main.date"); // NOI18N

        laEnergy.setText("_Energy:");
        laEnergy.setName("pv.main.energy"); // NOI18N

        laUser.setText("_User:");
        laUser.setName("pv.main.user"); // NOI18N

        laType.setText("_Type:");
        laType.setName("pv.main.type"); // NOI18N

        laMaximum.setText("_Maximum:");
        laMaximum.setName("pv.main.maximum"); // NOI18N

        laAverage.setText("_Average:");
        laAverage.setName("pv.main.average"); // NOI18N

        laTimeBelow.setText("_Time below:");
        laTimeBelow.setName("pv.main.time_below"); // NOI18N

        laRange.setText("_Range:");
        laRange.setName("pv.main.range"); // NOI18N

        laTimeWithin.setText("_Time within:");
        laTimeWithin.setName("pv.main.time_within"); // NOI18N

        laTimeAbove.setText("_Time above:");
        laTimeAbove.setName("pv.main.time_above"); // NOI18N

        laTypeValue.setText("_n.a.");

        laUserValue.setText("_n.a.");

        laDateValue.setText("_n.a.");

        laDurationValue.setText("_n.a.");

        laEnergyValue.setText("_n.a.");

        laMaximumValue.setText("_n.a.");

        laAverageValue.setText("_n.a.");

        laTimeBelowValue.setText("_n.a.");

        laTimeAboveValue.setText("_n.a.");

        laTimeWithinValue.setText("_n.a.");

        laGeneralData.setFont(laGeneralData.getFont().deriveFont(laGeneralData.getFont().getStyle() | java.awt.Font.BOLD));
        laGeneralData.setText("_General Data");
        laGeneralData.setName("pv.main.general_data"); // NOI18N

        javax.swing.GroupLayout pLeftLayout = new javax.swing.GroupLayout(pLeft);
        pLeft.setLayout(pLeftLayout);
        pLeftLayout.setHorizontalGroup(
            pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pLeftLayout.createSequentialGroup()
                .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laRange)
                            .addComponent(laTimeBelow)
                            .addComponent(laTimeWithin)
                            .addComponent(laTimeAbove))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laAverageValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laMaximumValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laEnergyValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laDateValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laUserValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laTypeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laTimeBelowValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laTimeWithinValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(laTimeAboveValue, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .addComponent(cbRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(laGeneralData)
                    .addComponent(laHeartRateData)
                    .addComponent(laHeartRateLimits)
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laMaximum)
                            .addComponent(laAverage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE))
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laType)
                            .addComponent(laUser)
                            .addComponent(laDate)
                            .addComponent(laDuration)
                            .addComponent(laEnergy))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pLeftLayout.setVerticalGroup(
            pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pLeftLayout.createSequentialGroup()
                .addComponent(laGeneralData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addComponent(laType)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laUser)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laDuration)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laEnergy))
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addComponent(laTypeValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laUserValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laDateValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laDurationValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laEnergyValue)))
                .addGap(25, 25, 25)
                .addComponent(laHeartRateData)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addComponent(laAverage)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laMaximum))
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addComponent(laAverageValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laMaximumValue)))
                .addGap(25, 25, 25)
                .addComponent(laHeartRateLimits)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addComponent(laTimeWithin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laTimeAbove))
                    .addGroup(pLeftLayout.createSequentialGroup()
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(laRange, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(laTimeBelowValue)
                            .addComponent(laTimeBelow))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laTimeWithinValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(laTimeAboveValue)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(pLeft);

        laRecordingMode.setFont(laRecordingMode.getFont().deriveFont(laRecordingMode.getFont().getStyle() | java.awt.Font.BOLD));
        laRecordingMode.setText("_Recording Mode");
        laRecordingMode.setName("pv.main.recording_mode"); // NOI18N

        laAltitude.setText("_Altitude:");
        laAltitude.setName("pv.main.altitude"); // NOI18N

        laSpeed.setText("_Speed:");
        laSpeed.setName("pv.main.speed"); // NOI18N

        laCadence.setText("_Cadence:");
        laCadence.setName("pv.main.cadence"); // NOI18N

        laPower.setText("_Power:");
        laPower.setName("pv.main.power"); // NOI18N

        laStatistics.setFont(laStatistics.getFont().deriveFont(laStatistics.getFont().getStyle() | java.awt.Font.BOLD));
        laStatistics.setText("_Statistics");
        laStatistics.setName("pv.main.statistics"); // NOI18N

        laTotalExerciseTime.setText("_Total exercise time:");
        laTotalExerciseTime.setName("pv.main.total_exercise_time"); // NOI18N

        laTotalRidingTime.setText("_Total riding time:");
        laTotalRidingTime.setName("pv.main.total_riding_time"); // NOI18N

        laTotalEnergy.setText("_Total energy:");
        laTotalEnergy.setName("pv.main.total_energy"); // NOI18N

        laOdometer.setText("_Odometer:");
        laOdometer.setName("pv.main.odometer"); // NOI18N

        laAltitudeValue.setText("_disabled");

        laSpeedValue.setText("_disabled");

        laCadenceValue.setText("_disabled");

        laPowerValue.setText("_disabled");

        laTotalExerciseTimeValue.setText("_n.a.");

        laTotalRidingTimeValue.setText("_n.a.");

        laTotalEnergyValue.setText("_n.a.");

        laOdometerValue.setText("_n.a.");

        javax.swing.GroupLayout pRightLayout = new javax.swing.GroupLayout(pRight);
        pRight.setLayout(pRightLayout);
        pRightLayout.setHorizontalGroup(
            pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pRightLayout.createSequentialGroup()
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laStatistics)
                    .addComponent(laRecordingMode)
                    .addGroup(pRightLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laTotalExerciseTime)
                            .addComponent(laTotalRidingTime)
                            .addComponent(laTotalEnergy)
                            .addComponent(laOdometer)
                            .addComponent(laAltitude)
                            .addComponent(laSpeed)
                            .addComponent(laCadence)
                            .addComponent(laPower))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laAltitudeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laSpeedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laCadenceValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laPowerValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laTotalExerciseTimeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laTotalRidingTimeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laTotalEnergyValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(laOdometerValue, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))))
                .addContainerGap())
        );
        pRightLayout.setVerticalGroup(
            pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pRightLayout.createSequentialGroup()
                .addComponent(laRecordingMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAltitude)
                    .addComponent(laAltitudeValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laSpeed)
                    .addComponent(laSpeedValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laCadence)
                    .addComponent(laCadenceValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laPower)
                    .addComponent(laPowerValue))
                .addGap(25, 25, 25)
                .addComponent(laStatistics)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalExerciseTime)
                    .addComponent(laTotalExerciseTimeValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalRidingTime)
                    .addComponent(laTotalRidingTimeValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalEnergy)
                    .addComponent(laTotalEnergyValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laOdometer)
                    .addComponent(laOdometerValue))
                .addContainerGap(133, Short.MAX_VALUE))
        );

        add(pRight);
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbRange;
    private javax.swing.JLabel laAltitude;
    private javax.swing.JLabel laAltitudeValue;
    private javax.swing.JLabel laAverage;
    private javax.swing.JLabel laAverageValue;
    private javax.swing.JLabel laCadence;
    private javax.swing.JLabel laCadenceValue;
    private javax.swing.JLabel laDate;
    private javax.swing.JLabel laDateValue;
    private javax.swing.JLabel laDuration;
    private javax.swing.JLabel laDurationValue;
    private javax.swing.JLabel laEnergy;
    private javax.swing.JLabel laEnergyValue;
    private javax.swing.JLabel laGeneralData;
    private javax.swing.JLabel laHeartRateData;
    private javax.swing.JLabel laHeartRateLimits;
    private javax.swing.JLabel laMaximum;
    private javax.swing.JLabel laMaximumValue;
    private javax.swing.JLabel laOdometer;
    private javax.swing.JLabel laOdometerValue;
    private javax.swing.JLabel laPower;
    private javax.swing.JLabel laPowerValue;
    private javax.swing.JLabel laRange;
    private javax.swing.JLabel laRecordingMode;
    private javax.swing.JLabel laSpeed;
    private javax.swing.JLabel laSpeedValue;
    private javax.swing.JLabel laStatistics;
    private javax.swing.JLabel laTimeAbove;
    private javax.swing.JLabel laTimeAboveValue;
    private javax.swing.JLabel laTimeBelow;
    private javax.swing.JLabel laTimeBelowValue;
    private javax.swing.JLabel laTimeWithin;
    private javax.swing.JLabel laTimeWithinValue;
    private javax.swing.JLabel laTotalEnergy;
    private javax.swing.JLabel laTotalEnergyValue;
    private javax.swing.JLabel laTotalExerciseTime;
    private javax.swing.JLabel laTotalExerciseTimeValue;
    private javax.swing.JLabel laTotalRidingTime;
    private javax.swing.JLabel laTotalRidingTimeValue;
    private javax.swing.JLabel laType;
    private javax.swing.JLabel laTypeValue;
    private javax.swing.JLabel laUser;
    private javax.swing.JLabel laUserValue;
    private javax.swing.JPanel pLeft;
    private javax.swing.JPanel pRight;
    // End of variables declaration//GEN-END:variables
    
}
