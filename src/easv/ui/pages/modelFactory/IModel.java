package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface IModel {
    void returnEmployees() throws RateException;

    void deleteEmployee(Employee employee) throws RateException;

    void setDisplayer(DisplayEmployees displayEmployees);

    ObservableList<Employee> getUsersToDisplay();


    /**
     * get the operational countries  observable list
     */
    ObservableList<Country> getOperationalCountries();

    /**
     * get the operational regions observable list
     */

    ObservableList<Region> getOperationalRegions();

    /**
     * get the  operational teams
     */
    ObservableList<Team> getOperationalTeams();


    Map<String, Country> getCountries();

    void addNewEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException, SQLException;

    void addTeamConfiguration(TeamConfiguration teamConfiguration, Team team) throws SQLException, RateException;

    /**
     * retrieve the teams with the overhead computed
     */
    List<Team> getCountryTeams();

    ObservableMap<Integer, Team> getTeams();

    /**
     * used for country input validation
     */
    void populateValidCountries(List<String> validCountries);

    /**
     * set the country that user has selected from the map
     */
    void setSelectedCountry(String selectedCountry);

    /**
     * get countries values as a Observable list
     */
    ObservableList<Country> getCountiesValues();


    /**
     * save the updated employee to the database
     */
    boolean updateEditedEmployee(Employee employee, Employee editedEmployee) throws RateException;

    List<String> getValidCountries();

    ObservableList<Employee> getSearchResult(String filter);


    void performSelectUserSearchOperation(Employee employee) throws RateException;

    void filterByCountry(Region region,List<Country> countries);


    /**
     * check if edit operation was performed
     */
    boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee);

    void performEmployeeSearchUndoOperation() throws RateException;

    ObservableList<Team> getTeamsForCountry(Country country);

    void filterByCountryAndTeam(Country selectedCountry, Team selectedTeam) throws RateException;


    /**
     * calculate the hourly rate for an employee
     */
    BigDecimal getComputedHourlyRate(Employee employee, double configurableHours);

    /**
     * calculate the day rate for an employee
     */

    BigDecimal getComputedDayRate(Employee employee);


    void filterByTeam(Team team) throws RateException;

    BigDecimal calculateGroupDayRate();

    BigDecimal calculateGroupHourlyRate();

    void teamFilterActiveRevert() throws RateException;

    void returnEmployeesByCountry() throws RateException;

    Employee getEmployeeById(int id);


    /**filter employees by the selected country*/
    void filterByCountryTeams(Country newValue);

    /**filter employees by the selected team */
   void filterEmployeesByTeam(Team selectedTeam);
}
