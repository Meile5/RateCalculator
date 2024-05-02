package easv.dal.countryDao;

import easv.be.Country;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CountryDao implements ICountryDao {
    private IConnection connectionManager;

    public CountryDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    @Override
    public Map<String, Country> getCountries() throws RateException {
        String sql = "SELECT  * FROM Countries";
        Map<String, Country> countries = new HashMap<>();
        try(Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("CountryId");
                    String name = rs.getString("Name");
                    Country country = new Country(name, id);
                    countries.put(country.getCountryName(), country);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return countries;
    }
}
