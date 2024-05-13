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
import javafx.collections.ObservableMap;

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
                for (Team team : employee.getTeams()) {
                    countries.addAll(retrieveCountriesForEmployee(team.getId(), conn));
                }
                employee.setCountries(countries);
            }
            for (Employee employee : employees.values()) {
                List<Region> regions = new ArrayList<>();
                for (Country country : employee.getCountries()) {
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
    private List<Team> retrieveTeamsForEmployee(int employeeId, Connection conn) throws SQLException {
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
    private List<Country> retrieveCountriesForEmployee(int teamId, Connection conn) throws SQLException {
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

    private List<Region> retrieveRegionsForEmployee(int countryId, Connection conn) throws SQLException {
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
                "conf.Date AS ConfigurationDate, conf.Active, conf.DayRate,conf.HourlyRate,conf.DayWorkingHours " +
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
                int dayWorkingHours = res.getInt("DayWorkingHours");
                Configuration configuration = new Configuration(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, configurationDate, active, dayRate, hourlyRate,dayWorkingHours);
                configurations.add(configuration);
            }
        }
        return configurations;
    }


    @Override
    public Integer addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        Integer employeeID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Employees (Name, employeeType, Currency) VALUES (?, ?, ?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, employee.getName());
                psmt.setString(2, employee.getEmployeeType().toString());
                psmt.setString(3, employee.getCurrency().toString());
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
                if(!teams.isEmpty()){
                    addEmployeeToTeam(employeeID, teams, conn);
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
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, UtilizationPercentage, WorkingHours, Date, Active, DayRate, HourlyRate, DayWorkingHours) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getUtilizationPercentage());
            psmt.setBigDecimal(5, configuration.getWorkingHours());
            psmt.setTimestamp(6, Timestamp.valueOf(configuration.getSavedDate()));
            psmt.setString(7, String.valueOf(configuration.isActive()));
            psmt.setBigDecimal(8, configuration.getDayRate());
            psmt.setBigDecimal(9, configuration.getHourlyRate());
            psmt.setInt(10, configuration.getDayWorkingHours());
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
    public void addEmployeeToTeam(int employeeID, List<Team> teams, Connection conn) throws RateException, SQLException {
        String sql = "INSERT INTO TeamEmployee (TeamID, EmployeeID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : teams) {
                psmt.setInt(1, team.getId());
                psmt.setInt(2, employeeID);
                psmt.executeUpdate();
            }
            psmt.executeBatch();
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
     *
     * @param editedEmployee     the employee object that was edited
     * @param oldConfigurationId the old configuration that needs to be set to inactive
     */
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
            editedEmployee.getActiveConfiguration().setConfigurationId(addConfigurationWithAdditionalMultipliers(conn, editedEmployee.getActiveConfiguration()));
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
                    LOGGER.log(Level.SEVERE, "error in save edit employee opeartion", e);
                    throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close the database connection", e);
                }
            }
        }
        return null;
    }

    /**
     * get all the teams with employees in the system
     */
    @Override
    public Map<Integer, Team> getTeamsWithEmployees() throws RateException {
        String sql = "SELECT t.TeamID,t.TeamName,e.EmployeeID,e.Name,e.EmployeeType,e.Currency FROM TeamEmployee  te " +
                "JOIN Employees e ON e.EmployeeID=te.EmployeeID " +
                "JOIN Teams t ON t.TeamId = te.TeamId " +
                "Order By TeamID; ";
        Map<Integer, Team> retrievedTeams = new HashMap<>();
        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int teamId = rs.getInt("TeamID");
                    Team currentTeam = retrievedTeams.get(teamId);
                    if (currentTeam == null) {
                        currentTeam = new Team(rs.getString("TeamName"), teamId, new ArrayList<>(), new ArrayList<>());
                        currentTeam.setTeamConfigurationsHistory(retrieveTeamConfigurations(currentTeam,conn));
                        retrievedTeams.put(currentTeam.getId(), currentTeam);
                    }
                    int employeeId = rs.getInt("EmployeeID");
                    String employeeName = rs.getString("Name");
                    EmployeeType employeeType = EmployeeType.valueOf(rs.getString("EmployeeType"));
                    Currency currency = Currency.valueOf(rs.getString("Currency"));
                    List<Configuration> employeeConfigurations = retrieveConfigurationsForEmployee(employeeId, conn);
                    Employee employee = new Employee(employeeName, employeeType, currency, employeeConfigurations);
                    currentTeam.addNewTeamMember(employee);
                }
            }
        } catch (SQLException | RateException e) {
            e.printStackTrace();
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return retrievedTeams;
    }


    /**
     * get all the  operational countries with teams
     */
    @Override
    public Map<Integer, Country> getCountriesWithTeams(Map<Integer, Team> teams) throws RateException {
        String sql = "SELECT c.* ,ct.TeamID from CountryTeam ct " +
                "JOIN Countries c ON c.CountryID =ct.CountryID ORDER BY c.CountryID;";

        Map<Integer, Country> retrievedCountries = new HashMap<>();

        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int countryId = rs.getInt("CountryID");
                    Country currentCountry = retrievedCountries.get(countryId);
                    if (currentCountry == null) {
                        currentCountry = new Country(rs.getString("CountryName"), countryId, new ArrayList<>());
                        retrievedCountries.put(currentCountry.getId(), currentCountry);
                    }
                    currentCountry.addNewTeam(teams.get(rs.getInt("TeamID")));

                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

        return retrievedCountries;
    }


    /**
     * get all regions in the system with associated countries
     */

    @Override
    public Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException {
        String sql = "SELECT r.*,rc.CountryID from RegionCountry rc " +
                "JOIN Region r ON r.RegionID = rc.RegionID " +
                "ORDER BY r.RegionID; ";
        Map<Integer, Region> retrievedRegions = new HashMap<>();

        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int regionId = rs.getInt("RegionID");
                    Region currentRegion = retrievedRegions.get(regionId);
                    if (currentRegion == null) {
                        currentRegion = new Region(rs.getString("RegionName"), regionId, new ArrayList<>());
                        retrievedRegions.put(currentRegion.getId(), currentRegion);
                    }
                    currentRegion.addCountryToRegion(countriesWithTeams.get(rs.getInt("CountryID")));
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

        return retrievedRegions;
    }


    /**
     * set the old configuration active status to false
     */
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

    /**
     * add configuration with grossMargin and markup
     */
    private Integer addConfigurationWithAdditionalMultipliers(Connection conn, Configuration configuration) throws SQLException, RateException {
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


    /**retrieve the team configurations and set the active configuration */
    private List<TeamConfiguration> retrieveTeamConfigurations(Team team, Connection conn) {
        String sql = "SELECT tc.* FROM TeamConfigurationsHistory tch " +
                "JOIN Teams t ON t.TeamID=tch.TeamID " +
                "JOIN TeamConfiguration tc ON tc.TeamConfigurationID = tch.TeamConfigurationID " +
                "WHERE t.TeamId=?";
        List<TeamConfiguration> teamConfigurations = new ArrayList<>();
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, team.getId());
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
            int configId = rs.getInt("TeamConfigurationID");
            BigDecimal teamDailyRate = BigDecimal.valueOf(rs.getDouble("TeamDailyRate"));
            BigDecimal teamHourlyRate = BigDecimal.valueOf(rs.getDouble("TeamHourlyRate"));
            double grossMargin = rs.getDouble("GrossMargin");
            double markupMultiplier = rs.getDouble("MarkupMultiplier");
            LocalDateTime savedDate = rs.getTimestamp("ConfigurationDate").toLocalDateTime();
            boolean active = Boolean.parseBoolean(rs.getString("Active"));
            List<TeamConfigurationEmployee> teamConfigurationEmployees = getEmployeesForTeamConfiguration(configId, conn);
            TeamConfiguration teamConfiguration = new TeamConfiguration(teamDailyRate, teamHourlyRate, grossMargin, markupMultiplier, savedDate, teamConfigurationEmployees, active);
            teamConfiguration.setId(configId);
            if (active) {
                team.setActiveConfiguration(teamConfiguration);
            }
            teamConfigurations.add(teamConfiguration);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teamConfigurations;
    }

    private List<TeamConfigurationEmployee> getEmployeesForTeamConfiguration(int teamConfigurationId, Connection conn) throws SQLException {
        String sql = "SELECT teh.EmployeeName,teh.EmployeeDailyRate,teh.EmployeeHourlyRate from TeamEmployeesHistory teh WHERE  teh.TeamConfigurationId=?";
        List<TeamConfigurationEmployee> configurationTeamMembers = new ArrayList<>();
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamConfigurationId);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString("EmployeeName");
                double employeeDailyRate = rs.getDouble("EmployeeDailyRate");
                double employeeHourlyRate = rs.getDouble("EmployeeHourlyRate");
                TeamConfigurationEmployee employee = new TeamConfigurationEmployee(employeeName, employeeDailyRate, employeeHourlyRate);
                configurationTeamMembers.add(employee);
            }
        }
        return configurationTeamMembers;
    }

}








