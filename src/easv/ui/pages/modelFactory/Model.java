package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.Utility.EmployeeValidation;
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

import java.util.*;

public class Model implements IModel {
    /**
     * the variable that decide how many elements to skip in the database, when retrieving teams
     */
    private int OFFSET = 3;
    /**
     * the variable that decide how many elements to retrieve from the database, when retrieving teams
     */
    private int ELEMENTS_NUMBER = 3;
    /**
     * computed variable that holds  the current index, from where to start to retrieve teams form the database
     * used for pagination
     */
    private int currentIndexToRetrieve;

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

    private ObservableList<Employee> displayedEmployees;
    private ObservableList<Employee> sortedEmployeesByName;
    private ObservableList<Employee> filteredEmployeesList;
    private ObservableMap<Integer, Employee> filteredEmployeesMap;


    public Model() throws RateException {
        this.employees = FXCollections.observableMap(new LinkedHashMap<>());
        this.filteredEmployeesMap = FXCollections.observableMap(new LinkedHashMap<>());
        this.filteredEmployeesList = FXCollections.observableArrayList();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.validMapViewCountryNameValues = new ArrayList<>();
        this.teams = FXCollections.observableHashMap();
        this.countryTeams = new ArrayList<>();
        this.displayedEmployees = FXCollections.observableArrayList();
        this.sortedEmployeesByName = FXCollections.observableArrayList();
        populateCountries();
        populateTeams();
        EmployeeValidation.getCountries(validMapViewCountryNameValues);
        EmployeeValidation.getTeams(teams);
    }

    public void setDisplayer(DisplayEmployees displayEmployees) {
        this.displayEmployees = displayEmployees;
    }


    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());

    }




    @Override

    public void returnEmployees() throws RateException {
        this.employees.putAll(employeeManager.returnEmployees());
        sortDisplayedEmployee();
        this.displayedEmployees = sortedEmployeesByName;
        System.out.println(employees + "all employees");
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

                try {
                    displayEmployees.displayEmployees();
                } catch (RateException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public void addEmployee(Employee employee, Configuration configuration) throws RateException {
        employee = employeeManager.addEmployee(employee, countries, teams, configuration);
        if (employee != null) {
            employees.put(employee.getId(), employee);
        }
    }

    /**
     * return the operational countries
     */
    public Map<String, Country> getCountries() {
        return countries;
    }


    /**
     * retrieve the countries as an observable list
     */
    public ObservableList<Country> getCountiesValues() {
        ObservableList<Country> coutriesList = FXCollections.observableArrayList();
        coutriesList.setAll(countries.values());
        return coutriesList;
    }

    @Override
    public boolean updateEditedEmployee(Employee originalEmployee, Employee editedEmployee) throws RateException {
        Employee editedEmployeeSaved = employeeManager.saveEditOperation(editedEmployee, originalEmployee.getActiveConfiguration().getConfigurationId());
        if (editedEmployeeSaved != null) {
            editedEmployeeSaved.addConfiguration(editedEmployeeSaved.getActiveConfiguration());
            this.employees.put(editedEmployee.getId(), editedEmployeeSaved);
            return true;
        }
        // if database failed display the error message , and do not close the window;
        return false;
    }

    /**
     * check if edit operation was performed
     */
    public boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee) {
        return employeeManager.isEmployeeEdited(originalEmployee, editedEmployee);
    }

    public synchronized List<TeamWithEmployees> getCountryTeams() {
        Country selectedCountry = countries.get(this.selectedCountry);
        List<TeamWithEmployees> countryTeams = teamManager.getTeamsOverheadByCountry(selectedCountry, currentIndexToRetrieve, ELEMENTS_NUMBER);
        this.countryTeams.addAll(countryTeams);
        currentIndexToRetrieve += OFFSET;
        return this.countryTeams;
    }


    /**
     * reset the currentIndexToRetrieve when retrieving for a new country
     */
    public void resetCurrentIndexToRetrieve() {
        this.currentIndexToRetrieve = 0;
        countryTeams.clear();
    }

    public void populateValidCountries(List<String> validCountries) {
        this.validMapViewCountryNameValues.addAll(validCountries);
    }

    public List<String> getValidCountries() {
        return validMapViewCountryNameValues;
    }


    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

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

    public void performSelectUserSearchOperation (Employee employee) throws RateException {
        displayedEmployees.setAll(employee);
        displayEmployees.displayEmployees();

    }
    public void performEmployeeSearchUndoOperation() throws RateException {
        sortDisplayedEmployee();
        displayedEmployees = sortedEmployeesByName;
        displayEmployees.displayEmployees();
    }

    @Override
    public void filterByCountry(Country country) throws RateException {
        filteredEmployeesList.setAll(employeeManager.filterByCountry(employees.values(), country));
        filteredEmployeesList.stream().forEach(employee -> filteredEmployeesMap.put(employee.getId(), employee));
        displayedEmployees.setAll(filteredEmployeesList);
        displayEmployees.displayEmployees();
    }

    @Override
    public void filterByTeam(Team team) throws RateException {
        filteredEmployeesList.setAll(employeeManager.filterByTeam(employees.values(), team));
        filteredEmployeesList.stream().forEach(employee -> filteredEmployeesMap.put(employee.getId(), employee));
        displayedEmployees.setAll(filteredEmployeesList);
        displayEmployees.displayEmployees();
    }

    @Override
    public void filterByCountryAndTeam(Country selectedCountry ,Team selectedTeam) throws RateException {
        filteredEmployeesList.setAll(employeeManager.filterByCountryAndTeam(employees.values(), selectedCountry, selectedTeam));
        filteredEmployeesList.stream().forEach(employee -> filteredEmployeesMap.put(employee.getId(), employee));
        displayedEmployees.setAll(filteredEmployeesList);
        displayEmployees.displayEmployees();
    }

    @Override
    public ObservableList<Team> getTeamsForCountry(Country country) {
        ObservableList<Team> teamsForCountry = FXCollections.observableArrayList();
        for (Employee employee : employees.values()) {
            if (employee.getCountry().equals(country) && !teamsForCountry.contains(employee.getTeam())) {
                teamsForCountry.add(employee.getTeam());
            }
        }
        return teamsForCountry;
    }

    public void teamFilterActiveRevert() throws RateException {
        displayedEmployees = filteredEmployeesList;
        displayEmployees.displayEmployees();
    }


    @Override
    public BigDecimal calculateGroupDayRate() {
        return employeeManager.calculateGroupDayRate(displayedEmployees);
    }

    @Override
    public BigDecimal calculateGroupHourlyRate() {
        return employeeManager.calculateGroupHourlyRate(displayedEmployees);
    }

    public ObservableList<Employee> getUsersToDisplay() {
       return displayedEmployees;
   }

   public ObservableList<Employee> getFilteredEmployeesList() {
       return filteredEmployeesList;
   }

   public ObservableMap<Integer, Employee> getFilteredEmployeesMap() {
       return filteredEmployeesMap;
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



}