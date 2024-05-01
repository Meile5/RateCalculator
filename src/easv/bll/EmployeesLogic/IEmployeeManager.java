package easv.bll.EmployeesLogic;

import easv.be.Employee;
import easv.exception.RateException;


import java.util.LinkedHashMap;
import java.util.Map;

public interface IEmployeeManager {
    Integer addEmployee(Employee employee);
    LinkedHashMap<Integer, Employee> returnEmployees() throws RateException;
}
