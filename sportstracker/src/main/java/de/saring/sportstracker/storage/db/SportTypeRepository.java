package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Equipment;
import de.saring.sportstracker.data.SportSubType;
import de.saring.sportstracker.data.SportType;
import de.saring.util.unitcalc.SpeedMode;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database repository for the SportType and related data.
 *
 * @author Stefan Saring
 */
public class SportTypeRepository {

    private final Connection connection;

    public SportTypeRepository(Connection connection) {
        this.connection = connection;
    }

    public List<SportType> readAllSportTypes() throws STException {
        var sportTypes = new ArrayList<SportType>();

        try(var statement = connection.prepareStatement("SELECT * FROM SPORT_TYPE")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var sportType = new SportType(rs.getInt("ID"));
                sportType.setName(rs.getString("NAME"));
                sportType.setRecordDistance(rs.getBoolean("RECORD_DISTANCE"));
                sportType.setSpeedMode(SpeedMode.valueOf(rs.getString("SPEED_MODE")));
                sportType.setColor(Color.web(rs.getString("COLOR")));
                sportType.setIcon(rs.getString("ICON"));
                sportType.setFitId(RepositoryUtil.getIntegerOrNull(rs, "FIT_ID"));
                sportTypes.add(sportType);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_SPORT_TYPES, "Failed to read all SportTypes!", e);
        }

        readAllSportSubTypes(sportTypes);
        readAllEquipments(sportTypes);
        return sportTypes;
    }

    private void readAllSportSubTypes(List<SportType> sportTypes) throws STException {

        try(var statement = connection.prepareStatement("SELECT * FROM SPORT_SUBTYPE")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var sportType = RepositoryUtil.getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var sportSubType = new SportSubType(rs.getInt("ID"));
                sportSubType.setName(rs.getString("NAME"));
                sportSubType.setFitId(RepositoryUtil.getIntegerOrNull(rs, "FIT_ID"));
                sportType.getSportSubTypeList().set(sportSubType);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_SPORT_SUBTYPES, "Failed to read all SportSubTypes!", e);
        }
    }

    private void readAllEquipments(List<SportType> sportTypes) throws STException {

        try(var statement = connection.prepareStatement("SELECT * FROM EQUIPMENT")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var sportType = RepositoryUtil.getSportTypeById(sportTypes, rs.getInt("SPORT_TYPE_ID"));
                var equipment = new Equipment(rs.getInt("ID"));
                equipment.setName(rs.getString("NAME"));
                equipment.setNotInUse(rs.getBoolean("NOT_IN_USE"));
                sportType.getEquipmentList().set(equipment);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_EQUIPMENTS, "Failed to read all Equipments!", e);
        }
    }
}
