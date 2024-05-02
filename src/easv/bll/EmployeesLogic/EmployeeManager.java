package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Employee;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;


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
    public Integer addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
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
