/*
 * RolloverButton.java - Class for buttons that implement rollovers
 *
 * Copyright (C) 2002 Kris Kopicki
 * Portions copyright (C) 2003 Slava Pestov
 * Portions copyright (C) 2008 Stefan Saring
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.saring.util.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import de.saring.util.gui.mac.MacSpecials;

/**
 * If you wish to have rollovers on your buttons, use this class.
 *
 * Unlike the Swing rollover support, this class works outside of
 * <code>JToolBar</code>s, and does not require undocumented client
 * property hacks or JDK1.4-specific API calls.<p>
 *
 * Note: You should not call <code>setBorder()</code> on your buttons,
 * as they probably won't work properly.
 * 
 * When running on Mac OS X the button will just have the typical 
 * toolbar behaviour, rollovers are not commomn there. 
 */
public class RolloverButton extends JButton {

    private static final long serialVersionUID = -420069024598467830L;

    private boolean isMacOSX = MacSpecials.isMacOSX();
    
	/**
     * Setup the border (invisible initially)
     */
    public RolloverButton () {
        if (isMacOSX) {
            putClientProperty("JButton.buttonType", "toolbar");
        }
        else {
            addMouseListener (new MouseOverHandler ());
        }
    }

    /**
     * Setup the border (invisible initially)
     *
     * @param icon the icon of this button
     */
    public RolloverButton (Icon icon) {
        this ();
        setIcon (icon);
    }

    @Override
    public void updateUI () {
        super.updateUI ();
        if (!isMacOSX) {
            setBorderPainted (false);
            setRequestFocusEnabled (false);
        }
    }

    @Override
    public void setEnabled (boolean b) {
        super.setEnabled (b);
        if (!isMacOSX) {
            setBorderPainted (false);
            repaint ();
        }
    }

    @Override
    public void setBorderPainted (boolean b) {
        if (isMacOSX) {
            super.setBorderPainted(b);
        }
        else {
            try {
                revalidateBlocked = true;
                super.setBorderPainted (b);
                setContentAreaFilled (b);
            } 
            finally {
                revalidateBlocked = false;
            }
        }
    }

    /**
     * We block calls to revalidate() from a setBorderPainted(), for
     * performance reasons.
     */
    @Override
    public void revalidate () {
        if (isMacOSX) {
            super.revalidate();
        }
        else {
            if (!revalidateBlocked) {
                super.revalidate ();
            }
        }
    }

    @Override
    public void paint (Graphics g) {
        if (isMacOSX) {
            super.paint(g);
        }
        else {
            if (isEnabled ()) {
                super.paint (g);
            } else {
                Graphics2D g2 = (Graphics2D) g;
                g2.setComposite (alphaComposite);
                super.paint (g2);
            }
        }
    }
    
    private static final AlphaComposite alphaComposite = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.5f);
    private boolean revalidateBlocked;

    /**
     * Make the border visible/invisible on rollovers
     */
    class MouseOverHandler extends MouseAdapter {

        @Override
        public void mouseEntered (MouseEvent e) {
            setContentAreaFilled (true);
            setBorderPainted (isEnabled ());
        }

        @Override
        public void mouseExited (MouseEvent e) {
            setContentAreaFilled (false);
            setBorderPainted (false);
        }
    }
}
