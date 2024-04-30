package easv.ui.pages;
import easv.be.*;
import easv.bll.Managers.EmployeeManager;
import easv.bll.Managers.IEmployeeManager;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

public class Model implements IModel {
    private ObservableMap<Integer, Employee> employees;

    private IEmployeeManager employeeManager;
    /**the logic layer responsible  of countries management*/
    private ICountryLogic countryLogic;
    /**holds the countries that are currently operational for the company*/
    private ObservableMap <Integer,Country> countries;


    public Model() throws RateException {
        this.employees = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        populateCountries();
        test();
    }


    private void  populateCountries() throws RateException {
        this.countries = countryLogic.getCountries();
    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {
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

    private void test() {
        Employee employee = new Employee("Meile", new BigDecimal(200000), new BigDecimal(10000), new BigDecimal(10), new BigDecimal(20), new BigDecimal(2000), new Country("Denmark"), new Team("IT"), EmployeeType.OVERHEAD, Currency.$);
        employee.setId(20);
        employees.put(employee.getId(), employee);
    }


    /**return the operational countries*/
    public ObservableMap<Integer, Country> getCountries() {
        return countries;
    }
}