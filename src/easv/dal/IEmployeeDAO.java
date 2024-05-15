package easv.dal;

import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IEmployeeDAO {
    LinkedHashMap<Integer, Employee> returnEmployees() throws RateException;

    Integer addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;
    void addNewCountryOrTeam(Employee employee, boolean newCountry, boolean newTeam, Connection conn) throws RateException, SQLException;
    Integer addCountry(Country country, Connection conn) throws RateException, SQLException;
    Integer addTeam(Team team, Connection conn) throws RateException, SQLException;
    void addEmployeeToTeam(int employeeID, List<Team> teams, Connection conn) throws RateException, SQLException;
    Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException;
    void addEmployeeConfiguration(int employeeID, int configurationID, Connection conn) throws RateException, SQLException;

    Employee saveEditOperation(Employee editedEmployee,int oldConfigurationId) throws RateException;


    /**retrieve the teams with associated  employees  */
    Map<Integer, Team> getTeamsWithEmployees() throws RateException;

    /**retrieve all the countries with the associated teams */
    Map<Integer, Country> getCountriesWithTeams(Map<Integer,Team> teams) throws RateException;

    /**retrieve regions with the associated countries*/
    Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException;

    Integer addNewTeamConfiguration(TeamConfiguration teamConfiguration, Team team) throws SQLException, RateException;
}
