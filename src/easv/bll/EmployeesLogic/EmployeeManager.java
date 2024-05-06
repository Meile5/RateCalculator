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
                // Find the latest configuration for the employee
                Configuration latestConfiguration = findActiveConfiguration(employee);
                // Set the latest configuration to the employee
                employee.setActiveConfiguration(latestConfiguration);
                // Calculate rates based on the latest configuration
            BigDecimal dayRate = rateCalculator.calculateDayRate(employee, latestConfiguration);
                BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee, latestConfiguration);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);


        });
        return employees;
    }


    private Configuration findActiveConfiguration(Employee employee) {
        List<Configuration> configurations = employee.getConfigurations();
        if (configurations != null && !configurations.isEmpty()) {
            for (Configuration configuration : configurations) {
                if (configuration.isActive()) {
                    return configuration;
                }
            }
        }
        return null;
    }

    public Boolean deleteEmployee(Employee employee) throws RateException {
        return employeeDAO.deleteEmployee(employee);
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

}
