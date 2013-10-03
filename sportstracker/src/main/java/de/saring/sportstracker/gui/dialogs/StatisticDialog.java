package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.statistic.StatisticCalculator;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.data.IdObjectList;
import de.saring.util.gui.DialogUtils;
import org.jdesktop.application.Action;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class is the implementation of the Statistics dialog.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class StatisticDialog extends JDialog {

    private STContext context;
    private STDocument document;

    @Inject
    private Provider<FilterDialog> prFilterDialog;
    @Inject
    private Provider<StatisticResultsDialog> prStatisticResultsDialog;

    /**
     * The exercise filter used for statistic calculation.
     */
    private ExerciseFilter statFilter;

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_CHANGE_FILTER = "st.dlg.statistic.change_filter";
    private static final String ACTION_CALCULATE = "st.dlg.statistic.calculate";
    private static final String ACTION_CLOSE = "st.dlg.statistic.close";

    /**
     * Creates new StatisticDialog instance.
     *
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public StatisticDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        initComponents();
        setLocationRelativeTo(getParent());
        this.getRootPane().setDefaultButton(btClose);
        btClose.requestFocus();

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        btChange.setAction(actionMap.get(ACTION_CHANGE_FILTER));
        btCalculate.setAction(actionMap.get(ACTION_CALCULATE));

        javax.swing.Action aClose = actionMap.get(ACTION_CLOSE);
        btClose.setAction(aClose);
        DialogUtils.setDialogEscapeKeyAction(this, aClose);

        // start with current filter criterias stored in document => user can change them
        statFilter = document.getCurrentFilter();
        setFilterValues();
    }

    /**
     * Sets the values of the current filter.
     */
    private void setFilterValues() {

        // create string for filter timespan
        DateFormat sdFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT);
        String strTimeSpan = sdFormat.format(statFilter.getDateStart()) +
                " - " + sdFormat.format(statFilter.getDateEnd());

        // create strings for sport type and subtype
        String strSportType = context.getResReader().getString("st.dlg.statistic.all.text");
        if (statFilter.getSportType() != null) {
            strSportType = statFilter.getSportType().getName();
        }

        String strSportSubtype = context.getResReader().getString("st.dlg.statistic.all.text");
        if (statFilter.getSportSubType() != null) {
            strSportSubtype = statFilter.getSportSubType().getName();
        }

        // create string for intensity
        String strIntensity = context.getResReader().getString("st.dlg.statistic.all.text");
        if (statFilter.getIntensity() != null) {
            strIntensity = statFilter.getIntensity().toString();
        }

        // create string for equipment
        String strEquipment = context.getResReader().getString("st.dlg.statistic.all.text");
        if (statFilter.getEquipment() != null) {
            strEquipment = statFilter.getEquipment().getName();
        }

        // create comment string
        String strComment = context.getResReader().getString("st.dlg.statistic.no_comment.text");
        if ((statFilter.getCommentSubString() != null) &&
                (statFilter.getCommentSubString().trim().length() > 0)) {
            strComment = statFilter.getCommentSubString();
            if (statFilter.isRegularExpressionMode()) {
                strComment += " " + context.getResReader().getString("st.dlg.statistic.reg_expression.text");
            }
        }

        // display created strings
        laTimespanValue.setText(strTimeSpan);
        laSportTypeValue.setText(strSportType);
        laSportSubTypeValue.setText(strSportSubtype);
        laIntensityValue.setText(strIntensity);
        laEquipmentValue.setText(strEquipment);
        laCommentValue.setText(strComment);
    }

    /**
     * Action for changing the filter for the statistic.
     */
    @Action(name = ACTION_CHANGE_FILTER)
    public void changeFilter() {

        // display filter dialog for current statistic filter
        FilterDialog dlg = prFilterDialog.get();
        dlg.setInitialFilter(statFilter);
        context.showDialog(dlg);

        // get and display the new selected filter when available
        if (dlg.getSelectedFilter() != null) {
            statFilter = dlg.getSelectedFilter();
            setFilterValues();
        }
    }

    /**
     * Action for starting the statistic calculation.
     */
    @Action(name = ACTION_CALCULATE)
    public void calculate() {

        // search for exercises with the selected filter criterias
        IdObjectList<Exercise> lFoundExercises =
                document.getExerciseList().getExercisesForFilter(statFilter);

        // make sure that at least one exercise was found
        if (lFoundExercises.size() == 0) {
            context.showMessageDialog(this, JOptionPane.INFORMATION_MESSAGE,
                    "common.info", "st.dlg.statistic.info.no_exercises_found");
            return;
        }

        // calculate statistic
        StatisticCalculator statistic = new StatisticCalculator(lFoundExercises);

        // finally display results in dialog
        StatisticResultsDialog dlg = prStatisticResultsDialog.get();
        dlg.setStatisticResults(statistic);
        context.showDialog(dlg);
    }

    /**
     * Action for closing this dialog.
     */
    @Action(name = ACTION_CLOSE)
    public void close() {
        this.dispose();
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
        btCalculate = new javax.swing.JButton();
        laCurrentFilter = new javax.swing.JLabel();
        laTimespan = new javax.swing.JLabel();
        laSportType = new javax.swing.JLabel();
        laSportSubType = new javax.swing.JLabel();
        laIntensity = new javax.swing.JLabel();
        laComment = new javax.swing.JLabel();
        laTimespanValue = new javax.swing.JLabel();
        laSportTypeValue = new javax.swing.JLabel();
        laSportSubTypeValue = new javax.swing.JLabel();
        laIntensityValue = new javax.swing.JLabel();
        laCommentValue = new javax.swing.JLabel();
        btChange = new javax.swing.JButton();
        separator = new javax.swing.JSeparator();
        laEquipment = new javax.swing.JLabel();
        laEquipmentValue = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.statistic"); // NOI18N
        setResizable(false);

        btClose.setText("_Close");
        btClose.setName("btClose"); // NOI18N

        btCalculate.setText("_Calculate");
        btCalculate.setName("btCalculate"); // NOI18N

        laCurrentFilter.setFont(laCurrentFilter.getFont().deriveFont(laCurrentFilter.getFont().getStyle() | java.awt.Font.BOLD));
        laCurrentFilter.setText("_Current Filter");
        laCurrentFilter.setName("st.dlg.statistic.current_filter"); // NOI18N

        laTimespan.setText("_Timespan:");
        laTimespan.setName("st.dlg.statistic.timespan"); // NOI18N

        laSportType.setText("_Sport type:");
        laSportType.setName("st.dlg.statistic.sport_type"); // NOI18N

        laSportSubType.setText("_Sport subtype:");
        laSportSubType.setName("st.dlg.statistic.sport_subtype"); // NOI18N

        laIntensity.setText("_Intensity:");
        laIntensity.setName("st.dlg.statistic.intensity"); // NOI18N

        laComment.setText("_Comment text:");
        laComment.setName("st.dlg.statistic.comment"); // NOI18N

        laTimespanValue.setText("_ - ");

        laSportTypeValue.setText("_all");

        laSportSubTypeValue.setText("_all");

        laIntensityValue.setText("_all");

        laCommentValue.setText("_(none)");

        btChange.setText("_Change");
        btChange.setName("btChange"); // NOI18N

        separator.setName("separator"); // NOI18N

        laEquipment.setText("_Equipment:");
        laEquipment.setName("st.dlg.statistic.equipment"); // NOI18N

        laEquipmentValue.setText("_all");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btCalculate)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btClose))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(laCurrentFilter)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
                                                .addComponent(btChange))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(10, 10, 10)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laSportType)
                                                        .addComponent(laTimespan)
                                                        .addComponent(laSportSubType)
                                                        .addComponent(laIntensity)
                                                        .addComponent(laComment)
                                                        .addComponent(laEquipment))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laEquipmentValue, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                                        .addComponent(laTimespanValue, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                                        .addComponent(laSportTypeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                                        .addComponent(laSportSubTypeValue, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                                        .addComponent(laIntensityValue, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                                                        .addComponent(laCommentValue, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap())
                        .addComponent(separator, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btCalculate, btClose});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laCurrentFilter)
                                        .addComponent(btChange))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laTimespan)
                                        .addComponent(laTimespanValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laSportType)
                                        .addComponent(laSportTypeValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laSportSubType)
                                        .addComponent(laSportSubTypeValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laIntensity)
                                        .addComponent(laIntensityValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laEquipment)
                                        .addComponent(laEquipmentValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laComment)
                                        .addComponent(laCommentValue))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(separator)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btClose)
                                        .addComponent(btCalculate))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCalculate;
    private javax.swing.JButton btChange;
    private javax.swing.JButton btClose;
    private javax.swing.JLabel laComment;
    private javax.swing.JLabel laCommentValue;
    private javax.swing.JLabel laCurrentFilter;
    private javax.swing.JLabel laEquipment;
    private javax.swing.JLabel laEquipmentValue;
    private javax.swing.JLabel laIntensity;
    private javax.swing.JLabel laIntensityValue;
    private javax.swing.JLabel laSportSubType;
    private javax.swing.JLabel laSportSubTypeValue;
    private javax.swing.JLabel laSportType;
    private javax.swing.JLabel laSportTypeValue;
    private javax.swing.JLabel laTimespan;
    private javax.swing.JLabel laTimespanValue;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables

}
