package de.saring.util.data

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import java.util.ArrayList

/**
 * Unit tests for IdObjectListChangeListener usage in IdObjectList. Test for a list without listeners are already in
 * IdObjectListTest.
 *
 * @author Stefan Saring
 */
class IdObjectListChangeListenerTest {

    private lateinit var idObjectList: IdObjectList<DummyIdObject>
    private lateinit var listenerMock: IdObjectListChangeListener

    @BeforeEach
    fun setUp() {
        idObjectList = IdObjectList()
        idObjectList.set(DummyIdObject(1))
        idObjectList.set(DummyIdObject(2))
        idObjectList.set(DummyIdObject(3))

        // create and init the listener mock
        listenerMock = mock(IdObjectListChangeListener::class.java)
        idObjectList.addListChangeListener(listenerMock)
    }

    /**
     * Test: Listener must be called when storing an object with the same ID (replaced in the list).
     */
    @Test
    fun testSetReplace() {
        val changedObject = DummyIdObject(2)

        idObjectList.set(changedObject)

        assertEquals(3, idObjectList.size())
        verify(listenerMock).listChanged(changedObject)
    }

    /**
     * Test: Listener must be called when storing an object with a new ID (added to the list).
     */
    @Test
    fun testSetAdd() {
        val newObject = DummyIdObject(5)

        idObjectList.set(newObject)

        assertEquals(4, idObjectList.size())
        verify(listenerMock).listChanged(newObject)
    }

    /**
     * Test: Both registered Listeners must be called when storing an object with the same ID (replaced in the list).
     */
    @Test
    fun testSetReplaceTwoListeners() {
        val changedObject = DummyIdObject(2)

        val listenerMock2 = mock(IdObjectListChangeListener::class.java)
        idObjectList.addListChangeListener(listenerMock2)

        idObjectList.set(changedObject)
        assertEquals(3, idObjectList.size())

        verify(listenerMock).listChanged(changedObject)
        verify(listenerMock2).listChanged(changedObject)
    }

    /**
     * Test: Listener must be called when removing an object with a known ID.
     */
    @Test
    fun testRemoveSuccess() {
        assertTrue(idObjectList.removeByID(2))

        assertEquals(2, idObjectList.size())
        verify(listenerMock).listChanged(null)
    }

    /**
     * Test: Listener must not be called when removing an object with an unknown
     * ID.
     */
    @Test
    fun testRemoveFailed() {
        assertFalse(idObjectList.removeByID(5))

        assertEquals(3, idObjectList.size())
        verifyNoInteractions(listenerMock)
    }

    /**
     * Test: Listener must be called when replacing the list content with method clearAndAddAll().
     */
    @Test
    fun testClearAndAddAll() {

        val tempEntries = ArrayList<DummyIdObject>()
        tempEntries.add(DummyIdObject(5))
        tempEntries.add(DummyIdObject(6))
        idObjectList.clearAndAddAll(tempEntries)

        assertEquals(2, idObjectList.size())
        verify(listenerMock).listChanged(null)
    }

    /**
     * Test: The read-methods must not call the Listener.
     */
    @Test
    fun testMethodsWithoutListenerNotification() {

        assertEquals(2, idObjectList.getAt(1).id)
        assertEquals(2, idObjectList.getByID(2)!!.id)
        assertEquals(2, idObjectList.indexOf(idObjectList.getAt(2)))
        assertEquals(4, idObjectList.getNewId())
        assertEquals(3, idObjectList.size())
        assertEquals(1, idObjectList.iterator().next().id)

        verifyNoInteractions(listenerMock)
    }

    /**
     * Subclass of abstract class IdObject for testing.
     */
    internal class DummyIdObject(id: Int) : IdObject(id)
}
