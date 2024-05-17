package easv.bll.EmployeesLogic;

import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IEmployeeManager {
    Employee addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException;
    Map<Integer, Employee> returnEmployees() throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;

    BigDecimal calculateTeamDayRate(Team team);
    BigDecimal calculateTeamHourlyRate(Team team);

    BigDecimal getEmployeeDayRateOnTeam(Employee employee, Team team);

    BigDecimal getEmployeeHourlyRateOnTeam(Employee employee, Team team);

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

    List<Employee> filterByCountry(Region region,List<Country> countries,Map<Integer,Employee> employees);



    /**retrieve the teams in the system*/
    Map<Integer,Team> getTeamWithEmployees() throws RateException;

    /**retrieve countries with the associated teams */
    Map<Integer,Country> getCountriesWithTeams(Map<Integer,Team> teams) throws RateException;

    /**retrieve all regions with countries*/
    Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException;

    Integer addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate) throws SQLException, RateException;

    List<Employee> filterTeamsByCountry(List<Team> countryTeams, ObservableMap<Integer, Employee> employees);

    List<Employee> filterEmployeesByTeam(Team selectedTeam,ObservableMap<Integer,Employee> employees);
}
