package easv.ui.pages;

import easv.be.Country;
import easv.be.Employee;
import javafx.collections.ObservableMap;

import java.sql.SQLException;

public interface IModel {

    ObservableMap<Integer, Employee> returnEmployees() throws SQLException;

    void addEmployee(Employee employee);
    ObservableMap<Integer, Country> getCountries();

}
