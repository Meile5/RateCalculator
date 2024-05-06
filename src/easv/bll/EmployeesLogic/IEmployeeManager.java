package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IEmployeeManager {
    Employee addEmployee(Employee employee, ObservableMap<String, Country> countries, ObservableMap<Integer, Team> teams, Configuration configuration) throws RateException;
    Map<Integer, Employee> returnEmployees() throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;

    public List<Employee> performSearchOperation (Collection<Employee> employees, String filter);

    /**check if an edit operation was performed on the epmloyee object*/
    boolean isEmployeeEdited(Employee originalEmployee,Employee editedEmployee);
    /**save the edit operation*/
    Employee saveEditOperation(Employee editedEmployee,int oldConfigurationId) throws RateException;
    List<Employee> sortedEmployeesByName(Collection<Employee> values);

}
