package de.saring.sportstracker.gui.dialogs

import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.data.statistic.EquipmentUsage
import de.saring.sportstracker.data.statistic.EquipmentUsageCalculator
import de.saring.sportstracker.data.statistic.EquipmentUsages
import de.saring.sportstracker.gui.STContext
import de.saring.sportstracker.gui.STDocument
import de.saring.util.gui.javafx.FormattedNumberCellFactory
import de.saring.util.gui.javafx.LocalDateCellFactory
import de.saring.util.gui.javafx.NameableStringConverter
import de.saring.util.unitcalc.TimeUtils
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableRow
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.Window
import javafx.util.Callback
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Controller (MVC) class of the Equipment Usage dialog (statistics) of the SportsTracker application.
 *
 * @constructor constructor for dependency injection
 * @param context the SportsTracker UI context
 * @property document the SportsTracker document / model
 *
 * @author Stefan Saring
 */
class EquipmentUsageDialogController(
        context: STContext,
        private val document: STDocument) : AbstractDialogController(context) {

    @FXML
    private lateinit var cbSportType: ChoiceBox<SportType>

    @FXML
    private lateinit var tvEquipmentUsages: TableView<EquipmentUsage>

    @FXML
    private lateinit var tcName: TableColumn<EquipmentUsage, String>
    @FXML
    private lateinit var tcDistance: TableColumn<EquipmentUsage, Number>
    @FXML
    private lateinit var tcDuration: TableColumn<EquipmentUsage, Number>
    @FXML
    private lateinit var tcFirstUsage: TableColumn<EquipmentUsage, LocalDateTime>
    @FXML
    private lateinit var tcLastUsage: TableColumn<EquipmentUsage, LocalDateTime>

    private lateinit var equipmentUsages: EquipmentUsages

    /**
     * Displays the Equipment Usage dialog.
     *
     * @param parent parent window of the dialog
     */
    fun show(parent: Window) {
        showInfoDialog("/fxml/dialogs/EquipmentUsageDialog.fxml", parent,
                context.resources.getString("st.dlg.equipment_usage.title"))
    }

    override fun setupDialogControls() {
        this.equipmentUsages = EquipmentUsageCalculator.calculateEquipmentUsage(
                document.exerciseList, document.sportTypeList)

        setupSportTypeSelection()
        setupEquipmentUsagesTable()
    }

    private fun setupSportTypeSelection() {
        // add all sport types for selection
        cbSportType.converter = NameableStringConverter()
        document.sportTypeList.forEach { cbSportType.items.add(it) }

        // update the usages table when sport type selection changes
        cbSportType.addEventHandler(ActionEvent.ACTION) { updateUsageTable() }
        cbSportType.selectionModel.select(0)
    }

    private fun setupEquipmentUsagesTable() {
        // setup custom factories for getting the cell values
        tcName.cellValueFactory = Callback { SimpleObjectProperty(it.value.equipment.getName()) }
        tcDistance.cellValueFactory = PropertyValueFactory("distance")
        tcDuration.cellValueFactory = PropertyValueFactory("duration")
        // convert LocalDate to LocalDateTime objects, so the  LocalDateCellFactory can be reused
        tcFirstUsage.cellValueFactory = Callback { SimpleObjectProperty(getDateTimeForDate(it.value.firstUsage)) }
        tcLastUsage.cellValueFactory = Callback { SimpleObjectProperty(getDateTimeForDate(it.value.lastUsage)) }

        // setup custom factories for displaying the cell values
        tcDistance.cellFactory = FormattedNumberCellFactory {
            context.formatUtils.distanceToString(it.toDouble(), 1)
        }
        tcDuration.cellFactory = FormattedNumberCellFactory {
            TimeUtils.seconds2TimeString(it.toInt())
        }
        tcFirstUsage.cellFactory = LocalDateCellFactory<EquipmentUsage>()
        tcLastUsage.cellFactory = LocalDateCellFactory<EquipmentUsage>()

        setupTableRowFactory()

        // sort rows by the equipment name column initially
        tvEquipmentUsages.sortOrder.add(tcName)
    }

    /**
     * Initializes the table rows coloring: equipment which is not in use anymore has to be shown in a gray color.
     */
    private fun setupTableRowFactory() {
        tvEquipmentUsages.rowFactory = Callback {
            val tableRow = TableRow<EquipmentUsage?>()

            // update table row color whenever the item value, the selection or the focus has been changed
            tableRow.itemProperty().addListener { _ -> updateTableRowColor(tableRow) }
            tableRow.selectedProperty().addListener { _ -> updateTableRowColor(tableRow) }
            tvEquipmentUsages.focusedProperty().addListener { _ -> updateTableRowColor(tableRow) }
            tableRow
        }
    }

    private fun updateTableRowColor(tableRow: TableRow<EquipmentUsage?>) {
        // display rows for equipment not in use in gray color, otherwise in default color
        // => use white when the row is selected and the table is focused (default)
        // => tableRow.setTextFill() does not work here, color must be set by a CSS style
        tableRow.item?.let {equipmentUsage ->

            val useDefaultColor = tableRow.isSelected && tvEquipmentUsages.isFocused
            val color = if (useDefaultColor) ROW_COLOR_SELECTED_FOCUSED else {
                if (equipmentUsage.equipment.isNotInUse) ROW_COLOR_NOT_IN_USE else ROW_COLOR_DEFAULT
            }
            tableRow.style = "-fx-text-background-color: $color;"
        }
    }

    /**
     * Updates the equipment usage table for the selected sport type.
     */
    private fun updateUsageTable() {
        val selectedSportType = cbSportType.value
        val equipmentUsage = this.equipmentUsages.sportTypeMap[selectedSportType]
                ?: error("Not found for SportType with ID ${selectedSportType.id}!")

        tvEquipmentUsages.items.setAll(equipmentUsage.equipmentMap.values)
        tvEquipmentUsages.sort()
    }

    private fun getDateTimeForDate(date: LocalDate?): LocalDateTime? {
        return if (date == null) return null
        else LocalDateTime.of(date, LocalTime.of(0, 0))
    }

    companion object {
        const val ROW_COLOR_SELECTED_FOCUSED = "#ffffff"
        const val ROW_COLOR_DEFAULT = "#333333"
        const val ROW_COLOR_NOT_IN_USE = "#909090"
    }
}
