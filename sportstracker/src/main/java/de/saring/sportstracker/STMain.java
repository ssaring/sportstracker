package de.saring.sportstracker;

import de.saring.sportstracker.gui.STApplication;

/**
 * Launcher class for the SportsTracker application. It just redirects to the STApplication class to start the
 * application. This is a needed workaround since JDK 11, because JavaFX is not part of the JDK anymore.<br/>
 * The reason is that the Java 11 runtime will check if the main class extends javafx.application.Application, and if
 * that is the case, it requires the JavaFX platform to be available as a module. When the main class does not extend
 * the Application class, then it's sufficient to have the JavaFX platform as JAR files in the classpath.<br/>
 * <br/>
 * Further details: https://github.com/javafxports/openjdk-jfx/issues/236
 *
 * @author Stefan Saring
 */
public class STMain {

    /**
     * Starts the SportsTracker application.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
        STApplication.main(args);
    }
}
