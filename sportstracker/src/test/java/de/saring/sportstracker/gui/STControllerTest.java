package de.saring.sportstracker.gui;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.data.ExerciseList;
import de.saring.sportstracker.gui.views.EntryView;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests of class STController/Impl. All the involved components will be
 * mocked via Mockito.
 *
 * @author Stefan Saring
 */
public class STControllerTest {

    private STController controller;

    private STContext contextMock;
    private STDocument documentMock;
    private STView viewMock;
    private EntryView exerciseViewMock;

    private static final int[] EXERCISE_IDS = new int[]{123, 234};
    private ExerciseList exerciseList;

    @Before
    public void setUp() {
        // create and init mocks
        contextMock = mock(STContext.class);
        documentMock = mock(STDocument.class);
        viewMock = mock(STView.class);
        exerciseViewMock = mock(EntryView.class);

        // create controller for testing
        controller = new STControllerImpl(contextMock, documentMock, viewMock);

        // create test exercise list
        exerciseList = new ExerciseList();
        for (int exeID : EXERCISE_IDS) {
            Exercise exe = new Exercise(exeID);
            exe.setDate(new Date());
            exerciseList.set(exe);
        }
    }

    /**
     * Test of method deleteEntry(). Scenario: There's no exercise selected, it
     * must delete nothing.
     */
    @Test
    public void testDeleteEntryNothingSelected() {

        when(viewMock.getCurrentView()).thenReturn(exerciseViewMock);
        when(exerciseViewMock.getSelectedExerciseCount()).thenReturn(0);
        when(exerciseViewMock.getSelectedNoteCount()).thenReturn(0);
        when(exerciseViewMock.getSelectedWeightCount()).thenReturn(0);

        controller.deleteEntry();

        verifyZeroInteractions(contextMock);
        verifyZeroInteractions(documentMock);
    }

    /**
     * Test of method deleteEntry(). Scenario: There's two exercise selected,
     * the user confirms that they must be deleted. The exercises must be
     * deleted and the view must be updated.
     */
    @Test
    public void testDeleteEntryConfirmed() {

        when(viewMock.getCurrentView()).thenReturn(exerciseViewMock);
        when(exerciseViewMock.getSelectedExerciseCount()).thenReturn(2);
        when(exerciseViewMock.getSelectedExerciseIDs()).thenReturn(EXERCISE_IDS);

        when(contextMock.showConfirmDialog(null, "st.view.confirm.delete.title",
                "st.view.confirm.delete.text")).thenReturn(JOptionPane.YES_OPTION);

        // create ExerciseList stored in document mock => must be empty after test
        when(documentMock.getExerciseList()).thenReturn(exerciseList);

        // register document mock as list change listener => must then be notified twice
        exerciseList.addListChangeListener(documentMock);

        controller.deleteEntry();

        assertEquals(0, exerciseList.size());
        verify(documentMock, times(2)).listChanged(null);
    }

    /**
     * Test of method deleteEntry(). Scenario: There's two exercise selected,
     * the user does not confirm the deletion - it must delete nothing.
     */
    @Test
    public void testDeleteEntryCancelled() {

        when(viewMock.getCurrentView()).thenReturn(exerciseViewMock);
        when(exerciseViewMock.getSelectedExerciseCount()).thenReturn(2);
        when(exerciseViewMock.getSelectedExerciseIDs()).thenReturn(EXERCISE_IDS);
        when(documentMock.getExerciseList()).thenReturn(exerciseList);

        when(contextMock.showConfirmDialog(null, "st.view.confirm.delete.title",
                "st.view.confirm.delete.text")).thenReturn(JOptionPane.NO_OPTION);

        controller.deleteEntry();

        assertEquals(2, exerciseList.size());
    }

    /**
     * Test of method print().
     */
    @Test
    public void testPrintSuccess() throws STException {
        when(viewMock.getCurrentView()).thenReturn(exerciseViewMock);

        controller.print();

        verify(exerciseViewMock).print();
    }
}
