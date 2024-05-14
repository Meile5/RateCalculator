package easv.bll.EmployeesLogic;
import easv.be.*;
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
    public Employee addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        Integer employeeID = employeeDAO.addEmployee(employee, configuration, teams);
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
        /*employees.values().forEach(( employee) -> {
            BigDecimal dayRate = rateCalculator.calculateDayRate(employee);
                BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);
        })*/
        return employees;
    }


    public Boolean deleteEmployee(Employee employee) throws RateException {
        return employeeDAO.deleteEmployee(employee);
    }


    @Override
    public List<Employee> filterByCountry(Region region, List<Country> countries, Map<Integer, Employee> employees) {
        List<Team> teams = countries.stream().flatMap(e -> e.getTeams().stream()).toList();
        Map<Integer, Employee> employeesFiltered = new HashMap<>();
        for (Team team : teams) {
            for (Employee employee : team.getEmployees()) {
                if (!employeesFiltered.containsKey(employee.getId())) {
                    setEmployeeRelatedInfo(employees, employee, employeesFiltered);
                }
            }
        }
        return employeesFiltered.values().stream().toList();
    }


    /**
     * set to the filtered employee the regions, countries,teams
     */
    private static void setEmployeeRelatedInfo(Map<Integer, Employee> employees, Employee employee, Map<Integer, Employee> employeesFiltered) {
        employee.setRegions(employees.get(employee.getId()).getRegions());
        employee.setCountries(employees.get(employee.getId()).getCountries());
        employee.setTeams(employees.get(employee.getId()).getTeams());
        employeesFiltered.put(employee.getId(), employee);
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
        /*if (!employees.isEmpty()) {
            return employees.stream()
                    .map(Employee::getDailyRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return BigDecimal.ZERO;
        }*/
        return null;
    }

    @Override
    public BigDecimal calculateGroupHourlyRate(Collection<Employee> employees) {
       /* if(!employees.isEmpty()){
            return employees.stream()
                    .map(Employee::getHourlyRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            return BigDecimal.ZERO;
        }*/
        return null;
    }

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
        return rateCalculator.calculateDayRate(employee);
    }

    /**
     * calculate the hourly rate for an employee
     */
    public BigDecimal getHourlyRate(Employee employee, double configurableHours) {
        return rateCalculator.calculateHourlyRate(employee, configurableHours);
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
    public List<Employee> filterTeamsByCountry(List<Team> countryTeams, ObservableMap<Integer, Employee> employees) {
        List<Employee> employeesFromCountryTeams = new ArrayList<>();
        countryTeams.stream().flatMap(e -> e.getEmployees().stream()).forEach(e -> {
            employeesFromCountryTeams.add(employees.get(e.getId()));
        });
        return employeesFromCountryTeams;
    }


    @Override
    public List<Employee> filterEmployeesByTeam(Team selectedTeam, ObservableMap<Integer, Employee> employees) {
        List<Employee> teamEmployees = new ArrayList<>();
        selectedTeam.getEmployees().forEach(e -> teamEmployees.add(employees.get(e.getId())));
        return teamEmployees;
    }


}
