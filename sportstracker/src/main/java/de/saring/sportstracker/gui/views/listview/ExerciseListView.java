package de.saring.sportstracker.gui.views.listview;

import javax.inject.Singleton;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.STController;
import de.saring.util.data.IdObject;
import de.saring.util.unitcalc.FormatUtils;
import java.awt.Component;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;

/**
 * This view class displays all the user exercises (or a filtered list) in
 * a table view.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
@Singleton
public class ExerciseListView extends BaseListView {
    
    /** Indice constants for columns order. */
    private static final int MAX_COLUMN_COUNT = 12;
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TYPE = 1;
    private static final int COLUMN_SUBTYPE = 2;
    private static final int COLUMN_DURATION = 3;
    private static final int COLUMN_INTENSITY = 4;
    private static final int COLUMN_DISTANCE = 5;
    private static final int COLUMN_AVG_SPEED = 6;
    private static final int COLUMN_AVG_HEARTRATE = 7;
    private static final int COLUMN_ASCENT = 8;
    private static final int COLUMN_ENERGY = 9;
    private static final int COLUMN_EQUIPMENT = 10;
    private static final int COLUMN_COMMENT = 11;
    private static final int HIDDEN_COLUMN_ID = 99;

    private String[] columnNames;
    private ArrayList<TableColumn> removedColumns;

    /** The table model of the exercises table. */
    private ExercisesTableModel tmExercises;

    
    @Override
    public void initView () {
        
        // initialize array of column names
        columnNames = new String[MAX_COLUMN_COUNT];
        columnNames[COLUMN_DATE]            = getContext ().getResReader ().getString ("st.exerciselistview.date");
        columnNames[COLUMN_TYPE]            = getContext ().getResReader ().getString ("st.exerciselistview.type");
        columnNames[COLUMN_SUBTYPE]         = getContext ().getResReader ().getString ("st.exerciselistview.subtype");
        columnNames[COLUMN_DURATION]        = getContext ().getResReader ().getString ("st.exerciselistview.duration");
        columnNames[COLUMN_INTENSITY]       = getContext ().getResReader ().getString ("st.exerciselistview.intensity");
        columnNames[COLUMN_DISTANCE]        = getContext ().getResReader ().getString ("st.exerciselistview.distance");
        columnNames[COLUMN_AVG_SPEED]       = getContext ().getResReader ().getString ("st.exerciselistview.avg_speed");
        columnNames[COLUMN_AVG_HEARTRATE]   = getContext ().getResReader ().getString ("st.exerciselistview.avg_heartrate");
        columnNames[COLUMN_ASCENT]          = getContext ().getResReader ().getString ("st.exerciselistview.ascent");
        columnNames[COLUMN_ENERGY]          = getContext ().getResReader ().getString ("st.exerciselistview.energy");
        columnNames[COLUMN_EQUIPMENT]       = getContext ().getResReader ().getString ("st.exerciselistview.equipment");
        columnNames[COLUMN_COMMENT]         = getContext ().getResReader ().getString ("st.exerciselistview.comment");

        // create tablemodel
        tmExercises = new ExercisesTableModel ();

        // create table and setup its context menu
        createTable ();
        getPopupMenu().insert(getController().getActionMap ().get(STController.ACTION_EXERCISE_ADD), 0);

        // overwrite option to allow the user to reorder the columns
        getTable().getTableHeader ().setReorderingAllowed (true);
      
        // set column widths (no autoresizing, the width is usally not enough)
        getTable ().setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
        TableColumnModel tcModel = getTable ().getColumnModel ();
        tcModel.getColumn (COLUMN_DATE).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_TYPE).setPreferredWidth (100);
        tcModel.getColumn (COLUMN_SUBTYPE).setPreferredWidth (100);
        tcModel.getColumn (COLUMN_DURATION).setPreferredWidth (80);
        tcModel.getColumn (COLUMN_INTENSITY).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_DISTANCE).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_AVG_SPEED).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_AVG_HEARTRATE).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_ASCENT).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_ENERGY).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_EQUIPMENT).setPreferredWidth (90);
        tcModel.getColumn (COLUMN_COMMENT).setPreferredWidth (500);

        // initialize list of removed columns
        removedColumns = new ArrayList<>();
    }
    
    @Override
    public void updateView () {
        
        // add wanted and remove unwanted columns (not the best way to do it
        // on each update, but it doesn't cost much)
        addOrRemoveColumn (COLUMN_AVG_HEARTRATE,
            getDocument().getOptions().isListViewShowAvgHeartrate ());
        addOrRemoveColumn (COLUMN_ASCENT,
            getDocument().getOptions().isListViewShowAscent ());
        addOrRemoveColumn (COLUMN_ENERGY,
            getDocument().getOptions().isListViewShowEnergy ());
        addOrRemoveColumn (COLUMN_EQUIPMENT,
            getDocument().getOptions().isListViewShowEquipment ());
        addOrRemoveColumn (COLUMN_COMMENT,
            getDocument().getOptions().isListViewShowComment ());

        super.updateView ();
    }

    private void addOrRemoveColumn (int columnIndex, boolean addOrRemove) {
        if (addOrRemove) {
            addColumnByIndex (columnIndex);
        }
        else {
            removeColumnByIndex (columnIndex);
        }
    }

    /**
     * Remove table column if identifier is found.
     * @param index index of column as defined in columnNames
     */
    private void removeColumnByIndex(int index) {
        try {            
            TableColumn column = getTable ().getColumn (columnNames[index]);
            removedColumns.add(column);
            getTable().removeColumn(column);
        }
        catch (IllegalArgumentException e) {
             // ignore when column name is unknown
        }
    }

    /*
     * Add a column to the table based on its identifier.
     * @param index index of column as defined in columnNames
     */
    private void addColumnByIndex(int index) {

        // check if the column already exists in current table
        try {
            getTable().getColumn(columnNames[index]);
            return;
        }
        catch (IllegalArgumentException e) {
            // the column is not in the table yet
        }

        // try to find column in the list of removed columns
        for (TableColumn removedColumn : removedColumns) {
            if (removedColumn.getIdentifier ().equals (columnNames[index])) {

                // try to find new column location
                int newColumnIndex = getTable ().getColumnCount ();
                for (int i = 0; i < getTable ().getColumnCount (); i++) {
                    String tempColumnName = getTable ().getColumnName (i);
                    if (getIndexForColumnName (tempColumnName) > index) {
                        // found existing column to place new column next to => break;
                        newColumnIndex = i;
                        break;
                    }
                }


                // add the column and move it to the right position
                getTable ().addColumn (removedColumn);
                getTable ().moveColumn (getTable ().getColumnCount () - 1, newColumnIndex);

                // done => delete re-added column in removed columns and quit
                removedColumns.remove(removedColumn);
                return;
            }
        }
    }

    private int getIndexForColumnName (String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnName.equals(columnNames[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException ("Unknown column name: " + columnName);
    }

    protected String[] getColumnNames () {
        return columnNames;
    }

    protected AbstractTableModel getTableModel () {
        return tmExercises;
    }
    
    @Override
    protected int getIdColumnIndex() {
        return HIDDEN_COLUMN_ID;
    }

    @Override
    protected DefaultTableCellRenderer getTableCellRender () {
        return new  ExercisesCellRenderer();
    }

    @Override
    public int getSelectedExerciseCount () {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedExerciseIDs () {
        return getSelectedEntryIDs();
    }
    
    @Override
    public void selectEntry(IdObject entry) {
        if (entry instanceof Exercise) {
            selectRowWithEntryId(entry.getId());
        }
    }

    @Override
    public void print () throws STException {
        if (!getController ().checkForExistingExercises ()) {
            return;
        }
        
        // display confirmation dialog when the list currently displays too many exercises
        if (getTable ().getRowCount () > 100) {
            if (getContext ().showConfirmDialog (getContext ().getMainFrame (), 
                "st.exerciselistview.confirm.print_many_exercises.title",
                "st.exerciselistview.confirm.print_many_exercises.text") != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        printList (
            getContext ().getResReader ().getString ("st.exerciselistview.print.title"),
            getContext ().getResReader ().getString ("st.exerciselistview.print.page"));
    }
    
    /**
     * Table model implementation for the exercises table.
     */
    class ExercisesTableModel extends BaseListView.BaseListTableModel {
        
        /** Returns the number of exercises. */
        @Override public int getRowCount() {
            return getView ().getDisplayedExercises ().size ();
        }

        /** Returns the class of the specified column (needes for sorting). */
        @Override public Class<?> getColumnClass (int col) {
            switch (col) {                
                case COLUMN_DATE:
                    return Date.class;
                case COLUMN_DURATION: 
                case COLUMN_AVG_HEARTRATE: 
                case COLUMN_ASCENT:
                case COLUMN_ENERGY:
                    return Integer.class;
                case COLUMN_INTENSITY:
                    return Exercise.IntensityType.class;
                case COLUMN_DISTANCE: 
                    return Float.class;
                case COLUMN_AVG_SPEED: 
                    return Float.class;
                default:
                    return Object.class;
            }
        }
        
        /** Returns the exercise value for the specified column. */
        @Override public Object getValueAt (int row, int col) {            
            Exercise exercise = getView ().getDisplayedExercises ().getAt (row);

            switch (col) {                
                case COLUMN_DATE:
                    return exercise.getDate ();
                case COLUMN_TYPE:
                    return exercise.getSportType ().getName ();
                case COLUMN_SUBTYPE: 
                    return exercise.getSportSubType ().getName ();
                case COLUMN_DURATION: 
                    return exercise.getDuration ();
                case COLUMN_INTENSITY:
                    return exercise.getIntensity ();
                case COLUMN_DISTANCE: 
                    return exercise.getDistance ();
                case COLUMN_AVG_SPEED: 
                    return exercise.getAvgSpeed ();
                case COLUMN_AVG_HEARTRATE: 
                    return exercise.getAvgHeartRate ();
                case COLUMN_COMMENT:
                    return exercise.getComment ();
                case COLUMN_ASCENT:
                    return exercise.getAscent();
                case COLUMN_ENERGY:
                    return exercise.getCalories();
                case COLUMN_EQUIPMENT:
                    return exercise.getEquipment () != null ?
                        exercise.getEquipment ().getName () : null;
                case HIDDEN_COLUMN_ID:
                    return exercise.getId ();
            }
            return null;
        }
    }
    
    /**
     * Cell renderer implementation for the exercises table.
     */
    class ExercisesCellRenderer extends BaseListView.BaseListCellRenderer {

        @Override
        public Component getTableCellRendererComponent (
            JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

            // get exercise of this cell => use the sport type color as foreground
            int modelRow = getTable ().convertRowIndexToModel (rowIndex);
            Exercise exercise = getView ().getDisplayedExercises ().getAt (modelRow);
            setForeground (exercise.getSportType ().getColor ());            

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
        }

        @Override
        protected String formatText (Object value, int rowIndex, int columnIndex) {
            String colName = getTable().getColumnName(columnIndex);
            FormatUtils formatUtils = getContext ().getFormatUtils ();

            if (colName.equals(columnNames[COLUMN_DURATION])) {
                return formatUtils.seconds2TimeString ((Integer) value);
            }
            if (colName.equals(columnNames[COLUMN_DISTANCE])) {
                return formatUtils.distanceToString ((Float) value, 2);
            }
            if (colName.equals(columnNames[COLUMN_AVG_SPEED])) {
                return formatUtils.speedToString ((Float) value, 2);
            }
            if (colName.equals(columnNames[COLUMN_AVG_HEARTRATE])) {
                return formatUtils.heartRateToString ((Integer) value);
            }
            if (colName.equals(columnNames[COLUMN_ASCENT])) {
                return formatUtils.heightToString((Integer) value);
            }
            if (colName.equals(columnNames[COLUMN_ENERGY])) {
                return formatUtils.caloriesToString ((Integer) value);
            }
            return super.formatText(value, rowIndex, columnIndex);
        }
    }
}
