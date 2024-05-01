package easv.dal;

import easv.be.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class EmployeesDAO implements IEmployeeDAO {

    private final ConnectionManager connectionManager;

    public EmployeesDAO() {
        this.connectionManager = new ConnectionManager();
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
        try (Connection conn = connectionManager.getConnection()) {
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
            }
        } catch ( SQLException e) {

        }
        return employees;

    }
}




