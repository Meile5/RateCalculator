package easv.dal.teamDao;

import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


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
                    Team team = new Team(name, id);
                    teams.put(team.getId(), team);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return teams;
    }


    @Override
    public boolean savePerformedDistribution(Map<Team, Map<RateType, BigDecimal>> receivedTeams, Team selectedTeamToDistributeFrom) throws RateException {

        Connection conn = null;

        System.out.println(selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate() + "from database");
        System.out.println(receivedTeams);
        BigDecimal sharefdDayRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.DAY_RATE);
        BigDecimal sharefdHourRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.HOUR_RATE);


        System.out.println(sharefdDayRate + "sharedDayRate + from dao");

        System.out.println(sharefdHourRate + "sharedDayRate + from dao");
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            //set previous configurations to inactive
            setPreviousConfigurationsToInactive(conn,selectedTeamToDistributeFrom,receivedTeams.keySet());
            //insert new configuration
            Map<Integer,Integer> insertedTeamConfigurationId = insertTeamNewConfiguration(conn, selectedTeamToDistributeFrom, receivedTeams.keySet());

            System.out.println(insertedTeamConfigurationId + "inserted id");
            // map team with configuration
            mapTeamWithConfiguration(conn,insertedTeamConfigurationId);
            //insert team employees in teamConfigurationHistory
            insertTeamConfigurationEmployees(conn,insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getEmployees());
            //insert team  shared distribution


            BigDecimal sharedDayRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.DAY_RATE);
            BigDecimal sharedHourRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.HOUR_RATE);
            insertTeamSharedDistribution(conn, insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getId(),sharedDayRate, sharedHourRate);
            //insert teams into received overhead
            insertReceivedOverhead(conn, insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getId(), receivedTeams);

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
     * save the  distribution resulted configuration , returns a map with the teamId and asociated resulted configuration id
     */
    private Map<Integer, Integer> insertTeamNewConfiguration(Connection conn, Team selectedTeam, Set<Team> receivedTeams) {
        System.out.println(selectedTeam.getActiveConfiguration());
        Map<Integer, Integer> teamConfigMap = new HashMap<>();
        String sql = "INSERT INTO TeamConfiguration (TeamDailyRate, TeamHourlyRate, GrossMargin, MarkupMultiplier, ConfigurationDate, Active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Add the main team configuration
            psmt.setDouble(1, selectedTeam.getActiveConfiguration().getTeamDayRate().doubleValue());
            psmt.setDouble(2, selectedTeam.getActiveConfiguration().getTeamHourlyRate().doubleValue());
            psmt.setDouble(3, selectedTeam.getActiveConfiguration().getGrossMargin());
            psmt.setDouble(4, selectedTeam.getActiveConfiguration().getMarkupMultiplier());
            psmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setString(6, "true");

            // Execute the main team configuration insert
            int affectedRows = psmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
            }

            // Retrieve the generated key for the main team configuration
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    teamConfigMap.put(selectedTeam.getId(), res.getInt(1));
                    selectedTeam.getActiveConfiguration().setId(res.getInt(1));
                    selectedTeam.getTeamConfigurationsHistory().add(selectedTeam.getActiveConfiguration());
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }

            // Insert configurations for each team in receivedTeams
            for (Team team : receivedTeams) {
                psmt.setDouble(1, team.getActiveConfiguration().getTeamDayRate().doubleValue());
                psmt.setDouble(2, team.getActiveConfiguration().getTeamHourlyRate().doubleValue());
                psmt.setDouble(3, team.getActiveConfiguration().getGrossMargin());
                psmt.setDouble(4, team.getActiveConfiguration().getMarkupMultiplier());
                psmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                psmt.setString(6, "true");
                affectedRows = psmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }

                // Retrieve the generated key for each team configuration
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        teamConfigMap.put(team.getId(), res.getInt(1));
                        team.getActiveConfiguration().setId(res.getInt(1));
                        team.getTeamConfigurationsHistory().add(team.getActiveConfiguration());
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
            }
        } catch (SQLException | RateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return teamConfigMap;
    }



    //set  teams old configuration  to inactive
    private boolean setPreviousConfigurationsToInactive(Connection conn, Team selectedTem, Set<Team> receivedTeams) throws RateException {
        String sql = "UPDATE TeamConfiguration set Active =? WHERE TeamConfigurationID=? ";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, "false");
            psmt.setInt(2, selectedTem.getActiveConfiguration().getId());
            psmt.addBatch();
            for (Team team : receivedTeams) {
                psmt.setString(1, "false");
                psmt.setInt(2, team.getActiveConfiguration().getId());
               psmt.addBatch();
            }
           int[] updatedRows= psmt.executeBatch();
            if(updatedRows.length==0){
                throw  new RateException(ErrorCode.OPERATION_DB_FAILED);
            }
            return true;
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }


    }


    //TOdo remove printStackTrace

    /**
     * save the resulted distribution configuration for the team
     */
    private boolean mapTeamWithConfiguration(Connection conn,Map<Integer,Integer> teamsConfigs) throws RateException {
        String sql = "INSERT INTO TeamConfigurationsHistory (TeamConfigurationID,TeamID) values(?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {

    for(Integer teamId : teamsConfigs.keySet()){
        psmt.setInt(1, teamsConfigs.get(teamId));
        psmt.setInt(2, teamId);
        psmt.addBatch();
    }
            int [] updatedRows = psmt.executeBatch();
            if (updatedRows.length == 0) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
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

    private boolean insertTeamSharedDistribution(Connection conn, int configId, int selectedTeamId, BigDecimal sharedDay,BigDecimal sharedHour ) throws RateException {
        String sql = "INSERT INTO TeamSharedDistribution (TeamConfigurationID,SharedTeamID,SharedDayOverhead,SharedHourOverhead)  values(?,?,?,?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, configId);
                psmt.setInt(2, selectedTeamId);
                psmt.setDouble(3,sharedDay.doubleValue() );
                psmt.setDouble(4, sharedHour.doubleValue());
                psmt.addBatch();
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
    private boolean insertReceivedOverhead(Connection conn, int configId, int selectedTeamId,Map<Team, Map<RateType, BigDecimal>> receivedTeams) throws RateException {
        String sql = "INSERT INTO TeamReceivedDistribution(TeamConfigurationID,ReceivedTeamID,ReceivedDayOverhead,ReceivedHourOverhead) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : receivedTeams.keySet()) {
                if(team.getId()!=selectedTeamId){
                    psmt.setInt(1, configId);
                    psmt.setInt(2, team.getId());
                    psmt.setDouble(3, receivedTeams.get(team).get(RateType.DAY_RATE).doubleValue());
                    psmt.setDouble(4, receivedTeams.get(team).get(RateType.HOUR_RATE).doubleValue());
                    psmt.addBatch();
                }

            }
            psmt.executeBatch();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }


}
