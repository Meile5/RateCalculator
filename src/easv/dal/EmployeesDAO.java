package easv.dal;
import easv.be.*;

import easv.be.Country;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.be.Team;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.RateException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class EmployeesDAO implements IEmployeeDAO {

    private final IConnection connectionManager;

    public EmployeesDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    /**
     * Retrieves all employees and puts them in a map
     */
    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {

       ObservableMap<Integer, Employee> employees = FXCollections.observableHashMap();
        String sql = "SELECT " +
                "e.EmployeeID, e.Name AS EmployeeName, e.employeeType, " +
                "c.Name AS Country, t.Name AS Team, e.Currency, " +
                "conf.AnnualSalary, conf.FixedAnnualAmount, conf.OverheadMultiplier, " +
                "conf.UtilizationPercentage, conf.WorkingHours, conf.Date AS ConfigurationDate " +
                "FROM " +
                "Employees e " +
                "INNER JOIN Countries c ON e.CountryID = c.CountryID " +
                "INNER JOIN Teams t ON e.TeamID = t.TeamID " +
                "LEFT JOIN EmployeeConfigurations ec ON e.EmployeeID = ec.EmployeeID " +
                "LEFT JOIN Configurations conf ON ec.ConfigurationID = conf.ConfigurationID";
        Connection conn = null;
        try  {conn=connectionManager.getConnection();


       /* ObservableMap<Integer, Employee> employees = FXCollections.observableHashMap();
        String sql = "SELECT E.EmployeeID, E.Name AS EmployeeName, E.AnnualSalary, " +
                "E.FixedAnnualAmount, E.OverheadMultiplier, E.UtilizationPercentage, " +
                "E.WorkingHours, E.employeeType, C.Name AS CountryName, T.Name AS TeamName " +
                "FROM Employees E " +
                "INNER JOIN Countries C ON E.CountryID = C.CountryID " +
                "INNER JOIN Teams T ON E.TeamID = T.TeamID";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();*/

            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet res = psmt.executeQuery();
                while (res.next()) {
                    int employeeID = res.getInt("EmployeeID");
                    String name = res.getString("EmployeeName");
                    String employeeType = res.getString("employeeType");
                    String countryName = res.getString("Country");
                    String teamName = res.getString("Team");
                    String currency1 = res.getString("Currency");

                    BigDecimal annualSalary = res.getBigDecimal("AnnualSalary");
                    BigDecimal fixedAnnualAmount = res.getBigDecimal("FixedAnnualAmount");
                    BigDecimal overheadMultiplier = res.getBigDecimal("OverheadMultiplier");
                    BigDecimal utilizationPercentage = res.getBigDecimal("UtilizationPercentage");
                    BigDecimal workingHours = res.getBigDecimal("WorkingHours");
                    //Date configurationDate = res.getDate("ConfigurationDate");

                    // Create Country object
                    Country country = new Country(countryName);
                    // Create Team object
                    Team team = new Team(teamName);
                    // Retrieve employee type as string
                    String employeeTypeStr = res.getString("employeeType");
                    // Convert string to enum
                    EmployeeType type = EmployeeType.valueOf(employeeTypeStr);
                    // Retrieve employee type as string
                    String currencyStr = res.getString("Currency");
                    // Convert string to enum
                    Currency currency = Currency.valueOf(currencyStr);


                    Employee employee = new Employee(name, annualSalary, fixedAnnualAmount,
                            overheadMultiplier, utilizationPercentage,
                            workingHours, country, team, type, currency );
                     employee.setId(employeeID);

                    // Add Employee to ObservableMap
                     employees.put(employeeID, employee);




                }
                //connectionManager.releaseConnection(conn);
            }
        } catch (SQLException | RateException e) {

        } /*finally {
            if (conn != null) {
                connectionManager.releaseConnection(conn);
            }
        }*/
        System.out.println(employees + "from dao");
        return employees;

    }


    @Override
    public Integer addEmployee(Employee employee) {
        Integer employeeID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Employees (Name, CountryID, TeamID, Currency) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, employee.getName());
                psmt.setInt(7, 1);
                psmt.setInt(8, 1);
                psmt.setString(9, employee.getCurrency().name());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        employeeID = res.getInt(1);
                    } else {
                        throw new SQLException("No keys generated");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RuntimeException(e);
        } /*finally {
            if (conn != null) {
                connectionManager.releaseConnection(conn);
            }
        }*/
        return employeeID;
    }



}







