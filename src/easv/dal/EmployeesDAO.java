package easv.dal;
import easv.be.*;
import easv.be.Country;
import easv.be.Currency;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.be.Team;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;




import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;


public class EmployeesDAO implements IEmployeeDAO {

    private final IConnection connectionManager;

    public EmployeesDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    /**
     * Retrieves all employees and puts them in a map
     */
    @Override
    public LinkedHashMap<Integer, Employee> returnEmployees() throws RateException {
        LinkedHashMap<Integer, Employee> employees = new LinkedHashMap<>();
        String sql = "SELECT " +
                "e.EmployeeID, e.Name AS EmployeeName, e.employeeType, " +
                "c.Name AS Country, t.Name AS Team, e.Currency " +
                "FROM " +
                "Employees e " +
                "INNER JOIN Countries c ON e.CountryID = c.CountryID " +
                "INNER JOIN Teams t ON e.TeamID = t.TeamID";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet res = psmt.executeQuery();
                while (res.next()) {
                    int employeeID = res.getInt("EmployeeID");
                    String name = res.getString("EmployeeName");
                    String employeeType = res.getString("employeeType");
                    String countryName = res.getString("Country");
                    String teamName = res.getString("Team");
                    String currency1 = res.getString("Currency");

                    // Create Country object
                    Country country = new Country(countryName);
                    // Create Team object
                    Team team = new Team(teamName);
                    // Retrieve employee type as string
                    EmployeeType type = EmployeeType.valueOf(employeeType);
                    // Retrieve employee type as string
                    Currency currency = Currency.valueOf(currency1);

                    Employee employee = new Employee(name, country, team, type, currency);
                    employee.setId(employeeID);

                    // Add Employee to LinkedHashMap
                    employees.put(employeeID, employee);
                }
            }
            // Retrieve configurations for employees
            for (Employee employee : employees.values()) {
                List<Configuration> configurations = retrieveConfigurationsForEmployee(employee.getId(), conn);
                employee.setConfigurations(configurations);
            }
            conn.commit(); // Commit transaction
        } catch (SQLException | RateException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback transaction if there's an exception
                }
            } catch (SQLException ex) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return employees;
    }


    //Todo i commented the code , becouse is having errors with the new database configuration,
    // it is complaining about the construtor , i solve it in the team dal
    private List<Configuration> retrieveConfigurationsForEmployee(int employeeId, Connection conn) throws SQLException {
        List<Configuration> configurations = new ArrayList<>();
        String sql = "SELECT " +
                "conf.ConfigurationID, conf.AnnualSalary, conf.FixedAnnualAmount, " +
                "conf.OverheadMultiplier, conf.UtilizationPercentage, conf.WorkingHours, " +
                "conf.Date AS ConfigurationDate " +
                "FROM " +
                "EmployeeConfigurations ec " +
                "INNER JOIN Configurations conf ON ec.ConfigurationID = conf.ConfigurationID " +
                "WHERE " +
                "ec.EmployeeID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employeeId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int configurationId = res.getInt("ConfigurationID");
                BigDecimal annualSalary = res.getBigDecimal("AnnualSalary");
                BigDecimal fixedAnnualAmount = res.getBigDecimal("FixedAnnualAmount");
                BigDecimal overheadMultiplier = res.getBigDecimal("OverheadMultiplier");
                BigDecimal utilizationPercentage = res.getBigDecimal("UtilizationPercentage");
                BigDecimal workingHours = res.getBigDecimal("WorkingHours");
                LocalDateTime configurationDate = res.getTimestamp("ConfigurationDate").toLocalDateTime();

//                Configuration configuration = new Configuration(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, configurationDate);
//                configurations.add(configuration);
            }
        }
        return configurations;
    }



    @Override
    public Integer addEmployee(Employee employee, boolean newCountry, boolean newTeam, Configuration configuration) throws RateException {
        Integer employeeID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            addNewCountryOrTeam(employee, newCountry, newTeam, conn);
            String sql = "INSERT INTO Employees (Name, employeeType, CountryID, TeamID, Currency) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, employee.getName());
                psmt.setString(2, employee.getEmployeeType().toString());
                psmt.setInt(3, employee.getCountry().getId());
                psmt.setInt(4, employee.getTeam().getId());
                psmt.setString(5, employee.getCurrency().name());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        employeeID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                if(configuration != null) {
                    Integer configurationID = addConfiguration(configuration, conn);
                    if (configurationID != null) {
                        addEmployeeConfiguration(employeeID, configurationID, conn);
                    }
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
        return employeeID;
    }

    @Override
    public void addNewCountryOrTeam(Employee employee, boolean newCountry, boolean newTeam, Connection conn) throws RateException, SQLException {
        if(newCountry) {
            Integer countryID = addCountry(employee.getCountry(), conn);
            if (countryID != null) {
                employee.getCountry().setId(countryID);
            }
        }
        if(newTeam) {
            Integer teamID = addTeam(employee.getTeam(), conn);
            if (teamID != null) {
                employee.getTeam().setId(teamID);
            }
        }
    }

    @Override
    public Integer addCountry(Country country, Connection conn) throws RateException, SQLException {
        Integer countryID = null;
        String sql = "INSERT INTO Countries (Name) VALUES (?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, country.getCountryName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        countryID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
            }
            return countryID;
    }

    @Override
    public Integer addTeam(Team team, Connection conn) throws RateException, SQLException {
        Integer teamID = null;
        String sql = "INSERT INTO Teams (Name) VALUES (?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setString(1, team.getTeam());
            psmt.executeUpdate();
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    teamID = res.getInt(1);
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }
        }
        return teamID;
    }

    @Override
    public Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException {
        Integer configurationID = null;
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, UtilizationPercentage, WorkingHours, Date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getUtilizationPercentage());
            psmt.setBigDecimal(5, configuration.getWorkingHours());
            psmt.setDate(6, Date.valueOf(configuration.getSavedDate().toLocalDate()));
            psmt.executeUpdate();
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    configurationID = res.getInt(1);
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }
        }
        return configurationID;
    }

    @Override
    public void addEmployeeConfiguration(int employeeID, int configurationID, Connection conn) throws RateException, SQLException {
        String sql = "INSERT INTO EmployeeConfigurations (EmployeeID, ConfigurationID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employeeID);
            psmt.setInt(2, configurationID);
            psmt.executeUpdate();
        }
    }


    @Override
    public Boolean deleteEmployee(Employee employee) throws RateException {
        boolean succeeded = false;
        String sql = "DELETE FROM Employees WHERE EmployeeID=?";
        try (Connection conn = connectionManager.getConnection()) {
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, employee.getId());
                psmt.executeUpdate();
                conn.commit();
                succeeded = true;
            } catch (SQLException e) {
                conn.rollback();
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        }
        return succeeded;
    }




}








