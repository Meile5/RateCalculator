package easv.ui.pages;

import easv.be.*;
import easv.bll.EmployeeLogic;
import easv.bll.EmployeeManager;
import easv.bll.IEmployeeManager;
import easv.bll.TeamLogic.ITeamLogic;
import easv.bll.TeamLogic.TeamLogic;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private EmployeeLogic employeeLogic;
    // the bussines logic object responsible of team logic
    private ITeamLogic teamManager;

    private IEmployeeManager employeeManager;
    /**
     * the logic layer responsible  of countries management
     */
    private ICountryLogic countryLogic;
    /**
     * holds the countries that are currently operational for the company
     */
    private ObservableMap<Integer, Country> countries;

    // collection that holds all the teams related to a country, with all the associated overhead
    private ObservableMap<TeamWithEmployees, List<BigDecimal>> countryTeams;

    public Model() throws RateException {
        this.employees = FXCollections.observableHashMap();
        employeeLogic = new EmployeeLogic();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.teamManager = new TeamLogic();
        this.countryTeams = FXCollections.observableHashMap();
        populateCountries();
    }


    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());
    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() throws SQLException {
        employees = employeeLogic.returnEmployees();
        return employees;
    }

    @Override
    public void addEmployee(Employee employee) {
        Integer employeeID = employeeManager.addEmployee(employee);
        if (employeeID != null) {
            employee.setId(employeeID);
            employees.put(employee.getId(), employee);
        }
    }


    /**
     * return the operational countries
     */
    public ObservableMap<Integer, Country> getCountries() {
        return countries;
    }


    /**
     * retrieve the teams , with the overhead computed, associated with a country
     * * @param country country to retrieve for
     */

    public Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(Country country) {
        currentIndexToRetrieve+=OFFSET;
        Map<TeamWithEmployees, List<BigDecimal>>  countryTeams = teamManager.getTeamsOverheadByCountry(country,currentIndexToRetrieve,ELEMENTS_NUMBER);
        this.countryTeams.putAll(countryTeams);
        System.out.println(countryTeams);
        System.out.println(currentIndexToRetrieve);
        return countryTeams;
    }

    /**reset the currentIndexToRetrieve when retrieving for a new country */
    public void resetCurrentIndexToRetrieve() {
        this.currentIndexToRetrieve = 0;
    }
}