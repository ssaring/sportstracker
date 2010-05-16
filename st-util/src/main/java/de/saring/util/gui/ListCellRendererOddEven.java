package de.saring.util.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Custom list cell renderer based on the default implementation. It uses
 * customizable background colors for odd and even row numbers.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class ListCellRendererOddEven extends DefaultListCellRenderer {

    private Color colorBackgroundOdd, colorBackgroundEven;
    
    /**
     * Standard c'tor.
     * @param colorBackgroundOdd background color for odd rows
     * @param colorBackgroundEven background color for even rows
     */
    public ListCellRendererOddEven (Color colorBackgroundOdd, Color colorBackgroundEven) {
        this.colorBackgroundOdd = colorBackgroundOdd;
        this.colorBackgroundEven = colorBackgroundEven;
    }
    
    /** Returns the cell component for the specified value and index. */
    @Override
    public Component getListCellRendererComponent (
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // use superclass renderer
        Component component = super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);

        // set background color depending on row number (not when selected)
        if (!isSelected) {
            setBackground (index % 2 == 0 ? colorBackgroundOdd : colorBackgroundEven);
        }            
        return component;
    }    
}
