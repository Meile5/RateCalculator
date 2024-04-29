package easv.dal;

import easv.be.Employee;
import javafx.collections.ObservableMap;

public interface IEmployeeDAO {
    ObservableMap<Integer, Employee> returnEmployees();
}
