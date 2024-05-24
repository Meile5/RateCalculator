package easv.bll.EmployeesLogic;

import easv.be.*;
import easv.dal.employeeDao.EmployeesDAO;
import easv.dal.employeeDao.IEmployeeDAO;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


public class EmployeeManager implements IEmployeeManager {
    private IEmployeeDAO employeeDAO;
    IRateCalculator rateCalculator = new RateCalculator();

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
     * filter teams by region
     */
    @Override
    public List<Team> filterTeamsByRegion(Region region, List<Country> countries) {
        return countries.stream().flatMap((e) -> e.getTeams().stream()).toList();
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

    @Override
    public BigDecimal getEmployeeDayRateOnTeam(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeDayRateOnTeam(employee, team);
    }

    @Override
    public BigDecimal getEmployeeHourlyRateOnTeam(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeHourlyRateOnTeam(employee, team);
    }

    public BigDecimal getEmployeeHourlyRateOnTeamE(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeHourlyRateOnTeamE(employee, team);
    }

    public BigDecimal getEmployeeDayRateOnTeamE(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeDayRateOnTeamE(employee, team);
    }

    public BigDecimal calculateTeamHourlyRateE(Team team) {
        return rateCalculator.calculateTeamHourlyRateE(team);
    }

    public BigDecimal calculateTeamDayRateE(Team team) {
        return rateCalculator.calculateTeamDailyRateE(team);
    }

    public Team saveTeamEditOperation(Team editedTeam, int idOriginalTeam, List<Employee> employeesToDelete, List<Employee> employees) throws RateException {
        return employeeDAO.saveEditOperationTeam(editedTeam, idOriginalTeam, employeesToDelete, employees);

    }


    /**
     * calculate the total overhead of the teams
     */
    public BigDecimal calculateGroupTotalDayRate(List<Team> filteredTeams) {
        BigDecimal teamsDayRateSum = BigDecimal.ZERO;
        System.out.println(filteredTeams.size());

        for (Team teams : filteredTeams) {
            TeamConfiguration teamConfiguration = teams.getActiveConfiguration();
            System.out.println(teamConfiguration+"config");
            if (teamConfiguration != null) {
                BigDecimal teamDayRate = teamConfiguration.getTeamDayRate();
                if (teamDayRate != null) {
                    teamsDayRateSum = teamsDayRateSum.add(teamDayRate);
                }
            }
        }
        System.out.println(teamsDayRateSum+"day rate sum");
        return teamsDayRateSum;
    }


    /**
     * calculate the teams total  hourly rate
     */
    public BigDecimal calculateGroupTotalHourRate(List<Team> filteredTeams) {
        BigDecimal teamsHourRateSum = BigDecimal.ZERO;
        for (Team teams : filteredTeams) {
            TeamConfiguration teamConfiguration = teams.getActiveConfiguration();
            if (teamConfiguration != null) {
                BigDecimal teamHourlyRate = teamConfiguration.getTeamHourlyRate();
                if (teamHourlyRate != null) {
                    teamsHourRateSum = teamsHourRateSum.add(teamHourlyRate);
                }
            }
        }
        return teamsHourRateSum;
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
        System.out.println(isActiveConfigurationEdited + " configuration");
        boolean areEmployeeValuesEdited = !originalEmployee.equals(editedEmployee);
        System.out.println(areEmployeeValuesEdited + " employee");

        return isActiveConfigurationEdited || areEmployeeValuesEdited;
    }


    ///TODO if the employee has no teams, just update the employee values

    /**
     * save the edit operation
     */
    public Employee saveEditOperation(Employee editedEmployee, Employee originalEmployee, List<Team> employeeTeams) throws RateException {
        List<Team> employeeTeamsNewConfigurations = new ArrayList<>();
        List<Team> validTeams = new ArrayList<>();
        //get the employee utilization per teams
        try {
            Map<Integer, BigDecimal> employeeTeamUtilization = new HashMap<>(employeeDAO.getEmployeeUtilizationPerTeams(editedEmployee.getId()));
            editedEmployee.setUtilPerTeams(employeeTeamUtilization);
        } catch (RateException e) {
            return null;
        }


        //remove the print statement
//        for (Team team : employeeTeams) {
//            for (Employee emplo : team.getTeamMembers()) {
//                System.out.println(emplo.getName() + "emplooverhead" + emplo.getName());
//            }
//        }

    //     Print old values , remove the print statatement
//        for (Team team : employeeTeams) {
//            System.out.println(team.getActiveConfiguration().getTeamHourlyRate() + team.getTeamName() + " old value hour");
//            System.out.println(team.getActiveConfiguration().getTeamDayRate() + team.getTeamName() + " old day rate");
//        }

        // Create copies of the original employee teams
        for (Team team : employeeTeams) {
            Team teamToEdit = new Team(team);
            teamToEdit.setActiveConfiguration(team.getActiveConfiguration());
            employeeTeamsNewConfigurations.add(teamToEdit);
        }


        //calculate employee new  day rate and hourly rate
        BigDecimal dayRateEmployee = rateCalculator.calculateEmployeeDayRateWithoutUtilization(editedEmployee);
        BigDecimal employeeHourRate = rateCalculator.calculateEmployeeHourlyRateWithoutUtilization(editedEmployee);

        System.out.println(dayRateEmployee + "dayRate");
        System.out.println(employeeHourRate + "hour rate");

        editedEmployee.getActiveConfiguration().setDayRate(dayRateEmployee);
        editedEmployee.getActiveConfiguration().setHourlyRate(employeeHourRate);

        // Replace the originalEmployee with the edited one in the copies to calculate the new team rates
        for (Team team : employeeTeamsNewConfigurations) {
            for (int i = 0; i < team.getTeamMembers().size(); i++) {
                if (team.getTeamMembers().get(i).getId() == editedEmployee.getId()) {
                    team.getTeamMembers().set(i, editedEmployee);
                    break;
                }
            }
        }

        // Calculate the new team day rates
        for (Team team : employeeTeamsNewConfigurations) {
            if(team.getActiveConfiguration()!=null){
                computeTeamNewDayRate(team, editedEmployee, dayRateEmployee, employeeHourRate);
                validTeams.add(team);
            }
            //  team.getActiveConfiguration().setTeamDayRate(dayRate);
            // BigDecimal hourRate = calculateTeamHourlyRate(team);
            //   team.getActiveConfiguration().setTeamHourlyRate(hourRate);
            System.out.println("alooo");
            //  System.out.println(dayRate.toString() + "new day Rate");
            //  System.out.println(hourRate.toString() + "new hour rate");
            //set the teams new day rate
        }

        System.out.println("io amwweuhflubwefef");
        // Print new values
        for (Team team : employeeTeamsNewConfigurations) {
            for (Employee emplo : team.getTeamMembers()) {
                System.out.println(emplo.getName() + "emplooverhead" + emplo.getName() + emplo.getActiveConfiguration().getDayRate() + "day rate");
            }
        }
        editedEmployee.setTeams(validTeams);
        editedEmployee.getTeams().forEach((e)->{
            System.out.println(e.getActiveConfiguration().isActive());});
       // return editedEmployee;
        return  employeeDAO.saveEditOperation(editedEmployee, originalEmployee.getActiveConfiguration().getConfigurationId());
    }


    /**
     * calculate the team new  day rate and hourly rate based on the new edited employee values
     */
    private void computeTeamNewDayRate(Team team, Employee employee, BigDecimal newEmployeeDayRate, BigDecimal newEmployeeHourRate) {

        //extract the employee utilization per team
        BigDecimal employeeUtilizationOnTeam = employee.getUtilPerTeams().get(team.getId());
        //extract the employee old rates from the team
        BigDecimal removeOldEmployeeDayRate = team.getActiveConfiguration().getTeamDayRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        BigDecimal removeEmployeeHourRate = team.getActiveConfiguration().getTeamHourlyRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        //calculate  employee new overhead on the team
        BigDecimal teamEmployeeNewDayRate = removeOldEmployeeDayRate.add(newEmployeeDayRate.multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))));
        BigDecimal teamEmployeeNewHourRate = removeEmployeeHourRate.add(newEmployeeHourRate.multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))));

        //add employee new rates on the team
        team.getActiveConfiguration().setTeamDayRate(teamEmployeeNewDayRate);
        team.getActiveConfiguration().setTeamHourlyRate(teamEmployeeNewHourRate);

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
    public Integer addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate) throws SQLException, RateException {
        return employeeDAO.addNewTeamConfiguration(teamConfiguration, team, employeeDayRate, employeeHourlyRate);
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


    /**
     * filter the employees for the selected team from the teams filter
     */
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
