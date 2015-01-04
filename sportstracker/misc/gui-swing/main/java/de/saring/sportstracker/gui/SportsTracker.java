package de.saring.sportstracker.gui;

import org.jdesktop.application.SingleFrameApplication;

/**
 * This class does the following trick:
 * All the application-wide resources will be read from STMain.properties (e.g.
 * ID and version), but I want to keep all resources for translation (I18N) in
 * one single special file. This base class provides the resource file
 * SportsTracker.properties for translations.
 *
 * @author Stefan Saring
 */
public abstract class SportsTracker extends SingleFrameApplication {
}
