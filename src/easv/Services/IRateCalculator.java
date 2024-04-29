package easv.Services;

import easv.be.Employee;
import easv.be.EmployeeType;

import java.math.BigDecimal;

public interface IRateCalculator {
    BigDecimal calculateDayRate(Employee employee);
    BigDecimal calculateHourlyRate(Employee employee);
}
