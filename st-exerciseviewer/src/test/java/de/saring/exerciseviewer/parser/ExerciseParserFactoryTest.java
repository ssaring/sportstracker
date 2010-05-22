package de.saring.exerciseviewer.parser;

import de.saring.exerciseviewer.parser.impl.PolarSRawParser;
import de.saring.exerciseviewer.core.EVException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains all unit tests for the ExerciseParserFactory class.
 *
 * @author Stefan Saring
 */
public class ExerciseParserFactoryTest {
    
    /**
     * Tests of getParser() method.
     */
    @Test
    public void testGetParser () throws EVException {
        
        // tests for SRD parser
        ExerciseParser parser = ExerciseParserFactory.getParser ("exercises/exercise1.srd");
        assertTrue (parser instanceof PolarSRawParser);

        parser = ExerciseParserFactory.getParser ("C:\\Test 123\\Exercise2.SRD");
        assertTrue (parser instanceof PolarSRawParser);

        // tests for RS200SD parser (implemented in Groovy)
        parser = ExerciseParserFactory.getParser ("exercises/exercise1.xml");
        assertEquals ("de.saring.exerciseviewer.parser.impl.PolarRS200SDParser", parser.getClass ().getName ());

        parser = ExerciseParserFactory.getParser ("C:\\Test 123\\Exercise2.XML");
        assertEquals ("de.saring.exerciseviewer.parser.impl.PolarRS200SDParser", parser.getClass ().getName ());
        
        try {
            // this parser is unknown, must fail
            parser = ExerciseParserFactory.getParser ("exercises/exercise1.xyz");
            fail ("Parser for suffix xyz must not be found!");
        }
        catch (EVException e) {}
    }    
}
