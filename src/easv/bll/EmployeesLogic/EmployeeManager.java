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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmployeeManager implements IEmployeeManager {
    private IEmployeeDAO employeeDAO;
    RateCalculator rateCalculator = new RateCalculator();

    public EmployeeManager() throws RateException {
        this.employeeDAO = new EmployeesDAO();
    }


    @Override
    public Employee addEmployee(Employee employee, ObservableMap<String, Country> countries, ObservableMap<Integer, Team> teams, Configuration configuration) {
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
        return teams.values().stream().noneMatch(t -> t.getTeam().equals(team.getTeam()));
    }

    @Override
    public Map<Integer, Employee> returnEmployees() throws RateException {
        LinkedHashMap<Integer, Employee> employees = employeeDAO.returnEmployees();
        employees.values().forEach(( employee) -> {
            //if(employee.getAnnualSalary() != null) {
                // Find the latest configuration for the employee
                Configuration latestConfiguration = findLatestConfiguration(employee);
                // Set the latest configuration to the employee
                employee.setLatestConfiguration(latestConfiguration);
                // Calculate rates based on the latest configuration
            BigDecimal dayRate = rateCalculator.calculateDayRate(employee, latestConfiguration);
                BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee, latestConfiguration);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);


        });
        return employees;
    }


    private Configuration findLatestConfiguration(Employee employee) {
        List<Configuration> configurations = employee.getConfigurations();
        if (configurations != null && !configurations.isEmpty()) {
            // Sort configurations by date in descending order to get the latest one first
            configurations.sort(Comparator.comparing(Configuration::getSavedDate).reversed());
            return configurations.get(0); // Return the first (latest) configuration
        }
        return null;
    }

    public Boolean deleteEmployee(Employee employee) throws RateException {
        return employeeDAO.deleteEmployee(employee);
    }

}
