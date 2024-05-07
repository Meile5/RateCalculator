package easv.dal.teamDao;
import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TeamDao implements ITeamDao {
    private IConnection connectionManager;
    public TeamDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    //TODO get the teams not the employyes by pagination

   /** retrieves the teams and the associated members
    * @param offset the number off elements that needs to be skipped
    * @param numberOfElements the number of elements that needs to be retrieved next */
   public List<TeamWithEmployees> getTeamsByCountry(Country country,int offset,int numberOfElements) {
       Map<Integer, TeamWithEmployees> teamsMap = new HashMap<>();
       String sql = "SELECT e.EmployeeId,e.Name,e.employeeType,e.Currency,t.TeamId,t.Name as TeamName FROM Employees e "
               +"JOIN Teams t ON e.TeamId = t.TeamId WHERE e.CountryId = ? ORDER BY e.TeamId ";
//               "OFFSET ? ROWS " +
//               "FETCH NEXT ? ROWS ONLY ";
       try (Connection conn = connectionManager.getConnection()) {
           try (PreparedStatement psmt = conn.prepareStatement(sql)) {
               psmt.setInt(1, country.getId());
//               psmt.setInt(2,offset);
//               psmt.setInt(3,numberOfElements);
               ResultSet rs = psmt.executeQuery();
               while (rs.next()) {
                   int teamId = rs.getInt("TeamId");
                   TeamWithEmployees currentTeam = teamsMap.get(teamId);
                   if (currentTeam == null) {
                       currentTeam = createTeamWithEmployees(rs);
                       teamsMap.put(teamId, currentTeam);
                   }
                   int employeeID = rs.getInt("EmployeeID");
                   String name = rs.getString("Name");
                   String employeeType = rs.getString("employeeType");
                   String currencyValue = rs.getString("Currency");
                   Configuration config =  getConfiguration(employeeID,conn);
                   EmployeeType type = EmployeeType.valueOf(employeeType);
                   Currency currency = Currency.valueOf(currencyValue);
                   List<Configuration> configs = new ArrayList<>();
                   configs.add(config);
                   Employee employee = new Employee(name, type, currency,configs);
                   employee.setId(employeeID);
                   if(config.isActive()){
                       employee.setActiveConfiguration(config);
                   }
                   currentTeam.addEmployee(employee);
               }
           }
       } catch (SQLException | RateException e) {
           throw new RuntimeException(e);
       }
       List<TeamWithEmployees> teams = new ArrayList<>(teamsMap.values());
       return teams;
   }

/**initialize an TeamWithEmployees object*/
    private TeamWithEmployees createTeamWithEmployees(ResultSet rs) throws SQLException {
        return new TeamWithEmployees(rs.getString("TeamName"), rs.getInt("TeamId"), new ArrayList<>());
    }



   /**retrieve the latest configuration added for an employee
    * @param employeeId  the employee associated with the config
    * @param conn database connection*/
    private Configuration getConfiguration(int employeeId,Connection conn) {
        Configuration config = null;
        String sql = "SELECT c.* " +
                "FROM EmployeeConfigurations ec " +
                "JOIN Employees e ON e.EmployeeId = ec.EmployeeId " +
                "JOIN Configurations c ON ec.ConfigurationId = c.ConfigurationId " +
                "WHERE ec.EmployeeId = ? " +
                "AND c.Active=?";
        try(PreparedStatement psmt = conn.prepareStatement(sql)){
            psmt.setInt(1,employeeId);
            psmt.setString(2,"true");
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                int configurationId =  rs.getInt("ConfigurationId");
                BigDecimal annualSalary = rs.getBigDecimal("AnnualSalary");
                BigDecimal fixedAnnualAmount = rs.getBigDecimal("FixedAnnualAmount");
                BigDecimal overheadMultiplier = rs.getBigDecimal("OverheadMultiplier");
                BigDecimal utilizationPercentage = rs.getBigDecimal("UtilizationPercentage");
                BigDecimal workingHours = rs.getBigDecimal("WorkingHours");
                boolean active = Boolean.parseBoolean(rs.getString("Active"));
                double markupMultiplier = rs.getDouble("Markup");
                double grossMargin = rs.getDouble("GrossMargin");
                config = new Configuration(configurationId,annualSalary,fixedAnnualAmount,overheadMultiplier,utilizationPercentage,workingHours,active);
                config.setMarkupMultiplier(markupMultiplier);
                config.setGrossMargin(grossMargin);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
         return config;
    }

    public Map<Integer, Team> getTeams() throws RateException {
        String sql = "SELECT  * FROM Teams";
        Map<Integer, Team> teams = new HashMap<>();
        try(Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("TeamId");
                    String name = rs.getString("Name");
                    Team team = new Team(name, id);
                    teams.put(team.getId(), team);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return teams;
    }


}
