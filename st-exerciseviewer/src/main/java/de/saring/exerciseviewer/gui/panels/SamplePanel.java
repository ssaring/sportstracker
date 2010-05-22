package de.saring.exerciseviewer.gui.panels;

import com.google.inject.Inject;
import de.saring.exerciseviewer.data.ExerciseSample;
import de.saring.exerciseviewer.data.EVExercise;
import de.saring.exerciseviewer.gui.EVContext;
import de.saring.util.ResourceReader;
import de.saring.util.gui.ListUtils;
import de.saring.util.gui.TableCellRendererOddEven;
import de.saring.util.unitcalc.FormatUtils;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

/**
 * This class is the implementation of the "Samples" panel, which displays all
 * recorded samples of the exercise in a table.
 * 
 * @author Stefan Saring
 * @version 1.0
 */
public class SamplePanel extends BasePanel {
    
    private static final int COLUMN_COUNT = 7;
    private static final int COLUMN_TIME = 0;
    private static final int COLUMN_HEARTRATE = 1;
    private static final int COLUMN_ALTITUDE = 2;
    private static final int COLUMN_SPEED = 3;
    private static final int COLUMN_DISTANCE = 4;
    private static final int COLUMN_CADENCE = 5;
    private static final int COLUMN_TEMPERATURE = 6;

    private String[] columnNames;

    
    /**
     * Standard c'tor.
     * @param context the ExerciseViewer context
     */
    @Inject
    public SamplePanel (EVContext context) {
        super (context);
        initComponents ();
        
        // initialize array of column names
        columnNames = new String[COLUMN_COUNT];
        columnNames[0] = getContext ().getResReader ().getString ("pv.samples.time");
        columnNames[1] = getContext ().getResReader ().getString ("pv.samples.heartrate");
        columnNames[2] = getContext ().getResReader ().getString ("pv.samples.altitude");
        columnNames[3] = getContext ().getResReader ().getString ("pv.samples.speed");
        columnNames[4] = getContext ().getResReader ().getString ("pv.samples.distance");
        columnNames[5] = getContext ().getResReader ().getString ("pv.samples.cadence");
        columnNames[6] = getContext ().getResReader ().getString ("pv.samples.temperature");

        tbSamples.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);
        ListUtils.increaseTableRowHeight (tbSamples);        
    }
    
    /** {@inheritDoc} */
    @Override
    public void displayExercise () {
        final EVExercise exercise = getDocument ().getExercise ();
        
        // create a new table model for the exercise samples
        tbSamples.setModel (new AbstractTableModel () {
            
            // returns the column name
            @Override public String getColumnName (int col) {
                return columnNames[col];
            }

            // returns the number of samples
            @Override public int getRowCount() {
                if (exercise.getSampleList () != null) {
                    return exercise.getSampleList ().length;
                }
                return 0;
            }

            // returns the number of columns
            @Override public int getColumnCount () {
                return columnNames.length; 
            }

            /** Returns the class of the specified column (needes for sorting). */
            @Override public Class<?> getColumnClass (int col) {
                switch (col) {                
                    case COLUMN_TIME:
                    case COLUMN_DISTANCE: 
                        return Integer.class;
                    case COLUMN_HEARTRATE: 
                    case COLUMN_ALTITUDE:
                    case COLUMN_CADENCE: 
                    case COLUMN_TEMPERATURE: 
                        return Short.class;
                    case COLUMN_SPEED: 
                        return Float.class;
                    default:
                        return Object.class;
                }
            }

            // returns the sample value for the specified column 
            @Override public Object getValueAt (int row, int col) {
                ExerciseSample sample = exercise.getSampleList ()[row];

                switch (col) {                
                    case COLUMN_TIME:
                        return Integer.valueOf (row * exercise.getRecordingInterval ());
                    case COLUMN_HEARTRATE:
                        return Short.valueOf (sample.getHeartRate ());
                    case COLUMN_ALTITUDE:
                        if (exercise.getRecordingMode ().isAltitude ()) {                    
                            return Short.valueOf (sample.getAltitude ());
                        } break;
                    case COLUMN_SPEED:
                        if (exercise.getRecordingMode ().isSpeed ()) {                    
                            return new Float (sample.getSpeed ());
                        } break;
                    case COLUMN_DISTANCE:
                        if (exercise.getRecordingMode ().isSpeed ()) {                    
                            return Integer.valueOf (sample.getDistance ());
                        } break;
                    case COLUMN_CADENCE:
                        if (exercise.getRecordingMode ().isCadence ()) {                    
                            return Short.valueOf (sample.getCadence ());
                        } break;
                    case COLUMN_TEMPERATURE:
                        if (exercise.getRecordingMode ().isTemperature ()) {                    
                            return Short.valueOf (sample.getTemperature ());
                        } break;
                }
                return null;
            }

            // the table cells are not editable
            @Override public boolean isCellEditable (int row, int col) {
                return false;
            }        
        });
        
        // setup table renderer for all columns
        SampleCellRenderer cellRenderer = new SampleCellRenderer ();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            tbSamples.getColumnModel ().getColumn (i).setCellRenderer (cellRenderer);
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

        laSamples = new javax.swing.JLabel();
        spSamples = new javax.swing.JScrollPane();
        tbSamples = new javax.swing.JTable();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

        laSamples.setFont(laSamples.getFont().deriveFont(laSamples.getFont().getStyle() | java.awt.Font.BOLD));
        laSamples.setText("_All Recorded Samples");
        laSamples.setName("pv.samples.recorded_samples"); // NOI18N

        tbSamples.setAutoCreateRowSorter(true);
        tbSamples.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbSamples.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbSamples.setShowHorizontalLines(false);
        tbSamples.setShowVerticalLines(false);
        tbSamples.getTableHeader().setReorderingAllowed(false);
        spSamples.setViewportView(tbSamples);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(laSamples)
                .addContainerGap(380, Short.MAX_VALUE))
            .addComponent(spSamples, javax.swing.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(laSamples)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spSamples, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel laSamples;
    private javax.swing.JScrollPane spSamples;
    private javax.swing.JTable tbSamples;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Cell renderer implementation for the sample table.
     */
    class SampleCellRenderer extends TableCellRendererOddEven {
        
        final FormatUtils formatUtils = getContext ().getFormatUtils ();
        
        /** Standard c'tor. */
        public SampleCellRenderer () {
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
                case COLUMN_TIME:
                    text = formatUtils.seconds2TimeString ((Integer) value); break;
                case COLUMN_HEARTRATE:
                    text = formatUtils.heartRateToString ((Short) value); break;
                case COLUMN_ALTITUDE:
                    if (value != null) {                    
                        text = formatUtils.heightToString ((Short) value);
                    } break;
                case COLUMN_SPEED:
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
