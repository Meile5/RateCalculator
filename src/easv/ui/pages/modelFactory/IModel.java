package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

public interface IModel {
    void returnEmployees() throws RateException;

    void deleteEmployee(Employee employee) throws RateException;

    void setDisplayer(DisplayEmployees displayEmployees);

    ObservableList<Employee> getUsersToDisplay();



    Map<String, Country> getCountries();

    void addEmployee(Employee employee, Configuration configuration) throws RateException;

    /**
     * retrieve the teams with the overhead computed
     */
    List<TeamWithEmployees> getCountryTeams();

    /**
     * used to reset the  index of the database retrieval
     */
    public void resetCurrentIndexToRetrieve();

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


    void performSelectUserSearchOperation (Employee employee) throws RateException ;

    void filterByCountry(Country country) throws RateException;


    /**check if edit operation was performed*/
    boolean isEditOperationPerformed( Employee originalEmployee, Employee editedEmployee);
    void performEmployeeSearchUndoOperation() throws RateException;

    ObservableList<Team> getTeamsForCountry(Country country);
    void filterByCountryAndTeam(Country selectedCountry ,Team selectedTeam) throws RateException;





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
    void teamFilterActiveRevert()throws RateException;

    void returnEmployeesByCountry() throws RateException;
}
