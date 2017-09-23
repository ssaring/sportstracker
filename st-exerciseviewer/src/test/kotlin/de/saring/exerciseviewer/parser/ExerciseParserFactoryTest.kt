package de.saring.exerciseviewer.parser

import de.saring.exerciseviewer.core.EVException
import de.saring.exerciseviewer.parser.impl.PolarSRawParser
import de.saring.exerciseviewer.parser.impl.TimexPwxParser
import de.saring.exerciseviewer.parser.impl.garminfit.GarminFitParser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * This class contains all unit tests for the ExerciseParserFactory class.
 *
 * @author Stefan Saring
 */
class ExerciseParserFactoryTest {

    /**
     * Tests of getParser() method.
     */
    @Test
    fun testGetParser() {

        // tests for SRD parser
        var parser = ExerciseParserFactory.getParser("exercises/exercise1.srd")
        assertTrue(parser is PolarSRawParser)

        parser = ExerciseParserFactory.getParser("C:\\Test 123\\Exercise2.SRD")
        assertTrue(parser is PolarSRawParser)

        parser = ExerciseParserFactory.getParser("exercises/shouldtherebeafile.pwx")
        assertTrue(parser is TimexPwxParser)

        parser = ExerciseParserFactory.getParser("C:\\Test 123\\Exercise3.fit")
        assertTrue(parser is GarminFitParser)

        // tests for RS200SD parser (implemented in Kotlin)
        parser = ExerciseParserFactory.getParser("exercises/exercise1.xml")
        assertEquals("de.saring.exerciseviewer.parser.impl.PolarRS200SDParser", parser.javaClass.name)

        parser = ExerciseParserFactory.getParser("C:\\Test 123\\Exercise2.XML")
        assertEquals("de.saring.exerciseviewer.parser.impl.PolarRS200SDParser", parser.javaClass.name)

        parser = ExerciseParserFactory.getParser("exercise1.ped")
        assertEquals("de.saring.exerciseviewer.parser.impl.PolarPedParser", parser.javaClass.name)

        try {
            // this parser is unknown, must fail
            ExerciseParserFactory.getParser("exercises/exercise1.xyz")
            fail("Parser for suffix xyz must not be found!")
        } catch (e: EVException) {
        }
    }
}
