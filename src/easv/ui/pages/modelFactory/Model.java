package easv.ui.pages.modelFactory;
import easv.be.*;
import easv.bll.EmployeesLogic.EmployeeManager;
import easv.bll.EmployeesLogic.IEmployeeManager;
import easv.bll.TeamLogic.ITeamLogic;
import easv.bll.TeamLogic.TeamLogic;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.collections.FXCollections;
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

    private LinkedHashMap<Integer, Employee> employees;


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
    private final Map<String, Country> countries;

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

    public Model() throws RateException {
        this.employees = new LinkedHashMap<>();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.validMapViewCountryNameValues = new ArrayList<>();
        this.countryTeams = new ArrayList<>();
        populateCountries();
    }


    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());
    }


    @Override
    public LinkedHashMap<Integer, Employee> returnEmployees() throws RateException {
        employees.putAll(employeeManager.returnEmployees());
        return employees;
    }

    @Override
    public void deleteEmployee(Employee employee) throws RateException {
        boolean deleted = employeeManager.deleteEmployee(employee);
        if(deleted){
            employees.remove(employee.getId());
        }
    }

    @Override
    public void addEmployee(Employee employee) {
        employee = employeeManager.addEmployee(employee);
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

    public  synchronized List<TeamWithEmployees> getCountryTeams() {
        Country selectedCountry = countries.get(this.selectedCountry);
        System.out.println(selectedCountry+" " +selectedCountry.getId() + " " + selectedCountry.getCountryName());
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
        System.out.println( "currentIndex is changed" + currentIndexToRetrieve);
        countryTeams.clear();
    }

    public void populateValidCountries(List<String> validCountries) {
        this.validMapViewCountryNameValues.addAll(validCountries);
    }


    public void setSelectedCountry(String selectedCountry) {
        System.out.println(selectedCountry + "the country is beeing seted");
        this.selectedCountry = selectedCountry;
        System.out.println(this.selectedCountry + "after");
    }
}