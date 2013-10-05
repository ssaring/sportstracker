package de.saring.sportstracker.core;

/**
 * This is the base Exception class for the whole SportsTracker application.
 * It extends the standard exception by an ID ofr being able to react properly
 * on problems.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class STException extends Exception {
    private static final long serialVersionUID = 1839511030013562371L;

    /**
     * ID of exception.
     */
    private STExceptionID id;

    /**
     * Returns the exception ID.
     *
     * @return the exception ID
     */
    public STExceptionID getId() {
        return id;
    }

    /**
     * Overrides base class constructor.
     *
     * @param id the exception ID
     * @param message the exception message
     */
    public STException(STExceptionID id, String message) {
        super(message);
        this.id = id;
    }

    /**
     * Overrides base class constructor.
     *
     * @param id the exception ID
     * @param message the exception message
     * @param innerEx the inner (previous) exception
     */
    public STException(STExceptionID id, String message, Exception innerEx) {
        super(message, innerEx);
        this.id = id;
    }
}
