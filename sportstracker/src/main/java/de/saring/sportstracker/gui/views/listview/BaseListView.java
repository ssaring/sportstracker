package de.saring.sportstracker.gui.views.listview;

import de.saring.sportstracker.gui.views.*;
import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.util.ResourceReader;
import de.saring.util.gui.ListUtils;
import de.saring.util.gui.TableCellRendererOddEven;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * This abstract class contains the common implementation for all views for
 * displaying entry data inside a table.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public abstract class BaseListView extends BaseView {

    private JScrollPane scrollPane;
    private JTable table;

    /**
     * This method returns the array of the table column names.
     * @return array of the table column names
     */
    protected abstract String[] getColumnNames ();

    /**
     * This method returns the table model to be used. 
     * @return the table model
     */
    protected abstract AbstractTableModel getTableModel ();
    
    /**
     * Returns the column index which stores the entry ID.
     * @return 
     */
    protected abstract int getIdColumnIndex();
    
    /**
     * Creates and initializes the table. The table model and the column names
     * must be created before this method gets called.
     */
    public final void createTable () {
        table = new JTable ();
        table.setAutoCreateRowSorter (true);
        table.getTableHeader ().setReorderingAllowed (false);

        scrollPane = new JScrollPane ();
        scrollPane.setViewportView (table);

        setLayout (new BorderLayout ());
        add (scrollPane, BorderLayout.CENTER);
        
        // set table model
        AbstractTableModel tableModel = getTableModel ();
        table.setModel (tableModel);
        tableModel.addTableModelListener (table);
        
        // setup table selection and sorting
        table.setSelectionMode (ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ListUtils.increaseTableRowHeight (table);
        
        // start with descending sort of the date column (date is always in the first column)
        table.getRowSorter().toggleSortOrder (0);
        table.getRowSorter().toggleSortOrder (0);
        
        // setup table renderer for all columns
        DefaultTableCellRenderer cellRenderer = getTableCellRender ();
        for (int i = 0; i < tableModel.getColumnCount (); i++) {
            table.getColumnModel ().getColumn (i).setCellRenderer (cellRenderer);
        }

        // add list selection listener => update entry actions on selection changes
        table.getSelectionModel().addListSelectionListener (new ListSelectionListener () {
            public void valueChanged (ListSelectionEvent e) {
                getView ().updateEntryActions ();
            }
        });

        // add mouse listener for double clicks => edit entry
        table.addMouseListener (new MouseAdapter () {
            @Override public void mouseClicked (MouseEvent e) {
                if ((e.getClickCount () == 2) &&
                    (table.getSelectedRowCount () == 1)) {
                    getController ().editEntry ();
                }
            }
        });
    }

    /**
     * Returns the displayed table.
     * @return the displayed table
     */
    protected final JTable getTable () {
        return table;
    }

    /**
     * This method returns the table cell renderer to be used.
     * @return the table cell renderer
     */
    protected DefaultTableCellRenderer getTableCellRender () {
        return new BaseListCellRenderer ();
    }

    /** 
     * Updates the view after data was modified.
     */
    public void updateView () {
        getTableModel ().fireTableDataChanged ();
    }

    /**
     * Removes the current selection in the page.
     */
    @Override
    public void removeSelection () {
        table.clearSelection ();
    }

    /**
     * Returns the number of selected entries/rows.
     * 
     * @return number of selected entries/rows
     */
    protected int getSelectedEntryCount() {
        return getTable ().getSelectedRowCount ();
    }
    
    /**
     * Returns an array of entry IDs for all selected rows.
     * 
     * @return array with IDs of all selected rows (empty array when no selection)
     */
    protected int[] getSelectedEntryIDs() {
        int[] selectedRows = getTable ().getSelectedRows ();
        int[] selectedIDs = new int[selectedRows.length];

        for (int i = 0; i < selectedIDs.length; i++) {
            int modelRow = getTable ().convertRowIndexToModel (selectedRows[i]);
            selectedIDs[i] = (Integer) getTableModel().getValueAt (modelRow, getIdColumnIndex());
        }
        return selectedIDs;
    }

    /**
     * Selects the table row with the specified entry ID. It also makes sure
     * that the selected row is visible.
     * 
     * @param entryId ID of the entry to be selected
     */
    protected void selectRowWithEntryId(int entryId) {
        int idColumnIndex = getIdColumnIndex();
        
        for (int i = 0; i < getTableModel().getRowCount(); i++) {
            int tempEntryId = (Integer) getTableModel().getValueAt (i, idColumnIndex);

            if (tempEntryId == entryId) {
                int rowIndex = getTable().convertRowIndexToView(i);
                getTable().setRowSelectionInterval(rowIndex, rowIndex);
                getTable().scrollRectToVisible(getTable().getCellRect(rowIndex, 0, true));
                return;
            }
        }
    }
    
    /**
     * Prints the content of the displayed entry list.
     * @param headerText the header text
     * @param footerText the footer text
     * @throws de.saring.sportstracker.core.STException
     */
    protected void printList (String headerText, String footerText) throws STException {
        
        // define print settings (fit to paper width, default oriantation is landscape)
        JTable.PrintMode printMode = JTable.PrintMode.FIT_WIDTH;        
        HashPrintRequestAttributeSet praSet = new HashPrintRequestAttributeSet ();
        praSet.add (OrientationRequested.LANDSCAPE);
        
        // set header and footer (with page number) on each page
        MessageFormat header = new MessageFormat (headerText);
        MessageFormat footer = new MessageFormat (footerText + " {0}");

        try {            
            table.print (printMode, header, footer, true, praSet, true);
        } 
        catch (PrinterException e) {
            throw new STException (STExceptionID.GUI_PRINT_VIEW_FAILED, 
                "Failed to print the entry list view ...", e);
        }
    }

    
    /**
     * Abstract table model implementation, must be extended by the child classes.
     */
    public abstract class BaseListTableModel extends AbstractTableModel {
        
        @Override public String getColumnName (int col) {
            return getColumnNames ()[col];
        }

        @Override public int getColumnCount () {
            return getColumnNames ().length;
        }
        
        @Override public boolean isCellEditable (int row, int col) {
            return false;
        }
    }

    
    /**
     * Cell renderer implementation for the notes table.
     */
    public class BaseListCellRenderer extends TableCellRendererOddEven {
        private DateFormat dFormat = SimpleDateFormat.getDateInstance (SimpleDateFormat.MEDIUM);

        /** Standard c'tor. */
        public BaseListCellRenderer () {
            super (
                getContext ().getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_ODD),
                getContext ().getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_EVEN));
        }

        @Override
        public Component getTableCellRendererComponent (
            JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

            // get component of superclass renderer for customization
            Component component = super.getTableCellRendererComponent (table, value, isSelected, hasFocus, rowIndex, vColIndex);

            // set formatted renderer text
            String text = formatText (value, rowIndex, vColIndex);
            setText (text);
            setToolTipText (text);
            return component;
        }

        /**
         * Returns the formatted text for the value to be rendered in the cell.
         * @param value the value assigned to the cell
         * @param rowIndex the row index of the cell
         * @param columnIndex the column index of the cell
         * @return the formatted text
         */
        protected String formatText (Object value, int rowIndex, int columnIndex) {
            if (value == null) {
                return "";
            }
            else if (value instanceof Date) {
                return dFormat.format ((Date) value);
            }
            else {
                return value.toString ();
            }
        }
    }
}
