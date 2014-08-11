package de.saring.sportstracker.gui.dialogsfx;

import de.saring.util.AppResources;
import de.saring.util.gui.javafx.GuiceFxmlLoader;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Controller (MVC) class of the About dialog window.
 *
 * @author Stefan Saring
 */
@Singleton
public class AboutDialogController extends AbstractDialogController {

    /**
     * Standard c'tor for dependency injection.
     *
     * @param resources the application resources
     * @param guiceFxmlLoader the Guice FXML loader
     */
    @Inject
    public AboutDialogController(final AppResources resources, final GuiceFxmlLoader guiceFxmlLoader) {
        super(resources, guiceFxmlLoader);
    }

    /**
     * Displays the About dialog.
     *
     * @param parent parent window of the dialog
     */
    public void show(final Window parent) {
        showInfoDialog("/fxml/AboutDialog.fxml", null, resources.getString("st.dlg.about.title"));
    }

    @Override
    protected void showInitialValues() {
        // no operations ...
    }
}
