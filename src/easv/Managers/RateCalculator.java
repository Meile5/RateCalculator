package easv.Managers;

import easv.be.Employee;

import java.math.BigDecimal;

public class RateCalculator implements IRateCalculator {
    @Override
    public BigDecimal calculateDayRate(Employee employee) {
        BigDecimal annualSalary = employee.getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getOverheadMultiplier();
        BigDecimal fixedAnnualAmount = employee.getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getWorkingHours();
        BigDecimal utilizationPercentage = employee.getUtilizationPercentage();

        BigDecimal dayRate = annualSalary
                .add(fixedAnnualAmount)
                .multiply(overheadMultiplier)
                .multiply(utilizationPercentage)
                .divide(annualEffectiveWorkingHours, 2, BigDecimal.ROUND_HALF_UP);

        return dayRate;
    }

    @Override
    public BigDecimal calculateHourlyRate(Employee employee) {
        BigDecimal annualSalary = employee.getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getOverheadMultiplier();
        BigDecimal fixedAnnualAmount = employee.getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getWorkingHours();
        BigDecimal utilizationPercentage = employee.getUtilizationPercentage();

        BigDecimal hourlyRate = calculateDayRate(employee)
                .divide(annualEffectiveWorkingHours, 2, BigDecimal.ROUND_HALF_UP);

        return hourlyRate;
    }

}
