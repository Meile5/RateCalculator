
package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.bll.EmployeesLogic.EmployeeManager;
import easv.bll.EmployeesLogic.IEmployeeManager;
import easv.bll.TeamLogic.ITeamLogic;
import easv.bll.TeamLogic.TeamLogic;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class Model implements IModel {


    private ObservableMap<Integer, Employee> employees;

    private IEmployeeManager employeeManager;


    // the bussines logic object responsible of team logic
    private ITeamLogic teamManager;
    /**
     * the logic layer responsible  of countries management
     */
    private ICountryLogic countryLogic;
    /**
     * holds the countries that are currently operational for the company
     */
    private final ObservableMap<String, Country> countries;

    /**
     * displayer of employees
     */
    private DisplayEmployees displayEmployees;

    // collection that holds all the teams related to a country, with all the associated overhead
    private List<TeamWithEmployees> countryTeams;


    /**
     * the value off the selected country from the view map
     */
    private String selectedCountry;


    /**
     * used to check if the inserted country is valid
     */

    private List<String> validMapViewCountryNameValues;
    private final ObservableMap<Integer, Team> teams;

    /**
     * holds all the data related to the teams, like history, employees, countries, regions and active configuration
     */
    private final ObservableMap<Integer, Team> teamsWithEmployees;

    /**
     * holds all the data related to the operational  countries
     */
    private final ObservableMap<Integer, Country> countriesWithTeams;
    /**
     * holds all the date related to the  operational regions
     */
    private final ObservableMap<Integer, Region> regionsWithCountries;

    private ObservableList<Employee> displayedEmployees;
    private ObservableList<Employee> sortedEmployeesByName;
    private ObservableList<Employee> filteredEmployeesListByRegion;
    private ObservableList<Employee> listEmployeeByCountryTemp;


    /**
     * holds the temporary values for the teams that user inserted in the distribution page
     */
    private final Map<Team, String> insertedDistributionPercentageFromTeams;

    /**
     * the selected team that user chose to distribute from and the associated value
     */
    private Team selectedTeamToDistributeFrom;


    public Model() throws RateException {
        this.employees = FXCollections.observableMap(new LinkedHashMap<>());
        this.filteredEmployeesListByRegion = FXCollections.observableArrayList();
        this.listEmployeeByCountryTemp = FXCollections.observableArrayList();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.validMapViewCountryNameValues = new ArrayList<>();
        this.teams = FXCollections.observableHashMap();
        this.countryTeams = new ArrayList<>();
        this.displayedEmployees = FXCollections.observableArrayList();
        this.sortedEmployeesByName = FXCollections.observableArrayList();
        teamsWithEmployees = FXCollections.observableHashMap();
        countriesWithTeams = FXCollections.observableHashMap();
        regionsWithCountries = FXCollections.observableHashMap();
        insertedDistributionPercentageFromTeams = new HashMap<>();
        populateCountries();
        populateTeams();

        populateTeamsWithEmployees();
        populateCountriesWithTeams();
        populateRegionsWithCountries();
    }


    public void setDisplayer(DisplayEmployees displayEmployees) {
        this.displayEmployees = displayEmployees;
    }

    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());
    }


    /**
     * populate the teamWithEmployees map with data
     */
    private void populateTeamsWithEmployees() throws RateException {
        this.teamsWithEmployees.putAll(employeeManager.getTeamWithEmployees());
    }

    /**
     * populate the countriesWithTeams  with data
     */
    private void populateCountriesWithTeams() throws RateException {
        this.countriesWithTeams.putAll(employeeManager.getCountriesWithTeams(teamsWithEmployees));
    }

    /**
     * populate regionsWithCountries with data
     */
    private void populateRegionsWithCountries() throws RateException {
        this.regionsWithCountries.putAll(employeeManager.getRegionsWithCountries(countriesWithTeams));
    }


    /**
     * get the operational countries  observable list
     */
    public ObservableList<Country> getOperationalCountries() {
        ObservableList<Country> observableCountryList = FXCollections.observableArrayList();
        observableCountryList.setAll(countriesWithTeams.values());
        return observableCountryList.sorted();
    }


    /**
     * get the operational regions observable list
     */

    public ObservableList<Region> getOperationalRegions() {
        ObservableList<Region> observableRegionList = FXCollections.observableArrayList();
        observableRegionList.setAll(regionsWithCountries.values());
        return observableRegionList.sorted();
    }


    /**
     * get the  operational teams
     */
    public ObservableList<Team> getOperationalTeams() {
        ObservableList<Team> observableTeamList = FXCollections.observableArrayList();
        observableTeamList.setAll(teamsWithEmployees.values());
        return observableTeamList.sorted();
    }


    @Override
    public void returnEmployees() throws RateException {
        this.employees.putAll(employeeManager.returnEmployees());
        sortDisplayedEmployee();
        this.displayedEmployees = sortedEmployeesByName;

    }


    public Employee getEmployeeById(int id) {
        return employees.get(id);
    }


    private void sortDisplayedEmployee() {
        sortedEmployeesByName.setAll(employeeManager.sortedEmployeesByName(employees.values()));
    }

    @Override
    public void deleteEmployee(Employee employee) throws RateException {
        boolean succeeded = employeeManager.deleteEmployee(employee);
        if (succeeded) {
            // If the deletion was successful, remove the employee from the observable map
            employees.remove(employee.getId());
            sortDisplayedEmployee();
            displayedEmployees = sortedEmployeesByName;
            Platform.runLater(() -> {
                displayEmployees.displayEmployees();
            });
        }
    }

    @Override
    public void addNewEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException, SQLException {
        employee = employeeManager.addEmployee(employee, configuration, teams);
        if (employee != null) {
            employees.put(employee.getId(), employee);

            for (Team team : teams) {
                team.addNewTeamMember(employee);
                TeamConfiguration teamConfiguration = getNewEmployeeTeamConfiguration(team);
                addTeamConfiguration(teamConfiguration, team);
                teamsWithEmployees.get(team.getId()).addNewTeamMember(employee);
            }
        }
    }

    @Override
    public void addTeamConfiguration(TeamConfiguration teamConfiguration, Team team) throws SQLException, RateException {
        int teamConfigurationID = employeeManager.addTeamConfiguration(teamConfiguration, team);
        if (teamConfiguration != null) {
            teamConfiguration.setId(teamConfigurationID);
            teamsWithEmployees.get(team.getId()).setActiveConfiguration(teamConfiguration);
        }
    }

    private TeamConfiguration getNewEmployeeTeamConfiguration(Team team) {
        BigDecimal teamHourlyRate = employeeManager.calculateTeamHourlyRate(team);
        BigDecimal teamDayRate = employeeManager.calculateTeamDayRate(team);
        double grossMargin = 0;
        double markupMultiplier = 0;
        if (team.getActiveConfiguration() != null) {
            grossMargin = checkNullValues(team.getActiveConfiguration().getGrossMargin());
            markupMultiplier = checkNullValues(team.getActiveConfiguration().getMarkupMultiplier());
        }
        LocalDateTime savedDate = LocalDateTime.now();
        return new TeamConfiguration(teamDayRate, teamHourlyRate, grossMargin, markupMultiplier, savedDate, true);
    }

    private double checkNullValues(double numberToCheck) {
        if (numberToCheck > 0) {
            return numberToCheck;
        } else {
            return 0;
        }
    }

    /*
    private BigDecimal calculateSumDayRate(Team team) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Employee employee : team.getEmployees()) {
            sum = sum.add(employee.getActiveConfiguration().getDayRate());
        }
        return sum;
    }

    private BigDecimal calculateSumHourlyRate(Team team) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Employee employee : team.getEmployees()) {
            sum = sum.add(employee.getActiveConfiguration().getHourlyRate());
        }
        return sum;
    }

     */

    //TODO, to delete this method? (it's on the interface) - NELSON
    @Override
    public List<Team> getCountryTeams() {
        return List.of();
    }


    /**
     * return the operational countries
     */
    public Map<String, Country> getCountries() {
        return countries;
    }


    //TODO use the new method called getOperationalCountries();

    /**
     * retrieve the countries as an observable list
     */
    public ObservableList<Country> getCountiesValues() {
        ObservableList<Country> coutriesList = FXCollections.observableArrayList();
        coutriesList.setAll(countries.values());
        return coutriesList;
    }


    /**
     * save the  edited employee to the database , and if the operation is performed
     * add it to the all employees map and update the filtered employees list
     *
     * @param originalEmployee the employee before editing
     * @param editedEmployee   the employee after editing
     */
    @Override
    public boolean updateEditedEmployee(Employee originalEmployee, Employee editedEmployee) throws RateException {
       /* Employee editedEmployeeSaved = employeeManager.saveEditOperation(editedEmployee, originalEmployee.getActiveConfiguration().getConfigurationId());
        if (editedEmployeeSaved != null) {
            editedEmployeeSaved.addConfiguration(editedEmployeeSaved.getActiveConfiguration());
            editedEmployeeSaved.setHourlyRate(employeeManager.getHourlyRate(editedEmployeeSaved,0));
            editedEmployeeSaved.setDailyRate(employeeManager.getDayRate(editedEmployeeSaved));
            this.employees.put(editedEmployee.getId(), editedEmployeeSaved);
            // update the filter list with the new updated values
            for (int i = 0; i < filteredEmployeesList.size(); i++) {
                if (displayedEmployees.get(i).getId()==editedEmployeeSaved.getId()) {
                    displayedEmployees.set(i, editedEmployeeSaved);
                    break;
                }
            }
            return true;
        }*/
        return false;

    }

    /**
     * check if edit operation was performed
     */
    public boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee) {
        return employeeManager.isEmployeeEdited(originalEmployee, editedEmployee);
    }


    public void populateValidCountries(List<String> validCountries) {
        this.validMapViewCountryNameValues.addAll(validCountries);
    }

    public List<String> getValidCountries() {
        System.out.println(validMapViewCountryNameValues);
        return validMapViewCountryNameValues;
    }


    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }


    //Todo if possible use the getOperationalTeams () method
    private void populateTeams() throws RateException {
        this.teams.putAll(teamManager.getTeams());
    }

    public ObservableMap<Integer, Team> getTeams() {
        return teams;
    }

    public ObservableList<Employee> getSearchResult(String filter) {
        ObservableList searchResults = FXCollections.observableArrayList();
        searchResults.setAll(employeeManager.performSearchOperation(employees.values(), filter));
        return searchResults;
    }

    public void performSelectUserSearchOperation(Employee employee) throws RateException {
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        displayedEmployees.setAll(employee);
        displayEmployees.displayEmployees();

    }


    //undo all the filters to display all the employees  in the system
    public void performEmployeeSearchUndoOperation() {
        sortDisplayedEmployee();
        displayedEmployees = sortedEmployeesByName;
        displayEmployees.displayEmployees();
    }


    /**
     * filter the employees that are present in the countries from the selected region
     */
    @Override
    public void filterByRegion(Region region, List<Country> countries) {
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        displayedEmployees.setAll(employeeManager.filterByCountry(region, countries, employees));
        displayEmployees.displayEmployees();
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        listEmployeeByCountryTemp.setAll(displayedEmployees);
    }


    /**
     * filter the employees that are present in the teams from the selected country
     */
    @Override
    public void filterByCountryTeams(Country selectedCountry) {
        displayedEmployees.setAll(employeeManager.filterTeamsByCountry(countriesWithTeams.get(selectedCountry.getId()).getTeams(), employees));
        displayEmployees.displayEmployees();
        //save the values for the selected  country

        /**delete if not need annymore*/
        listEmployeeByCountryTemp.setAll(displayedEmployees);

    }

    /**
     * filter employees by selected team
     */
    public void filterEmployeesByTeam(Team selectedTeam) {
        ObservableList<Employee> teamEmployees = FXCollections.observableArrayList();
        teamEmployees.setAll(employeeManager.filterEmployeesByTeam(selectedTeam, employees));
        displayedEmployees.setAll(employeeManager.filterEmployeesByTeam(selectedTeam, employees));
        displayEmployees.displayEmployees();
        // see if the filtered list needs to be updated
    }


    public void teamFilterActiveRevert() throws RateException {
        displayedEmployees = filteredEmployeesListByRegion;
        displayEmployees.displayEmployees();
    }


    /**
     * undo the country filter selection to show all the employees in the selected region , or all the employees in the system
     */
    public void returnEmployeesByRegion() {

        displayedEmployees.setAll(filteredEmployeesListByRegion);
        displayEmployees.displayEmployees();
        if (areObservableListsEqual(filteredEmployeesListByRegion, displayedEmployees)) {
            filteredEmployeesListByRegion.setAll(displayedEmployees);
        }
        System.out.println("CT Disp" + displayedEmployees);
        System.out.println("CT Filt" + filteredEmployeesListByRegion);
    }


    /**
     * if the country filter is active undo the teams filter to show the employees
     * from all the teams for the active country filter
     */
    @Override
    public void returnEmployeesByCountry() {
        displayedEmployees.setAll(listEmployeeByCountryTemp);
        displayEmployees.displayEmployees();
    }

    private boolean areObservableListsEqual(ObservableList<Employee> list1, ObservableList<Employee> list2) {
        for (Employee employee : list1) {
            if (!list2.contains(employee)) {
                return false;
            }
        }
        return true;
    }

    public ObservableList<Employee> getUsersToDisplay() {
        return displayedEmployees;
    }

    /**
     * calculate the hourly rate for an employee
     */
    public BigDecimal getComputedHourlyRate(Employee employee, double configurableHours) {
        return employeeManager.getHourlyRate(employee, configurableHours);
    }

    /**
     * calculate the day rate for an employee
     */

    public BigDecimal getComputedDayRate(Employee employee) {
        return employeeManager.getDayRate(employee);
    }


/**OVERHEAD DISTRIBUTION RELATED LOGIC*/


    /**
     * calculate the  selected team to distribute from regions overhead
     */
    public List<OverheadComputationPair<String, BigDecimal>> teamRegionsOverhead(int teamId) {
        List<OverheadComputationPair<String, BigDecimal>> teamRegionsOverhead = new ArrayList<>();
        for (Region region : teamsWithEmployees.get(teamId).getRegions()) {
            OverheadComputationPair<String, BigDecimal> regionOverhead = teamManager.computeRegionOverhead(region);
            teamRegionsOverhead.add(regionOverhead);
        }
        return teamRegionsOverhead;
    }


    /**
     * add the team and the percentage that user chose to distribute
     *
     * @param team               the team that will receive overhead
     * @param overheadPercentage the overhead percentage received by the team
     */
    public void addDistributionPercentageTeam(Team team, String overheadPercentage) {
        this.insertedDistributionPercentageFromTeams.put(team, overheadPercentage);
    }

    public Map<Team, String> getInsertedDistributionPercentageFromTeams() {
        return insertedDistributionPercentageFromTeams;
    }


    /**add the  overhead value to distribute,inserted by the user*/
    @Override
    public void setDistributionPercentageTeam(Team selectedTeam,String newValue) {
         this.insertedDistributionPercentageFromTeams.put(selectedTeam,newValue);
    }

    /**
     * remove the team and the inserted overhead percentage from the map
     */
    public void removeDistributionPercentageTeam(Team team) {
        this.insertedDistributionPercentageFromTeams.remove(team.getId());
    }


    /**
     * set the selected team that user chose to distribute from and the associated  value
     *
     * @param selectedTeamToDistributeFrom selected team and associated percentage
     */

    public void setDistributeFromTeam(Team selectedTeamToDistributeFrom) {
        this.selectedTeamToDistributeFrom = selectedTeamToDistributeFrom;
    }

    public Team getDistributeFromTeam() {
        return this.selectedTeamToDistributeFrom;
    }

    @Override
    public Map <Team,String> validateInputs() {
        return teamManager.validateDistributionInputs(insertedDistributionPercentageFromTeams);
    }


}
