package easv.bll.EmployeesLogic;

import easv.be.Employee;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;


import java.math.BigDecimal;
import java.util.LinkedHashMap;
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
            if(employee.getAnnualSalary() != null) {
                BigDecimal dayRate = rateCalculator.calculateDayRate(employee);
                BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee);
                employee.setDailyRate(dayRate);
                employee.setHourlyRate(hourRate);

            }
        });

        return employees;
    }
}
