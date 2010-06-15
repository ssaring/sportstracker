package de.saring.exerciseviewer.gui;

import javax.swing.ActionMap;
import javax.swing.JDialog;

import org.jdesktop.application.Action;

import com.google.inject.Inject;

import de.saring.exerciseviewer.gui.panels.DiagramPanel;
import de.saring.exerciseviewer.gui.panels.LapPanel;
import de.saring.exerciseviewer.gui.panels.MainPanel;
import de.saring.exerciseviewer.gui.panels.OptionalPanel;
import de.saring.exerciseviewer.gui.panels.SamplePanel;
import de.saring.exerciseviewer.gui.panels.TrackPanel;

/**
 * This class contains all view (MVC) related data and functionality of the 
 * ExerciseViewer application.
 *
 * @author Stefan Saring
 * @version 1.0
 */
public class EVView extends JDialog {
    
    private static final String APPLICATION_NAME = "ExerciseViewer";
    private static final String ACTION_CLOSE = "pv.view.close";
    
    private EVContext context;
    private EVDocument document;

    private MainPanel mainPanel;
    private OptionalPanel optionalPanel;
    private LapPanel lapPanel;
    private SamplePanel samplePanel;
    private DiagramPanel diagramPanel;
    private TrackPanel trackPanel;

    
    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     * @param mainPanel the main panel
     * @param optionalPanel the optional panel
     * @param lapPanel the lap panel
     * @param samplePanel the sample panel
     * @param diagramPanel the diagram panel
     * @param trackPanel the track panel
     */
    @Inject
    public EVView (EVContext context, MainPanel mainPanel, OptionalPanel optionalPanel,
        LapPanel lapPanel, SamplePanel samplePanel, DiagramPanel diagramPanel, TrackPanel trackPanel) {
        
        super (context.getMainFrame ());
        this.context = context;
        this.mainPanel = mainPanel;
        this.optionalPanel = optionalPanel;
        this.lapPanel = lapPanel;
        this.samplePanel = samplePanel;
        this.diagramPanel = diagramPanel;
        this.trackPanel = trackPanel;
    }

    /**
     * Initializes the PVView and all contained components.
     * @param the ExerciseViewer document
     */
    public void initView (EVDocument document) {
        initComponents ();

        // The PVDocument can't be injected in this class and all panels, Guice would
        // create new instances for it. The declaration of PVDocument as @Singleton does
        // not help here, because the user can open multiple ExerciseViewer instances at 
        // the same time. So the dependency injection must be done manually :-(
        this.document = document;
        mainPanel.setDocument (document);
        optionalPanel.setDocument (document);
        lapPanel.setDocument (document);
        samplePanel.setDocument (document);
        diagramPanel.setDocument (document);
        trackPanel.setDocument (document);
        
        mainPanel.setDiagramPanel (diagramPanel);
        this.getRootPane ().setDefaultButton (btClose);
        
        tabbedPane.add (context.getResReader ().getString ("pv.view.main"), mainPanel);
        tabbedPane.add (context.getResReader ().getString ("pv.view.optional"), optionalPanel);
        tabbedPane.add (context.getResReader ().getString ("pv.view.laps"), lapPanel);
        tabbedPane.add (context.getResReader ().getString ("pv.view.samples"), samplePanel);
        tabbedPane.add (context.getResReader ().getString ("pv.view.diagram"), diagramPanel);
        tabbedPane.add (context.getResReader ().getString ("pv.view.track"), trackPanel);

        // start resource injection for automatic widget internationalization for this
        // component and all subcomponents
        context.getSAFContext ().getResourceMap ().injectComponents (this);
                
        // setup actions
        ActionMap actionMap = context.getSAFContext ().getActionMap (getClass (), this);
        btClose.setAction (actionMap.get (ACTION_CLOSE));
    }
    
    /**
     * Shows or hides this dialog. Before showing the dialog, the minimum size
     * will be set to the prefered size (all widgets are initialized now). This
     * makes sure that all the widgets are always displayed, the window size
     * can not be reduced too much.
     * @param fVisible true for show, false for hide
     */
    @Override
    public void setVisible (boolean fVisible) {
        if (fVisible) {
            this.setMinimumSize (this.getPreferredSize ());
        }
        super.setVisible (fVisible);
    }
        
    /**
     * Action for closing the ExerciseViewer dialog.
     */
    @Action(name=ACTION_CLOSE)
    public void close () {
        dispose ();
    }
    
    /**
     * Displays the current exercise in all panels.
     */
    public void displayExercise () {
        
        // show filename in window title
        this.setTitle (APPLICATION_NAME + " - " + document.getExerciseFilename ());
        
        mainPanel.displayExercise();
        optionalPanel.displayExercise();
        lapPanel.displayExercise();
        samplePanel.displayExercise();
        diagramPanel.displayExercise();
        trackPanel.displayExercise();
        
        pack ();
    }
    
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pMain = new javax.swing.JPanel();
        btClose = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ExerciseViewer");
        setName("pv.dialog"); // NOI18N

        btClose.setText("_Close");

        javax.swing.GroupLayout pMainLayout = new javax.swing.GroupLayout(pMain);
        pMain.setLayout(pMainLayout);
        pMainLayout.setHorizontalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addComponent(btClose, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        pMainLayout.setVerticalGroup(
            pMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pMainLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btClose)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JPanel pMain;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
}
