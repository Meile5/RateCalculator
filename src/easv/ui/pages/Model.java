package easv.ui.pages;
import easv.be.*;
import easv.bll.EmployeeLogic;
import easv.bll.EmployeeManager;
import easv.bll.IEmployeeManager;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;



import java.math.BigDecimal;
import java.sql.SQLException;

public class Model implements IModel {
    private ObservableMap<Integer, Employee> employees;
    private EmployeeLogic employeeLogic;

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

        this.employees = new LinkedHashMap<>();

        this.employees = new LinkedHashMap<>();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        populateCountries();
    }


    private void populateCountries() throws RateException {
        this.countries.putAll(countryLogic.getCountries());
    }

    @Override
    public LinkedHashMap<Integer, Employee> returnEmployees() throws RateException {
        employees = employeeManager.returnEmployees();
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

}