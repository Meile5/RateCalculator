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
import java.sql.SQLException;
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

    private ObservableMap<Integer, Employee> displayedEmployees;


    public Model() throws RateException {
        this.employees = FXCollections.observableMap(new LinkedHashMap<>());
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.validMapViewCountryNameValues = new ArrayList<>();
        this.teams = FXCollections.observableHashMap();
        this.countryTeams = new ArrayList<>();
        this.displayedEmployees = FXCollections.observableMap(new LinkedHashMap<>());
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
        this.displayedEmployees = employees;
    }


    @Override
    public void deleteEmployee(Employee employee) throws RateException {
        boolean succeeded = employeeManager.deleteEmployee(employee);
        if (succeeded) {
            // If the deletion was successful, remove the employee from the observable map
            employees.remove(employee.getId());

            displayedEmployees = employees;


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
      Employee editedEmployeeSaved= employeeManager.saveEditOperation(editedEmployee, originalEmployee.getActiveConfiguration().getConfigurationId());
        System.out.println(originalEmployee.getActiveConfiguration().getConfigurationId()+"from model");
      if(editedEmployeeSaved!=null) {
            editedEmployeeSaved.addConfiguration(editedEmployeeSaved.getActiveConfiguration());
            this.employees.put(editedEmployee.getId(),editedEmployeeSaved);
            return true;
        }
        // if database failed display the error message , and do not close the window;
        return false;
    }

    /**check if edit operation was performed*/
    public boolean isEditOperationPerformed( Employee originalEmployee, Employee editedEmployee){
      return employeeManager.isEmployeeEdited(originalEmployee,editedEmployee);
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
    public void performSelectUserSearchOperation (int employeeId, Employee employee) throws RateException {
        displayedEmployees.clear();
        displayedEmployees.put(employeeId, employee);
        displayEmployees.displayEmployees();
    }


   public ObservableMap<Integer, Employee> getUsersToDisplay() {
       return this.displayedEmployees;
   }


}