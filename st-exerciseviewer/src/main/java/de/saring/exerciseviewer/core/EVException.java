package de.saring.exerciseviewer.core;

/** 
 * This is the base Excpetion class for the whole ExerciseViewer application.
 * It doesn't contain additional functionality right now, but it can be
 * added simply in future.
 * 
 * @author  Stefan Saring
 * @version 1.0
 */
public class EVException extends Exception {
	
    private static final long serialVersionUID = 1300179122574614829L;

	/**
     * Overrides base class constructor.
     */
    public EVException () {
        super ();
    }
    
    /**
     * Overrides base class constructor.
     * @param message the exception message
     */
    public EVException (String message) {
        super (message);
    }
    
    /**
     * Overrides base class constructor.
     * @param message the exception message
     * @param cause the inner (previous) exception
     */
    public EVException (String message, Exception cause) {
        super (message, cause);
    }
}