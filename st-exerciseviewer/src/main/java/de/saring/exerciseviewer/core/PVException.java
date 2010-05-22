package de.saring.exerciseviewer.core;

/** 
 * This is the base Excpetion class for the whole ExerciseViewer application.
 * It doesn't contain additional functionality right now, but it can be
 * added simply in future.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class PVException extends Exception 
{
    /**
     * Overrides base class constructor.
     */
    public PVException () {
        super ();
    }
    
    /**
     * Overrides base class constructor.
     * @param message the exception message
     */
    public PVException (String message) {
        super (message);
    }
    
    /**
     * Overrides base class constructor.
     * @param message the exception message
     * @param cause the inner (previous) exception
     */
    public PVException (String message, Exception cause) {
        super (message, cause);
    }
}