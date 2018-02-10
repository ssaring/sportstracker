package de.saring.sportstracker.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of class STDocument/Impl. All the involved components will be
 * mocked via Mockito.
 *
 * @author Stefan Saring
 */
public class STDocumentTest {

    private STDocument document;

    @BeforeEach
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

        document.evaluateCommandLineParameters(new ArrayList<>());
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        // must not work, the user must use the '=' character
        document.evaluateCommandLineParameters(List.of("--datadir", "temp"));
        assertEquals(defaultDataDirectory, documentImpl.getDataDirectory());

        document.evaluateCommandLineParameters(List.of("--foo", "--datadir=temp"));
        assertEquals("temp", documentImpl.getDataDirectory());
    }
}
