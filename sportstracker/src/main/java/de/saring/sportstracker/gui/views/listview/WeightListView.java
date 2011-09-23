package de.saring.sportstracker.gui.views.listview;

import javax.inject.Singleton;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Weight;
import de.saring.util.data.IdObject;
import de.saring.util.unitcalc.FormatUtils;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * This view class displays the list of the users weight entries in a table view.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
@Singleton
public class WeightListView extends BaseListView {
    
    /** Constants for columns indexes. */
    private static final int COLUMN_COUNT = 3;
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_WEIGHT = 1;
    private static final int COLUMN_COMMENT = 2;
    private static final int HIDDEN_COLUMN_ID = 3;

    private String[] columnNames;

    /** The table model of the weights table. */
    private WeightsTableModel tmWeights;

    
    @Override
    public void initView () {
        
        // initialize array of column names
        columnNames = new String[COLUMN_COUNT];
        columnNames[0] = getContext ().getResReader ().getString ("st.weightlistview.date");
        columnNames[1] = getContext ().getResReader ().getString ("st.weightlistview.weight");
        columnNames[2] = getContext ().getResReader ().getString ("st.weightlistview.comment");

        // create tablemodel
        tmWeights = new WeightsTableModel ();

        // create basic view
        createTable ();
        
        // set column widths 
        getTable ().setAutoResizeMode (JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel tcModel = getTable ().getColumnModel ();
        tcModel.getColumn (COLUMN_DATE).setWidth (80);
        tcModel.getColumn (COLUMN_WEIGHT).setWidth (80);
        tcModel.getColumn (COLUMN_COMMENT).setPreferredWidth (520);
    }

    @Override
    protected String[] getColumnNames () {
        return columnNames;
    }

    @Override
    protected AbstractTableModel getTableModel () {
        return tmWeights;
    }
    
    @Override
    protected int getIdColumnIndex() {
        return HIDDEN_COLUMN_ID;
    }

    @Override
    protected DefaultTableCellRenderer getTableCellRender () {
        return new WeightCellRenderer();
    }

    @Override
    public int getSelectedWeightCount () {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedWeightIDs () {
        return getSelectedEntryIDs();
    }
    
    @Override
    public void selectEntry(IdObject entry) {
        if (entry instanceof Weight) {
            selectRowWithEntryId(entry.getId());
        }
    }

    @Override
    public void print () throws STException {
        if (!getController ().checkForExistingWeights ()) {
            return;
        }
        
        printList (
            getContext ().getResReader ().getString ("st.weightlistview.print.title"),
            getContext ().getResReader ().getString ("st.weightlistview.print.page"));
    }
    
    /**
     * Table model implementation for the Weights table.
     */
    class WeightsTableModel extends BaseListView.BaseListTableModel {
        
        /** Returns the number of weights. */
        @Override public int getRowCount() {
            return getDocument ().getWeightList ().size ();
        }

        /** Returns the class of the specified column (needed for sorting). */
        @Override public Class<?> getColumnClass (int col) {
            switch (col) {                
                case COLUMN_DATE:
                    return Date.class;
                case COLUMN_WEIGHT:
                    return Float.class;
                default:
                    return Object.class;
            }
        }

        /** Returns the exercise value for the specified column. */
        @Override public Object getValueAt (int row, int col) {            
            Weight weight = getDocument ().getWeightList ().getAt (row);

            switch (col) {                
                case COLUMN_DATE:
                    return weight.getDate ();
                case COLUMN_WEIGHT:
                    return weight.getValue ();
                case COLUMN_COMMENT:
                    return weight.getComment ();
                case HIDDEN_COLUMN_ID:
                    return weight.getId ();
            }
            return null;
        }
    }

    /**
     * Cell renderer implementation for the weight entries table.
     */
    class WeightCellRenderer extends BaseListView.BaseListCellRenderer {

        @Override
        protected String formatText (Object value, int rowIndex, int columnIndex) {
            FormatUtils formatUtils = getContext ().getFormatUtils ();

            switch (columnIndex) {
                case COLUMN_WEIGHT:
                    // use special format for the weight column
                    return formatUtils.weightToString ((Float) value, 2);
                case COLUMN_COMMENT:
                    return formatUtils.firstLineOfText ((String) value);
                default:
                    return super.formatText (value, rowIndex, columnIndex);
            }
        }
    }
}
