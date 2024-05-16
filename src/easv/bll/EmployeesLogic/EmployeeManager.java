package easv.bll.EmployeesLogic;

import easv.be.*;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;
import javafx.collections.ObservableMap;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class EmployeeManager implements IEmployeeManager {
    private IEmployeeDAO employeeDAO;
    RateCalculator rateCalculator = new RateCalculator();

    public EmployeeManager() throws RateException {
        this.employeeDAO = new EmployeesDAO();
    }


    @Override
    public Employee addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        Integer employeeID = employeeDAO.addEmployee(employee, configuration, teams);
        if (employeeID != null) {
            employee.setId(employeeID);
        }
        return employee;
    }

/*
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

 */

    @Override
    public Map<Integer, Employee> returnEmployees() throws RateException {
        LinkedHashMap<Integer, Employee> employees = employeeDAO.returnEmployees();
        /*employees.values().forEach(( employee) -> {
            BigDecimal dayRate = rateCalculator.calculateEmployeeTotalDayRate(employee);
                BigDecimal hourRate = rateCalculator.calculateEmployeeTotalHourlyRate(employee);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);
        })*/
        return employees;
    }


    public Boolean deleteEmployee(Employee employee) throws RateException {
        return employeeDAO.deleteEmployee(employee);
    }


    /**
     * filter the employees by the selected country
     */
    @Override
    public List<Employee> filterByCountry(Region region, List<Country> countries, Map<Integer, Employee> employees) {
        List<Team> teams = countries.stream().flatMap(e -> e.getTeams().stream()).toList();
        Map<Integer, Employee> employeesFiltered = new HashMap<>();
        for (Team team : teams) {
            if (team.getEmployees() != null) {
                for (Employee employee : team.getEmployees()) {
                    if (!employeesFiltered.containsKey(employee.getId())) {
                        setEmployeeRelatedInfo(employees, employee, employeesFiltered);
                    }
                }
            }
        }
        return employeesFiltered.values().stream().toList();
    }


    /**
     * set to the filtered employee the regions, countries,and teams value in order to be displayed
     */
    private static void setEmployeeRelatedInfo(Map<Integer, Employee> employees, Employee employee, Map<Integer, Employee> employeesFiltered) {
        employee.setRegions(employees.get(employee.getId()).getRegions());
        employee.setCountries(employees.get(employee.getId()).getCountries());
        employee.setTeams(employees.get(employee.getId()).getTeams());
        employeesFiltered.put(employee.getId(), employee);
    }

    @Override
    public BigDecimal calculateTeamDayRate(Team team) {
        return rateCalculator.calculateTeamDailyRate(team);
    }

    @Override
    public BigDecimal calculateTeamHourlyRate(Team team) {
        return rateCalculator.calculateTeamHourlyRate(team);
    }

    /*
    private BigDecimal calculateSumTeamDayRate(Team team) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Employee employee : team.getEmployees()) {
            BigDecimal dayRate = rateCalculator.calculateEmployeeDayRateOnTeam(employee, team);
            sum = sum.add(dayRate);
        }
        return sum;
    }

    private BigDecimal calculateSumTeamHourlyRate(Team team) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Employee employee : team.getEmployees()) {
            sum = sum.add(rateCalculator.calculateEmployeeHourlyRateOnTeam(employee, team));
        }
        return sum;
    }

     */

    @Override
    public List<Employee> performSearchOperation(Collection<Employee> employees, String filter) {
        String filterToLowerCase = filter.toLowerCase();
        return employees.stream().filter((employee) -> {
            String name = employee.getName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }

    /**
     * sort employees by name  alphabetically
     */
    public List<Employee> sortedEmployeesByName(Collection<Employee> values) {
        return values.stream().sorted(Comparator.comparing(Employee::getName)).collect(Collectors.toList());
    }

    /**
     * check if the editOperation was performed on the employee object
     *
     * @param originalEmployee the original employee object
     * @param editedEmployee   the edited employee object
     */

    public boolean isEmployeeEdited(Employee originalEmployee, Employee editedEmployee) {
        boolean isActiveConfigurationEdited = !originalEmployee.getActiveConfiguration().isEqualTo(editedEmployee.getActiveConfiguration());
        boolean areEmployeeValuesEdited = !originalEmployee.equals(editedEmployee);
        return isActiveConfigurationEdited || areEmployeeValuesEdited;
    }


    /**
     * save the edit operation
     */
    public Employee saveEditOperation(Employee editedEmployee, int oldConfigurationId) throws RateException {
        return employeeDAO.saveEditOperation(editedEmployee, oldConfigurationId);
    }


    /**
     * calculate the day rate for an employee
     */
    public BigDecimal getDayRate(Employee employee) {
        return rateCalculator.calculateEmployeeTotalDayRate(employee);
    }

    /**
     * calculate the hourly rate for an employee
     */
    public BigDecimal getHourlyRate(Employee employee, double configurableHours) {
        return rateCalculator.calculateEmployeeTotalHourlyRate(employee, configurableHours);
    }

    /**
     * retrieve all the teams with associated employees from the database
     */
    @Override
    public Map<Integer, Team> getTeamWithEmployees() throws RateException {
        return employeeDAO.getTeamsWithEmployees();
    }

    @Override
    public Map<Integer, Country> getCountriesWithTeams(Map<Integer, Team> teams) throws RateException {
        return employeeDAO.getCountriesWithTeams(teams);
    }

    @Override
    public Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException {
        return employeeDAO.getRegionsWithCountries(countriesWithTeams);
    }

    @Override
    public Integer addTeamConfiguration(TeamConfiguration teamConfiguration, Team team) throws SQLException, RateException {
        return employeeDAO.addNewTeamConfiguration(teamConfiguration, team);
    }


    /**
     * filter the employees by the selected country from the filter
     */
    @Override
    public List<Employee> filterTeamsByCountry(List<Team> countryTeams, ObservableMap<Integer, Employee> employees) {
        List<Employee> employeesFromCountryTeams = new ArrayList<>();
        if (countryTeams == null) {
            return employeesFromCountryTeams; // Return an empty list if countryTeams is null
        }

        countryTeams.stream()
                .flatMap(team -> Optional.ofNullable(team.getEmployees()).stream().flatMap(Collection::stream))
                .forEach(employee -> {
                    if (employee != null) {
                        employeesFromCountryTeams.add(employees.get(employee.getId()));
                    }
                });

        return employeesFromCountryTeams;
    }


    /**filter the employees for the selected team from the teams filter*/
    @Override
    public List<Employee> filterEmployeesByTeam(Team selectedTeam, ObservableMap<Integer, Employee> employees) {
        List<Employee> teamEmployees = new ArrayList<>();
        if (selectedTeam != null && selectedTeam.getEmployees() != null) {
            selectedTeam.getEmployees().forEach(e -> {
                Employee emp = employees.get(e.getId());
                if (emp != null) {
                    teamEmployees.add(emp);
                }
            });
        }
        return teamEmployees;
    }


}
