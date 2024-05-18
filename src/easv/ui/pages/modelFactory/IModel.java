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

    void addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate) throws SQLException, RateException;

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
     * save the updated employee to the database
     */
    boolean updateEditedEmployee(Employee employee, Employee editedEmployee) throws RateException;

    List<String> getValidCountries();

    ObservableList<Employee> getSearchResult(String filter);


    void performSelectUserSearchOperation(Employee employee) throws RateException;

    void filterByRegion(Region region, List<Country> countries);


    /**
     * check if edit operation was performed
     */
    boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee);

    void performEmployeeSearchUndoOperation();


    /**
     * calculate the hourly rate for an employee
     */
    BigDecimal getComputedHourlyRate(Employee employee, double configurableHours);

    /**
     * calculate the day rate for an employee
     */

    BigDecimal getComputedDayRate(Employee employee);

    void teamFilterActiveRevert() throws RateException;

    void returnEmployeesByRegion();

    Employee getEmployeeById(int id);


    /**
     * filter employees by the selected country
     */
    void filterByCountryTeams(Country newValue);

    /**
     * filter employees by the selected team
     */
    void filterEmployeesByTeam(Team selectedTeam);

    /**
     * undo the team filter operation to display the country active filter
     */
    void returnEmployeesByCountry();


    /**OVERHEAD DISTRIBUTION RELATED LOGIC*/

    /**
     * calculate team  regions overhead
     */
    List<OverheadComputationPair<String, BigDecimal>> teamRegionsOverhead(int teamId);

    /**
     * add the team and the percentage that user chose to distribute
     *
     * @param teamId             the  id of team that will receive overhead
     * @param overheadPercentage the overhead percentage received by the team
     */
    void addDistributionPercentageTeam(Integer teamId, String overheadPercentage);

    /**
     * remove the team and the inserted overhead percentage from the map
     */
    void removeDistributionPercentageTeam(Integer teamId);


    /**
     * set the selected team that user chose to distribute from and the associated  value
     *
     * @param selectedTeamToDistributeFrom selected team and associated percentage
     */

    void setDistributeFromTeam(Team selectedTeamToDistributeFrom);


    Team getDistributeFromTeam();


    /**
     * validate distribution operation inputs
     */
    DistributionValidation validateInputs();

    Map<Integer, String> getInsertedDistributionPercentageFromTeams();

    /**
     * set the percentage of the selected team to distribute
     */
    void setDistributionPercentageTeam(Integer selectedTeamId, String newValue);

    /** calculate total overhead inserted in order to update the displayed value*/
    double calculateTeTotalOverheadInserted();


    /**get the team name based on the  team id , is used in order to display the error message in distribution page */
    String  getTeamName(int teamId);

    Map<OverheadHistory, List<Team>> performSimulation();
    void addNewRegion(Region region, List<Country> countries) throws RateException;

    void updateRegion(Region region, List<Country> countries) throws RateException;
}
