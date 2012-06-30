package de.saring.sportstracker.gui;

import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jdesktop.application.View;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.saring.exerciseviewer.gui.EVContext;
import de.saring.sportstracker.core.STException;

/**
 * This is the main class of SportsTracker which starts the entire application.
 *
 * @author  Stefan Saring
 * @version 1.0
 */
public class STMain extends SportsTracker {
    private static final Logger LOGGER = Logger.getLogger (STMain.class.getName ()); 
    
    /** The SportsTracker application context. */
    private STContext context;
    
    /** The view instance of SportsTracker (MVC). */
    private STDocument document;
    
    /** The view instance of SportsTracker (MVC). */
    private STView view;
    
    /** The view controller of SportsTracker (MVC). */
    private STController controller;
    
    /** List of command line parameters passed to the application. */
    private String[] cmdLineParameters; 
    
    
    /**
     * Initializes the SportsTracker application.
     */
    @Override
    protected void initialize (String[] args) {
        super.initialize (args);
        cmdLineParameters = args;
    }
    
    /**
     * Starts up the SportsTracker application.
     */
    @Override 
    protected void startup() {
        
        // create and configure the Guice injector 
        Injector injector = Guice.createInjector(new AbstractModule() {
            public void configure() {
            	
                // create and bind SportsTracker GUI context, which can be used everywhere
                context = new STContextImpl(getContext());
                bind(STContext.class).toInstance(context);
                bind(EVContext.class).toInstance(context);
                
                // bind the component interfaces to implementations
                bind(STDocument.class).to(STDocumentImpl.class);
                bind(STView.class).to(STViewImpl.class);
                bind(STController.class).to(STControllerImpl.class);
            }
        });
        
        // create and initialize the document
        document = injector.getInstance(STDocument.class);
        document.evaluateCommandLineParameters (cmdLineParameters);
        document.loadOptions ();
        initLookAndFeel (document.getOptions ().getLookAndFeelClassName ());        
        
        // create the controller and add the exit listener
        controller = injector.getInstance (STController.class);
        addExitListener (new ExitListener() { 
            public boolean canExit (EventObject e) {
                return controller.saveBeforeExit ();
            }
            public void willExit (EventObject e) {}            
        });
        
        // create the view and display the application
        view = injector.getInstance (STView.class);
        view.initView ();
        show ((View) view);
    }

    /**
     * Called when the startup of the SportsTracker application is completed.
     */
    @Override 
    protected void ready () {        
        view.postInit ();

        // create application directory
        try {
            document.createApplicationDirectory ();
        }
        catch (STException se) {
            LOGGER.log (Level.SEVERE, "Failed to create the application directory!", se);
            context.showMessageDialog (getMainFrame (), JOptionPane.ERROR_MESSAGE, 
                "common.error", "st.main.error.create_dir");
        }

        // load application data by starting the load action (executed in background)
        controller.startActionManually (STController.ACTION_LOAD);
    }
    
    /**
     * Shuts down the SportsTracker application and persists the state.
     */
    @Override 
    protected void shutdown () {
        document.storeOptions ();
        super.shutdown ();
    }
    
    /**
     * Initializes the Look&Feel of the application. Must be done before creating the view 
     * components.
     * @param lookAndFeelClassName class name of the look and feel (or null for system default)
     */
    private void initLookAndFeel (String lookAndFeelClassName) {
        if (lookAndFeelClassName == null) {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName ();
        }
        
        try {
            UIManager.setLookAndFeel (lookAndFeelClassName);
        }
        catch (Exception e) {
            LOGGER.log (Level.WARNING, "Failed to set look&feel to " + lookAndFeelClassName + "!", e);
        }
    }

    /**
     * Starts the SportsTracker application.
     * @param args command line parameters
     */
    public static void main (String args[]) {        
        // Mac specific intialization
        STViewImpl.preInit();

        launch (STMain.class, args);
    }
}
