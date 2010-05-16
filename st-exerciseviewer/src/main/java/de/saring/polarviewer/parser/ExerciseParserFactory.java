package de.saring.polarviewer.parser;

import de.saring.polarviewer.core.PVException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * This factory will always returns the proper ExerciseParser implementation
 * for the specified filename. This factory uses the singleton pattern.
 * 
 * New parsers can be added just by implementing the ExerciseParser interfaces
 * and registering them in the META-INF/service directory. New parsers must not
 * be part of the SportsTracker-Jar, the parser-Jar just needs to be added to
 * the application classpath.
 *
 * The parsers can also be implemented as Groovy scripts, but these needs to be
 * compiled to class-Files too. It would also be possible to run the Groovy 
 * parsers without compile using the GroovyClassLoader, but the disadvantages
 * are slower performance and no syntax check via the compiler.
 *
 * @author  Stefan Saring
 * @version 2.0
 */
public class ExerciseParserFactory {
    
    /** The singleton instance. */
    private static ExerciseParserFactory instance;

    /** The ServiceLoader instance for all ExerciseParser implementations. */
    ServiceLoader<ExerciseParser> exerciseParserLoader;

    /** 
     * Creates a new instance of ExerciseParserFactory and loads all
     * ExerciseParser implementations available in the classpath.
     */
    private ExerciseParserFactory () {
        exerciseParserLoader =  ServiceLoader.load (ExerciseParser.class);
    }
    
    /**
     * Returns the instance of the appropriate exercise parser for the specified
     * exercise filename. The proper parser will be assigned by using the 
     * filename suffix.
     *
     * @param filename name of the exercise file to parse
     * @return instance of the appropriate exercise parser 
     * @throws PVException when no proper parser has been found
     */
    public static ExerciseParser getParser (String filename) throws PVException {        
        createInstance ();
        
        // return the parser implementation which matches the filename suffix
        for (ExerciseParser parser : instance.exerciseParserLoader) {
            for (String suffix : parser.getInfo ().getSuffixes ()) {                
                if (filename.endsWith ("." + suffix)) {
                    return parser;
                }
            }
        }
        
        throw new PVException ("No parser has been found for filename '" + filename + "' ...");
    }
    
    /**
     * Returns the list of all ExerciseParserInfo objects for all available parser
     * implementations (usefull e.g. for File Open dialogs for list of suffixes).
     * @return list of ExerciseParserInfo objects for all parser implementations
     */
    public static List<ExerciseParserInfo> getExerciseParserInfos () {
        createInstance ();
        
        List<ExerciseParserInfo> lInfos = new ArrayList<ExerciseParserInfo> ();
        for (ExerciseParser parser : instance.exerciseParserLoader) {
            lInfos.add (parser.getInfo ());
        }
        return lInfos;
    }
    
    /**
     * Creates the singleton instance of the factory when not done yet.
     */
    private static synchronized void createInstance () {        
        if (instance == null) {
            instance = new ExerciseParserFactory ();
        }
    }    
}
