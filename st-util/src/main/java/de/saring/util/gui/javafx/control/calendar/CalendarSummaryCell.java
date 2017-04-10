package de.saring.util.gui.javafx.control.calendar;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Calendar cell implementation which shows the summary cell of a week. It displays the
 * week number and the summary information below.
 *
 * @author Stefan Saring
 */
class CalendarSummaryCell extends AbstractCalendarCell {

    /**
     * Standard c'tor.
     */
    public CalendarSummaryCell() {
    	
        /**
         * Hide laNumber label, and draw a red circle to list activities.
         */
    	
        getStyleClass().add("calendar-control-summary-cell");
        addCircle();
		
    }
    
    public void addCircle() {
        Circle cercle=new Circle(10,10,5);
		cercle.setFill(Color.RED);
		cercle.setStroke(Color.DARKRED);
		cercle.setStrokeWidth(2);
		getChildren().remove(super.getNumberLabel());
		getChildren().add(cercle);
		
		// Little animation on the circle when mouse pressed
		cercle.setOnMousePressed(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event) 
			{
				FadeTransition ft = new FadeTransition(Duration.millis(400), cercle);
				ft.setFromValue(1.0);
				ft.setToValue(0.1);
				ft.setCycleCount(40);
				ft.setAutoReverse(true);
				ft.play();

			}
		});
    }

    /**
     * Sets the summary entries to be shown in this cell. Each entry will be shown as
     * a separate label / line.
     *
     * @param entries summary entries
     */
    public void setEntries(final List<String> entries) {
        updateEntryLabels(entries.stream() //
                .map(entry -> new Label(entry)) //
                .collect(Collectors.toList()));
    }
}
