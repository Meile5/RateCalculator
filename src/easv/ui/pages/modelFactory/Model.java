package easv.ui.pages.modelFactory;
import easv.be.*;
import easv.bll.EmployeesLogic.EmployeeManager;
import easv.bll.EmployeesLogic.IEmployeeManager;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Model implements IModel {
    private LinkedHashMap<Integer, Employee> employees;


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
    private Map<TeamWithEmployees, List<BigDecimal>> countryTeams;

    public Model() throws RateException {
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
        employees.putAll (employeeManager.returnEmployees());
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