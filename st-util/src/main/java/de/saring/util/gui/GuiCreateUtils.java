package de.saring.util.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;

import net.miginfocom.swing.MigLayout;

/**
 * This utility class contains helper methods for typical GUI widget creation.
 *
 * @author Stefan Saring
 */
public final class GuiCreateUtils {

    private GuiCreateUtils() {
    }

    /**
     * Creates a JLabel with the specified name (not text!).
     *
     * @param name name of the label
     * @param boldFont flag for using a bold font
     * @return the created label
     */
    public static JLabel createLabel(String name, boolean boldFont) {
        JLabel label = new JLabel();
        label.setName(name);
        if (boldFont) {
            label.setFont(label.getFont().deriveFont(
                    label.getFont().getStyle() | java.awt.Font.BOLD));
        }
        return label;
    }

    /**
     * Creates a JXDatePicker component, the displayed date uses the medium
     * format (see DateFormat).
     *
     * @return the created JXDatePicker
     */
    public static JXDatePicker createDatePicker() {
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats(DateFormat.getDateInstance(DateFormat.MEDIUM));

        // don't show the link panel in date pickers ("today is ...")
        datePicker.setLinkPanel(null);

        // remove the Cancel Action (started by ESCAPE key) in the picker and 
        // it's editor, otherwise the user can't close the dialogs with ESCAPE
        datePicker.getActionMap().remove(JXDatePicker.CANCEL_KEY);
        datePicker.getEditor().getActionMap().remove(
                BasicDatePickerUI.EditorCancelAction.TEXT_CANCEL_KEY);

        return datePicker;
    }

    /**
     * Creates a JSpinner component which uses the specified model. This spinner
     * selects the complete textfield content when it gets the focus. The
     * spinner also has mouse wheel support (works for all model types).
     *
     * @param model the model to be used
     * @return the created JSpinner
     */
    public static JSpinner createSpinner(SpinnerModel model) {
        JSpinner spinner = new JSpinner(model);

        // add a focus listener, it needs to select the whole spinner text 
        // when focus is gained (due to some internal JSpinner problems we 
        // need to use invokeLater here :-( )
        FocusAdapter focusListenerSelectAll = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                final JTextField tf = (JTextField) e.getSource();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tf.selectAll();
                    }
                });
            }
        };
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addFocusListener(focusListenerSelectAll);

        GuiCreateUtils.addMouseWheelSupportToSpinner(spinner);
        return spinner;
    }

    /**
     * Adds mouse wheel support to the specified JSpinner widget. It's generic
     * and works for all SpinnerModels.
     *
     * @param spinner the spinner widget
     */
    public static void addMouseWheelSupportToSpinner(final JSpinner spinner) {
        spinner.addMouseWheelListener(event -> {
            boolean fForward = event.getWheelRotation() >= 0;
            int count = Math.abs(event.getWheelRotation());

            for (int i = 0; i < count; i++) {
                Object newValue = fForward ? 
                        spinner.getModel().getNextValue() : spinner.getModel().getPreviousValue();

                if (newValue != null) {
                    spinner.setValue(newValue);
                } else {
                    break;
                }
            }
        });
    }

    /**
     * Creates an typical button panel for the bottom of a dialog. This panel
     * contains all specified buttons next to each other (horizontal alignment).
     * The panel uses all the available horizontal space, the buttons will be
     * placed on the right side. Each button will have the same width. A
     * horizontal JSeparator is located at the top of the panel.<br/>
     *
     * @param buttons the buttons to be placed
     * @return the created JPanel
     */
    public static JPanel createDialogButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new MigLayout("insets 0, gap 10, fillx", "[grow][]", "[][]"));
        panel.add(new JSeparator(), "spanx, growx, wrap");
        panel.add(new JPanel());
        for (JButton button : buttons) {
            panel.add(button, "sg 1");
        }
        return panel;
    }
}
