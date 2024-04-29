package easv.dal;

import easv.be.Country;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.be.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeesDAO implements IEmployeeDAO {

    private final ConnectionManager connectionManager;

    public EmployeesDAO() {
        this.connectionManager = new ConnectionManager();
    }

    /**
     * Retrieves all employees and puts the in a map
     */
    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {

          ObservableMap<Integer, Employee> employees = FXCollections.observableHashMap();
        String sql = "SELECT E.EmployeeID, E.Name AS EmployeeName, E.AnnualSalary, " +
                "E.FixedAnnualAmount, E.OverheadMultiplier, E.UtilizationPercentage, " +
                "E.WorkingHours, E.employeeType, C.Name AS CountryName, T.Name AS TeamName " +
                "FROM Employees E " +
                "INNER JOIN Countries C ON E.CountryID = C.CountryID " +
                "INNER JOIN Teams T ON E.TeamID = T.TeamID";
        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet res = psmt.executeQuery();
                while (res.next()) {
                    int id = res.getInt("EmployeeID");
                    String name = res.getString("Name");
                    BigDecimal annualSalary = res.getBigDecimal("AnnualSalary");
                    BigDecimal fixedAnnualAmount = res.getBigDecimal("FixedAnnualAmount");
                    BigDecimal overheadMultiplier = res.getBigDecimal("OverheadMultiplier");
                    BigDecimal utilizationPercentage = res.getBigDecimal("UtilizationPercentage");
                    BigDecimal workingHours = res.getBigDecimal("WorkingHours");
                    String employeeType = res.getString("employeeType");
                    String countryName = res.getString("CountryName");
                    String teamName = res.getString("TeamName");
                    EmployeeType type = EmployeeType.valueOf(employeeType);

                    // Create Country object
                    Country country = new Country(countryName);

                    // Create Team object
                    Team team = new Team(teamName);

                  //  Employee employee = new Employee(name, annualSalary, fixedAnnualAmount,
                   //         overheadMultiplier, utilizationPercentage,
                    //        workingHours, country, team, type);
                    // employee.setId(id);

                    // Add Employee to ObservableMap
                  //  employees.put(id, employee);
                }
            }
        } catch ( SQLException e) {

        }
        return employees;

    }
}




