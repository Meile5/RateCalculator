package easv.dal;

import easv.be.Employee;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.util.LinkedHashMap;
import java.util.Map;

public interface IEmployeeDAO {
    LinkedHashMap<Integer, Employee> returnEmployees() throws RateException;

    Integer addEmployee(Employee employee);
    Boolean deleteEmployee(Employee employee);
}
