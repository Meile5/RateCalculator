package easv.bll;

import easv.be.Employee;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.SQLException;

public class EmployeeLogic {
    IEmployeeDAO employeeDAO = new EmployeesDAO();
    RateCalculator rateCalculator = new RateCalculator();

    public EmployeeLogic() throws RateException {
    }

    public ObservableMap<Integer, Employee> returnEmployees() throws SQLException {
        ObservableMap<Integer, Employee> employees = employeeDAO.returnEmployees();
        employees.forEach((id, employee) -> {
            BigDecimal dayRate = rateCalculator.calculateDayRate(employee);
            BigDecimal hourRate = rateCalculator.calculateHourlyRate(employee);
            employee.setDailyRate(dayRate);
            employee.setHourlyRate(hourRate);
        });
        return employees;
    }







}
