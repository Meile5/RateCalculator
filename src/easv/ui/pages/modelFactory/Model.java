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
import javafx.collections.ObservableMap;
import java.math.BigDecimal;
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
    /**computed variable that holds  the current index, from where to start to retrieve teams form the database
     *  used for pagination*/
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
    private final ObservableMap<Integer, Team> teams;

    // collection that holds all the teams related to a country, with all the associated overhead
    private Map<TeamWithEmployees, List<BigDecimal>> countryTeams;
    private DisplayEmployees displayEmployees;

    public Model() throws RateException {
        this.employees = FXCollections.observableMap(new LinkedHashMap<>());
        this.countries = FXCollections.observableHashMap();
        this.teams = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.countryTeams=new HashMap<>();
        populateCountries();
        populateTeams();
    }

    public void setDisplayer(DisplayEmployees displayEmployees){
        this.displayEmployees=displayEmployees;
    }


    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());
    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() throws RateException {
        employees.putAll (employeeManager.returnEmployees());
        return employees;
    }




    @Override
    public void deleteEmployee(Employee employee) throws RateException {
        boolean succeeded = employeeManager.deleteEmployee(employee);
        if (succeeded) {
            // If the deletion was successful, remove the employee from the observable map
            employees.remove(employee.getId());
            Platform.runLater(()-> {
                try {
                    displayEmployees.displayEmployees();
                } catch (RateException | SQLException e) {
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
    public ObservableMap<String, Country> getCountries() {
        System.out.println(countries);
        return countries;
    }

    public Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(String country) {
        currentIndexToRetrieve+=OFFSET;
        Country countrySeLected;
        countrySeLected = countries.values().stream().filter(e->e.getCountryName().equals(country)).findFirst().get();
        Map<TeamWithEmployees, List<BigDecimal>>  countryTeams = teamManager.getTeamsOverheadByCountry(countrySeLected,currentIndexToRetrieve,ELEMENTS_NUMBER);
        System.out.println(countrySeLected.getId());
        if(countryTeams==null){
            System.out.println(countryTeams + "co" + "");
            return new HashMap<>();
        }
        this.countryTeams.putAll(countryTeams);
        System.out.println(countryTeams);
        System.out.println(currentIndexToRetrieve);
        return countryTeams;
    }

    private void populateTeams() throws RateException {
        this.teams.putAll(teamManager.getTeams());

    }

    public ObservableMap<Integer, Team> getTeams() {
        return teams;
    }

    /**reset the currentIndexToRetrieve when retrieving for a new country */
    public void resetCurrentIndexToRetrieve() {
        this.currentIndexToRetrieve = 0;
    }

}