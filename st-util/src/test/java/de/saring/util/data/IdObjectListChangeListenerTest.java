package de.saring.util.data;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for IdObjectListChangeListener usage in IdObjectList. Test for a
 * list without listeners are allready in IdObjectListTest.
 *
 * @author Stefan Saring
 */
public class IdObjectListChangeListenerTest {

    private IdObjectList<DummyIdObject> idObjectList;
    private IdObjectListChangeListener listenerMock;

    @Before
    public void setUp() {
        idObjectList = new IdObjectList<>();
        idObjectList.set(new DummyIdObject(1));
        idObjectList.set(new DummyIdObject(2));
        idObjectList.set(new DummyIdObject(3));

        // create and init the listener mock
        listenerMock = EasyMock.createMock(IdObjectListChangeListener.class);
        idObjectList.addListChangeListener(listenerMock);
    }

    @After
    public void tearDown() {
        EasyMock.verify(listenerMock);
    }

    /**
     * Test: Listener must be called when storing an object with the same ID
     * (replaced in the list).
     */
    @Test
    public void testSetReplace() {
        DummyIdObject changedObject = new DummyIdObject(2);
        listenerMock.listChanged(changedObject);
        EasyMock.replay(listenerMock);

        idObjectList.set(changedObject);
        assertEquals(3, idObjectList.size());
    }

    /**
     * Test: Listener must be called when storing an object with a new ID (added
     * to the list).
     */
    @Test
    public void testSetAdd() {
        DummyIdObject newObject = new DummyIdObject(5);
        listenerMock.listChanged(newObject);
        EasyMock.replay(listenerMock);

        idObjectList.set(newObject);
        assertEquals(4, idObjectList.size());
    }

    /**
     * Test: Both registered Listeners must be called when storing an object
     * with the same ID (replaced in the list).
     */
    @Test
    public void testSetReplaceTwoListeners() {
        DummyIdObject changedObject = new DummyIdObject(2);

        listenerMock.listChanged(changedObject);
        EasyMock.replay(listenerMock);

        IdObjectListChangeListener listenerMock2 = EasyMock.createMock(IdObjectListChangeListener.class);
        idObjectList.addListChangeListener(listenerMock2);
        listenerMock2.listChanged(changedObject);
        EasyMock.replay(listenerMock2);

        idObjectList.set(changedObject);
        assertEquals(3, idObjectList.size());

        EasyMock.verify(listenerMock2);
    }

    /**
     * Test: Listener must be called when removing an object with a known ID.
     */
    @Test
    public void testRemoveSuccess() {
        listenerMock.listChanged(null);
        EasyMock.replay(listenerMock);

        assertTrue(idObjectList.removeByID(2));
        assertEquals(2, idObjectList.size());
    }

    /**
     * Test: Listener must not be called when removing an object with an unknown
     * ID.
     */
    @Test
    public void testRemoveFailed() {
        EasyMock.replay(listenerMock);

        assertFalse(idObjectList.removeByID(5));
        assertEquals(3, idObjectList.size());
    }

    /**
     * Test: The read-methods must not call the Listener.
     */
    @Test
    public void testMethodsWithoutListenerNotification() {
        EasyMock.replay(listenerMock);

        assertEquals(2, idObjectList.getAt(1).getId());
        assertEquals(2, idObjectList.getByID(2).getId());
        assertEquals(2, idObjectList.indexOf(idObjectList.getAt(2)));
        assertEquals(4, idObjectList.getNewID());
        assertEquals(3, idObjectList.size());
        assertEquals(1, idObjectList.iterator().next().getId());
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
