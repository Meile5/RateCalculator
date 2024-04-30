package easv.ui.pages;

import easv.be.Employee;
import javafx.collections.ObservableMap;

public interface IModel {
    ObservableMap<Integer, Employee> returnEmployees();

    void addEmployee(Employee employee);
}
