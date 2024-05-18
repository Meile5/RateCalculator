package easv.dal.regionDAO;

import easv.be.Country;
import easv.be.Region;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RegionDAO implements IRegionDAO{

    private final IConnection connectionManager;
    private static final Logger LOGGER = Logger.getLogger(IRegionDAO.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println("Logger could not be created");
        }
    }


    public RegionDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }


    @Override
    public Integer addRegion(Region region, List<Country> countries) throws RateException {
        Integer regionID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Region (Name) VALUES (?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, region.getRegionName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        regionID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                if(!countries.isEmpty()){
                    addCountryToRegion(regionID, countries, conn);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return regionID;
    }

    @Override
    public void addCountryToRegion(Integer regionID, List<Country> countries, Connection conn) throws SQLException {
        String sql = "INSERT INTO RegionCountry (RegionID, CountryID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Country country : countries) {
                psmt.setInt(1, regionID);
                psmt.setInt(2, country.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        }
    }

    @Override
    public void updateRegion(Region region, List<Country> countries) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE Region SET Name = ? WHERE ID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, region.getRegionName());
                psmt.setInt(2, region.getId());
                psmt.executeUpdate();

                if(!countries.isEmpty()){
                    addCountryToRegion(region.getId(), countries, conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
    }

    @Override
    public void deleteRegion(Region region) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "DELETE FROM Region WHERE ID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, region.getId());
                psmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
    }

}
