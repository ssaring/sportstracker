package de.saring.sportstracker.storage.db;

import de.saring.sportstracker.core.STException;
import de.saring.sportstracker.core.STExceptionID;
import de.saring.sportstracker.data.Weight;
import de.saring.util.Date310Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Database repository for the Weight data.
 *
 * @author Stefan Saring
 */
public class WeightRepository {

    private static final Logger LOGGER = Logger.getLogger(WeightRepository.class.getName());

    private final Connection connection;

    public WeightRepository(Connection connection) {
        this.connection = connection;
    }

    public List<Weight> readAllWeights() throws STException {
        LOGGER.info("Reading all Weights");
        var weights = new ArrayList<Weight>();

        try(var statement = connection.prepareStatement("SELECT * FROM WEIGHT")) {
            var rs = statement.executeQuery();
            while(rs.next())
            {
                var weight = new Weight(rs.getInt("ID"));
                weight.setDateTime(Date310Utils.dateToLocalDateTime(rs.getDate("DATE_TIME")));
                weight.setValue(rs.getFloat("VALUE"));
                weight.setComment(rs.getString("COMMENT"));
                weights.add(weight);
            }
        } catch (SQLException e) {
            throw new STException(STExceptionID.DBSTORAGE_READ_WEIGHTS, "Failed to read all Weights!", e);
        }
        return weights;
    }
}
