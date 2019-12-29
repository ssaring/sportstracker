package de.saring.sportstracker.gui.dialogs

import de.saring.sportstracker.data.SportType
import de.saring.sportstracker.data.statistic.EquipmentUsageCalculator
import de.saring.sportstracker.data.statistic.EquipmentUsages
import de.saring.sportstracker.gui.STContext
import de.saring.sportstracker.gui.STDocument
import de.saring.util.gui.javafx.NameableStringConverter
import javafx.fxml.FXML
import javafx.scene.control.ChoiceBox
import javafx.stage.Window

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

        equipmentUsages = EquipmentUsageCalculator.calculateEquipmentUsage(document.exerciseList, document.sportTypeList)

        // fill sport type choice box
        cbSportType.converter = NameableStringConverter()
        document.sportTypeList.forEach { cbSportType.items.add(it) }
        cbSportType.value = document.sportTypeList.first()
    }
}
