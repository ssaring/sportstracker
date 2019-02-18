package de.saring.sportstracker.gui.update

import de.saring.sportstracker.gui.STContext
import de.saring.util.SemanticVersion
import javafx.application.Platform
import javafx.scene.control.Alert
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Class for checking whether there is a SportsTracker update available.
 *
 * @property context the SportsTracker context (dependency injection)
 *
 * @author Stefan Saring
 */
@Singleton
open class STUpdateChecker @Inject constructor(
        private val context: STContext) {

    /**
     * Checks whether there is an update available for the SportsTracker application. If so, a info dialog will be
     * displayed.
     */
    fun checkForUpdates() {
        LOGGER.info("Checking for SportsTracker updates...")

        val installedVersion = SemanticVersion.parse(context.resources.getString("application.version"))
        val latestVersion = getLatestVersion()

        if (installedVersion < latestVersion) {
            LOGGER.info("An update of SportsTracker is available, latest version is: $latestVersion")
            displayUpdateInformation(installedVersion, latestVersion)
        }
    }

    /**
     * Downloads the latest stable version number from the SportsTracker website. It uses the new HttpClient API
     * introduced with Java 11.
     *
     * @return the latest version number or version 0.0.0 in case of errors
     */
    internal open fun getLatestVersion(): SemanticVersion {
        val client = HttpClient.newHttpClient()

        try {
            val request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_LATEST_VERSION))
                    .build()

            val response = client.send(request, BodyHandlers.ofString())
            val status = response.statusCode()

            if (status == HttpURLConnection.HTTP_OK) {
                val strLatestVersion = response.body()
                return SemanticVersion.parse(strLatestVersion.trim())
            }

            LOGGER.severe("Failed to download latest SportsTracker version number, got HTTP status: $status!")
        }
        catch (e: Exception) {
            LOGGER.severe("Failed to download latest SportsTracker version number! Error: $e")
        }

        return SemanticVersion(0, 0, 0)
    }

    internal open fun displayUpdateInformation(installedVersion: SemanticVersion, latestVersion: SemanticVersion) {
        Platform.runLater {
            context.showMessageDialog(context.primaryStage, Alert.AlertType.INFORMATION,
                    "common.info", "st.main.info.update_available",
                    installedVersion, latestVersion)
        }
    }

    companion object {
        private val LOGGER = Logger.getLogger(STUpdateChecker::class.java.name)

        private const val URL_LATEST_VERSION = "https://saring.de/sportstracker/latest-version.txt"
    }
}
