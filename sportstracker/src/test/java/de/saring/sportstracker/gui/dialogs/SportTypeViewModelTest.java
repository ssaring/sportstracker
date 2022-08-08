package de.saring.sportstracker.gui.dialogs;

import com.garmin.fit.Sport;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.sportstracker.gui.dialogs.SportTypeDialogController.SpeedModeItem;
import de.saring.sportstracker.gui.dialogs.SportTypeViewModel.FitMappingEntry;
import de.saring.util.unitcalc.SpeedMode;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests of class SportTypeViewModel.
 *
 * @author Stefan Saring
 */
public class SportTypeViewModelTest {

    private SportType sportType;

    @BeforeEach
    public void setUp() {
        sportType = new SportType(123);
        sportType.setName("Foo Bar");
        sportType.setRecordDistance(false);
        sportType.setSpeedMode(SpeedMode.PACE);
        sportType.setColor(Color.color(0.8d, 0.7d, 0.6d));
        sportType.setIcon("fooBar.png");
        sportType.setFitId(Integer.valueOf(Sport.CYCLING.getValue()));

        sportType.getSportSubTypeList().set(new SportSubType(200));
        sportType.getSportSubTypeList().set(new SportSubType(201));

        sportType.getEquipmentList().set(new Equipment(300));
        sportType.getEquipmentList().set(new Equipment(301));
    }

    /**
     * Test of method getSportType() without modifications.
     */
    @Test
    public void testGetSportTypeUnmodified() {
        SportTypeViewModel viewModel = new SportTypeViewModel(sportType);

        SportType unmodifiedSportType = viewModel.getSportType();
        assertEquals(sportType.getId(), unmodifiedSportType.getId());
        assertEquals(sportType.getName(), unmodifiedSportType.getName());
        assertEquals(sportType.isRecordDistance(), unmodifiedSportType.isRecordDistance());
        assertEquals(sportType.getSpeedMode(), unmodifiedSportType.getSpeedMode());
        assertEquals(sportType.getColor(), unmodifiedSportType.getColor());
        assertEquals(sportType.getIcon(), unmodifiedSportType.getIcon());
        assertEquals(sportType.getFitId(), unmodifiedSportType.getFitId());

        assertEquals(2, unmodifiedSportType.getSportSubTypeList().size());
        assertEquals(200, unmodifiedSportType.getSportSubTypeList().getAt(0).getId());
        assertEquals(201, unmodifiedSportType.getSportSubTypeList().getAt(1).getId());

        assertEquals(2, unmodifiedSportType.getEquipmentList().size());
        assertEquals(300, unmodifiedSportType.getEquipmentList().getAt(0).getId());
        assertEquals(301, unmodifiedSportType.getEquipmentList().getAt(1).getId());
    }

    /**
     * Test of method getSportType() after modifications.
     */
    @Test
    public void testGetSportTypeModified() {
        SportTypeViewModel viewModel = new SportTypeViewModel(sportType);

        viewModel.name.set("  Bar Foo  ");
        viewModel.recordDistance.set(true);
        viewModel.speedMode.set(SpeedModeItem.SPEED.SPEED);
        viewModel.color.set(Color.color(1.0d, 0.5d, 0d));
        viewModel.fitSportType.set(new FitMappingEntry(Sport.RUNNING.getValue(), ""));

        viewModel.sportSubtypes.removeByID(200);
        viewModel.equipments.set(new Equipment(302));

        SportType modifiedSportType = viewModel.getSportType();
        assertEquals("Bar Foo", modifiedSportType.getName());
        assertTrue(modifiedSportType.isRecordDistance());
        assertEquals(SpeedMode.SPEED, modifiedSportType.getSpeedMode());
        assertEquals(Color.color(1.0d, 0.5d, 0d), modifiedSportType.getColor());
        assertEquals(Sport.RUNNING.getValue(), modifiedSportType.getFitId().intValue());

        assertEquals(1, modifiedSportType.getSportSubTypeList().size());
        assertEquals(201, modifiedSportType.getSportSubTypeList().getAt(0).getId());

        assertEquals(3, modifiedSportType.getEquipmentList().size());
        assertEquals(300, modifiedSportType.getEquipmentList().getAt(0).getId());
        assertEquals(301, modifiedSportType.getEquipmentList().getAt(1).getId());
        assertEquals(302, modifiedSportType.getEquipmentList().getAt(2).getId());
    }
}
