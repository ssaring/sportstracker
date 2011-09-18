package de.saring.sportstracker.gui.views;

import de.saring.util.data.IdDateObject;
import javax.inject.Inject;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.sportstracker.gui.STView;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 * This is the base class of all special entry view classes. It contains all the
 * common functionality and default implementations. 
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public abstract class BaseView extends JPanel implements EntryView {

    @Inject private STContext context;
    @Inject private STDocument document;
    @Inject private STView view;
    @Inject private STController controller;
    
    /**
     * Creates a new BaseView instance.
     */
    public BaseView () {
        // set the preferred initial size at first application start
        this.setPreferredSize (new Dimension (770, 500));
    }

    @Override
    public int getSelectedExerciseCount () {
        return 0;
    }

    @Override
    public int[] getSelectedExerciseIDs () {
        return new int[0];
    }

    @Override
    public int getSelectedNoteCount () {
        return 0;
    }

    @Override
    public int[] getSelectedNoteIDs () {
        return new int[0];
    }

    @Override
    public int getSelectedWeightCount () {
        return 0;
    }

    @Override
    public int[] getSelectedWeightIDs () {
        return new int[0];
    }

    @Override
    public void selectEntry(IdDateObject entry) {
        // TODO: implement in all views
        System.err.println("TODO: not implemented yet !");
    }

    protected STContext getContext () {
        return context;
    }

    protected STDocument getDocument () {
        return document;
    }

    protected STView getView () {
        return view;
    }
    
    protected STController getController () {
        return controller;
    }
}
