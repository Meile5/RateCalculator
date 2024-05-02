package easv.dal.teamDao;
import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.RateException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TeamDao implements ITeamDao {
    private IConnection connectionManager;
    public TeamDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }


   /** retrieves the teams and the associated members
    * @param offset the number off elements that needs to be skipped
    * @param numberOfElements the number of elements that needs to be retrieved next */
    public List<TeamWithEmployees> getTeamsByCountry(Country country,int offset,int numberOfElements) {
        List<TeamWithEmployees> teams = new ArrayList<>();
        TeamWithEmployees currentTeam = null;
        String sql = "SELECT e.EmployeeId,e.Name,e.employeeType,e.Currency,t.TeamId,t.Name as TeamName FROM Employees e "
                +"JOIN Teams t ON e.TeamId = t.TeamId WHERE e.CountryId = ? ORDER BY e.TeamId "+
                "OFFSET ? ROWS " +
                "FETCH NEXT ? ROWS ONLY ";
        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, country.getId());
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    if (currentTeam == null || rs.getInt("TeamId") != currentTeam.getId()) {
                        currentTeam = createTeamWithEmployees(rs);
                        teams.add(currentTeam);
                    }
                    int employeeID = rs.getInt("EmployeeID");
                    String name = rs.getString("Name");
                    String employeeType = rs.getString("employeeType");
                    String currencyValue = rs.getString("Currency");
                    Configuration config =  getConfiguration(employeeID,conn);
                    EmployeeType type = EmployeeType.valueOf(employeeType);
                    Currency currency = Currency.valueOf(currencyValue);
                    Employee employee = new Employee(name,config.getAnnualSalary() , config.getFixedAnnualAmount(),
                            config.getOverheadMultiplier(), config.getUtilizationPercentage(),
                            config.getWorkingHours(), type, currency);
                    employee.setId(employeeID);
                    currentTeam.addEmployee(employee);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RuntimeException(e);
        }
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
        String sql = "SELECT TOP 1 c.* " +
                "FROM EmployeeConfigurations ec " +
                "JOIN Employees e ON e.EmployeeId = ec.EmployeeId " +
                "JOIN Configurations c ON ec.ConfigurationId = c.ConfigurationId " +
                "WHERE ec.EmployeeId = ? " +
                "ORDER BY c.Date DESC";
        try(PreparedStatement psmt = conn.prepareStatement(sql)){
            psmt.setInt(1,employeeId);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                int configurationId =  rs.getInt("ConfigurationId");
                BigDecimal annualSalary = rs.getBigDecimal("AnnualSalary");
                BigDecimal fixedAnnualAmount = rs.getBigDecimal("FixedAnnualAmount");
                BigDecimal overheadMultiplier = rs.getBigDecimal("OverheadMultiplier");
                BigDecimal utilizationPercentage = rs.getBigDecimal("UtilizationPercentage");
                BigDecimal workingHours = rs.getBigDecimal("WorkingHours");
                config = new Configuration(configurationId,annualSalary,fixedAnnualAmount,overheadMultiplier,utilizationPercentage,workingHours);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
         return config;
    }


}
