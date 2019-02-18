package de.saring.sportstracker.gui.update

import de.saring.sportstracker.gui.STContext
import de.saring.sportstracker.util.MockitoKotlin.any
import de.saring.util.AppResources
import de.saring.util.SemanticVersion
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

/**
 * Unit tests of class [STUpdateChecker]. The tests are not downloading the latest version numbers here, this method
 * is mocked.
 *
 * @author Stefan Saring
 */
class STUpdateCheckerTest {

    private val installedVersion = SemanticVersion(7, 5, 0)

    private lateinit var contextMock: STContext
    private lateinit var updateCheckerSpy: STUpdateChecker

    @BeforeEach
    fun setupMocks() {

        val resourcesMock = mock(AppResources::class.java)
        `when`(resourcesMock.getString("application.version")).thenReturn(installedVersion.toString())

        contextMock = mock(STContext::class.java)
        `when`(contextMock.resources).thenReturn(resourcesMock)
        `when`(contextMock.primaryStage).thenReturn(null)

        updateCheckerSpy = spy(STUpdateChecker(contextMock))
        doNothing().`when`(updateCheckerSpy).displayUpdateInformation(any(), any())
    }

    /**
     * Test of checkForUpdates(): there is a newer version available, so the update information needs to be shown.
     */
    @Test
    fun testCheckForUpdatesAvailable() {

        val latestVersion = SemanticVersion(7, 6, 0)
        doReturn(latestVersion).`when`(updateCheckerSpy).getLatestVersion()

        updateCheckerSpy.checkForUpdates()

        verify(updateCheckerSpy).displayUpdateInformation(installedVersion, latestVersion)
    }

    /**
     * Test of checkForUpdates(): there is no newer version available, so no update information has to be shown.
     */
    @Test
    fun testCheckForUpdatesNotAvailable() {

        val latestVersion = SemanticVersion(7, 5, 0)
        doReturn(latestVersion).`when`(updateCheckerSpy).getLatestVersion()

        updateCheckerSpy.checkForUpdates()

        verify(updateCheckerSpy, never()).displayUpdateInformation(any(), any())
    }
}