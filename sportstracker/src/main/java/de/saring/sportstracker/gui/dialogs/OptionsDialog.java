package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.core.STOptions.AutoCalculation;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.DialogUtils;
import de.saring.util.unitcalc.FormatUtils.SpeedView;
import de.saring.util.unitcalc.FormatUtils.UnitSystem;
import org.jdesktop.application.Action;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the implementation of the Options dialog.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class OptionsDialog extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(OptionsDialog.class.getName());

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_OK = "st.dlg.options.ok";
    private static final String ACTION_CANCEL = "st.dlg.options.cancel";

    private STContext context;
    private STDocument document;

    /**
     * Creates new OptionsDialog instance.
     *
     * @param context the SportsTracker context
     * @param document the application document component
     */
    @Inject
    public OptionsDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        initComponents();
        setLocationRelativeTo(getParent());
        setTextTranslations();
        this.getRootPane().setDefaultButton(btOK);

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        btOK.setAction(actionMap.get(ACTION_OK));

        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);

        setInitialValues();
    }

    /**
     * Sets the text translations for all widgets where it's not done automatically.
     */
    private void setTextTranslations() {
        tabbedPane.setTitleAt(0, context.getResReader().getString("st.dlg.options.main.title"));
        tabbedPane.setTitleAt(1, context.getResReader().getString("st.dlg.options.units.title"));
        tabbedPane.setTitleAt(2, context.getResReader().getString("st.dlg.options.listview.title"));
        tabbedPane.setTitleAt(3, context.getResReader().getString("st.dlg.options.exerciseviewer.title"));
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues() {
        STOptions options = document.getOptions();

        if (options.getInitialView() == STOptions.View.Calendar) {
            rbCalendar.setSelected(true);
        } else {
            rbExerciseList.setSelected(true);
        }

        if (options.getUnitSystem() == UnitSystem.Metric) {
            rbMetric.setSelected(true);
        } else {
            rbEnglish.setSelected(true);
        }

        if (options.getSpeedView() == SpeedView.DistancePerHour) {
            rbDistanceHour.setSelected(true);
        } else {
            rbMinutesDistance.setSelected(true);
        }

        if (options.isWeekStartSunday()) {
            rbSunday.setSelected(true);
        } else {
            rbMonday.setSelected(true);
        }

        cbSecondGraph.setSelected(options.isDisplaySecondDiagram());
        cbSaveOnExit.setSelected(options.isSaveOnExit());

        // fill combobox with all autocalculation types and select current type
        cbDefaultAutoCalculation.removeAllItems();
        cbDefaultAutoCalculation.addItem(new AutoCalculationComboBoxItem(
                AutoCalculation.Distance, context.getResReader().getString("st.dlg.options.distance.text")));
        cbDefaultAutoCalculation.addItem(new AutoCalculationComboBoxItem(
                AutoCalculation.AvgSpeed, context.getResReader().getString("st.dlg.options.avg_speed.text")));
        cbDefaultAutoCalculation.addItem(new AutoCalculationComboBoxItem(
                AutoCalculation.Duration, context.getResReader().getString("st.dlg.options.duration.text")));

        for (int i = 0; i < cbDefaultAutoCalculation.getItemCount(); i++) {
            AutoCalculationComboBoxItem item = (AutoCalculationComboBoxItem) cbDefaultAutoCalculation.getItemAt(i);
            if (item.getAutoCalculation() == options.getDefaultAutoCalcuation()) {
                cbDefaultAutoCalculation.setSelectedItem(item);
                break;
            }
        }

        // listview options
        cbShowAvgHeartrate.setSelected(options.isListViewShowAvgHeartrate());
        cbShowAscent.setSelected(options.isListViewShowAscent());
        cbShowEnergy.setSelected(options.isListViewShowEnergy());
        cbShowEquipment.setSelected(options.isListViewShowEquipment());
        cbShowComment.setSelected(options.isListViewShowComment());

        // fill combobox with all available look&feels and select current look&feel
        LookAndFeelInfo[] lafInfos = UIManager.getInstalledLookAndFeels();
        String currentLAFClassName = UIManager.getLookAndFeel().getClass().getName();
        cbLookAndFeel.removeAllItems();

        for (LookAndFeelInfo info : lafInfos) {
            LAFComboBoxItem cbItem = new LAFComboBoxItem(info);
            cbLookAndFeel.addItem(cbItem);

            if (info.getClassName().equals(currentLAFClassName)) {
                cbLookAndFeel.setSelectedItem(cbItem);
            }
        }
    }

    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name = ACTION_OK)
    public void ok() {
        STOptions options = document.getOptions();

        // get options from widgets
        options.setInitialView(rbCalendar.isSelected() ?
                STOptions.View.Calendar : STOptions.View.List);

        options.setUnitSystem(rbMetric.isSelected() ?
                UnitSystem.Metric : UnitSystem.English);

        options.setSpeedView(rbDistanceHour.isSelected() ?
                SpeedView.DistancePerHour : SpeedView.MinutesPerDistance);

        options.setWeekStartSunday(rbSunday.isSelected());
        options.setDisplaySecondDiagram(cbSecondGraph.isSelected());
        options.setSaveOnExit(cbSaveOnExit.isSelected());

        AutoCalculationComboBoxItem acItem = (AutoCalculationComboBoxItem) cbDefaultAutoCalculation.getSelectedItem();
        options.setDefaultAutoCalcuation(acItem.getAutoCalculation());

        LAFComboBoxItem lafItem = (LAFComboBoxItem) cbLookAndFeel.getSelectedItem();
        options.setLookAndFeelClassName(lafItem.getLookAndFeelInfo().getClassName());
        setLookAndFeel(options.getLookAndFeelClassName());

        // list view options
        options.setListViewShowAvgHeartrate(cbShowAvgHeartrate.isSelected());
        options.setListViewShowEnergy(cbShowEnergy.isSelected());
        options.setListViewShowAscent(cbShowAscent.isSelected());
        options.setListViewShowEquipment(cbShowEquipment.isSelected());
        options.setListViewShowComment(cbShowComment.isSelected());

        // save new options and close dialog
        document.storeOptions();
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
     * Sets the specified look&feel for the whole GUI if it's not allready active yet.
     *
     * @param lookAndFeelClassName the class name of the new look&feel
     */
    private void setLookAndFeel(String lookAndFeelClassName) {
        if (!UIManager.getLookAndFeel().getClass().getName().equals(lookAndFeelClassName)) {
            try {
                UIManager.setLookAndFeel(lookAndFeelClassName);
                SwingUtilities.updateComponentTreeUI(context.getMainFrame());
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to change look&feel to " + lookAndFeelClassName + "!", e);
            }
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgInitialView = new javax.swing.ButtonGroup();
        bgUnitSystem = new javax.swing.ButtonGroup();
        bgSpeedUnitView = new javax.swing.ButtonGroup();
        bgWeekStart = new javax.swing.ButtonGroup();
        tabbedPane = new javax.swing.JTabbedPane();
        paMain = new javax.swing.JPanel();
        laInitialView = new javax.swing.JLabel();
        rbCalendar = new javax.swing.JRadioButton();
        rbExerciseList = new javax.swing.JRadioButton();
        laDefaultAutoCalculation = new javax.swing.JLabel();
        cbDefaultAutoCalculation = new javax.swing.JComboBox();
        laLookAndFeel = new javax.swing.JLabel();
        cbLookAndFeel = new javax.swing.JComboBox();
        laSaveOnExit = new javax.swing.JLabel();
        cbSaveOnExit = new javax.swing.JCheckBox();
        paUnits = new javax.swing.JPanel();
        laWeekStart = new javax.swing.JLabel();
        laUnitSystem = new javax.swing.JLabel();
        rbMetric = new javax.swing.JRadioButton();
        rbEnglish = new javax.swing.JRadioButton();
        laSpeedView = new javax.swing.JLabel();
        rbDistanceHour = new javax.swing.JRadioButton();
        rbMinutesDistance = new javax.swing.JRadioButton();
        rbMonday = new javax.swing.JRadioButton();
        rbSunday = new javax.swing.JRadioButton();
        paListView = new javax.swing.JPanel();
        laOptionalFields = new javax.swing.JLabel();
        cbShowAvgHeartrate = new javax.swing.JCheckBox();
        cbShowAscent = new javax.swing.JCheckBox();
        cbShowEnergy = new javax.swing.JCheckBox();
        cbShowEquipment = new javax.swing.JCheckBox();
        cbShowComment = new javax.swing.JCheckBox();
        paExerciseViewer = new javax.swing.JPanel();
        laDiagram = new javax.swing.JLabel();
        cbSecondGraph = new javax.swing.JCheckBox();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.options"); // NOI18N
        setResizable(false);

        tabbedPane.setName("tabbedPane"); // NOI18N

        paMain.setName("paMain"); // NOI18N

        laInitialView.setFont(laInitialView.getFont().deriveFont(laInitialView.getFont().getStyle() | java.awt.Font.BOLD));
        laInitialView.setText("_Initial View");
        laInitialView.setName("st.dlg.options.initial_view"); // NOI18N

        bgInitialView.add(rbCalendar);
        rbCalendar.setText("_Calendar");
        rbCalendar.setName("st.dlg.options.calendar"); // NOI18N

        bgInitialView.add(rbExerciseList);
        rbExerciseList.setText("_Exercise list");
        rbExerciseList.setName("st.dlg.options.exercise_list"); // NOI18N

        laDefaultAutoCalculation.setFont(laDefaultAutoCalculation.getFont().deriveFont(laDefaultAutoCalculation.getFont().getStyle() | java.awt.Font.BOLD));
        laDefaultAutoCalculation.setText("_Default Automatic Calculation");
        laDefaultAutoCalculation.setName("st.dlg.options.defaultautocalc"); // NOI18N

        cbDefaultAutoCalculation.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        cbDefaultAutoCalculation.setName("cbDefaultAutoCalculation"); // NOI18N

        laLookAndFeel.setFont(laLookAndFeel.getFont().deriveFont(laLookAndFeel.getFont().getStyle() | java.awt.Font.BOLD));
        laLookAndFeel.setText("_Look & Feel");
        laLookAndFeel.setName("st.dlg.options.lookandfeel"); // NOI18N

        cbLookAndFeel.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        cbLookAndFeel.setName("cbLookAndFeel"); // NOI18N

        laSaveOnExit.setFont(laSaveOnExit.getFont().deriveFont(laSaveOnExit.getFont().getStyle() | java.awt.Font.BOLD));
        laSaveOnExit.setText("_Save on Exit");
        laSaveOnExit.setName("st.dlg.options.save_exit"); // NOI18N

        cbSaveOnExit.setText("_Automatically save on application exit");
        cbSaveOnExit.setName("st.dlg.options.autosave_exit"); // NOI18N

        javax.swing.GroupLayout paMainLayout = new javax.swing.GroupLayout(paMain);
        paMain.setLayout(paMainLayout);
        paMainLayout.setHorizontalGroup(
                paMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paMainLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(paMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(laInitialView)
                                        .addGroup(paMainLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(paMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(rbExerciseList)
                                                        .addComponent(rbCalendar)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paMainLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(cbDefaultAutoCalculation, 0, 319, Short.MAX_VALUE))
                                        .addComponent(laDefaultAutoCalculation)
                                        .addGroup(paMainLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(cbLookAndFeel, 0, 319, Short.MAX_VALUE))
                                        .addComponent(laLookAndFeel)
                                        .addGroup(paMainLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(cbSaveOnExit))
                                        .addComponent(laSaveOnExit))
                                .addContainerGap())
        );
        paMainLayout.setVerticalGroup(
                paMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paMainLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(laInitialView)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbCalendar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbExerciseList)
                                .addGap(18, 18, 18)
                                .addComponent(laDefaultAutoCalculation)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbDefaultAutoCalculation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(laLookAndFeel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbLookAndFeel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(laSaveOnExit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbSaveOnExit)
                                .addContainerGap())
        );

        tabbedPane.addTab("_Main", paMain);

        paUnits.setName("paUnits"); // NOI18N

        laWeekStart.setFont(laWeekStart.getFont().deriveFont(laWeekStart.getFont().getStyle() | java.awt.Font.BOLD));
        laWeekStart.setText("_Week Start");
        laWeekStart.setName("st.dlg.options.week_start"); // NOI18N

        laUnitSystem.setFont(laUnitSystem.getFont().deriveFont(laUnitSystem.getFont().getStyle() | java.awt.Font.BOLD));
        laUnitSystem.setText("_Unit System");
        laUnitSystem.setName("st.dlg.options.unit_system"); // NOI18N

        bgUnitSystem.add(rbMetric);
        rbMetric.setText("_Metric (e.g. kilometers)");
        rbMetric.setName("st.dlg.options.metric"); // NOI18N

        bgUnitSystem.add(rbEnglish);
        rbEnglish.setText("_English (e.g. miles)");
        rbEnglish.setName("st.dlg.options.english"); // NOI18N

        laSpeedView.setFont(laSpeedView.getFont().deriveFont(laSpeedView.getFont().getStyle() | java.awt.Font.BOLD));
        laSpeedView.setText("_Speed Unit View");
        laSpeedView.setName("st.dlg.options.speed_unit"); // NOI18N

        bgSpeedUnitView.add(rbDistanceHour);
        rbDistanceHour.setText("_Distance per hour (e.g. km/h)");
        rbDistanceHour.setName("st.dlg.options.distance_hour"); // NOI18N

        bgSpeedUnitView.add(rbMinutesDistance);
        rbMinutesDistance.setText("_Minutes per distance (e.g. min/km)");
        rbMinutesDistance.setName("st.dlg.options.minutes_distance"); // NOI18N

        bgWeekStart.add(rbMonday);
        rbMonday.setText("_Monday");
        rbMonday.setName("st.dlg.options.monday"); // NOI18N

        bgWeekStart.add(rbSunday);
        rbSunday.setText("_Sunday");
        rbSunday.setName("st.dlg.options.sunday"); // NOI18N

        javax.swing.GroupLayout paUnitsLayout = new javax.swing.GroupLayout(paUnits);
        paUnits.setLayout(paUnitsLayout);
        paUnitsLayout.setHorizontalGroup(
                paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paUnitsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(laUnitSystem)
                                        .addGroup(paUnitsLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(rbEnglish)
                                                        .addComponent(rbMetric)))
                                        .addComponent(laSpeedView)
                                        .addGroup(paUnitsLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(rbMinutesDistance)
                                                        .addComponent(rbDistanceHour)))
                                        .addComponent(laWeekStart)
                                        .addGroup(paUnitsLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(rbSunday)
                                                        .addComponent(rbMonday))))
                                .addContainerGap(79, Short.MAX_VALUE))
        );
        paUnitsLayout.setVerticalGroup(
                paUnitsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paUnitsLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(laUnitSystem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbMetric)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbEnglish)
                                .addGap(18, 18, 18)
                                .addComponent(laSpeedView)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbDistanceHour)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbMinutesDistance)
                                .addGap(18, 18, 18)
                                .addComponent(laWeekStart)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbMonday)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbSunday)
                                .addContainerGap())
        );

        tabbedPane.addTab("_Units", paUnits);

        paListView.setName("paListView"); // NOI18N

        laOptionalFields.setFont(laOptionalFields.getFont().deriveFont(laOptionalFields.getFont().getStyle() | java.awt.Font.BOLD));
        laOptionalFields.setText("_Display Optional Fields");
        laOptionalFields.setName("st.dlg.options.list_optional_fields"); // NOI18N

        cbShowAvgHeartrate.setText("_Average heart rate");
        cbShowAvgHeartrate.setName("st.dlg.options.show_avg_heartrate"); // NOI18N

        cbShowAscent.setText("_Ascent");
        cbShowAscent.setName("st.dlg.options.show_ascent"); // NOI18N

        cbShowEnergy.setText("_Calorie consumed");
        cbShowEnergy.setName("st.dlg.options.show_energy"); // NOI18N

        cbShowEquipment.setText("_Equipment");
        cbShowEquipment.setName("st.dlg.options.show_equipment"); // NOI18N

        cbShowComment.setText("_Comment");
        cbShowComment.setName("st.dlg.options.show_comment"); // NOI18N

        javax.swing.GroupLayout paListViewLayout = new javax.swing.GroupLayout(paListView);
        paListView.setLayout(paListViewLayout);
        paListViewLayout.setHorizontalGroup(
                paListViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paListViewLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(paListViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(laOptionalFields)
                                        .addGroup(paListViewLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addGroup(paListViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(cbShowAvgHeartrate)
                                                        .addComponent(cbShowEnergy)
                                                        .addComponent(cbShowAscent)
                                                        .addComponent(cbShowEquipment)
                                                        .addComponent(cbShowComment))))
                                .addContainerGap(168, Short.MAX_VALUE))
        );
        paListViewLayout.setVerticalGroup(
                paListViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paListViewLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(laOptionalFields)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbShowAvgHeartrate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbShowAscent)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbShowEnergy)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbShowEquipment)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbShowComment)
                                .addGap(137, 137, 137))
        );

        tabbedPane.addTab("_List View", paListView);

        paExerciseViewer.setName("paExerciseViewer"); // NOI18N

        laDiagram.setFont(laDiagram.getFont().deriveFont(laDiagram.getFont().getStyle() | java.awt.Font.BOLD));
        laDiagram.setText("_Diagram");
        laDiagram.setName("st.dlg.options.diagram"); // NOI18N

        cbSecondGraph.setText("_Show two graphs initially on available data");
        cbSecondGraph.setName("st.dlg.options.second_graph"); // NOI18N

        javax.swing.GroupLayout paExerciseViewerLayout = new javax.swing.GroupLayout(paExerciseViewer);
        paExerciseViewer.setLayout(paExerciseViewerLayout);
        paExerciseViewerLayout.setHorizontalGroup(
                paExerciseViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paExerciseViewerLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(paExerciseViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(paExerciseViewerLayout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(cbSecondGraph))
                                        .addComponent(laDiagram))
                                .addContainerGap(32, Short.MAX_VALUE))
        );
        paExerciseViewerLayout.setVerticalGroup(
                paExerciseViewerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(paExerciseViewerLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(laDiagram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbSecondGraph)
                                .addContainerGap())
        );

        tabbedPane.addTab("_ExerciseViewer", paExerciseViewer);

        btOK.setText("_OK");
        btOK.setName("btOK"); // NOI18N

        btCancel.setText("_Cancel");
        btCancel.setName("btCancel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btOK)
                                        .addComponent(btCancel))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgInitialView;
    private javax.swing.ButtonGroup bgSpeedUnitView;
    private javax.swing.ButtonGroup bgUnitSystem;
    private javax.swing.ButtonGroup bgWeekStart;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOK;
    private javax.swing.JComboBox cbDefaultAutoCalculation;
    private javax.swing.JComboBox cbLookAndFeel;
    private javax.swing.JCheckBox cbSaveOnExit;
    private javax.swing.JCheckBox cbSecondGraph;
    private javax.swing.JCheckBox cbShowAscent;
    private javax.swing.JCheckBox cbShowAvgHeartrate;
    private javax.swing.JCheckBox cbShowComment;
    private javax.swing.JCheckBox cbShowEnergy;
    private javax.swing.JCheckBox cbShowEquipment;
    private javax.swing.JLabel laDefaultAutoCalculation;
    private javax.swing.JLabel laDiagram;
    private javax.swing.JLabel laInitialView;
    private javax.swing.JLabel laLookAndFeel;
    private javax.swing.JLabel laOptionalFields;
    private javax.swing.JLabel laSaveOnExit;
    private javax.swing.JLabel laSpeedView;
    private javax.swing.JLabel laUnitSystem;
    private javax.swing.JLabel laWeekStart;
    private javax.swing.JPanel paListView;
    private javax.swing.JPanel paMain;
    private javax.swing.JPanel paExerciseViewer;
    private javax.swing.JPanel paUnits;
    private javax.swing.JRadioButton rbCalendar;
    private javax.swing.JRadioButton rbDistanceHour;
    private javax.swing.JRadioButton rbEnglish;
    private javax.swing.JRadioButton rbExerciseList;
    private javax.swing.JRadioButton rbMetric;
    private javax.swing.JRadioButton rbMinutesDistance;
    private javax.swing.JRadioButton rbMonday;
    private javax.swing.JRadioButton rbSunday;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    /**
     * This class is for combobox items for the look & feel selection.
     */
    private static class LAFComboBoxItem {
        private LookAndFeelInfo lookAndFeelInfo;

        public LAFComboBoxItem(LookAndFeelInfo lookAndFeelInfo) {
            this.lookAndFeelInfo = lookAndFeelInfo;
        }

        public LookAndFeelInfo getLookAndFeelInfo() {
            return lookAndFeelInfo;
        }

        @Override
        public String toString() {
            return lookAndFeelInfo.getName();
        }
    }

    /**
     * This class is for combobox items for automatic calculation type selection.
     */
    private static class AutoCalculationComboBoxItem {
        private STOptions.AutoCalculation autoCalculation;
        private String text;

        public AutoCalculationComboBoxItem(AutoCalculation autoCalculation, String text) {
            this.autoCalculation = autoCalculation;
            this.text = text;
        }

        public AutoCalculation getAutoCalculation() {
            return autoCalculation;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
