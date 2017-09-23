package de.saring.exerciseviewer.core

/**
 * This is the base exception class for the whole ExerciseViewer application.
 * It doesn't contain additional functionality right now, but it can be added simply in future.
 *
 * @author Stefan Saring
 */
class EVException : Exception {

    /**
     * Overrides base class constructor.
     *
     * @param message the exception message
     */
    constructor(message: String) : super(message)

    /**
     * Overrides base class constructor.
     *
     * @param message the exception message
     * @param cause the cause exception
     */
    constructor(message: String, cause: Exception) : super(message, cause)
}