package de.saring.sportstracker.gui.views.listviews;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.saring.sportstracker.data.Weight;
import de.saring.sportstracker.gui.STContext;
import de.saring.sportstracker.gui.STController;
import de.saring.sportstracker.gui.STDocument;
import de.saring.util.StringUtils;
import de.saring.util.data.IdObject;
import de.saring.util.gui.javafx.FormattedNumberCellFactory;
import de.saring.util.gui.javafx.LocalDateCellFactory;

/**
 * Controller class of the Weight List View, which displays all the user weight entries in a
 * table view.
 *
 * @author Stefan Saring
 */
@Singleton
public class WeightListViewController extends AbstractListViewController<Weight> {

    @FXML
    private TableView<Weight> tvWeights;

    @FXML
    private TableColumn<Weight, LocalDateTime> tcDate;
    @FXML
    private TableColumn<Weight, Number> tcWeight;
    @FXML
    private TableColumn<Weight, String> tcComment;

    /**
     * Standard c'tor for dependency injection.
     *
     * @param context the SportsTracker UI context
     * @param document the SportsTracker document / model
     * @param controller the SportsTracker UI controller
     */
    @Inject
    public WeightListViewController(final STContext context, final STDocument document, final STController controller) {
        super(context, document, controller);
    }

    @Override
    public ViewType getViewType() {
        return ViewType.WEIGHT_LIST;
    }

    @Override
    public int getSelectedWeightCount() {
        return getSelectedEntryCount();
    }

    @Override
    public int[] getSelectedWeightIDs() {
        return getSelectedEntryIDs();
    }

    @Override
    public void selectEntry(final IdObject entry) {
        if (entry != null && entry instanceof Weight) {
            selectAndScrollToEntry((Weight) entry);
        }
    }

    @Override
    protected String getFxmlFilename() {
        return "/fxml/views/WeightListView.fxml";
    }

    @Override
    protected TableView<Weight> getTableView() {
        return tvWeights;
    }

    @Override
    protected void setupTableColumns() {

        // setup factories for providing cell values
        tcDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        tcWeight.setCellValueFactory(new PropertyValueFactory<>("value"));
        tcComment.setCellValueFactory(cellData -> new SimpleStringProperty( //
                StringUtils.getFirstLineOfText(cellData.getValue().getComment())));

        // setup custom factories for displaying cells
        tcDate.setCellFactory(new LocalDateCellFactory<>());
        tcWeight.setCellFactory(new FormattedNumberCellFactory<>(value -> //
                value == null ? null : getContext().getFormatUtils().weightToString(value.floatValue(), 2)));
    }

    @Override
    protected void setupDefaultSorting() {
        // default sort order is by date descending
        tcDate.setSortType(TableColumn.SortType.DESCENDING);
        tvWeights.getSortOrder().addAll(tcDate);
    }

    @Override
    protected List<Weight> getTableEntries() {
        return getDocument().getWeightList().stream().collect(Collectors.toList());
    }
}
