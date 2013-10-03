package de.saring.sportstracker.gui;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Unit tests of class STDocument/Impl. All the involved components will be
 * mocked via Mockito.
 *
 * @author Stefan Saring
 */
public class STDocumentTest {

    private STDocument document;

    @Before
    public void setUp() {
        // STContext needs to be mocked
        STContext contextMock = mock(STContext.class);
        document = new STDocumentImpl(contextMock, null);
    }

    /**
     * Test of method evaluateCommandLineParameters().
     */
    @Test
    public void testEvaluateCommandLineParameters() {
        final String defaultDataDirectory = System.getProperty("user.home") + "/.sportstracker";

        // the document implementation must be used for checking
        STDocumentImpl documentImpl = (STDocumentImpl) document;

        document.evaluateCommandLineParameters(null);
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        // must not work, the user must use the '=' character
        document.evaluateCommandLineParameters(new String[]{"--datadir", "temp"});
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        document.evaluateCommandLineParameters(new String[]{"--foo", "--datadir=temp"});
        assertEquals("temp", documentImpl.getDataDirectory());
    }
}
