package de.saring.sportstracker.gui.dialogs;

import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ResourceReader;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.ListCellRendererOddEven;
import de.saring.util.gui.ListUtils;
import org.jdesktop.application.Action;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is the implementation of the dialog for adding/editing sport types.
 *
 * @author Stefan Saring
 * @version 1.1
 */
public class SportTypeDialog extends JDialog {

    /**
     * Constants for action and property names.
     */
    private static final String ACTION_SELECT = "st.dlg.sporttype.select";
    private static final String ACTION_ADD_SUBTYPE = "st.dlg.sporttype.add_subtype";
    private static final String ACTION_EDIT_SUBTYPE = "st.dlg.sporttype.edit_subtype";
    private static final String ACTION_DELETE_SUBTYPE = "st.dlg.sporttype.delete_subtype";
    private static final String ACTION_ADD_EQUIPMENT = "st.dlg.sporttype.add_equipment";
    private static final String ACTION_EDIT_EQUIPMENT = "st.dlg.sporttype.edit_equipment";
    private static final String ACTION_DELETE_EQUIPMENT = "st.dlg.sporttype.delete_equipment";
    private static final String ACTION_OK = "st.dlg.sporttype.ok";
    private static final String ACTION_CANCEL = "st.dlg.sporttype.cancel";

    private static final String PROPERTY_SPORTSUBTYPE_SELECTED = "sportSubtypeSelected";
    private static final String PROPERTY_EQUIPMENT_SELECTED = "equipmentSelected";

    private STContext context;
    private STDocument document;

    /**
     * The edited sport type.
     */
    private SportType sportType;

    /**
     * Standard c'tor. The method setSportType() must be called before showing this dialog.
     *
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public SportTypeDialog(STContext context, STDocument document) {
        super(context.getMainFrame(), true);
        this.context = context;
        this.document = document;

        initComponents();
        setLocationRelativeTo(getParent());
        this.getRootPane().setDefaultButton(btOK);

        // set custom list cell renderer
        liSubtypes.setCellRenderer(new ListCellRendererOddEven(
                context.getResReader().getColor(ResourceReader.COMMON_TABLE_BACKGROUND_ODD),
                context.getResReader().getColor(ResourceReader.COMMON_TABLE_BACKGROUND_EVEN)));
        liEquipment.setCellRenderer(new ListCellRendererOddEven(
                context.getResReader().getColor(ResourceReader.COMMON_TABLE_BACKGROUND_ODD),
                context.getResReader().getColor(ResourceReader.COMMON_TABLE_BACKGROUND_EVEN)));
        ListUtils.increaseListCellHeight(liSubtypes);
        ListUtils.increaseListCellHeight(liEquipment);

        // setup actions
        ActionMap actionMap = context.getSAFContext().getActionMap(getClass(), this);
        btSelect.setAction(actionMap.get(ACTION_SELECT));
        btAddSubtype.setAction(actionMap.get(ACTION_ADD_SUBTYPE));
        btEditSubtype.setAction(actionMap.get(ACTION_EDIT_SUBTYPE));
        btDeleteSubtype.setAction(actionMap.get(ACTION_DELETE_SUBTYPE));
        btAddEquipment.setAction(actionMap.get(ACTION_ADD_EQUIPMENT));
        btEditEquipment.setAction(actionMap.get(ACTION_EDIT_EQUIPMENT));
        btDeleteEquipment.setAction(actionMap.get(ACTION_DELETE_EQUIPMENT));
        btOK.setAction(actionMap.get(ACTION_OK));

        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);

        // add list selection listener 
        // => fire property change events for updating the action status
        liSubtypes.addListSelectionListener(event ->
                firePropertyChange(PROPERTY_SPORTSUBTYPE_SELECTED, true, false));
        liEquipment.addListSelectionListener(event ->
                firePropertyChange(PROPERTY_EQUIPMENT_SELECTED, true, false));

        // add mouse listener for double clicks => edit sport subtype or equipment
        liSubtypes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 2) &&
                        (liSubtypes.getSelectedIndex() != -1)) {
                    editSportSubtype();
                }
            }
        });
        liEquipment.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getClickCount() == 2) &&
                        (liEquipment.getSelectedIndex() != -1)) {
                    editEquipment();
                }
            }
        });
    }

    /**
     * Sets the SportType to be edited and fills the intial values for all controls.
     *
     * @param sportType the sport type to be edited
     */
    public void setSportType(SportType sportType) {
        // create a copy of sport type 
        // => prevents source object modification when dialog is closed using "Cancel" action
        this.sportType = sportType.clone();

        tfName.setText(this.sportType.getName());
        cbDistance.setSelected(this.sportType.isRecordDistance());

        // the record distance mode can only be changed, when no exercises exists for
        // this sport type => disable checkbutton, when such exercises were found
        Optional<Exercise> oExercise = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().equals(this.sportType))
                .findFirst();
        cbDistance.setEnabled(!oExercise.isPresent());

        // use sport type color in button foreground
        btSelect.setForeground(this.sportType.getColor());

        updateSportSubtypeList();
        updateEquipmentList();
    }

    /**
     * Sets the dialog title (must be done here, otherwise the AppFramework overwrites it).
     *
     * @param title the title
     */
    @Override
    public void setTitle(String title) {
        // display "Add New ..." title when it's a new sport type
        if (sportType.getName() == null) {
            super.setTitle(context.getResReader().getString("st.dlg.sporttype.title.add"));
        } else {
            super.setTitle(title);
        }
    }

    /**
     * Fills the list with all current sport subtypes.
     */
    private void updateSportSubtypeList() {
        String[] sportSubtypeNames = new String[sportType.getSportSubTypeList().size()];
        for (int i = 0; i < sportSubtypeNames.length; i++) {
            sportSubtypeNames[i] = sportType.getSportSubTypeList().getAt(i).getName();
        }
        liSubtypes.setListData(sportSubtypeNames);
    }

    /**
     * Fills the list with all current equipment's.
     */
    private void updateEquipmentList() {
        String[] equipmentNames = new String[sportType.getEquipmentList().size()];
        for (int i = 0; i < equipmentNames.length; i++) {
            equipmentNames[i] = sportType.getEquipmentList().getAt(i).getName();
        }
        liEquipment.setListData(equipmentNames);
    }

    /**
     * Action for selecting the sport type color.
     */
    @Action(name = ACTION_SELECT)
    public void selectColor() {

        // start color chooser dialog for current color
        Color newColor = JColorChooser.showDialog(
                this, context.getResReader().getString("st.dlg.sporttype.colorchooser.title"), this.sportType.getColor());

        // store new color when choosen
        if (newColor != null) {
            this.sportType.setColor(newColor);
            btSelect.setForeground(newColor);
        }
    }

    /**
     * Action for adding a new sport subtype.
     */
    @Action(name = ACTION_ADD_SUBTYPE)
    public void addSportSubtype() {

        // create a new SportSubType object and display in the edit dialog
        SportSubType newSubType = new SportSubType(this.sportType.getSportSubTypeList().getNewID());
        editSportSubType(newSubType);
    }

    /**
     * Action for editing the selected sport subtype.
     */
    @Action(name = ACTION_EDIT_SUBTYPE, enabledProperty = PROPERTY_SPORTSUBTYPE_SELECTED)
    public void editSportSubtype() {

        // display edit dialog for selected sport subtype
        SportSubType subType = this.sportType.getSportSubTypeList().getAt(liSubtypes.getSelectedIndex());
        editSportSubType(subType);
    }

    /**
     * Action for deleting the selected sport subtype.
     */
    @Action(name = ACTION_DELETE_SUBTYPE, enabledProperty = PROPERTY_SPORTSUBTYPE_SELECTED)
    public void deleteSportSubtype() {

        // display confirmation dialog
        if (context.showConfirmDialog(this, "st.dlg.sporttype.confirm.delete_subtype.title",
                "st.dlg.sporttype.confirm.delete_subtype.text") != JOptionPane.YES_OPTION) {
            return;
        }

        // are there any existing exercises for this sport subtype?
        SportSubType selSubType = this.sportType.getSportSubTypeList().getAt(liSubtypes.getSelectedIndex());

        List<Exercise> lRefExercises = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().equals(this.sportType)
                        && exercise.getSportSubType().equals(selSubType))
                .collect(Collectors.toList());

        // when there are referenced exercises => these exercises needs to be deleted too
        if (!lRefExercises.isEmpty()) {

            // show confirmation message box again
            if (context.showConfirmDialog(this, "st.dlg.sporttype.confirm.delete_subtype.title",
                    "st.dlg.sporttype.confirm.delete_subtype_existing.text") != JOptionPane.YES_OPTION) {
                return;
            }

            // delete reference exercises
            lRefExercises.forEach(exercise -> document.getExerciseList().removeByID(exercise.getId()));
        }

        // finally delete sport subtype
        this.sportType.getSportSubTypeList().removeByID(selSubType.getId());
        updateSportSubtypeList();
    }

    /**
     * Action for adding a new equipment.
     */
    @Action(name = ACTION_ADD_EQUIPMENT)
    public void addEquipment() {

        // create a new Equipment object and display in the edit dialog
        Equipment newEquipment = new Equipment(
                this.sportType.getEquipmentList().getNewID());
        editEquipment(newEquipment);
    }

    /**
     * Action for editing the selected equipment.
     */
    @Action(name = ACTION_EDIT_EQUIPMENT, enabledProperty = PROPERTY_EQUIPMENT_SELECTED)
    public void editEquipment() {

        // display edit dialog for selected equipment
        Equipment equipment = this.sportType.getEquipmentList().getAt(liEquipment.getSelectedIndex());
        editEquipment(equipment);
    }

    /**
     * Action for deleting the selected equipment.
     */
    @Action(name = ACTION_DELETE_EQUIPMENT, enabledProperty = PROPERTY_EQUIPMENT_SELECTED)
    public void deleteEquipment() {

        // display confirmation dialog
        if (context.showConfirmDialog(this, "st.dlg.sporttype.confirm.delete_equipment.title",
                "st.dlg.sporttype.confirm.delete_equipment.text") != JOptionPane.YES_OPTION) {
            return;
        }

        // are there any existing exercises for this equipment?
        Equipment selEquipment = this.sportType.getEquipmentList().getAt(liEquipment.getSelectedIndex());

        List<Exercise> lRefExercises = document.getExerciseList().stream()
                .filter(exercise -> exercise.getSportType().equals(this.sportType)
                        && exercise.getEquipment() != null && exercise.getEquipment().equals(selEquipment))
                .collect(Collectors.toList());

        // when there are referenced exercises => the equipment must be deleted in those too
        if (lRefExercises.size() > 0) {

            // show confirmation message box again
            if (context.showConfirmDialog(this, "st.dlg.sporttype.confirm.delete_equipment.title",
                    "st.dlg.sporttype.confirm.delete_equipment_existing.text") != JOptionPane.YES_OPTION) {
                return;
            }

            // delete equipment in all exercises which use it
            lRefExercises.forEach(exercise -> exercise.setEquipment(null));
        }

        // finally delete the equipment
        this.sportType.getEquipmentList().removeByID(selEquipment.getId());
        updateEquipmentList();
    }

    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name = ACTION_OK)
    public void ok() {

        // make sure that user has entered a name
        final String strName = tfName.getText().trim();
        if (strName.length() == 0) {
            tfName.selectAll();
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.sporttype.error.no_name");
            tfName.requestFocus();
            return;
        }

        // make sure that the entered name is not in use by other sport types yet
        Optional<SportType> oSportTypeSameName = document.getSportTypeList().stream()
                .filter(stTemp -> stTemp.getId() != this.sportType.getId() && stTemp.getName().equals(strName))
                .findFirst();

        if (oSportTypeSameName.isPresent()) {
            tfName.selectAll();
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.sporttype.error.name_in_use");
            tfName.requestFocus();
            return;
        }
        this.sportType.setName(strName);

        // store record-distance flag 
        sportType.setRecordDistance(cbDistance.isSelected());

        // make sure that there's at least one sport subtype
        if (this.sportType.getSportSubTypeList().size() == 0) {
            context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                    "common.error", "st.dlg.sporttype.error.no_subtype");
            return;
        }

        // store the sport type in the documents list
        document.getSportTypeList().set(this.sportType);
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
     * This property is true when a sport subtype is selected in the list.
     *
     * @return true when a sport subtype is selected
     */
    public boolean isSportSubtypeSelected() {
        return !liSubtypes.isSelectionEmpty();
    }

    /**
     * This property is true when a equipment is selected in the list.
     *
     * @return true when a equipment is selected
     */
    public boolean isEquipmentSelected() {
        return !liEquipment.isSelectionEmpty();
    }

    /**
     * Displays the add/edit dialog for the specified sport subtype name (includes
     * error checking and dialog redisplay). The modified sport subtype will be
     * stored in the sport type.
     *
     * @param subType the sport subtype to be edited
     */
    private void editSportSubType(SportSubType subType) {

        // start with current subtype name
        String strName = subType.getName();

        // title text depends on editing a new or an existing subtype
        String dlgTitle = strName == null ? "st.dlg.sportsubtype.add.title" : "st.dlg.sportsubtype.edit.title";

        while (true) {
            // display dialog 
            strName = (String) JOptionPane.showInputDialog(this,
                    context.getResReader().getString("st.dlg.sportsubtype.name"),
                    context.getResReader().getString(dlgTitle),
                    JOptionPane.QUESTION_MESSAGE, null, null, strName);

            // exit when user has pressed Cancel button
            if (strName == null) {
                return;
            }

            // check the entered name => display error messages on problems
            strName = strName.trim();
            if (strName.length() == 0) {
                // no name was entered
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.sportsubtype.error.no_name");
            } else {
                // make sure that the entered name is not in use by other sport subtypes yet
                boolean fInUse = false;
                for (SportSubType sstTemp : this.sportType.getSportSubTypeList()) {

                    if ((sstTemp.getId() != subType.getId()) &&
                            (sstTemp.getName().equals(strName))) {
                        context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                                "common.error", "st.dlg.sportsubtype.error.in_use");
                        fInUse = true;
                        break;
                    }
                }

                if (!fInUse) {
                    // the name is OK, store the modified subtype and update the list
                    subType.setName(strName);
                    this.sportType.getSportSubTypeList().set(subType);
                    updateSportSubtypeList();
                    return;
                }
            }
        }
    }

    /**
     * Displays the add/edit dialog for the specified equipment name (includes
     * error checking and dialog redisplay). The modified equipment will be
     * stored in the sport type.
     *
     * @param equipment the equipment to be edited
     */
    private void editEquipment(Equipment equipment) {

        // start with current subtype name
        String strName = equipment.getName();

        // title text depends on editing a new or an existing equipment
        String dlgTitle = strName == null ? "st.dlg.equipment.add.title" : "st.dlg.equipment.edit.title";

        while (true) {
            // display dialog 
            strName = (String) JOptionPane.showInputDialog(this,
                    context.getResReader().getString("st.dlg.equipment.name"),
                    context.getResReader().getString(dlgTitle),
                    JOptionPane.QUESTION_MESSAGE, null, null, strName);

            // exit when user has pressed Cancel button
            if (strName == null) {
                return;
            }

            // check the entered name => display error messages on problems
            strName = strName.trim();
            if (strName.length() == 0) {
                // no name was entered
                context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                        "common.error", "st.dlg.equipment.error.no_name");
            } else {
                // make sure that the entered name is not in use by other equipment's yet
                boolean fInUse = false;
                for (Equipment eqTemp : this.sportType.getEquipmentList()) {

                    if ((eqTemp.getId() != equipment.getId()) &&
                            (eqTemp.getName().equals(strName))) {
                        context.showMessageDialog(this, JOptionPane.ERROR_MESSAGE,
                                "common.error", "st.dlg.equipment.error.in_use");
                        fInUse = true;
                        break;
                    }
                }

                if (!fInUse) {
                    // the name is OK, store the modified equipment and update the list
                    equipment.setName(strName);
                    this.sportType.getEquipmentList().set(equipment);
                    updateEquipmentList();
                    return;
                }
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

        laProperties = new javax.swing.JLabel();
        laName = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        laDistance = new javax.swing.JLabel();
        cbDistance = new javax.swing.JCheckBox();
        laColor = new javax.swing.JLabel();
        btSelect = new javax.swing.JButton();
        laSubtypes = new javax.swing.JLabel();
        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        separator = new javax.swing.JSeparator();
        spSubtypes = new javax.swing.JScrollPane();
        liSubtypes = new javax.swing.JList<>();
        btAddSubtype = new javax.swing.JButton();
        btEditSubtype = new javax.swing.JButton();
        btDeleteSubtype = new javax.swing.JButton();
        laEquipmentList = new javax.swing.JLabel();
        btAddEquipment = new javax.swing.JButton();
        btEditEquipment = new javax.swing.JButton();
        btDeleteEquipment = new javax.swing.JButton();
        spEquipment = new javax.swing.JScrollPane();
        liEquipment = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.sporttype"); // NOI18N
        setResizable(false);

        laProperties.setFont(laProperties.getFont().deriveFont(laProperties.getFont().getStyle() | java.awt.Font.BOLD));
        laProperties.setText("_Sport Type Properties");
        laProperties.setName("st.dlg.sporttype.properties"); // NOI18N

        laName.setText("_Name:");
        laName.setName("st.dlg.sporttype.name"); // NOI18N

        laDistance.setText("_Distance:");
        laDistance.setName("st.dlg.sporttype.distance"); // NOI18N

        cbDistance.setText("_Record Distance and Average Speed");
        cbDistance.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        cbDistance.setName("st.dlg.sporttype.record_distance"); // NOI18N

        laColor.setText("_Color:");
        laColor.setName("st.dlg.sporttype.color"); // NOI18N

        btSelect.setText("_Select");

        laSubtypes.setFont(laSubtypes.getFont().deriveFont(laSubtypes.getFont().getStyle() | java.awt.Font.BOLD));
        laSubtypes.setText("_Sport Subtype List");
        laSubtypes.setName("st.dlg.sporttype.subtypes"); // NOI18N

        btOK.setText("_OK");

        btCancel.setText("_Cancel");

        spSubtypes.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        liSubtypes.setVisibleRowCount(7);
        spSubtypes.setViewportView(liSubtypes);

        btAddSubtype.setText("_Add");

        btEditSubtype.setText("_Edit");

        btDeleteSubtype.setText("_Delete");

        laEquipmentList.setFont(laEquipmentList.getFont().deriveFont(laEquipmentList.getFont().getStyle() | java.awt.Font.BOLD));
        laEquipmentList.setText("_Equipment List (Optional)");
        laEquipmentList.setName("st.dlg.sporttype.equipment"); // NOI18N

        btAddEquipment.setText("_Add");

        btEditEquipment.setText("_Edit");

        btDeleteEquipment.setText("_Delete");

        spEquipment.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        liEquipment.setVisibleRowCount(7);
        spEquipment.setViewportView(liEquipment);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(laProperties))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(laName)
                                                        .addComponent(laDistance)
                                                        .addComponent(laColor))
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(tfName, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                                                        .addComponent(cbDistance)
                                                        .addComponent(btSelect)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addContainerGap(292, Short.MAX_VALUE)
                                                .addComponent(btCancel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btOK))
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addGap(12, 12, 12)
                                                                .addComponent(spSubtypes, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(btAddSubtype)
                                                                        .addComponent(btEditSubtype)
                                                                        .addComponent(btDeleteSubtype)))
                                                        .addComponent(laSubtypes))))
                                .addContainerGap())
                        .addComponent(separator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(laEquipmentList)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(spEquipment, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(btAddEquipment)
                                                        .addComponent(btEditEquipment)
                                                        .addComponent(btDeleteEquipment))))
                                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btCancel, btOK});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btAddSubtype, btDeleteSubtype, btEditSubtype});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[]{btAddEquipment, btDeleteEquipment, btEditEquipment});

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(laProperties)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laName)
                                        .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(laDistance)
                                        .addComponent(cbDistance))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(laColor)
                                        .addComponent(btSelect))
                                .addGap(18, 18, 18)
                                .addComponent(laSubtypes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btAddSubtype)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btEditSubtype)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btDeleteSubtype))
                                        .addComponent(spSubtypes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(laEquipmentList)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btAddEquipment)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btEditEquipment)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btDeleteEquipment))
                                        .addComponent(spEquipment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btOK)
                                        .addComponent(btCancel))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAddEquipment;
    private javax.swing.JButton btAddSubtype;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btDeleteEquipment;
    private javax.swing.JButton btDeleteSubtype;
    private javax.swing.JButton btEditEquipment;
    private javax.swing.JButton btEditSubtype;
    private javax.swing.JButton btOK;
    private javax.swing.JButton btSelect;
    private javax.swing.JCheckBox cbDistance;
    private javax.swing.JLabel laColor;
    private javax.swing.JLabel laDistance;
    private javax.swing.JLabel laEquipmentList;
    private javax.swing.JLabel laName;
    private javax.swing.JLabel laProperties;
    private javax.swing.JLabel laSubtypes;
    private javax.swing.JList<String> liEquipment;
    private javax.swing.JList<String> liSubtypes;
    private javax.swing.JSeparator separator;
    private javax.swing.JScrollPane spEquipment;
    private javax.swing.JScrollPane spSubtypes;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables

}
