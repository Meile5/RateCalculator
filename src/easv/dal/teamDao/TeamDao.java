package easv.dal.teamDao;

import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeamDao implements ITeamDao {
    private IConnection connectionManager;

    public TeamDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }


    public Map<Integer, Team> getTeams() throws RateException {
        String sql = "SELECT  * FROM Teams";
        Map<Integer, Team> teams = new HashMap<>();
        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("TeamId");
                    String name = rs.getString("TeamName");
                    Currency currency = Currency.valueOf(rs.getString("TeamCurrency"));
                    Team team = new Team(name, id, currency);
                    teams.put(team.getId(), team);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return teams;
    }


    @Override
    public boolean savePerformedDistribution(Map<Team, Map<RateType, Double>> receivedTeams, Team selectedTeamToDistributeFrom, Double sharedOverheadDay, Double sharedOverheadHour) throws RateException {
        Connection conn = null;


        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            //insert new configuration
            int insertedTeamConfigurationId = insertTeamNewConfiguration(conn, selectedTeamToDistributeFrom.getActiveConfiguration());
            System.out.println(insertedTeamConfigurationId + "inserted id");
            // map team with configuration
            mapTeamWithConfiguration(conn, selectedTeamToDistributeFrom.getId(), insertedTeamConfigurationId);
            //insert team employees in teamConfigurationHistory
            insertTeamConfigurationEmployees(conn, insertedTeamConfigurationId, selectedTeamToDistributeFrom.getEmployees());
            //insert team  shared distribution
            insertTeamSharedDistribution(conn, insertedTeamConfigurationId, receivedTeams);
            //insert teams into received overhead
            insertReceivedOverhead(conn, insertedTeamConfigurationId, receivedTeams);

            conn.commit();
            return true;
        } catch (RateException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }


    // TOdo remove the    print  stackTrace

    /**
     * save the  distribution resulted configuration
     */
    private int insertTeamNewConfiguration(Connection conn, TeamConfiguration teamConfiguration) {
        System.out.println(teamConfiguration);
        Integer teamConfigurationId = null;
        String sql = "INSERT INTO TeamConfiguration (TeamDailyRate,TeamHourlyRate,GrossMargin,MarkupMultiplier,ConfigurationDate,Active) values(?,?,?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            psmt.setDouble(1, teamConfiguration.getTeamDayRate().doubleValue());
            psmt.setDouble(2, teamConfiguration.getTeamHourlyRate().doubleValue());
            psmt.setDouble(3, teamConfiguration.getGrossMargin());
            psmt.setDouble(4, teamConfiguration.getMarkupMultiplier());
            psmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setString(6, "true");
             int updatedRows = psmt.executeUpdate();
             if(updatedRows==0){
                 throw  new RateException(ErrorCode.OPERATION_DB_FAILED);
             }

            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    teamConfigurationId = res.getInt(1);
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } catch (SQLException | RateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return teamConfigurationId;
    }


    //TOdo remove printStackTrace

    /**
     * save the resulted distribution configuration for the team
     */
    private boolean mapTeamWithConfiguration(Connection conn, int teamId, int configId) throws RateException {
        String sql = "INSERT INTO TeamConfigurationsHistory (TeamConfigurationID,TeamID) values(?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, configId);
            psmt.setInt(2, teamId);
            int updatedRows = psmt.executeUpdate();
            if(updatedRows==0){
                throw  new RateException(ErrorCode.OPERATION_DB_FAILED);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }


    //TODO remove the stackTrace

    /**
     * insert team  configuration employees
     */
    private boolean insertTeamConfigurationEmployees(Connection conn, int configId, List<Employee> employees) throws RateException {
        String sql = "INSERT INTO TeamEmployeesHistory (EmployeeName,EmployeeDailyRate,EmployeeHourlyRate,TeamConfigurationId,Currency) values(?,?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : employees) {
                psmt.setString(1, employee.getName());
                psmt.setDouble(2, employee.getActiveConfiguration().getDayRate().doubleValue());
                psmt.setDouble(3, employee.getActiveConfiguration().getHourlyRate().doubleValue());
                psmt.setInt(4, configId);
                psmt.setString(5, employee.getCurrency().name());
                psmt.addBatch();
            }
            psmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * insert team shared distribution
     */

    private boolean insertTeamSharedDistribution(Connection conn, int configId, Map<Team, Map<RateType, Double>> receivedTeams) throws RateException {
        String sql = "INSERT INTO TeamSharedDistribution (TeamConfigurationID,SharedTeamID,SharedDayOverhead,SharedHourOverhead)  values(?,?,?,?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : receivedTeams.keySet()) {
                psmt.setInt(1, configId);
                psmt.setInt(2, team.getId());
                psmt.setDouble(3, receivedTeams.get(team).get(RateType.DAY_RATE));
                psmt.setDouble(4, receivedTeams.get(team).get(RateType.HOUR_RATE));
                psmt.addBatch();
            }
            psmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

    }


    /**
     * insert the received  overhead from the distribution operation
     */
    private boolean insertReceivedOverhead(Connection conn, int configId, Map<Team, Map<RateType, Double>> receivedTeams) throws RateException {
        String sql = "INSERT INTO TeamReceivedDistribution(TeamConfigurationID,ReceivedTeamID,ReceivedDayOverhead,ReceivedHourOverhead) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : receivedTeams.keySet()) {
                psmt.setInt(1, configId);
                psmt.setInt(2, team.getId());
                psmt.setDouble(3, receivedTeams.get(team).get(RateType.DAY_RATE));
                psmt.setDouble(4, receivedTeams.get(team).get(RateType.HOUR_RATE));
                psmt.addBatch();
            }
            psmt.executeBatch();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }


}
