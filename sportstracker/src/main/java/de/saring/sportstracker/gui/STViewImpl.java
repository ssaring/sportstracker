package de.saring.sportstracker.gui;

import de.saring.sportstracker.core.STOptions;
import de.saring.sportstracker.data.Exercise;
import de.saring.sportstracker.gui.views.BaseView;
import de.saring.sportstracker.gui.views.EntryView;
import de.saring.sportstracker.gui.views.calendarview.CalendarView;
import de.saring.sportstracker.gui.views.listview.ExerciseListView;
import de.saring.sportstracker.gui.views.listview.NoteListView;
import de.saring.sportstracker.gui.views.listview.WeightListView;
import de.saring.util.data.IdDateObjectList;
import de.saring.util.data.IdObject;
import de.saring.util.data.IdObjectListChangeListener;
import de.saring.util.gui.mac.MacSpecials;
import de.saring.util.unitcalc.CalculationUtils;
import de.saring.util.unitcalc.FormatUtils;
import org.jdesktop.application.FrameView;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * This class contains all view (MVC) related data and functionality of the
 * SportsTracker application.
 * This view extends the Application Framework FrameView, which provides the major
 * GUI componentes. The Netbeans GUI designer is not used here, the JSR 296 support
 * is not mature enough yet (the GUI is so simple here, it's not needed).
 *
 * @author Stefan Saring
 * @version 1.0
 */
@Singleton
public class STViewImpl extends FrameView implements STView {

    private static final String SEPARATOR = "---";

    private STContext context;
    private STDocument document;
    private STController controller;

    /**
     * The currently displayed view.
     */
    private BaseView currentView;

    /**
     * The view panel which displays the exercises in a (month) calendar.
     */
    private CalendarView calendarView;

    /**
     * The view panel which displays the exercise list.
     */
    private ExerciseListView exerciseListView;

    /**
     * The view panel which displays the note list.
     */
    private NoteListView noteListView;

    /**
     * The view panel which displays the weight list.
     */
    private WeightListView weightListView;


    /**
     * The list of exercises to be displayed in the views. This list will be
     * cached here, otherwise we need to filter the complete exercise list for
     * each row in the list view or for each month in the calendar view.
     */
    private IdDateObjectList<Exercise> displayedExercises = new IdDateObjectList<>();

    /**
     * The statusbar label.
     */
    private JLabel laStatusBar;


    /**
     * Standard c'tor.
     *
     * @param context the SportsTracker context
     * @param document the document component
     * @param controller the controller component
     * @param calendarView the CalendarView component
     * @param exerciseListView the ExerciseListView component
     * @param noteListView the NoteListView component
     * @param weightListView the WeightListView component
     */
    @Inject
    public STViewImpl(STContext context, STDocument document, STController controller,
                      CalendarView calendarView, ExerciseListView exerciseListView,
                      NoteListView noteListView, WeightListView weightListView) {

        super(context.getSAFContext().getApplication());
        this.context = context;
        this.document = document;
        this.controller = controller;
        this.calendarView = calendarView;
        this.exerciseListView = exerciseListView;
        this.noteListView = noteListView;
        this.weightListView = weightListView;
    }

    /**
     * Mac-specific pre-initialization, must be called as soon as possible
     * (otherwise the settings will be ignored)
     */
    public static void preInit() {
        if (MacSpecials.isMacOSX()) {
            // Set Mac application menu name
            MacSpecials.setApplicationMenuName("SportsTracker");
        }
    }

    @Override
    public void initView() {
        createView();

        // set format utils for current configuration in the context
        STOptions options = document.getOptions();
        context.setFormatUtils(new FormatUtils(options.getUnitSystem(), options.getSpeedView()));

        // init views and select the intital view
        calendarView.initView();
        exerciseListView.initView();
        noteListView.initView();
        weightListView.initView();

        if (options.getInitialView() == STOptions.View.Calendar) {
            switchToView(EntryView.ViewType.CALENDAR);
        } else {
            switchToView(EntryView.ViewType.EXERCISE_LIST);
        }

        // show tooltips always for 10 seconds, default is 4 seconds 
        // (needed e.g. in CalendarView or Track panel of ExerciseViewer)
        ToolTipManager.sharedInstance().setDismissDelay(10 * 1000);
    }

    /**
     * Creates the applications main view (menubar, toolbar, statusbar).
     */
    private void createView() {
        initMacUI();

        // create menu bar
        this.setMenuBar(createMenuBar());

        // create toolbar
        this.setToolBar(new JToolBar());
        createToolBarButtons(new String[]{
                STController.ACTION_OPEN_EXERCISEVIEWER,
                STController.ACTION_SAVE,
                STController.ACTION_PRINT,
                SEPARATOR,
                STController.ACTION_CALENDAR_VIEW,
                STController.ACTION_EXERCISE_LIST_VIEW,
                STController.ACTION_NOTE_LIST_VIEW,
                STController.ACTION_WEIGHT_LIST_VIEW,
                SEPARATOR,
                STController.ACTION_FILTER_EXERCISES,
                STController.ACTION_FILTER_DISABLE,
                SEPARATOR,
                STController.ACTION_EXERCISE_ADD,
                STController.ACTION_NOTE_ADD,
                STController.ACTION_WEIGHT_ADD,
                STController.ACTION_ENTRY_EDIT,
                STController.ACTION_ENTRY_COPY,
                STController.ACTION_ENTRY_DELETE,
                STController.ACTION_VIEW_HRM,
                SEPARATOR,
                STController.ACTION_SPORTTYPE_EDITOR,
                STController.ACTION_STATISTICS,
                STController.ACTION_OVERVIEW_DIAGRAM
        });

        // create statusbar
        JPanel statusPanel = new JPanel(new BorderLayout());
        JSeparator statusPanelSeparator = new JSeparator();
        laStatusBar = new JLabel(" ");
        statusPanel.add(statusPanelSeparator, BorderLayout.NORTH);
        statusPanel.add(laStatusBar, BorderLayout.WEST);
        statusPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        this.setStatusBar(statusPanel);
    }

    /**
     * Set application window icons in all available sizes, this can't be done by SAF resource
     * loader. The multiple sizes are needed for the different window and dock sizes depending
     * on the operating system and user settings (automatic scaling looks ugly).
     */
    private void setupWindowIcons() {
        getFrame().setIconImages(Arrays.asList(
                loadImage("/icons/st-logo-512.png"),
                loadImage("/icons/st-logo-256.png"),
                loadImage("/icons/st-logo-128.png"),
                loadImage("/icons/st-logo-64.png"),
                loadImage("/icons/st-logo-48.png"),
                loadImage("/icons/st-logo-32.png"),
                loadImage("/icons/st-logo-24.png")
        ));
    }

    private Image loadImage(String filename) {
        return new ImageIcon(STViewImpl.class.getResource(filename)).getImage();
    }

    /**
     * Creates the applications menu bar.
     *
     * @return the menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        this.getFrame().setJMenuBar(menuBar);

        String[] fileMenuItems = {
                STController.ACTION_OPEN_EXERCISEVIEWER,
                STController.ACTION_SAVE,
                SEPARATOR,
                STController.ACTION_PRINT,
                SEPARATOR,
                STController.ACTION_QUIT
        };

        // Mac: File menu without quit menu item
        if (MacSpecials.isMacOSX()) {
            fileMenuItems = Arrays.copyOf(fileMenuItems, fileMenuItems.length - 2);
        }

        menuBar.add(createMenu("st.view.file", fileMenuItems));

        String[] editMenuItems = {
                STController.ACTION_EXERCISE_ADD,
                STController.ACTION_NOTE_ADD,
                STController.ACTION_WEIGHT_ADD,
                STController.ACTION_ENTRY_EDIT,
                STController.ACTION_ENTRY_COPY,
                STController.ACTION_ENTRY_DELETE,
                SEPARATOR,
                STController.ACTION_VIEW_HRM,
                SEPARATOR,
                STController.ACTION_PREFERENCES
        };

        // Mac: Edit menu without preferences menu item
        if (MacSpecials.isMacOSX()) {
            editMenuItems = Arrays.copyOf(editMenuItems, editMenuItems.length - 2);
        }

        menuBar.add(createMenu("st.view.edit", editMenuItems));

        menuBar.add(createMenu("st.view.view", new String[]{
                STController.ACTION_CALENDAR_VIEW,
                STController.ACTION_EXERCISE_LIST_VIEW,
                STController.ACTION_NOTE_LIST_VIEW,
                STController.ACTION_WEIGHT_LIST_VIEW,
                SEPARATOR,
                STController.ACTION_FILTER_EXERCISES,
                STController.ACTION_FILTER_DISABLE,
        }));

        menuBar.add(createMenu("st.view.tools", new String[]{
                STController.ACTION_SPORTTYPE_EDITOR,
                STController.ACTION_STATISTICS,
                STController.ACTION_OVERVIEW_DIAGRAM
        }));

        // Mac: No help menu with just the about menu item
        if (!MacSpecials.isMacOSX()) {
            menuBar.add(createMenu("st.view.help", new String[]{
                    STController.ACTION_ABOUT
            }));
        }

        return menuBar;
    }

    /**
     * Creates a JMenu instance with the specified menu name and the list of
     * menu items specified by the action names. The resources are read from the
     * applications resource bundle.
     *
     * @param menuName widget name of the menu
     * @param actionNames list of action names of all menu items
     * @return the created menu
     */
    private JMenu createMenu(String menuName, String[] actionNames) {
        int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenu menu = new JMenu();
        menu.setName(menuName);

        for (String actionName : actionNames) {
            if (actionName.equals(SEPARATOR)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(controller.getActionMap().get(actionName));

                // use the operating systems default shortcut key modifier (e.g. CTRL on Linux, Cmd on Mac)
                KeyStroke keyStroke = menuItem.getAccelerator();
                if (keyStroke != null) {
                    menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke.getKeyCode(), modifier));
                }
                menu.add(menuItem);
            }
        }
        return menu;
    }

    /**
     * Creates the toolbar buttons for the specified list of action names
     * and adds them to the toolbar. The resources are read from the
     * applications resource bundle.
     *
     * @param actionNames list of action names of all toolbar buttons
     */
    private void createToolBarButtons(String[] actionNames) {
        for (String actionName : actionNames) {
            if (actionName.equals(SEPARATOR)) {
                this.getToolBar().addSeparator();
            } else {
                // disable the buttons mnemonic, otherwise menu items with the
                // same mnemonic will not work anymore
                JButton button = new JButton(controller.getActionMap().get(actionName));
                button.setFocusable(false);
                button.setHideActionText(true);
                button.setMnemonic(-1);
                if (MacSpecials.isMacOSX()) {
                    // remove button borders in OS X 
                    button.putClientProperty("JButton.buttonType", "toolbar");
                }
                this.getToolBar().add(button);
            }
        }
    }

    /**
     * Initializes MacOS X specific UI settings.
     */
    private void initMacUI() {
        if (MacSpecials.isMacOSX()) {
            // On Mac the menu bar should appear in the application bar
            MacSpecials.useScreenMenuBar(true);

            // Set Mac application dock icon
            ImageIcon appIcon = new ImageIcon(getClass().getResource("/icons/st-logo-512.png"));
            MacSpecials.getInstance().setDockIcon(appIcon.getImage());

            // Set Mac application menu about/preferences actions
            // (correct Quit handling is done by BSAF since version 1.9RC5)
            Action aboutAction = controller.getActionMap().get(STController.ACTION_ABOUT);
            MacSpecials.getInstance().setAboutAction(aboutAction);
            Action prefsAction = controller.getActionMap().get(STController.ACTION_PREFERENCES);
            MacSpecials.getInstance().setPreferencesAction(prefsAction);
        }
    }

    @Override
    public void postInit() {
        setupWindowIcons();

        // start with current date in calendar
        calendarView.selectToday();
    }

    @Override
    public EntryView getCurrentView() {
        return currentView;
    }

    @Override
    public void updateView() {
        // update format utils in context (setting may have changed)
        STOptions options = document.getOptions();
        context.setFormatUtils(new FormatUtils(options.getUnitSystem(), options.getSpeedView()));

        // update list of exercises to be displayed and update current view
        displayedExercises = document.getFilterableExerciseList();
        currentView.updateView();
        updateEntryActions();
    }

    @Override
    public void registerViewForDataChanges() {
        // register a listener which updates view after each change and selects
        // the changed object if specified
        document.registerListChangeListener(changedObject -> {
            updateView();
            if (changedObject != null) {
                getCurrentView().selectEntry(changedObject);
            }
        });
    }

    @Override
    public IdDateObjectList<Exercise> getDisplayedExercises() {
        return displayedExercises;
    }

    @Override
    public void switchToView(EntryView.ViewType viewType) {

        // get specified view, update it and remove its selections
        switch (viewType) {
            case CALENDAR:
                currentView = calendarView;
                break;
            case EXERCISE_LIST:
                currentView = exerciseListView;
                break;
            case NOTE_LIST:
                currentView = noteListView;
                break;
            case WEIGHT_LIST:
                currentView = weightListView;
                break;
        }

        currentView.updateView();
        if (currentView.getSelectedExerciseCount() > 0) {
            currentView.removeSelection();
        }

        // update entry and view actions
        updateEntryActions();
        controller.getActionMap().get(STController.ACTION_CALENDAR_VIEW).setEnabled(currentView != calendarView);
        controller.getActionMap().get(STController.ACTION_EXERCISE_LIST_VIEW).setEnabled(currentView != exerciseListView);
        controller.getActionMap().get(STController.ACTION_NOTE_LIST_VIEW).setEnabled(currentView != noteListView);
        controller.getActionMap().get(STController.ACTION_WEIGHT_LIST_VIEW).setEnabled(currentView != weightListView);

        // display the new view
        this.setComponent(currentView);
        currentView.revalidate();
        currentView.repaint();
    }

    @Override
    public void updateEntryActions() {

        // enable/disable the exercise actions depending on current entry selection
        int selExerciseCount = currentView.getSelectedExerciseCount();
        int selNoteCount = currentView.getSelectedNoteCount();
        int selWeightCount = currentView.getSelectedWeightCount();

        boolean fEditEnabled = selExerciseCount == 1 || selNoteCount == 1 || selWeightCount == 1;
        boolean fDeleteEnabled = selExerciseCount > 0 || selNoteCount > 0 || selWeightCount > 0;
        boolean fHRMEnabled = selExerciseCount == 1;

        // additional checks for HRM action 
        // => this action is only enabled when the selected exercise contains a HRM file
        if (fHRMEnabled) {
            int selExerciseID = currentView.getSelectedExerciseIDs()[0];
            Exercise selExercise = document.getExerciseList().getByID(selExerciseID);
            fHRMEnabled = (selExercise.getHrmFile() != null) && (selExercise.getHrmFile().trim().length() > 0);
        }

        controller.getActionMap().get(STController.ACTION_ENTRY_EDIT).setEnabled(fEditEnabled);
        controller.getActionMap().get(STController.ACTION_ENTRY_COPY).setEnabled(fEditEnabled);
        controller.getActionMap().get(STController.ACTION_ENTRY_DELETE).setEnabled(fDeleteEnabled);
        controller.getActionMap().get(STController.ACTION_VIEW_HRM).setEnabled(fHRMEnabled);

        // Exercise Filter is only enabled in Calendar and Exercise List view
        boolean filterableView = currentView == calendarView || currentView == exerciseListView;
        controller.getActionMap().get(STController.ACTION_FILTER_EXERCISES).setEnabled(
                filterableView);
        // "Disable filter" is only enabled when a filter is currently used
        controller.getActionMap().get(STController.ACTION_FILTER_DISABLE).setEnabled(
                filterableView && document.isFilterEnabled());

        // statusbar text needs to be updated when selection was changed
        updateStatusBar();
        updateSaveAction();
    }

    @Override
    public void updateSaveAction() {
        controller.getActionMap().get(STController.ACTION_SAVE).setEnabled(document.isDirtyData());
    }

    /**
     * Updates the content of the Statusbar depending on the exercise selection.
     */
    private void updateStatusBar() {
        String statusText = " ";

        // create status bar text only when exercises are selected
        int[] selExerciseIDs = currentView.getSelectedExerciseIDs();
        if (selExerciseIDs.length > 0) {

            float sumDistance = 0;
            float sumAvgSpeed = 0;
            int sumDuration = 0;

            if (selExerciseIDs.length == 1) {
                // get summary distance, AVG speed and duration of selected exercise
                Exercise selExercise = document.getExerciseList().getByID(selExerciseIDs[0]);
                sumDistance = selExercise.getDistance();
                sumAvgSpeed = selExercise.getAvgSpeed();
                sumDuration = selExercise.getDuration();
            } else {
                // calculate summary distance, AVG speed and duration for all selected exercises
                for (int exerciseID : selExerciseIDs) {
                    Exercise selExercise = document.getExerciseList().getByID(exerciseID);
                    sumDistance += selExercise.getDistance();
                    sumDuration += selExercise.getDuration();
                }

                sumAvgSpeed = CalculationUtils.calculateAvgSpeed(sumDistance, sumDuration);
            }

            // build status bar text
            String strCount = String.valueOf(selExerciseIDs.length);
            String strDistance = context.getFormatUtils().distanceToString(sumDistance, 3);
            String strAVGSpeed = context.getFormatUtils().speedToString(sumAvgSpeed, 3);
            String strDuration = context.getFormatUtils().seconds2TimeString(sumDuration);
            statusText = context.getResReader().getString("st.view.statusbar",
                    strCount, strDistance, strAVGSpeed, strDuration);
        }

        laStatusBar.setText(statusText);
    }
}
