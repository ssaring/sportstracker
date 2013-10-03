package de.saring.util.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Custom list cell renderer based on the default implementation. It uses
 * customizable background colors for odd and even row numbers.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class ListCellRendererOddEven extends DefaultListCellRenderer {

    private static final long serialVersionUID = -6530870819648810487L;

    private Color colorBackgroundOdd, colorBackgroundEven;

    /**
     * Standard c'tor.
     *
     * @param colorBackgroundOdd background color for odd rows
     * @param colorBackgroundEven background color for even rows
     */
    public ListCellRendererOddEven(Color colorBackgroundOdd, Color colorBackgroundEven) {
        this.colorBackgroundOdd = colorBackgroundOdd;
        this.colorBackgroundEven = colorBackgroundEven;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        // use superclass renderer
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // set background color depending on row number (not when selected)
        if (!isSelected) {
            setBackground(index % 2 == 0 ? colorBackgroundOdd : colorBackgroundEven);
        }
        return component;
    }
}
