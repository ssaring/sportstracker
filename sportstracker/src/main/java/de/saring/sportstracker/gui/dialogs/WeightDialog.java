package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.GuiCreateUtils;
import de.saring.util.unitcalc.ConvertUtils;
import de.saring.util.unitcalc.FormatUtils;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXDatePicker;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Calendar;

/**
 * This class is the implementation of the dialog for editing / adding
 * Weight entries.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class WeightDialog extends JDialog {

    private STContext context;
    private STDocument document;

    private JXDatePicker dpDate;
    private JSpinner spHour, spMinute;
    private JTextField tfWeight;
    private JTextArea taComment;
    private JButton btOK, btCancel;

    /**
     * This is the exercises object edited in this dialog.
     */
    private Weight weight;

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_OK = "st.dlg.weight.ok";
    private static final String ACTION_CANCEL = "st.dlg.weight.cancel";


    /**
     * Standard c'tor. The method setWeight() needs to be called before
     * showing the dialog.
     *
     * @param context the SportsTracker context
     * @param document the application document component
     */
    @Inject
    public WeightDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;
        initGUI();

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        btOK.setAction(actionMap.get(ACTION_OK));

        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);
    }

    private void initGUI() {
        setName("st.dlg.weight");

        // create all controls
        JLabel laDate = GuiCreateUtils.createLabel("st.dlg.weight.date", false);
        JLabel laTime = GuiCreateUtils.createLabel("st.dlg.weight.time", false);
        JLabel laTimeSeparator = new JLabel(":");
        JLabel laWeight = GuiCreateUtils.createLabel("st.dlg.weight.weight", false);
        JLabel laWeightUnit = GuiCreateUtils.createLabel(null, false);
        laWeightUnit.setText(context.getFormatUtils().getWeightUnitName());
        JLabel laText = GuiCreateUtils.createLabel("st.dlg.weight.comment", false);

        dpDate = GuiCreateUtils.createDatePicker();
        spHour = GuiCreateUtils.createSpinner(new SpinnerNumberModel(12, 0, 23, 1));
        spMinute = GuiCreateUtils.createSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        tfWeight = new JTextField(8);

        taComment = new JTextArea();
        taComment.setLineWrap(true);
        taComment.setWrapStyleWord(true);
        JScrollPane spText = new JScrollPane(taComment);
        // use same font in textarea as in textfield (not default on Win32)
        taComment.setFont(new JTextField().getFont());

        btOK = new JButton();
        btCancel = new JButton();
        JPanel pButtons = GuiCreateUtils.createDialogButtonPanel(btCancel, btOK);

        // use MigLayout as layout manager
        Container pane = getContentPane();
        pane.setLayout(new MigLayout(
                "insets 12, gap 12",   // Layout Constraints
                "[][]25[][]5[]5[]",    // Column constraints
                "[][]16[]8[grow][]")); // Row constraints

        pane.add(laDate);
        pane.add(dpDate, "growx");
        pane.add(laTime);
        pane.add(spHour);
        pane.add(laTimeSeparator);
        pane.add(spMinute, "wrap");
        pane.add(laWeight);
        pane.add(tfWeight, "split, growx");
        pane.add(laWeightUnit, "wrap");
        pane.add(laText, "spanx, wrap");
        pane.add(spText, "spanx, width 300, height 100, grow, wrap");
        pane.add(pButtons, "spanx, growx");

        getRootPane().setDefaultButton(btOK);
        setResizable(false);
        pack();
    }

    /**
     * Initializes the dialog with the specified weight.
     *
     * @param weight the Weight object to be edited
     */
    public void setWeight(Weight weight) {
        this.weight = weight;
        setInitialValues();
    }

    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {
        // display "Add New ..." title when it's a new Weight
        if (document.getWeightList().getByID(weight.getId()) == null) {
            super.setTitle(context.getResReader().getString("st.dlg.weight.title.add"));
        } else {
            super.setTitle(title);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // must be done before displaying, otherwise it's not placed centered
            setLocationRelativeTo(getParent());
        }
        super.setVisible(visible);
    }

    /**
     * Sets the initial exercise values for all controls.
     */
    private void setInitialValues() {
        // set date (formating is done by the textfield) and time
        dpDate.setDate(weight.getDate());

        Calendar calTemp = Calendar.getInstance();
        calTemp.setTime(weight.getDate());
        spHour.setValue(calTemp.get(Calendar.HOUR_OF_DAY));
        spMinute.setValue(calTemp.get(Calendar.MINUTE));

        tfWeight.setText(context.getFormatUtils().weightToStringWithoutUnitName(
                weight.getValue(), 2));

        taComment.setText(weight.getComment());
        taComment.setCaretPosition(0);
    }

    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name = ACTION_OK)
    public void ok() {

        // create a new Weight, because user can cancel after validation errors
        // => so we don't modify the original Weight
        Weight newWeight = new Weight(weight.getId());

        // check date input
        if (dpDate.getDate() == null) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.weight.error.date");
            dpDate.requestFocus();
            return;
        }

        // store date and time of exercise
        Calendar calTemp = Calendar.getInstance();
        calTemp.setTime(dpDate.getDate());
        calTemp.set(Calendar.HOUR_OF_DAY, (Integer) spHour.getValue());
        calTemp.set(Calendar.MINUTE, (Integer) spMinute.getValue());
        calTemp.set(Calendar.SECOND, 0);
        newWeight.setDate(calTemp.getTime());

        // check and store weight value (and convert when not metric unit system)
        try {
            float value = NumberFormat.getInstance().parse(tfWeight.getText()).floatValue();
            if (value <= 0) {
                throw new Exception("The value must be greater than 0 ...");
            }

            if (document.getOptions().getUnitSystem() == FormatUtils.UnitSystem.English) {
                value = (float) ConvertUtils.convertLbs2Kilogram(value);
            }
            newWeight.setValue(value);
        } catch (Exception e) {
            tfWeight.selectAll();
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.weight.error.weight");
            tfWeight.requestFocus();
            return;
        }

        // get comment (optional)
        String strComment = taComment.getText().trim();
        if (strComment.length() > 0) {
            newWeight.setComment(strComment);
        }

        // finally store the new Weight and close dialog
        document.getWeightList().set(newWeight);
        this.dispose();
    }

    /**
     * Action for closing the dialog with the Cancel button.
     */
    @Action(name = ACTION_CANCEL)
    public void cancel() {
        this.dispose();
    }
}