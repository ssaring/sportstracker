package de.saring.sportstracker.gui.update

import de.saring.sportstracker.gui.STContext
import de.saring.util.SemanticVersion
import javafx.application.Platform
import javafx.scene.control.Alert
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * TODO
 */
@Singleton
class STUpdateChecker @Inject constructor(
        private val context: STContext) {

    fun checkForUpdates() {

        val installedVersion = SemanticVersion.parse(context.resources.getString("application.version"))
        val latestVersion = getLatestVersion()

        if (installedVersion < latestVersion) {
            LOGGER.info("An update of SportsTracker is available, latest version is: $latestVersion")
            Platform.runLater {
                context.showMessageDialog(context.primaryStage, Alert.AlertType.INFORMATION,
                        "common.info", "st.main.info.update_available",
                        installedVersion, latestVersion)
            }
        }
    }

    private fun getLatestVersion(): SemanticVersion {
        // TODO
        // use HTTP Client API for downloading the version number?
        // => https://openjdk.java.net/groups/net/httpclient/intro.html
        return SemanticVersion(7, 6, 2)
    }

    companion object {
        val LOGGER = Logger.getLogger(STUpdateChecker::class.java.name)
    }
}
