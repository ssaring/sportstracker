package de.saring.sportstracker.gui;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests of class STDocument/Impl. All the involved components will be 
 * mocked via EasyMock.
 * 
 * @author Stefan Saring
 */
public class STDocumentTest {

    private STDocument document;
    
    private STContext contextMock;

    @Before
    public void setUp () {
        // create and init the context mock
        contextMock = EasyMock.createMock (STContext.class);
        EasyMock.expect (contextMock.getResReader ()).andReturn (null);
        EasyMock.replay (contextMock);
        
        document = new STDocumentImpl (contextMock, null);
    }

    @After
    public void tearDown () {
        EasyMock.verify (contextMock);        
    }

    /**
     * Test of method evaluateCommandLineParameters().
     */
    @Test
    public void testEvaluateCommandLineParameters () {        
        final String defaultDataDirectory = System.getProperty ("user.home") + "/.sportstracker";
        
        // the document implementation must be used for checking
        STDocumentImpl documentImpl = (STDocumentImpl) document;
        
        document.evaluateCommandLineParameters (null);
        assertEquals (defaultDataDirectory, documentImpl.getDataDirectory ());

        // must not work, the user must use the '=' character
        document.evaluateCommandLineParameters (new String[] {"--datadir", "temp"});
        assertEquals (defaultDataDirectory, documentImpl.getDataDirectory ());

        document.evaluateCommandLineParameters (new String[] {"--foo", "--datadir=temp"});
        assertEquals ("temp", documentImpl.getDataDirectory ());
    }
}
