package de.saring.exerciseviewer.gui.panels;

import com.google.inject.Inject;
import de.saring.exerciseviewer.data.Lap;
import de.saring.exerciseviewer.data.PVExercise;
import de.saring.exerciseviewer.gui.PVContext;
import de.saring.util.ResourceReader;
import de.saring.util.gui.ListUtils;
import de.saring.util.gui.TableCellRendererOddEven;
import de.saring.util.unitcalc.FormatUtils;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the implementation of the "Laps" panel, which displays all the
 * laps of the current exercise.
 * 
 * @author Stefan Saring
 * @version 1.0
 */
public class LapPanel extends BasePanel {
    
    private static final int COLUMN_COUNT = 13;
    private static final int COLUMN_LAP = 0;
    private static final int COLUMN_LAP_TIME = 1;
    private static final int COLUMN_SPLIT_TIME = 2;
    private static final int COLUMN_HEARTRATE = 3;
    private static final int COLUMN_AVG_HEARTRATE= 4;
    private static final int COLUMN_MAX_HEARTRATE = 5;
    private static final int COLUMN_END_SPEED = 6;
    private static final int COLUMN_AVG_SPEED = 7;
    private static final int COLUMN_DISTANCE = 8;
    private static final int COLUMN_CADENCE = 9;
    private static final int COLUMN_ALTITUDE = 10;
    private static final int COLUMN_ASCENT = 11;
    private static final int COLUMN_TEMPERATURE = 12;

    private String[] columnNames;

    
    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     */
    @Inject
    public LapPanel (PVContext context) {
        super (context);
        initComponents ();
        
        // initialize array of column names
        columnNames = new String[COLUMN_COUNT];        
        columnNames[0]  = getContext ().getResReader ().getString ("pv.laps.lap");
        columnNames[1]  = getContext ().getResReader ().getString ("pv.laps.lap_time");
        columnNames[2]  = getContext ().getResReader ().getString ("pv.laps.split_time");
        columnNames[3]  = getContext ().getResReader ().getString ("pv.laps.heartrate");
        columnNames[4]  = getContext ().getResReader ().getString ("pv.laps.avg_heartrate");
        columnNames[5]  = getContext ().getResReader ().getString ("pv.laps.max_heartrate");
        columnNames[6]  = getContext ().getResReader ().getString ("pv.laps.end_speed");
        columnNames[7]  = getContext ().getResReader ().getString ("pv.laps.avg_speed");
        columnNames[8]  = getContext ().getResReader ().getString ("pv.laps.distance");
        columnNames[9]  = getContext ().getResReader ().getString ("pv.laps.cadence");
        columnNames[10] = getContext ().getResReader ().getString ("pv.laps.altitude");
        columnNames[11] = getContext ().getResReader ().getString ("pv.laps.ascent");
        columnNames[12] = getContext ().getResReader ().getString ("pv.laps.temperature");

        tbLaps.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        ListUtils.increaseTableRowHeight (tbLaps);        
    }
    
    /** {@inheritDoc} */
    @Override
    public void displayExercise () {
        final PVExercise exercise = getDocument ().getExercise ();
        
        // create a new table model for the exercise laps
        tbLaps.setModel (new AbstractTableModel () {
            
            // returns the column name
            public String getColumnName (int col) {
                return columnNames[col];
            }

            // returns the number of laps
            public int getRowCount() {
                if (exercise.getLapList () != null) {
                    return exercise.getLapList ().length;
                }
                return 0;
            }

            // returns the number of columns
            public int getColumnCount () {
                return columnNames.length; 
            }

            /** Returns the class of the specified column (needes for sorting). */
            @Override public Class<?> getColumnClass (int col) {
                switch (col) {                
                    case COLUMN_LAP:
                    case COLUMN_LAP_TIME: 
                    case COLUMN_SPLIT_TIME: 
                    case COLUMN_DISTANCE: 
                    case COLUMN_ASCENT: 
                        return Integer.class;
                    case COLUMN_HEARTRATE: 
                    case COLUMN_AVG_HEARTRATE: 
                    case COLUMN_MAX_HEARTRATE: 
                    case COLUMN_ALTITUDE:
                    case COLUMN_CADENCE: 
                    case COLUMN_TEMPERATURE: 
                        return Short.class;
                    case COLUMN_END_SPEED: 
                    case COLUMN_AVG_SPEED: 
                        return Float.class;
                    default:
                        return Object.class;
                }
            }

            // returns the lap value for the specified column 
            public Object getValueAt (int row, int col) {
                Lap lap = exercise.getLapList ()[row];
                
                switch (col) {                
                    case COLUMN_LAP: // lap number
                        return Integer.valueOf (row + 1);
                        
                    case COLUMN_LAP_TIME: // lap time (= split time of current - split time of previous lap)
                        int previousLapSplitTime = 0;
                        if (row > 0) {
                            previousLapSplitTime = exercise.getLapList ()[row-1].getTimeSplit ();
                        }
            		return Integer.valueOf (lap.getTimeSplit () - previousLapSplitTime);
                        
                    case COLUMN_SPLIT_TIME: // lap split time
                        return Integer.valueOf (lap.getTimeSplit ());
                    
                    case COLUMN_HEARTRATE: // heartrate at lap split
                        return Short.valueOf (lap.getHeartRateSplit ());
                    
                    case COLUMN_AVG_HEARTRATE: // average heartrate of lap
                        return Short.valueOf (lap.getHeartRateAVG ());
                    
                    case COLUMN_MAX_HEARTRATE: // maximal heartrate of lap
                        return Short.valueOf (lap.getHeartRateMax ());
                    
                    case COLUMN_END_SPEED: // speed at lap split
                        if (lap.getSpeed () != null) {
                            return new Float (lap.getSpeed ().getSpeedEnd ());
                        } break;
                    
                    case COLUMN_AVG_SPEED: // average speed of lap
                        if (lap.getSpeed () != null) {
                            return new Float (lap.getSpeed ().getSpeedAVG ());
                        } break;
                    
                    case COLUMN_DISTANCE: // distance at lap split
                        if (lap.getSpeed () != null) {
                            return Integer.valueOf (lap.getSpeed ().getDistance ());
                        } break;
                    
                    case COLUMN_CADENCE: // cadence at lap split
                        if ((lap.getSpeed () != null) && (exercise.getRecordingMode ().isCadence ())) {
                            return Short.valueOf (lap.getSpeed ().getCadence ());
                        } break;                        
                    
                    case COLUMN_ALTITUDE: // altitude at lap split
                        if (lap.getAltitude () != null) {
                            return Short.valueOf (lap.getAltitude ().getAltitude ());
                        } break;
                    
                    case COLUMN_ASCENT: // ascent at lap split (lap ascent can't be displayed for HRM files)
                        if ((lap.getAltitude () != null) && 
                            (exercise.getFileType () != PVExercise.ExerciseFileType.HRM)) {
                            return Integer.valueOf (lap.getAltitude ().getAscent ());
                        } break;
                    
                    case COLUMN_TEMPERATURE: // temperature at lap split
                        if (lap.getTemperature () != null) {
                            return Short.valueOf (lap.getTemperature ().getTemperature ());
                        } break;
                }
                return null;
            }

            // the table cells are not editable
            public boolean isCellEditable (int row, int col) {
                return false;
            }        
        });
        
        // setup table renderer for all columns
        LapCellRenderer cellRenderer = new LapCellRenderer ();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            tbLaps.getColumnModel ().getColumn (i).setCellRenderer (cellRenderer);
        }
    }
            
    /** 
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        laLaps = new javax.swing.JLabel();
        spLaps = new javax.swing.JScrollPane();
        tbLaps = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        laLaps.setFont(laLaps.getFont().deriveFont(laLaps.getFont().getStyle() | java.awt.Font.BOLD));
        laLaps.setText("_All Recorded Laps");
        laLaps.setName("pv.laps.recorded_laps"); // NOI18N

        tbLaps.setAutoCreateRowSorter(true);
        tbLaps.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbLaps.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tbLaps.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbLaps.setShowHorizontalLines(false);
        tbLaps.setShowVerticalLines(false);
        tbLaps.getTableHeader().setReorderingAllowed(false);
        spLaps.setViewportView(tbLaps);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(laLaps)
                .addContainerGap(403, Short.MAX_VALUE))
            .addComponent(spLaps, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(laLaps)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spLaps, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel laLaps;
    private javax.swing.JScrollPane spLaps;
    private javax.swing.JTable tbLaps;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Cell renderer implementation for the lap table.
     */
    class LapCellRenderer extends TableCellRendererOddEven {
        
        final FormatUtils formatUtils = getContext ().getFormatUtils ();
        
        /** Standard c'tor. */
        public LapCellRenderer () {
            super (getContext ().getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_ODD),
                getContext ().getResReader ().getColor (ResourceReader.COMMON_TABLE_BACKGROUND_EVEN));
        }
        
        /** Returns the cell component for the specified value and position. */
        @Override
        public Component getTableCellRendererComponent (
            JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

            // get component of superclass renderer, this will only be customized
            Component component = super.getTableCellRendererComponent (table, value, isSelected, hasFocus, rowIndex, vColIndex);
            
            // format value text depending on column
            String text = null;
            switch (vColIndex) {     
                case COLUMN_LAP:
                    text = value.toString (); break;
                    
                case COLUMN_LAP_TIME: 
                case COLUMN_SPLIT_TIME:
                    text = formatUtils.tenthSeconds2TimeString ((Integer) value); break;
                    
                case COLUMN_HEARTRATE:
                case COLUMN_AVG_HEARTRATE:
                case COLUMN_MAX_HEARTRATE:
                    text = formatUtils.heartRateToString ((Short) value); break;
                    
                case COLUMN_END_SPEED:
                case COLUMN_AVG_SPEED:
                    if (value != null) {
                        text = formatUtils.speedToString ((Float) value, 2);
                    } break;
                    
                case COLUMN_DISTANCE:
                    if (value != null) {
                        text = formatUtils.distanceToString ((Integer) value / 1000f, 3);
                    } break;
                case COLUMN_CADENCE: 
                    if (value != null) {
                        text = formatUtils.cadenceToString ((Short) value);
                    } break;
                case COLUMN_ALTITUDE:
                    if (value != null) {
                        text = formatUtils.heightToString ((Short) value);
                    } break;
                case COLUMN_ASCENT:
                    if (value != null) {
                        text = formatUtils.heightToString ((Integer) value);
                    } break;
                case COLUMN_TEMPERATURE:
                    if (value != null) {
                        text = formatUtils.temperatureToString ((Short) value);
                    } break;
            }
            
            setText (text);
            return component;
        }    
    }
}
