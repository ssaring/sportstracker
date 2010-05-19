package de.saring.sportstracker.gui.dialogs;

import com.google.inject.Inject;
import de.saring.sportstracker.data.statistic.StatisticCalculator;
import de.saring.sportstracker.gui.STContext;
import de.saring.util.gui.DialogUtils;
import de.saring.util.unitcalc.FormatUtils;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import org.jdesktop.application.Action;

/**
 * This dialog class displays the results for the calculated exercise statistics.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public class StatisticResultsDialog extends JDialog {

    /** Constants for action and property names. */
    private static final String ACTION_CLOSE = "st.dlg.statistic_results.close";

    private STContext context;
    
    /** 
     * Standard c'tor. The method setStatisticResults() must be called before the 
     * dialog gets shown. 
     * @param context the SportsTracker context
     */
    @Inject
    public StatisticResultsDialog (STContext context) {
        super (context.getMainFrame (), true);
        this.context = context;
        initComponents ();
        this.getRootPane ().setDefaultButton (btClose);      
        
        // setup actions
        ActionMap actionMap = context.getSAFContext ().getActionMap (getClass (), this);
        javax.swing.Action aClose = actionMap.get(ACTION_CLOSE);
        btClose.setAction(aClose);
        DialogUtils.setDialogEscapeKeyAction(this, aClose);
    }
   
    /**
     * Sets the statistic result values to be displayed in this dialog.
     * @param statistic the calculated statistic data
     */
    public void setStatisticResults (StatisticCalculator statistic) {        
        FormatUtils formatUtils = context.getFormatUtils ();

        // display total values
        laTotalExercisesValue.setText (String.valueOf (statistic.getExerciseCount ()));
        laTotalDistanceValue.setText (formatUtils.distanceToString (statistic.getTotalDistance (), 2));
        laTotalDurationValue.setText (formatUtils.seconds2TimeString (statistic.getTotalDuration ()) + " (hh:mm:ss)");
        laTotalAscentValue.setText (formatUtils.heightToString (statistic.getTotalAscent ()));
        if (statistic.getTotalCalories () > 0) {
            laTotalCaloriesValue.setText (formatUtils.caloriesToString (statistic.getTotalCalories ()));
        }
        else {
            laTotalCaloriesValue.setText (" ");
        }
        
        // display average values
        laAvgDistanceValue.setText (formatUtils.distanceToString (statistic.getAvgDistance (), 2));
        laAvgAvgSpeedValue.setText (formatUtils.speedToString (statistic.getAvgSpeed (), 2));
        laAvgDurationValue.setText (formatUtils.seconds2TimeString (statistic.getAvgDuration ()) + " (hh:mm:ss)");
        laAvgAscentValue.setText (formatUtils.heightToString (statistic.getAvgAscent ()));
        if (statistic.getAvgHeartRate () > 0) {
            laAvgAvgHeartrateValue.setText (formatUtils.heartRateToString (statistic.getAvgHeartRate ()));
        }
        else {
            laAvgAvgHeartrateValue.setText (" ");
        }
        if (statistic.getAvgCalories () > 0) {
            laAvgCaloriesValue.setText (formatUtils.caloriesToString (statistic.getAvgCalories ()));
        }
        else {
            laAvgCaloriesValue.setText (" ");
        }

        // display minimum values
        laMinDistanceValue.setText (formatUtils.distanceToString (statistic.getMinDistance (), 2));
        laMinAvgSpeedValue.setText (formatUtils.speedToString (statistic.getMinAvgSpeed (), 2));
        laMinDurationValue.setText (formatUtils.seconds2TimeString (statistic.getMinDuration ()) + " (hh:mm:ss)");
        laMinAscentValue.setText (formatUtils.heightToString (statistic.getMinAscent ()));
        if (statistic.getMinAvgHeartRate () > 0) {
            laMinAvgHeartrateValue.setText (formatUtils.heartRateToString (statistic.getMinAvgHeartRate ()));
        }
        else {
            laMinAvgHeartrateValue.setText (" ");
        }
        if (statistic.getMinCalories () > 0) {
            laMinCaloriesValue.setText (formatUtils.caloriesToString (statistic.getMinCalories ()));
        }
        else {
            laMinCaloriesValue.setText (" ");
        }
        
        // display maximum values
        laMaxDistanceValue.setText (formatUtils.distanceToString (statistic.getMaxDistance (), 2));
        laMaxAvgSpeedValue.setText (formatUtils.speedToString (statistic.getMaxAvgSpeed (), 2));
        laMaxDurationValue.setText (formatUtils.seconds2TimeString (statistic.getMaxDuration ()) + " (hh:mm:ss)");
        laMaxAscentValue.setText (formatUtils.heightToString (statistic.getMaxAscent ()));
        if (statistic.getMaxAvgHeartRate () > 0) {
            laMaxAvgHeartrateValue.setText (formatUtils.heartRateToString (statistic.getMaxAvgHeartRate ()));
        }
        else {
            laMaxAvgHeartrateValue.setText (" ");
        }
        if (statistic.getMaxCalories () > 0) {
            laMaxCaloriesValue.setText (formatUtils.caloriesToString (statistic.getMaxCalories ()));
        }
        else {
            laMaxCaloriesValue.setText (" ");
        }
    }

    /**
     * Shows or hides this dialog. Before showing the dialog widgets will be packed
     * once again. The AppFramework stores the size after close and uses them for 
     * next show. This size can be wrong, when the calculated values are much 
     * bigger than the last displayed values.
     * @param fVisible true for show, false for hide
     */
    @Override
    public void setVisible (boolean fVisible) {
        if (fVisible) {
            pack ();
        }
        super.setVisible (fVisible);
    }
    
    /**
     * Action for closing this dialog.
     */
    @Action(name=ACTION_CLOSE)
    public void close () {
        this.dispose ();
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btClose = new javax.swing.JButton();
        pData = new javax.swing.JPanel();
        pDataLeft = new javax.swing.JPanel();
        laTotal = new javax.swing.JLabel();
        laTotalExercises = new javax.swing.JLabel();
        laTotalDistance = new javax.swing.JLabel();
        laTotalDuration = new javax.swing.JLabel();
        laTotalAscent = new javax.swing.JLabel();
        laTotalCalories = new javax.swing.JLabel();
        laTotalExercisesValue = new javax.swing.JLabel();
        laTotalDistanceValue = new javax.swing.JLabel();
        laTotalDurationValue = new javax.swing.JLabel();
        laTotalAscentValue = new javax.swing.JLabel();
        laTotalCaloriesValue = new javax.swing.JLabel();
        laSpacer = new javax.swing.JLabel();
        laMinimum = new javax.swing.JLabel();
        laMinDistance = new javax.swing.JLabel();
        laMinAvgSpeed = new javax.swing.JLabel();
        laMinDuration = new javax.swing.JLabel();
        laMinAscent = new javax.swing.JLabel();
        laMinAvgHeartrate = new javax.swing.JLabel();
        laMinCalories = new javax.swing.JLabel();
        laMinDistanceValue = new javax.swing.JLabel();
        laMinAvgSpeedValue = new javax.swing.JLabel();
        laMinDurationValue = new javax.swing.JLabel();
        laMinAscentValue = new javax.swing.JLabel();
        laMinAvgHeartrateValue = new javax.swing.JLabel();
        laMinCaloriesValue = new javax.swing.JLabel();
        pDataRight = new javax.swing.JPanel();
        laAverage = new javax.swing.JLabel();
        laAvgDistance = new javax.swing.JLabel();
        laAvgAvgSpeed = new javax.swing.JLabel();
        laAvgDuration = new javax.swing.JLabel();
        laAvgAscent = new javax.swing.JLabel();
        laAvgAvgHeartrate = new javax.swing.JLabel();
        laAvgCalories = new javax.swing.JLabel();
        laAvgDistanceValue = new javax.swing.JLabel();
        laAvgAvgSpeedValue = new javax.swing.JLabel();
        laAvgDurationValue = new javax.swing.JLabel();
        laAvgAscentValue = new javax.swing.JLabel();
        laAvgAvgHeartrateValue = new javax.swing.JLabel();
        laAvgCaloriesValue = new javax.swing.JLabel();
        laMaximum = new javax.swing.JLabel();
        laMaxDistance = new javax.swing.JLabel();
        laMaxAvgSpeed = new javax.swing.JLabel();
        laMaxDuration = new javax.swing.JLabel();
        laMaxAscent = new javax.swing.JLabel();
        laMaxAvgHeartrate = new javax.swing.JLabel();
        laMaxCalories = new javax.swing.JLabel();
        laMaxDistanceValue = new javax.swing.JLabel();
        laMaxAvgSpeedValue = new javax.swing.JLabel();
        laMaxDurationValue = new javax.swing.JLabel();
        laMaxAscentValue = new javax.swing.JLabel();
        laMaxAvgHeartrateValue = new javax.swing.JLabel();
        laMaxCaloriesValue = new javax.swing.JLabel();
        separator = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.statistic_results"); // NOI18N
        setResizable(false);

        btClose.setText("_Close");
        btClose.setName("btClose"); // NOI18N

        pData.setName("pData"); // NOI18N
        pData.setLayout(new java.awt.GridLayout(1, 2, 30, 20));

        pDataLeft.setName("pDataLeft"); // NOI18N

        laTotal.setFont(laTotal.getFont().deriveFont(laTotal.getFont().getStyle() | java.awt.Font.BOLD));
        laTotal.setText("_Total");
        laTotal.setName("st.dlg.statistic_results.total"); // NOI18N

        laTotalExercises.setText("_Exercises:");
        laTotalExercises.setName("st.dlg.statistic_results.total_exercises"); // NOI18N

        laTotalDistance.setText("_Total distance:");
        laTotalDistance.setName("st.dlg.statistic_results.total_distance"); // NOI18N

        laTotalDuration.setText("_Total duration:");
        laTotalDuration.setName("st.dlg.statistic_results.total_duration"); // NOI18N

        laTotalAscent.setText("_Total ascent:");
        laTotalAscent.setName("st.dlg.statistic_results.total_ascent"); // NOI18N

        laTotalCalories.setText("_Calorie cons.:");
        laTotalCalories.setName("st.dlg.statistic_results.calories"); // NOI18N

        laTotalExercisesValue.setText("_n.a.");

        laTotalDistanceValue.setText("_n.a.");
        laTotalDistanceValue.setName(""); // NOI18N

        laTotalDurationValue.setText("_n.a.");
        laTotalDurationValue.setName(""); // NOI18N

        laTotalAscentValue.setText("_n.a.");
        laTotalAscentValue.setName(""); // NOI18N

        laTotalCaloriesValue.setText("_n.a.");
        laTotalCaloriesValue.setName(""); // NOI18N

        laSpacer.setText("   ");
        laSpacer.setName("laSpacer"); // NOI18N

        laMinimum.setFont(laMinimum.getFont().deriveFont(laMinimum.getFont().getStyle() | java.awt.Font.BOLD));
        laMinimum.setText("_Minimum");
        laMinimum.setName("st.dlg.statistic_results.minimum"); // NOI18N

        laMinDistance.setText("_Distance:");
        laMinDistance.setName("st.dlg.statistic_results.distance"); // NOI18N

        laMinAvgSpeed.setText("_Avg. speed:");
        laMinAvgSpeed.setName("st.dlg.statistic_results.avg_speed"); // NOI18N

        laMinDuration.setText("_Duration:");
        laMinDuration.setName("st.dlg.statistic_results.duration"); // NOI18N

        laMinAscent.setText("_Ascent:");
        laMinAscent.setName("st.dlg.statistic_results.ascent"); // NOI18N

        laMinAvgHeartrate.setText("_Avg. heart rate:");
        laMinAvgHeartrate.setName("st.dlg.statistic_results.avg_heartrate"); // NOI18N

        laMinCalories.setText("_Calorie cons.:");
        laMinCalories.setName("st.dlg.statistic_results.calories"); // NOI18N

        laMinDistanceValue.setText("_n.a.");
        laMinDistanceValue.setName("laMinDistanceValue"); // NOI18N

        laMinAvgSpeedValue.setText("_n.a.");
        laMinAvgSpeedValue.setName("laMinAvgSpeedValue"); // NOI18N

        laMinDurationValue.setText("_n.a.");
        laMinDurationValue.setName("laMinDurationValue"); // NOI18N

        laMinAscentValue.setText("_n.a.");
        laMinAscentValue.setName("laMinAscentValue"); // NOI18N

        laMinAvgHeartrateValue.setText("_n.a.");
        laMinAvgHeartrateValue.setName("laMinAvgHeartrateValue"); // NOI18N

        laMinCaloriesValue.setText("_n.a.");
        laMinCaloriesValue.setName("laMinCaloriesValue"); // NOI18N

        javax.swing.GroupLayout pDataLeftLayout = new javax.swing.GroupLayout(pDataLeft);
        pDataLeft.setLayout(pDataLeftLayout);
        pDataLeftLayout.setHorizontalGroup(
            pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laMinDistance)
                    .addComponent(laMinAvgSpeed)
                    .addComponent(laMinDuration)
                    .addComponent(laMinAscent)
                    .addComponent(laMinAvgHeartrate)
                    .addComponent(laMinCalories))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laMinDistanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMinAvgSpeedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMinDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMinAscentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMinAvgHeartrateValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMinCaloriesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addComponent(laMinimum)
                .addContainerGap(195, Short.MAX_VALUE))
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(laSpacer)
                .addContainerGap(245, Short.MAX_VALUE))
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addComponent(laTotal)
                .addContainerGap(226, Short.MAX_VALUE))
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laTotalDistance)
                    .addComponent(laTotalDuration)
                    .addComponent(laTotalAscent)
                    .addComponent(laTotalCalories)
                    .addComponent(laTotalExercises))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laTotalExercisesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(laTotalDistanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(laTotalAscentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(laTotalCaloriesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                    .addComponent(laTotalDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        pDataLeftLayout.setVerticalGroup(
            pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addComponent(laTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laTotalExercisesValue)
                    .addComponent(laTotalExercises))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalDistance)
                    .addComponent(laTotalDistanceValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalDuration)
                    .addComponent(laTotalDurationValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalAscent)
                    .addComponent(laTotalAscentValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laTotalCalories)
                    .addComponent(laTotalCaloriesValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(laSpacer)
                .addGap(18, 18, 18)
                .addComponent(laMinimum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinDistance)
                    .addComponent(laMinDistanceValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinAvgSpeed)
                    .addComponent(laMinAvgSpeedValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinDuration)
                    .addComponent(laMinDurationValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinAscent)
                    .addComponent(laMinAscentValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinAvgHeartrate)
                    .addComponent(laMinAvgHeartrateValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMinCalories)
                    .addComponent(laMinCaloriesValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pData.add(pDataLeft);

        pDataRight.setName("pDataRight"); // NOI18N

        laAverage.setFont(laAverage.getFont().deriveFont(laAverage.getFont().getStyle() | java.awt.Font.BOLD));
        laAverage.setText("_Average");
        laAverage.setName("st.dlg.statistic_results.average"); // NOI18N

        laAvgDistance.setText("_Distance:");
        laAvgDistance.setName("st.dlg.statistic_results.distance"); // NOI18N

        laAvgAvgSpeed.setText("_Avg. speed:");
        laAvgAvgSpeed.setName("st.dlg.statistic_results.avg_speed"); // NOI18N

        laAvgDuration.setText("_Duration:");
        laAvgDuration.setName("st.dlg.statistic_results.duration"); // NOI18N

        laAvgAscent.setText("_Ascent:");
        laAvgAscent.setName("st.dlg.statistic_results.ascent"); // NOI18N

        laAvgAvgHeartrate.setText("_Avg. heart rate:");
        laAvgAvgHeartrate.setName("st.dlg.statistic_results.avg_heartrate"); // NOI18N

        laAvgCalories.setText("_Calorie cons.:");
        laAvgCalories.setName("st.dlg.statistic_results.calories"); // NOI18N

        laAvgDistanceValue.setText("_n.a.");
        laAvgDistanceValue.setName("laAvgDistanceValue"); // NOI18N

        laAvgAvgSpeedValue.setText("_n.a.");
        laAvgAvgSpeedValue.setName("laAvgAvgSpeedValue"); // NOI18N

        laAvgDurationValue.setText("_n.a.");
        laAvgDurationValue.setName("laAvgDurationValue"); // NOI18N

        laAvgAscentValue.setText("_n.a.");
        laAvgAscentValue.setName("laAvgAscentValue"); // NOI18N

        laAvgAvgHeartrateValue.setText("_n.a.");
        laAvgAvgHeartrateValue.setName("laAvgAvgHeartrateValue"); // NOI18N

        laAvgCaloriesValue.setText("_n.a.");
        laAvgCaloriesValue.setName("laAvgCaloriesValue"); // NOI18N

        laMaximum.setFont(laMaximum.getFont().deriveFont(laMaximum.getFont().getStyle() | java.awt.Font.BOLD));
        laMaximum.setText("_Maximum");
        laMaximum.setName("st.dlg.statistic_results.maximum"); // NOI18N

        laMaxDistance.setText("_Distance:");
        laMaxDistance.setName("st.dlg.statistic_results.distance"); // NOI18N

        laMaxAvgSpeed.setText("_Avg. speed:");
        laMaxAvgSpeed.setName("st.dlg.statistic_results.avg_speed"); // NOI18N

        laMaxDuration.setText("_Duration:");
        laMaxDuration.setName("st.dlg.statistic_results.duration"); // NOI18N

        laMaxAscent.setText("_Ascent:");
        laMaxAscent.setName("st.dlg.statistic_results.ascent"); // NOI18N

        laMaxAvgHeartrate.setText("_Avg. heart rate:");
        laMaxAvgHeartrate.setName("st.dlg.statistic_results.avg_heartrate"); // NOI18N

        laMaxCalories.setText("_Calorie cons.:");
        laMaxCalories.setName("st.dlg.statistic_results.calories"); // NOI18N

        laMaxDistanceValue.setText("_n.a.");
        laMaxDistanceValue.setName("laMaxDistanceValue"); // NOI18N

        laMaxAvgSpeedValue.setText("_n.a.");
        laMaxAvgSpeedValue.setName("laMaxAvgSpeedValue"); // NOI18N

        laMaxDurationValue.setText("_n.a.");
        laMaxDurationValue.setName("laMaxDurationValue"); // NOI18N

        laMaxAscentValue.setText("_n.a.");
        laMaxAscentValue.setName("laMaxAscentValue"); // NOI18N

        laMaxAvgHeartrateValue.setText("_n.a.");
        laMaxAvgHeartrateValue.setName("laMaxAvgHeartrateValue"); // NOI18N

        laMaxCaloriesValue.setText("_n.a.");
        laMaxCaloriesValue.setName("laMaxCaloriesValue"); // NOI18N

        javax.swing.GroupLayout pDataRightLayout = new javax.swing.GroupLayout(pDataRight);
        pDataRight.setLayout(pDataRightLayout);
        pDataRightLayout.setHorizontalGroup(
            pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laMaxDistance)
                    .addComponent(laMaxAvgSpeed)
                    .addComponent(laMaxDuration)
                    .addComponent(laMaxAscent)
                    .addComponent(laMaxAvgHeartrate)
                    .addComponent(laMaxCalories))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laMaxDistanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMaxAvgSpeedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMaxDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMaxAscentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMaxAvgHeartrateValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laMaxCaloriesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addComponent(laMaximum)
                .addContainerGap(191, Short.MAX_VALUE))
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addComponent(laAverage)
                .addContainerGap(201, Short.MAX_VALUE))
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laAvgDistance)
                    .addComponent(laAvgAvgSpeed)
                    .addComponent(laAvgDuration)
                    .addComponent(laAvgAscent)
                    .addComponent(laAvgAvgHeartrate)
                    .addComponent(laAvgCalories))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(laAvgDistanceValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laAvgAvgSpeedValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laAvgDurationValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laAvgAscentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laAvgAvgHeartrateValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
                    .addComponent(laAvgCaloriesValue, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        pDataRightLayout.setVerticalGroup(
            pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addComponent(laAverage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgDistance)
                    .addComponent(laAvgDistanceValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgAvgSpeed)
                    .addComponent(laAvgAvgSpeedValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgDuration)
                    .addComponent(laAvgDurationValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgAscent)
                    .addComponent(laAvgAscentValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgAvgHeartrate)
                    .addComponent(laAvgAvgHeartrateValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laAvgCalories)
                    .addComponent(laAvgCaloriesValue))
                .addGap(18, 18, 18)
                .addComponent(laMaximum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxDistance)
                    .addComponent(laMaxDistanceValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxAvgSpeed)
                    .addComponent(laMaxAvgSpeedValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxDuration)
                    .addComponent(laMaxDurationValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxAscent)
                    .addComponent(laMaxAscentValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxAvgHeartrate)
                    .addComponent(laMaxAvgHeartrateValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laMaxCalories)
                    .addComponent(laMaxCaloriesValue))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pData.add(pDataRight);

        separator.setName("separator"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(separator, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pData, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                    .addComponent(btClose))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pData, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JLabel laAverage;
    private javax.swing.JLabel laAvgAscent;
    private javax.swing.JLabel laAvgAscentValue;
    private javax.swing.JLabel laAvgAvgHeartrate;
    private javax.swing.JLabel laAvgAvgHeartrateValue;
    private javax.swing.JLabel laAvgAvgSpeed;
    private javax.swing.JLabel laAvgAvgSpeedValue;
    private javax.swing.JLabel laAvgCalories;
    private javax.swing.JLabel laAvgCaloriesValue;
    private javax.swing.JLabel laAvgDistance;
    private javax.swing.JLabel laAvgDistanceValue;
    private javax.swing.JLabel laAvgDuration;
    private javax.swing.JLabel laAvgDurationValue;
    private javax.swing.JLabel laMaxAscent;
    private javax.swing.JLabel laMaxAscentValue;
    private javax.swing.JLabel laMaxAvgHeartrate;
    private javax.swing.JLabel laMaxAvgHeartrateValue;
    private javax.swing.JLabel laMaxAvgSpeed;
    private javax.swing.JLabel laMaxAvgSpeedValue;
    private javax.swing.JLabel laMaxCalories;
    private javax.swing.JLabel laMaxCaloriesValue;
    private javax.swing.JLabel laMaxDistance;
    private javax.swing.JLabel laMaxDistanceValue;
    private javax.swing.JLabel laMaxDuration;
    private javax.swing.JLabel laMaxDurationValue;
    private javax.swing.JLabel laMaximum;
    private javax.swing.JLabel laMinAscent;
    private javax.swing.JLabel laMinAscentValue;
    private javax.swing.JLabel laMinAvgHeartrate;
    private javax.swing.JLabel laMinAvgHeartrateValue;
    private javax.swing.JLabel laMinAvgSpeed;
    private javax.swing.JLabel laMinAvgSpeedValue;
    private javax.swing.JLabel laMinCalories;
    private javax.swing.JLabel laMinCaloriesValue;
    private javax.swing.JLabel laMinDistance;
    private javax.swing.JLabel laMinDistanceValue;
    private javax.swing.JLabel laMinDuration;
    private javax.swing.JLabel laMinDurationValue;
    private javax.swing.JLabel laMinimum;
    private javax.swing.JLabel laSpacer;
    private javax.swing.JLabel laTotal;
    private javax.swing.JLabel laTotalAscent;
    private javax.swing.JLabel laTotalAscentValue;
    private javax.swing.JLabel laTotalCalories;
    private javax.swing.JLabel laTotalCaloriesValue;
    private javax.swing.JLabel laTotalDistance;
    private javax.swing.JLabel laTotalDistanceValue;
    private javax.swing.JLabel laTotalDuration;
    private javax.swing.JLabel laTotalDurationValue;
    private javax.swing.JLabel laTotalExercises;
    private javax.swing.JLabel laTotalExercisesValue;
    private javax.swing.JPanel pData;
    private javax.swing.JPanel pDataLeft;
    private javax.swing.JPanel pDataRight;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables
    
}
