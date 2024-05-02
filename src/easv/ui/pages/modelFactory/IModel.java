package easv.ui.pages.modelFactory;

import easv.be.Country;
import easv.be.Employee;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public interface IModel {


    LinkedHashMap<Integer, Employee> returnEmployees() throws SQLException, RateException;




    void addEmployee(Employee employee);
    ObservableMap<Integer, Country> getCountries();
}
