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
import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;


public class EmployeesDAO implements IEmployeeDAO {
    private final IConnection connectionManager;




        private static final Logger LOGGER = Logger.getLogger(EmployeesDAO.class.getName());

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





    public EmployeesDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    /**
     * Retrieves all employees and puts them in a map
     * Retrieves all the employee info like employee teams, configurations and countries and sets them in the lists in employee
     * Finds active configuration and set it in employee
     */
    @Override
    public LinkedHashMap<Integer, Employee> returnEmployees() throws RateException {
        LinkedHashMap<Integer, Employee> employees = new LinkedHashMap<>();
        String sql = "SELECT * FROM Employees";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet res = psmt.executeQuery();
                while (res.next()) {
                    int employeeID = res.getInt("EmployeeID");
                    String name = res.getString("Name");
                    String employeeType = res.getString("EmployeeType");
                    String currency1 = res.getString("Currency");


                    // Retrieve employee type as string
                    EmployeeType type = EmployeeType.valueOf(employeeType);
                    // Retrieve employee type as string
                    Currency currency = Currency.valueOf(currency1);

                    Employee employee = new Employee(name, type, currency);
                    employee.setId(employeeID);

                    // Add Employee to LinkedHashMap
                    employees.put(employeeID, employee);
                }
            }
            for (Employee employee : employees.values()) {
                List<Team> teams = retrieveTeamsForEmployee(employee.getId(), conn);
                employee.setTeams(teams);
            }
            for (Employee employee : employees.values()) {
                List<Country> countries = new ArrayList<>();
                for(Team team : employee.getTeams()){
                    countries.addAll(retrieveCountriesForEmployee(team.getId(), conn));
                }
                employee.setCountries(countries);
            }
            for (Employee employee : employees.values()) {
                List<Region> regions = new ArrayList<>();
                for(Country country : employee.getCountries()){
                    regions.addAll(retrieveRegionsForEmployee(country.getId(), conn));
                }
                employee.setRegions(regions);
            }


            // Retrieve configurations for employees
            for (Employee employee : employees.values()) {
                List<Configuration> configurations = retrieveConfigurationsForEmployee(employee.getId(), conn);
                employee.setConfigurations(configurations);
                for (Configuration configuration : configurations) {
                    if (configuration.isActive()) {
                        employee.setActiveConfiguration(configuration);

                    }
                    }

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

    /**
     * Retrieves the teams for employee
     */
    private List<Team> retrieveTeamsForEmployee(int employeeId, Connection conn)throws SQLException{
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.TeamID, t.TeamName " +
                "FROM TeamEmployee te " +
                "JOIN Teams t ON te.TeamID = t.TeamID " +
                "WHERE te.EmployeeID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employeeId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int teamID = res.getInt("TeamID");
                String teamName = res.getString("TeamName");

                Team team = new Team(teamName, teamID);
                teams.add(team);
            }
        }
        return teams;
    }
    /**
     * Retrieves the countries for employee by using team id
     */
    private List<Country> retrieveCountriesForEmployee(int teamId, Connection conn)throws SQLException{
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT c.CountryID, c.CountryName " +
                "FROM CountryTeam ct " +
                "JOIN Countries c ON ct.CountryID = c.CountryID " +
                "WHERE ct.TeamID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int countryID = res.getInt("CountryID");
                String countryName = res.getString("CountryName");

                Country country = new Country(countryName, countryID);
                countries.add(country);
            }
        }
        return countries;

    }
    private List<Region> retrieveRegionsForEmployee(int countryId, Connection conn)throws SQLException{
        List<Region> regions = new ArrayList<>();
        String sql = "SELECT r.RegionID, r.RegionName " +
                "FROM RegionCountry rc " +
                "JOIN Region r ON rc.RegionID = r.RegionID " +
                "WHERE rc.CountryID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, countryId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int regionID = res.getInt("RegionID");
                String regionName = res.getString("RegionName");

                Region region = new Region(regionName, regionID);
                regions.add(region);
            }
        }
        return regions;

    }

    /**
     * Retrieves the configurations for employee
     */
    private List<Configuration> retrieveConfigurationsForEmployee(int employeeId, Connection conn) throws SQLException {
        List<Configuration> configurations = new ArrayList<>();
        String sql = "SELECT " +
                "conf.ConfigurationID, conf.AnnualSalary, conf.FixedAnnualAmount, " +
                "conf.OverheadMultiplier, conf.UtilizationPercentage, conf.WorkingHours, " +
                "conf.Date AS ConfigurationDate, conf.Active, conf.DayRate,conf.HourlyRate " +
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
                boolean active = Boolean.parseBoolean(res.getString("Active"));
                BigDecimal dayRate = res.getBigDecimal("DayRate");
                BigDecimal hourlyRate = res.getBigDecimal("HourlyRate");
                Configuration configuration = new Configuration(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, configurationDate, active, dayRate, hourlyRate);
                configurations.add(configuration);
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
                if (configuration != null) {
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
        if (newCountry) {
            Integer countryID = addCountry(employee.getCountry(), conn);
            if (countryID != null) {
                employee.getCountry().setId(countryID);
            }
        }
        if (newTeam) {
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
            psmt.setString(1, team.getTeamName());
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


    /**
     * set the newest configuration to be the active one
     */
    @Override
    public Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException {
        Integer configurationID = null;
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, UtilizationPercentage, WorkingHours, Date,Active,Markup,GrossMargin) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getUtilizationPercentage());
            psmt.setBigDecimal(5, configuration.getWorkingHours());
            psmt.setTimestamp(6, Timestamp.valueOf(configuration.getSavedDate()));
            psmt.setString(7, String.valueOf(configuration.isActive()));
            psmt.setDouble(8,0);
            psmt.setDouble(9,0);
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


    /**
     * save the edit operation , change the active configuration of the user
     *@param editedEmployee  the employee object that was edited
     * @param oldConfigurationId  the old configuration that needs to be set to inactive*/
    @Override
    public Employee saveEditOperation(Employee editedEmployee, int oldConfigurationId) throws RateException {
        String sql = "UPDATE Employees SET Name=? , employeeType=? , CountryId=? , TeamId=? , Currency=? WHERE EmployeeId=?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, editedEmployee.getName());
                psmt.setString(2, editedEmployee.getType().toString());
                psmt.setInt(3, editedEmployee.getCountry().getId());
                psmt.setInt(4, editedEmployee.getTeam().getId());
                psmt.setString(5, editedEmployee.getCurrency().name());
                psmt.setInt(6, editedEmployee.getId());
                psmt.executeUpdate();
            }

            editedEmployee.getActiveConfiguration().setConfigurationId(addConfigurationWithAdditionalMultipliers(conn,editedEmployee.getActiveConfiguration()));
            setOldConfigurationToInactive(oldConfigurationId, conn);
            addEmployeeConfiguration(editedEmployee.getId(), editedEmployee.getActiveConfiguration().getConfigurationId(), conn);
            conn.commit();
            return editedEmployee;
        } catch (RateException | SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE,"error in save edit employee opeartion",e);
                    throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                   LOGGER.log(Level.SEVERE,"Failed to close the database connection",e);
                }
            }
        }
        return null;
    }


    /**set the old configuration active status to false*/
    private void setOldConfigurationToInactive(int configurationId, Connection conn) throws RateException {
        String sql = "UPDATE  Configurations  Set Active =? where Configurations.ConfigurationId=?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, "false");
            psmt.setInt(2, configurationId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**add configuration with grossMargin and markup */
    private Integer addConfigurationWithAdditionalMultipliers(Connection conn, Configuration configuration) throws SQLException, RateException {
       Integer configurationID =null;
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, UtilizationPercentage, WorkingHours, Date,Active,Markup,GrossMargin) VALUES (?, ?, ?, ?, ?, ?, ?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getUtilizationPercentage());
            psmt.setBigDecimal(5, configuration.getWorkingHours());
            psmt.setTimestamp(6, Timestamp.valueOf(configuration.getSavedDate()));
            psmt.setString(7, String.valueOf(configuration.isActive()));
            psmt.setDouble(8, configuration.getMarkupMultiplier());
            psmt.setDouble(9, configuration.getGrossMargin());
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

}








