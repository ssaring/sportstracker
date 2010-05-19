package de.saring.sportstracker.gui.dialogs;

import com.google.inject.Inject;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseFilter;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.gui.DialogUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

/**
 * This class is the implementation of the dialog for setting filter criterias
 * for the exercise list.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class FilterDialog extends JDialog {    
    private static final Logger LOGGER = Logger.getLogger (FilterDialog.class.getName ()); 
    
    private STContext context;
    private STDocument document;
    
    /** 
     * The exercise filter selected by the user when the dialog was closed.
     * Is null when the dialog was canceled. 
     */
    private ExerciseFilter selectedFilter = null;
    
    /** Constants for action and property names. */
    private static final String ACTION_CURRENT_WEEK = "st.dlg.filter.current_week";
    private static final String ACTION_CURRENT_MONTH = "st.dlg.filter.current_month";
    private static final String ACTION_CURRENT_YEAR = "st.dlg.filter.current_year";
    private static final String ACTION_ALL_TIME = "st.dlg.filter.all_time";
    private static final String ACTION_OK = "st.dlg.filter.ok";
    private static final String ACTION_CANCEL = "st.dlg.filter.cancel";

    
    /** 
     * Standard c'tor. The method setInitialFilter() muste be called before 
     * showing the dialog.
     * @param context the SportsTracker context
     * @param document the applications document component
     */
    @Inject
    public FilterDialog (STContext context, STDocument document) {        
        super (context.getMainFrame (), true);
        this.context = context;
        this.document = document;
        initComponents ();
        this.getRootPane ().setDefaultButton (btOK);
        
        // set date format in date pickers
        DateFormat dateFormat = DateFormat.getDateInstance (DateFormat.MEDIUM);
        dpDateStart.setFormats (dateFormat);
        dpDateEnd.setFormats (dateFormat);

        // don't show the link panel in date pickers ("today is ...")
        dpDateStart.setLinkPanel (null);
        dpDateEnd.setLinkPanel (null);

        // setup actions
        ActionMap actionMap = context.getSAFContext ().getActionMap (getClass (), this);
        btCurrentWeek.setAction (actionMap.get (ACTION_CURRENT_WEEK));
        btCurrentMonth.setAction (actionMap.get (ACTION_CURRENT_MONTH));
        btCurrentYear.setAction (actionMap.get (ACTION_CURRENT_YEAR));
        btAllTime.setAction (actionMap.get (ACTION_ALL_TIME));
        btOK.setAction (actionMap.get (ACTION_OK));
        
        javax.swing.Action aCancel = actionMap.get(ACTION_CANCEL);
        btCancel.setAction(aCancel);
        DialogUtils.setDialogEscapeKeyAction(this, aCancel);

        // fill the comoboxes with all sport types and intensity types
        cbSportType.removeAllItems ();
        cbSportType.addItem (context.getResReader ().getString ("st.dlg.filter.all.text"));
        for (SportType sportType : document.getSportTypeList ()) {
            cbSportType.addItem (sportType.getName ());
        }
        
        cbIntensity.removeAllItems ();
        cbIntensity.addItem (context.getResReader ().getString ("st.dlg.filter.all.text"));
        for (Exercise.IntensityType intensity : Exercise.IntensityType.values ()) {
            cbIntensity.addItem (intensity);
        }        
    }

    /**
     * Sets the initial exercise filter values for all controls.
     * @param iFilter the initial filter values
     */
    public void setInitialFilter (ExerciseFilter iFilter) {
        
        // preselect dates
        dpDateStart.setDate (iFilter.getDateStart ());
        dpDateEnd.setDate (iFilter.getDateEnd ());

        // preselect sport type from filter (when available)
        if (iFilter.getSportType() != null) {
            // the sport type index in the combobox is same as index in sport type list + 1
            int sportTypeIndex = document.getSportTypeList ().indexOf (iFilter.getSportType ());
            cbSportType.setSelectedIndex (sportTypeIndex + 1);            
        }
        
        // force the refill of sport subtype option menu
        updateForSelectedSportType ();

        // preselect sport subtype from filter (when available)
        if (iFilter.getSportType () != null && iFilter.getSportSubType () != null) {
            // the sport subtype index in the combobox is same as index in sport type + 1
            int subTypeIndex = iFilter.getSportType().getSportSubTypeList().indexOf (iFilter.getSportSubType());
            cbSportSubType.setSelectedIndex (subTypeIndex + 1);            
        }
        
        // preselect intensity from filter (when available)
        if (iFilter.getIntensity () != null) {
            cbIntensity.setSelectedItem (iFilter.getIntensity ());
        }

        // preselect equipment from filter (when available)
        if (iFilter.getSportType () != null && iFilter.getEquipment () != null) {
            // the equipment index in the combobox is same as index in sport type + 1
            int equipmentIndex = iFilter.getSportType().getEquipmentList ().indexOf (iFilter.getEquipment ());
            cbEquipment.setSelectedIndex (equipmentIndex + 1);            
        }
        
        // preselect comment string
        tfCommentString.setText (iFilter.getCommentSubString ());
        cbRegExpression.setSelected (iFilter.isRegularExpressionMode ());

        // add listener for sport subtype and equipment updates when sport type was selected
        cbSportType.addActionListener (new ActionListener () {
            public void actionPerformed (ActionEvent e) {
                updateForSelectedSportType ();
            }
        });        
    }
     
    /** 
     * Updates the sport subtype and equipment list depending on the current 
     * selected sport type.
     */
    private void updateForSelectedSportType () {
        cbSportSubType.removeAllItems ();
        cbSportSubType.addItem (context.getResReader ().getString ("st.dlg.filter.all.text"));
        cbEquipment.removeAllItems ();
        cbEquipment.addItem (context.getResReader ().getString ("st.dlg.filter.all.text"));

        // do nothing when no specific sport type is selected ("all" is the first item)
        int selSportTypeIndex = cbSportType.getSelectedIndex ();
        selSportTypeIndex--;
        if (selSportTypeIndex < 0) {
            return;
        }

        // append all sport subtypes and equipments of the selected sport type
        SportType selSportType = document.getSportTypeList ().getAt (selSportTypeIndex);
        for (SportSubType sportSubType : selSportType.getSportSubTypeList ()) {
            cbSportSubType.addItem (sportSubType.getName ());
        }
        for (Equipment equipment : selSportType.getEquipmentList ()) {
            cbEquipment.addItem (equipment.getName ());
        }
    }
        
    /**
     * Action for setting the filter time period for the current week.
     */
    @Action(name=ACTION_CURRENT_WEEK)
    public void setCurrentWeek () {
        
        Calendar cStart = Calendar.getInstance ();
        Calendar cEnd = Calendar.getInstance ();
        Calendar cNow = Calendar.getInstance ();
        cStart.clear ();
        cEnd.clear ();
        cStart.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), cNow.get (Calendar.DAY_OF_MONTH), 0, 0, 0);        
        cEnd.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), cNow.get (Calendar.DAY_OF_MONTH), 23, 59, 59);

        // compute the count of days of the week start
        int weekStartDayCount = 0;                
        if (document.getOptions ().isWeekStartSunday ()) {
            switch (cNow.get (Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:    weekStartDayCount = -1; break;
                case Calendar.TUESDAY:   weekStartDayCount = -2; break;
                case Calendar.WEDNESDAY: weekStartDayCount = -3; break;
                case Calendar.THURSDAY:  weekStartDayCount = -4; break;
                case Calendar.FRIDAY:    weekStartDayCount = -5; break;
                case Calendar.SATURDAY:  weekStartDayCount = -6; break;
            }
        } 
        else {
            switch (cNow.get (Calendar.DAY_OF_WEEK)) {
                case Calendar.TUESDAY:   weekStartDayCount = -1; break;
                case Calendar.WEDNESDAY: weekStartDayCount = -2; break;
                case Calendar.THURSDAY:  weekStartDayCount = -3; break;
                case Calendar.FRIDAY:    weekStartDayCount = -4; break;
                case Calendar.SATURDAY:  weekStartDayCount = -5; break;
                case Calendar.SUNDAY:    weekStartDayCount = -6; break;
            }
        }            
        
        // roll start and end date to week begin and end
        cStart.add (Calendar.DATE, weekStartDayCount);
        cEnd.add (Calendar.DATE, weekStartDayCount + 6);
        dpDateStart.setDate (cStart.getTime ());
        dpDateEnd.setDate (cEnd.getTime ());
    }
    
    /**
     * Action for setting the filter time period for the current month.
     */
    @Action(name=ACTION_CURRENT_MONTH)
    public void setCurrentMonth () {
        Calendar cStart = Calendar.getInstance ();
        Calendar cEnd = Calendar.getInstance ();
        Calendar cNow = Calendar.getInstance ();
        cStart.clear ();
        cEnd.clear ();
        cStart.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), 1, 0, 0, 0);        
        cEnd.set (cNow.get (Calendar.YEAR), cNow.get (Calendar.MONTH), cNow.getActualMaximum (Calendar.DAY_OF_MONTH), 23, 59, 59);
        dpDateStart.setDate (cStart.getTime ());
        dpDateEnd.setDate (cEnd.getTime ());
    }
    
    /**
     * Action for setting the filter time period for the current year.
     */
    @Action(name=ACTION_CURRENT_YEAR)
    public void setCurrentYear () {
        Calendar cStart = Calendar.getInstance ();
        Calendar cEnd = Calendar.getInstance ();
        Calendar cNow = Calendar.getInstance ();
        cStart.clear ();
        cEnd.clear ();
        cStart.set (cNow.get (Calendar.YEAR), 1-1, 1, 0, 0, 0);        
        cEnd.set (cNow.get (Calendar.YEAR), 12-1, 31, 23, 59, 59);
        dpDateStart.setDate (cStart.getTime ());
        dpDateEnd.setDate (cEnd.getTime ());
    }
    
    /**
     * Action for setting the filter time period for all the time.
     */
    @Action(name=ACTION_ALL_TIME)
    public void setAllTime () {
        Calendar cStart = Calendar.getInstance ();
        Calendar cEnd = Calendar.getInstance ();
        cStart.clear ();
        cEnd.clear ();
        cStart.set (1900, 1-1, 1, 0, 0, 0);        
        cEnd.set (2999, 12-1, 31, 23, 59, 59);        
        dpDateStart.setDate (cStart.getTime ());
        dpDateEnd.setDate (cEnd.getTime ());
    }
    
    /**
     * Action for closing the dialog with the OK button.
     */
    @Action(name=ACTION_OK)
    public void ok () {

        // create and fill filter criteria object
        ExerciseFilter filter = new ExerciseFilter ();

        // check the start and end dates
        if (dpDateStart.getDate () == null) {
            context.showMessageDialog (this, JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.dlg.filter.error.date");
            dpDateStart.requestFocus ();
            return;
        }
        
        if (dpDateEnd.getDate () == null) {
            context.showMessageDialog (this, JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.dlg.filter.error.date");
            dpDateEnd.requestFocus ();
            return;
        }
        
        // create start and end dates (start date at day start and end date at 
        // day end, otherwise the exercises of the last day will not be included)
        Calendar cTemp = Calendar.getInstance ();
        cTemp.clear ();
        cTemp.setTime (dpDateStart.getDate ());
        cTemp.set (Calendar.HOUR_OF_DAY, 0);
        cTemp.set (Calendar.MINUTE, 0);
        cTemp.set (Calendar.SECOND, 0);
        filter.setDateStart (cTemp.getTime ());
        
        cTemp.clear ();
        cTemp.setTime (dpDateEnd.getDate ());
        cTemp.set (Calendar.HOUR_OF_DAY, 23);
        cTemp.set (Calendar.MINUTE, 59);
        cTemp.set (Calendar.SECOND, 59);
        filter.setDateEnd (cTemp.getTime ());

        // make sure that start date is before end date
        if (!filter.getDateStart ().before (filter.getDateEnd ())) {
            context.showMessageDialog (this, JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.dlg.filter.error.start_after_end");            
            return;
        }

        // get selected sport type, subtype and equipment 
        // (index - 1, because the first item is "all")
        int selSportTypeIndex = cbSportType.getSelectedIndex () - 1;
        if (selSportTypeIndex >= 0) {
            filter.setSportType (
                document.getSportTypeList ().getAt (selSportTypeIndex));
            
            int selSportSubTypeIndex = cbSportSubType.getSelectedIndex () - 1;
            if (selSportSubTypeIndex >= 0) {
                filter.setSportSubType (
                    filter.getSportType().getSportSubTypeList ().getAt (selSportSubTypeIndex));
            }

            int selEquipmentIndex = cbEquipment.getSelectedIndex () - 1;
            if (selEquipmentIndex >= 0) {
                filter.setEquipment (
                    filter.getSportType().getEquipmentList ().getAt (selEquipmentIndex));
            }
        }        

        // get selected intensity (first item is "all")
        int selIntensityIndex = cbIntensity.getSelectedIndex () - 1;
        if (selIntensityIndex >= 0) {
            filter.setIntensity ((Exercise.IntensityType) cbIntensity.getSelectedItem ());
        }

        // get inputs for comment search
        filter.setCommentSubString (tfCommentString.getText ().trim ());
        filter.setRegularExpressionMode (cbRegExpression.isSelected ());

        // check regular expression, when this mode is enabled
        if (filter.isRegularExpressionMode ()) {
            try {
                Pattern.compile (filter.getCommentSubString ());
            }
            catch (Exception e) {
                // syntax error in regular expression => the user has to correct it
                LOGGER.log (Level.WARNING, "Syntax error in the regular expression search string: " 
                    + filter.getCommentSubString (), e);
                tfCommentString.selectAll ();
                context.showMessageDialog (this, JOptionPane.ERROR_MESSAGE, 
                    "common.error", "st.dlg.filter.error.reg_expression_error");            
                tfCommentString.requestFocus ();
                return;
            }
        }

        // finally store the new filter
        selectedFilter = filter;
        this.dispose ();
    }
    
    /**
     * Action for closing the dialog with the Cancel button.
     */
    @Action(name=ACTION_CANCEL)
    public void cancel () {
        this.dispose ();
    }
    
    /**
     * Returns the exercise filter selected by the user. It returns null
     * when the dialog is still open or was canceled.
     * @return the selected exercise filter or null
     */
    public ExerciseFilter getSelectedFilter () {
        return selectedFilter;
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btOK = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        laTimePeriod = new javax.swing.JLabel();
        laFrom = new javax.swing.JLabel();
        laTo = new javax.swing.JLabel();
        laFilter = new javax.swing.JLabel();
        pFilterDataGrid = new javax.swing.JPanel();
        pDataLeft = new javax.swing.JPanel();
        laSportType = new javax.swing.JLabel();
        cbSportType = new javax.swing.JComboBox();
        pDataCenter = new javax.swing.JPanel();
        laSportSubType = new javax.swing.JLabel();
        cbSportSubType = new javax.swing.JComboBox();
        pDataRight = new javax.swing.JPanel();
        laIntensity = new javax.swing.JLabel();
        cbIntensity = new javax.swing.JComboBox();
        laCommentString = new javax.swing.JLabel();
        tfCommentString = new javax.swing.JTextField();
        cbRegExpression = new javax.swing.JCheckBox();
        separator = new javax.swing.JSeparator();
        pTimeButtons = new javax.swing.JPanel();
        btCurrentWeek = new javax.swing.JButton();
        btCurrentMonth = new javax.swing.JButton();
        btCurrentYear = new javax.swing.JButton();
        btAllTime = new javax.swing.JButton();
        dpDateStart = new org.jdesktop.swingx.JXDatePicker();
        dpDateEnd = new org.jdesktop.swingx.JXDatePicker();
        laEquipment = new javax.swing.JLabel();
        cbEquipment = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("st.dlg.filter"); // NOI18N
        setResizable(false);

        btOK.setText("OK");
        btOK.setName("btOK"); // NOI18N

        btCancel.setText("Cancel");
        btCancel.setName("btCancel"); // NOI18N

        laTimePeriod.setFont(laTimePeriod.getFont().deriveFont(laTimePeriod.getFont().getStyle() | java.awt.Font.BOLD));
        laTimePeriod.setText("_Time Period");
        laTimePeriod.setName("st.dlg.filter.time_period"); // NOI18N

        laFrom.setText("_From:");
        laFrom.setName("st.dlg.filter.from"); // NOI18N

        laTo.setText("_to:");
        laTo.setName("st.dlg.filter.to"); // NOI18N

        laFilter.setFont(laFilter.getFont().deriveFont(laFilter.getFont().getStyle() | java.awt.Font.BOLD));
        laFilter.setText("_Filter");
        laFilter.setName("st.dlg.filter.filter"); // NOI18N

        pFilterDataGrid.setName("pFilterDataGrid"); // NOI18N
        pFilterDataGrid.setLayout(new java.awt.GridLayout(1, 3, 20, 0));

        pDataLeft.setName("pDataLeft"); // NOI18N

        laSportType.setText("_Sport type:");
        laSportType.setName("st.dlg.filter.sport_type"); // NOI18N

        cbSportType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1" }));
        cbSportType.setName("cbSportType"); // NOI18N

        javax.swing.GroupLayout pDataLeftLayout = new javax.swing.GroupLayout(pDataLeft);
        pDataLeft.setLayout(pDataLeftLayout);
        pDataLeftLayout.setHorizontalGroup(
            pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addComponent(laSportType)
                .addContainerGap(89, Short.MAX_VALUE))
            .addComponent(cbSportType, 0, 167, Short.MAX_VALUE)
        );
        pDataLeftLayout.setVerticalGroup(
            pDataLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataLeftLayout.createSequentialGroup()
                .addComponent(laSportType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSportType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pFilterDataGrid.add(pDataLeft);

        pDataCenter.setName("pDataCenter"); // NOI18N

        laSportSubType.setText("_Sport subtype:");
        laSportSubType.setName("st.dlg.filter.sport_subtype"); // NOI18N

        cbSportSubType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1" }));
        cbSportSubType.setName("cbSportSubType"); // NOI18N

        javax.swing.GroupLayout pDataCenterLayout = new javax.swing.GroupLayout(pDataCenter);
        pDataCenter.setLayout(pDataCenterLayout);
        pDataCenterLayout.setHorizontalGroup(
            pDataCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataCenterLayout.createSequentialGroup()
                .addComponent(laSportSubType)
                .addContainerGap(66, Short.MAX_VALUE))
            .addComponent(cbSportSubType, 0, 167, Short.MAX_VALUE)
        );
        pDataCenterLayout.setVerticalGroup(
            pDataCenterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataCenterLayout.createSequentialGroup()
                .addComponent(laSportSubType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSportSubType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pFilterDataGrid.add(pDataCenter);

        pDataRight.setName("pDataRight"); // NOI18N

        laIntensity.setText("_Intensity:");
        laIntensity.setName("st.dlg.filter.intensity"); // NOI18N

        cbIntensity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1" }));
        cbIntensity.setName("cbIntensity"); // NOI18N

        javax.swing.GroupLayout pDataRightLayout = new javax.swing.GroupLayout(pDataRight);
        pDataRight.setLayout(pDataRightLayout);
        pDataRightLayout.setHorizontalGroup(
            pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addComponent(laIntensity)
                .addContainerGap(102, Short.MAX_VALUE))
            .addComponent(cbIntensity, 0, 167, Short.MAX_VALUE)
        );
        pDataRightLayout.setVerticalGroup(
            pDataRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pDataRightLayout.createSequentialGroup()
                .addComponent(laIntensity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbIntensity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pFilterDataGrid.add(pDataRight);

        laCommentString.setText("_String in comments:");
        laCommentString.setName("st.dlg.filter.string_comments"); // NOI18N

        tfCommentString.setName("tfCommentString"); // NOI18N

        cbRegExpression.setText("_Regular expression mode (case sensitive)");
        cbRegExpression.setName("st.dlg.filter.reg_expression"); // NOI18N

        separator.setName("separator"); // NOI18N

        pTimeButtons.setName("pTimeButtons"); // NOI18N
        pTimeButtons.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        btCurrentWeek.setText("_Current Week");
        btCurrentWeek.setName("btCurrentWeek"); // NOI18N
        pTimeButtons.add(btCurrentWeek);

        btCurrentMonth.setText("_Current Month");
        btCurrentMonth.setName("btCurrentMonth"); // NOI18N
        pTimeButtons.add(btCurrentMonth);

        btCurrentYear.setText("_Current Year");
        btCurrentYear.setName("btCurrentYear"); // NOI18N
        pTimeButtons.add(btCurrentYear);

        btAllTime.setText("_All Time");
        btAllTime.setName("btAllTime"); // NOI18N
        pTimeButtons.add(btAllTime);

        dpDateStart.setName("dpDateStart"); // NOI18N

        dpDateEnd.setName("dpDateEnd"); // NOI18N

        laEquipment.setText("_Equipment:");
        laEquipment.setName("st.dlg.filter.equipment"); // NOI18N

        cbEquipment.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEquipment.setName("cbEquipment"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(laTimePeriod)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(laFrom)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dpDateStart, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(laTo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dpDateEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(btCancel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btOK))
                                    .addComponent(pTimeButtons, javax.swing.GroupLayout.PREFERRED_SIZE, 541, Short.MAX_VALUE)))
                            .addComponent(laFilter)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(laEquipment)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbEquipment, 0, 451, Short.MAX_VALUE))
                            .addComponent(pFilterDataGrid, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(laCommentString)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfCommentString, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
                            .addComponent(cbRegExpression))))
                .addContainerGap())
            .addComponent(separator, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btCancel, btOK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(laTimePeriod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laFrom)
                    .addComponent(dpDateStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(laTo)
                    .addComponent(dpDateEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pTimeButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(laFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pFilterDataGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laEquipment)
                    .addComponent(cbEquipment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(laCommentString)
                    .addComponent(tfCommentString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbRegExpression)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btOK)
                    .addComponent(btCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAllTime;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btCurrentMonth;
    private javax.swing.JButton btCurrentWeek;
    private javax.swing.JButton btCurrentYear;
    private javax.swing.JButton btOK;
    private javax.swing.JComboBox cbEquipment;
    private javax.swing.JComboBox cbIntensity;
    private javax.swing.JCheckBox cbRegExpression;
    private javax.swing.JComboBox cbSportSubType;
    private javax.swing.JComboBox cbSportType;
    private org.jdesktop.swingx.JXDatePicker dpDateEnd;
    private org.jdesktop.swingx.JXDatePicker dpDateStart;
    private javax.swing.JLabel laCommentString;
    private javax.swing.JLabel laEquipment;
    private javax.swing.JLabel laFilter;
    private javax.swing.JLabel laFrom;
    private javax.swing.JLabel laIntensity;
    private javax.swing.JLabel laSportSubType;
    private javax.swing.JLabel laSportType;
    private javax.swing.JLabel laTimePeriod;
    private javax.swing.JLabel laTo;
    private javax.swing.JPanel pDataCenter;
    private javax.swing.JPanel pDataLeft;
    private javax.swing.JPanel pDataRight;
    private javax.swing.JPanel pFilterDataGrid;
    private javax.swing.JPanel pTimeButtons;
    private javax.swing.JSeparator separator;
    private javax.swing.JTextField tfCommentString;
    // End of variables declaration//GEN-END:variables
    
}
