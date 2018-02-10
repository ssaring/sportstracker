package de.saring.util.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for IdObjectListChangeListener usage in IdObjectList. Test for a
 * list without listeners are allready in IdObjectListTest.
 *
 * @author Stefan Saring
 */
public class IdObjectListChangeListenerTest {

    private IdObjectList<DummyIdObject> idObjectList;
    private IdObjectListChangeListener listenerMock;

    @BeforeEach
    public void setUp() {
        idObjectList = new IdObjectList<>();
        idObjectList.set(new DummyIdObject(1));
        idObjectList.set(new DummyIdObject(2));
        idObjectList.set(new DummyIdObject(3));

        // create and init the listener mock
        listenerMock = mock(IdObjectListChangeListener.class);
        idObjectList.addListChangeListener(listenerMock);
    }

    /**
     * Test: Listener must be called when storing an object with the same ID
     * (replaced in the list).
     */
    @Test
    public void testSetReplace() {
        DummyIdObject changedObject = new DummyIdObject(2);

        idObjectList.set(changedObject);

        assertEquals(3, idObjectList.size());
        verify(listenerMock).listChanged(changedObject);
    }

    /**
     * Test: Listener must be called when storing an object with a new ID (added
     * to the list).
     */
    @Test
    public void testSetAdd() {
        DummyIdObject newObject = new DummyIdObject(5);

        idObjectList.set(newObject);

        assertEquals(4, idObjectList.size());
        verify(listenerMock).listChanged(newObject);
    }

    /**
     * Test: Both registered Listeners must be called when storing an object
     * with the same ID (replaced in the list).
     */
    @Test
    public void testSetReplaceTwoListeners() {
        DummyIdObject changedObject = new DummyIdObject(2);

        IdObjectListChangeListener listenerMock2 = mock(IdObjectListChangeListener.class);
        idObjectList.addListChangeListener(listenerMock2);

        idObjectList.set(changedObject);
        assertEquals(3, idObjectList.size());

        verify(listenerMock).listChanged(changedObject);
        verify(listenerMock2).listChanged(changedObject);
    }

    /**
     * Test: Listener must be called when removing an object with a known ID.
     */
    @Test
    public void testRemoveSuccess() {
        assertTrue(idObjectList.removeByID(2));

        assertEquals(2, idObjectList.size());
        verify(listenerMock).listChanged(null);
    }

    /**
     * Test: Listener must not be called when removing an object with an unknown
     * ID.
     */
    @Test
    public void testRemoveFailed() {
        assertFalse(idObjectList.removeByID(5));

        assertEquals(3, idObjectList.size());
        verifyZeroInteractions(listenerMock);
    }

    /**
     * Test: Listener must be called when replacing the list content with method clearAndAddAll().
     */
    @Test
    public void testClearAndAddAll() {

        ArrayList<DummyIdObject> tempEntries = new ArrayList<>();
        tempEntries.add(new DummyIdObject(5));
        tempEntries.add(new DummyIdObject(6));
        idObjectList.clearAndAddAll(tempEntries);

        assertEquals(2, idObjectList.size());
        verify(listenerMock).listChanged(null);
    }

    /**
     * Test: The read-methods must not call the Listener.
     */
    @Test
    public void testMethodsWithoutListenerNotification() {

        assertEquals(2, idObjectList.getAt(1).getId());
        assertEquals(2, idObjectList.getByID(2).getId());
        assertEquals(2, idObjectList.indexOf(idObjectList.getAt(2)));
        assertEquals(4, idObjectList.getNewID());
        assertEquals(3, idObjectList.size());
        assertEquals(1, idObjectList.iterator().next().getId());

        verifyZeroInteractions(listenerMock);
    }

    /**
     * Subclass of abstract class IdObject for testing.
     */
    static class DummyIdObject extends IdObject {

        public DummyIdObject(int id) {
            super(id);
        }
    }
}
