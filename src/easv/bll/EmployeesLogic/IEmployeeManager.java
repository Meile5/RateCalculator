package easv.bll.EmployeesLogic;

import easv.be.Employee;
import easv.exception.RateException;


import java.util.LinkedHashMap;
import java.util.Map;

public interface IEmployeeManager {
    Integer addEmployee(Employee employee);
    Map<Integer, Employee> returnEmployees() throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;
}
