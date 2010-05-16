package de.saring.util.gui;

import javax.swing.JList;
import javax.swing.JTable;

/**
 * Utility methods for Swing's list and table widgets.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class ListUtils {

    /**
     * Increases the default cell height of the specified list by using a prototype (2 pixels).
     * @param list the list to change
     */
    public static void increaseListCellHeight (JList list) {
        list.setPrototypeCellValue("Ag123");
        list.setFixedCellHeight (list.getFixedCellHeight () + 2);
    }

    /**
     * Increases the row height of the specified table (2 pixels).
     * @param table the table to change
     */
    public static void increaseTableRowHeight (JTable table) {
        table.setRowHeight (table.getRowHeight () + 2);
    }
}
