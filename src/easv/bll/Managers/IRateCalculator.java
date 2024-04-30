package easv.bll.Managers;

import easv.be.Employee;

import java.math.BigDecimal;

public interface IRateCalculator {
    BigDecimal calculateDayRate(Employee employee);
    BigDecimal calculateHourlyRate(Employee employee);
}
