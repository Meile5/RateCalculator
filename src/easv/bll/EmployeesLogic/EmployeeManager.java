package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeManager implements IEmployeeManager {
    private IEmployeeDAO employeeDAO;
    RateCalculator rateCalculator = new RateCalculator();

    public EmployeeManager() throws RateException {
        this.employeeDAO = new EmployeesDAO();
    }


    @Override
    public Employee addEmployee(Employee employee, ObservableMap<String, Country> countries, ObservableMap<Integer, Team> teams, Configuration configuration) throws RateException {
        boolean isNewCountry = checkIfNewCountry(employee.getCountry(), countries);
        boolean isNewTeam = checkIfNewTeam(employee.getTeam(), teams);
        Integer employeeID = employeeDAO.addEmployee(employee, isNewCountry, isNewTeam, configuration);
        if (employeeID != null) {
            employee.setId(employeeID);
        }
        return employee;
    }



    private boolean checkIfNewCountry(Country country, ObservableMap<String, Country> countries) {
        if (country == null) {
            return false;
        }
        return countries.values().stream().noneMatch(t -> t.getCountryName().equals(country.getCountryName()));
    }

    private boolean checkIfNewTeam(Team team, ObservableMap<Integer, Team> teams) {
        if (team == null) {
            return false;
        }
        return teams.values().stream().noneMatch(t -> t.getTeamName().equals(team.getTeamName()));
    }

    @Override
    public Map<Integer, Employee> returnEmployees() throws RateException {
        LinkedHashMap<Integer, Employee> employees = employeeDAO.returnEmployees();
        employees.values().forEach(( employee) -> {
            BigDecimal dayRate = rateCalculator.calculateDayRate(employee);
                BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);
        });
        return employees;
    }



    public Boolean deleteEmployee(Employee employee) throws RateException {
        return employeeDAO.deleteEmployee(employee);
    }

    @Override
    public List<Employee> filterByCountry(Collection<Employee> employees, Country country) {
        return employees.stream().filter((employee) -> {
            Country employeeCountry = employee.getCountry();
            return employeeCountry != null && employeeCountry.getCountryName().equals(country.getCountryName());
        }).toList();
    }

    @Override
    public List<Employee> filterByTeam(Collection<Employee> employees, Team team) {
        return employees.stream().filter((employee) -> {
            Team employeeTeam = employee.getTeam();
            return employeeTeam != null && employeeTeam.getTeamName().equals(team.getTeamName());
        }).toList();
    }

    @Override
    public List<Employee> filterByCountryAndTeam(Collection<Employee> employees, Country country, Team team) {
        return employees.stream()
                .filter(employee -> {
                    Country employeeCountry = employee.getCountry();
                    Team employeeTeam = employee.getTeam();
                    return employeeCountry != null && employeeCountry.equals(country) &&
                            employeeTeam != null && employeeTeam.equals(team);
                })
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateGroupDayRate(Collection<Employee> employees) {
        if (!employees.isEmpty()) {
            return employees.stream()
                    .map(Employee::getDailyRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal calculateGroupHourlyRate(Collection<Employee> employees) {
        if(!employees.isEmpty()){
            return employees.stream()
                    .map(Employee::getHourlyRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public List<Employee> performSearchOperation (Collection<Employee> employees, String filter) {
        String filterToLowerCase = filter.toLowerCase();
        return employees.stream().filter((employee) -> {
            String name = employee.getName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }
    /**sort employees by name  alphabetically */
    public List<Employee> sortedEmployeesByName(Collection<Employee> values){
        return values.stream().sorted(Comparator.comparing(Employee::getName)).collect(Collectors.toList());
    }

    /**check if the editOperation was performed on the employee object
     *@param originalEmployee the original employee object
     *@param editedEmployee the edited employee object*/

    public boolean isEmployeeEdited(Employee originalEmployee,Employee editedEmployee){
        boolean isActiveConfigurationEdited = !originalEmployee.getActiveConfiguration().isEqualTo(editedEmployee.getActiveConfiguration());
        boolean areEmployeeValuesEdited = !originalEmployee.equals(editedEmployee);
        return isActiveConfigurationEdited || areEmployeeValuesEdited;
    }


    /**save the edit operation*/
    public Employee saveEditOperation(Employee editedEmployee,int oldConfigurationId) throws RateException {
        return employeeDAO.saveEditOperation(editedEmployee,oldConfigurationId);
    }


    /**calculate the day rate for an employee*/
    public BigDecimal getDayRate(Employee employee){
        return rateCalculator.calculateDayRate(employee);
    }

    /**calculate the hourly rate for an employee*/
    public BigDecimal getHourlyRate(Employee employee,double configurableHours){
        return rateCalculator.calculateHourlyRate(employee,configurableHours);
    }




}
