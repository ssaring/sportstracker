package de.saring.sportstracker.gui.dialogs;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;

import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.data.SportTypeList;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.ResourceReader;
import de.saring.util.gui.DialogUtils;
import de.saring.util.gui.ListCellRendererOddEven;
import de.saring.util.gui.ListUtils;

/**
 * This class is the implementation of the dialog for editing the sport type list.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public class SportTypeListDialog extends JDialog {

    /** Constants for action and property names. */
    private static final String ACTION_ADD = "st.dlg.sporttype_list.add";
    private static final String ACTION_EDIT = "st.dlg.sporttype_list.edit";
    private static final String ACTION_DELETE = "st.dlg.sporttype_list.delete";
    private static final String ACTION_CLOSE = "st.dlg.sporttype_list.close";
    
    private static final String PROPERTY_SPORTTYPE_SELECTED = "sportTypeSelected";

    private STContext context;
    private STDocument document;
    
    @Inject private Provider<SportTypeDialog> prSportTypeDialog;    
    
    /** 
     * Creates new SportTypeListDialog instance.
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public SportTypeListDialog (STContext context, STDocument document) {
        
        super (context.getMainFrame (), true);
        this.context = context;
        this.document = document;
        initComponents ();
        setLocationRelativeTo(getParent());
        this.getRootPane ().setDefaultButton (btClose);
        liSportTypes.setCellRenderer (new SportTypeCellRenderer ());
        ListUtils.increaseListCellHeight (liSportTypes);
        
        // setup actions
        ActionMap actionMap = context.getSAFContext ().getActionMap (getClass (), this);
        btAdd.setAction (actionMap.get (ACTION_ADD));
        btEdit.setAction (actionMap.get (ACTION_EDIT));
        btDelete.setAction (actionMap.get (ACTION_DELETE));
        
        javax.swing.Action aClose = actionMap.get(ACTION_CLOSE);
        btClose.setAction(aClose);
        DialogUtils.setDialogEscapeKeyAction(this, aClose);

        // add list selection listener 
        // => fire sportTypeSelected property change for updating the action status
        liSportTypes.addListSelectionListener (new ListSelectionListener () {
            public void valueChanged (ListSelectionEvent e) {
                firePropertyChange (PROPERTY_SPORTTYPE_SELECTED, true, false);
            }
        });
        
        // add mouse listener for double clicks => edit sport type
        liSportTypes.addMouseListener (new MouseAdapter () {
            @Override public void mouseClicked (MouseEvent e) {
                if ((e.getClickCount () == 2) &&
                    (liSportTypes.getSelectedIndex () != -1)) {
                    editSportType ();
                }
            }
        });
        
        updateSportTypeList ();
    }
    
    /**
     * Fills the list with all current sport types.
     */
    private void updateSportTypeList () {        
        SportTypeList sportTypes = document.getSportTypeList ();
        
        String[] sportTypeNames = new String[sportTypes.size ()];
        for (int i = 0; i < sportTypeNames.length; i++) {
            sportTypeNames[i] = sportTypes.getAt (i).getName ();
        }        
        liSportTypes.setListData (sportTypeNames);
    }

    /**
     * Action for adding a new sport type.
     */
    @Action(name=ACTION_ADD)
    public void addSportType () {
        
        // start edit dialog for a new created SportType object
        SportType newSportType = new SportType (document.getSportTypeList ().getNewID ());
        SportTypeDialog dlg = prSportTypeDialog.get ();
        dlg.setSportType (newSportType);
        context.showDialog (dlg);
        updateSportTypeList ();
    }
    
    /**
     * Action for editing the selected sport type.
     */
    @Action(name=ACTION_EDIT, enabledProperty=PROPERTY_SPORTTYPE_SELECTED)
    public void editSportType () {
        
        // start edit dialog for selected sport type
        int selectedIndex = liSportTypes.getSelectedIndex ();
        SportType sportType = document.getSportTypeList ().getAt (selectedIndex);
        SportTypeDialog dlg = prSportTypeDialog.get ();
        dlg.setSportType (sportType);
        context.showDialog (dlg);
        updateSportTypeList ();
    }
    
    /**
     * Action for deleting the selected sport type.
     */
    @Action(name=ACTION_DELETE, enabledProperty=PROPERTY_SPORTTYPE_SELECTED)
    public void deleteSportType () {
        
        // display confirmation dialog
        if (context.showConfirmDialog (this, "st.dlg.sporttype_list.confirm.delete.title",
            "st.dlg.sporttype_list.confirm.delete.text") != JOptionPane.YES_OPTION) {
            return;
        }
        
        // are there any existing exercises for this sport type?
        int selectedIndex = liSportTypes.getSelectedIndex ();
        SportType sportType = document.getSportTypeList ().getAt (selectedIndex);
        List<Integer> lRefExerciseIDs = new ArrayList<> ();
        for (Exercise exercise : document.getExerciseList ()) {
            
            if (exercise.getSportType ().getId () == sportType.getId ()) {
                lRefExerciseIDs.add (exercise.getId ());
            }
        }

        // when there are referenced exercises => these exercises needs to be deleted too
        if (lRefExerciseIDs.size () > 0) {
            
            // show confirmation message box again
            if (context.showConfirmDialog (this, "st.dlg.sporttype_list.confirm.delete.title",
                "st.dlg.sporttype_list.confirm.delete_existing.text") != JOptionPane.YES_OPTION) {
                return;
            }
        
            // delete reference exercises
            for (int refExerciseID : lRefExerciseIDs) {
                document.getExerciseList ().removeByID (refExerciseID);
            }
        }

        // finally delete the sport type
        document.getSportTypeList ().removeByID (sportType.getId ());
        updateSportTypeList ();
    }
    
    /**
     * Action for closing the dialog.
     */
    @Action(name=ACTION_CLOSE)
    public void close () {
        this.dispose ();
    }
    
    /**
     * This property is true when a sport type is selected in the list.
     * @return true when a sport type is selected
     */
    public boolean isSportTypeSelected () {
        return !liSportTypes.isSelectionEmpty ();
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
        separator = new javax.swing.JSeparator();
        spSportTypes = new javax.swing.JScrollPane();
        liSportTypes = new javax.swing.JList();
        btAdd = new javax.swing.JButton();
        btEdit = new javax.swing.JButton();
        btDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("_Edit Sport Type List");
        setModal(true);
        setName("st.dlg.sporttype_list"); // NOI18N
        setResizable(false);

        btClose.setText("_Close");

        spSportTypes.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        liSportTypes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spSportTypes.setViewportView(liSportTypes);

        btAdd.setText("_Add");

        btEdit.setText("_Edit");

        btDelete.setText("_Delete");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spSportTypes, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btAdd)
                    .addComponent(btEdit)
                    .addComponent(btDelete))
                .addContainerGap())
            .addComponent(separator, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(329, Short.MAX_VALUE)
                .addComponent(btClose)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btAdd, btDelete, btEdit});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btEdit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btDelete))
                    .addComponent(spSportTypes, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btClose;
    private javax.swing.JButton btDelete;
    private javax.swing.JButton btEdit;
    private javax.swing.JList liSportTypes;
    private javax.swing.JSeparator separator;
    private javax.swing.JScrollPane spSportTypes;
    // End of variables declaration//GEN-END:variables

    /**
     * Cell renderer implementation for the sport type list.
     */
    class SportTypeCellRenderer extends ListCellRendererOddEven {
        
        /** Standard c'tor. */
        public SportTypeCellRenderer () {
            super (context.getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_ODD),
                context.getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_EVEN));
        }

        /** Returns the cell component for the specified value and index. */
        @Override
        public Component getListCellRendererComponent (
            JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            // get component by superclass renderer
            Component component = super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);
            
            // use sport type color as foreground (not for prototypes when no sport types defined)
            if (document.getSportTypeList ().size () > 0) {
                SportType sportType = document.getSportTypeList ().getAt (index);
                component.setForeground (sportType.getColor ());
            }
            return component;
        }    
    }
}
