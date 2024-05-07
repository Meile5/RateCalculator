package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IEmployeeManager {
    Employee addEmployee(Employee employee, ObservableMap<String, Country> countries, ObservableMap<Integer, Team> teams, Configuration configuration) throws RateException;
    Map<Integer, Employee> returnEmployees() throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;

    BigDecimal calculateGroupDayRate(Collection<Employee> employees);

    BigDecimal calculateGroupHourlyRate(Collection<Employee> employees);

    public List<Employee> performSearchOperation (Collection<Employee> employees, String filter);

    /**check if an edit operation was performed on the epmloyee object*/
    boolean isEmployeeEdited(Employee originalEmployee,Employee editedEmployee);
    /**save the edit operation*/
    Employee saveEditOperation(Employee editedEmployee,int oldConfigurationId) throws RateException;
    List<Employee> sortedEmployeesByName(Collection<Employee> values);

    /**calculate the day rate for an employee*/
    BigDecimal getDayRate(Employee employee);

    /**calculate the hourly rate for an employee, with the configurable hours*/
    BigDecimal getHourlyRate(Employee employee,double configurableHours);

    List<Employee> filterByCountry(Collection<Employee> employees, Country country);

    List<Employee> filterByTeam(Collection<Employee> employees, Team team);

    List<Employee> filterByCountryAndTeam(Collection<Employee> employees, Country country, Team team);
}
