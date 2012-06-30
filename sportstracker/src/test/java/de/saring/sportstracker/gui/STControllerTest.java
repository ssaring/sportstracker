package de.saring.sportstracker.gui;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.gui.views.EntryView;
import java.util.Date;
import javax.swing.JOptionPane;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class STController/Impl. All the involved components will be 
 * mocked via EasyMock.
 * 
 * @author Stefan Saring
 */
public class STControllerTest {

    private STController controller;
    
    private STContext contextMock;
    private STDocument documentMock;
    private STView viewMock;    
    private EntryView exerciseViewMock;

    private static final int[] EXERCISE_IDS = new int[] {123, 234};
    
    @Before
    public void setUp () {
        // create and init mocks
        contextMock = EasyMock.createMock (STContext.class);
        documentMock = EasyMock.createMock (STDocument.class);
        viewMock = EasyMock.createMock (STView.class);                
        exerciseViewMock = EasyMock.createMock (EntryView.class);

        // create controller for testing
        controller = new STControllerImpl (contextMock, documentMock, viewMock);
    }

    @After
    public void tearDown () {
        // verify mocking usage
        EasyMock.verify (contextMock);
        EasyMock.verify (documentMock);
        EasyMock.verify (viewMock);
        EasyMock.verify (exerciseViewMock);
    }

    /**
     * Test of method deleteEntry(). Scenario:
     * There's no exercise selected, it must delete nothing.
     */
    @Test
    public void testDeleteEntryNothingSelected () {
        EasyMock.expect (viewMock.getCurrentView ()).andReturn (exerciseViewMock).times (3);
        EasyMock.expect (exerciseViewMock.getSelectedExerciseCount ()).andReturn (0);
        EasyMock.expect (exerciseViewMock.getSelectedNoteCount ()).andReturn (0);
        EasyMock.expect (exerciseViewMock.getSelectedWeightCount ()).andReturn (0);
        
        replayMocks ();
        controller.deleteEntry ();
    }

    /**
     * Test of method deleteEntry(). Scenario:
     * There's two exercise selected, the user confirms that they must be deleted. The 
     * exercises must be deleted and the view must be updated.
     */
    @Test
    public void testDeleteEntryConfirmed () {
        EasyMock.expect (viewMock.getCurrentView ()).andReturn (exerciseViewMock).times (2);
        EasyMock.expect (exerciseViewMock.getSelectedExerciseCount ()).andReturn (2);
        EasyMock.expect (exerciseViewMock.getSelectedExerciseIDs ()).andReturn (EXERCISE_IDS);

        EasyMock.expect (contextMock.getMainFrame ()).andReturn (null);
        EasyMock.expect (contextMock.showConfirmDialog (null, "st.view.confirm.delete.title", 
            "st.view.confirm.delete.text")).andReturn (JOptionPane.YES_OPTION);

        // create ExerciseList stored in document mock => must be empty after test
        ExerciseList exeList = new ExerciseList ();
        for (int exeID : EXERCISE_IDS) {
            Exercise exe = new Exercise (exeID);
            exe.setDate (new Date ());
            exeList.set (exe);
        }
        EasyMock.expect (documentMock.getExerciseList ()).andReturn (exeList);

        // register document mock as list change listener => must be called twice
        exeList.addListChangeListener (documentMock);
        documentMock.listChanged (null);
        documentMock.listChanged (null);
        
        replayMocks ();
        controller.deleteEntry ();

        assertEquals (0, exeList.size ());
    }

    /**
     * Test of method deleteEntry(). Scenario:
     * There's two exercise selected, the user does not confirm the deletion - 
     * it must delete nothing.
     */
    @Test
    public void testDeleteEntryCancelled () {
        EasyMock.expect (viewMock.getCurrentView ()).andReturn (exerciseViewMock).times (2);
        EasyMock.expect (exerciseViewMock.getSelectedExerciseCount ()).andReturn (2);
        EasyMock.expect (exerciseViewMock.getSelectedExerciseIDs ()).andReturn (EXERCISE_IDS);
        EasyMock.expect (documentMock.getExerciseList ()).andReturn (new ExerciseList ());

        EasyMock.expect (contextMock.getMainFrame ()).andReturn (null);
        EasyMock.expect (contextMock.showConfirmDialog (null, "st.view.confirm.delete.title", 
            "st.view.confirm.delete.text")).andReturn (JOptionPane.NO_OPTION);
        
        replayMocks ();
        controller.deleteEntry ();
    }
    
    /**
     * Test of method print(). 
     */
    @Test
    public void testPrintSuccess () throws STException {
        EasyMock.expect (viewMock.getCurrentView ()).andReturn (exerciseViewMock);
        exerciseViewMock.print ();
        
        replayMocks ();
        controller.print ();
    }

    private void replayMocks () {
        EasyMock.replay (contextMock);
        EasyMock.replay (documentMock);
        EasyMock.replay (viewMock);
        EasyMock.replay (exerciseViewMock);
    }
}
