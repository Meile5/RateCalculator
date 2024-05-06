package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class RateCalculator implements IRateCalculator {
    private long HoursInDay = 8;

    public BigDecimal calculateDayRate(Employee employee, Configuration latestConfiguration) {
        BigDecimal annualSalary = latestConfiguration.getAnnualSalary();
        BigDecimal overheadMultiplier = latestConfiguration.getOverheadMultiplier().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = latestConfiguration.getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = latestConfiguration.getWorkingHours();
        BigDecimal utilizationPercentage = latestConfiguration.getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);

        BigDecimal dayRate = ((annualSalary.add(fixedAnnualAmount))
                .multiply(overheadMultiplier)
                .multiply(utilizationPercentage)
                .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(HoursInDay));
        return dayRate;
    }

    public BigDecimal calculateHourlyRate(Employee employee, Configuration latestConfiguration) {

        BigDecimal hourlyRate = calculateDayRate(employee, latestConfiguration)
                .divide(BigDecimal.valueOf(HoursInDay));

        return hourlyRate;
    }


    /**method can be improved for readability and testing
     * this method is calculating the day rate for an employee
     *
     * @param employee the employee to calculate for
     *if no values are present for the employee returns BigDecimal.ZERO*/
    public BigDecimal calculateDayRate(Employee employee) {
        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();
        BigDecimal utilizationPercentage = employee.getActiveConfiguration().getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal dayRate= BigDecimal.ZERO;

        if(employee.getEmployeeType()==EmployeeType.Overhead){
            dayRate = ((annualSalary.add(fixedAnnualAmount))
                    .multiply(overheadMultiplier)
                    .multiply(utilizationPercentage)
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(HoursInDay));
        }else{
            dayRate=        ((annualSalary.add(fixedAnnualAmount))
                    .multiply(overheadMultiplier)
                    .multiply(BigDecimal.ONE.subtract(utilizationPercentage))
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(HoursInDay));
        }

        return dayRate.setScale(2,RoundingMode.HALF_UP);


    }

    /**
     * calculate hourly rate for an employee based on the hours of a configurable day
     *
     * @param employee          the employee to calculate for
     * @param configurableHours the configurable hours  of an working day
     * if no configuration is present for the employee it returns BigDecimal.ZERO
     */
    public BigDecimal calculateHourlyRate(Employee employee, double configurableHours) {

        BigDecimal hourlyRate = BigDecimal.ZERO;
        if (configurableHours == 0) {
            hourlyRate = calculateDayRate(employee)
                    .divide(BigDecimal.valueOf(HoursInDay),RoundingMode.HALF_UP);
        } else {
            hourlyRate = calculateDayRate(employee)
                    .divide(BigDecimal.valueOf(configurableHours),RoundingMode.HALF_UP);
        }

        return hourlyRate.setScale(2,RoundingMode.HALF_UP);

    }

    /**
     * calculate the overhead of the team by summing the employees  salary overhead
     */
    public BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team) {
        return team.getTeamMembers().stream().map(this::calculateEmployeeSalaryOverhead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * calculate the team total overhead by summing the team member total overhead
     */
    public BigDecimal calculateTeamTotalOverhead(TeamWithEmployees team) {
        return team.getTeamMembers().stream().map(this::calculateEmployeeTotalOverHead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * calculate the team (total overhead) productive overhead by  summing the  team members productive overhead
     */
    public BigDecimal calculateProductiveOverHead(TeamWithEmployees team) {
        setEmployeesTotalOverhead(team);
        return team.getTeamMembers().stream().map(Employee::getOverhead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * calculate the salary overhead for an employee by multiplying the annual salary to the overhead multiplier
     */
    private BigDecimal calculateEmployeeSalaryOverhead(Employee employee) {
        Configuration config = employee.getActiveConfiguration();
        BigDecimal overheadMultiplier = BigDecimal.ONE.add(config.getOverheadMultiplier().divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP));
        return new BigDecimal(String.valueOf(config.getAnnualSalary().multiply(overheadMultiplier)));
    }


    /**
     * calculate the  total employee overhead by adding the employee annual fixed  amount to the salary overhead
     */
    private BigDecimal calculateEmployeeTotalOverHead(Employee employee) {
        Configuration configuration = employee.getActiveConfiguration();
        BigDecimal salaryOverhead = calculateEmployeeSalaryOverhead(employee);
        return new BigDecimal(String.valueOf(salaryOverhead.add(configuration.getFixedAnnualAmount())));
    }

    /**
     * calculate the employee (total overhead) productive overhead by dividing the employee total overhead with the utilization percentage
     */
    private BigDecimal calculateEmployeeProductiveOverhead(Employee employee) {
        Configuration employeConfig = employee.getActiveConfiguration();
        BigDecimal totalOverHead = calculateEmployeeTotalOverHead(employee);
        BigDecimal utilizationPercentage = employeConfig.getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        return new BigDecimal(String.valueOf(totalOverHead.multiply(utilizationPercentage)));
    }


    /**
     * calculate total overhead for each employee
     */
    private void setEmployeesTotalOverhead(TeamWithEmployees team) {
        for (Employee teamMember : team.getTeamMembers()) {
            if (teamMember.getEmployeeType() == EmployeeType.Overhead) {
                teamMember.setOverhead(calculateEmployeeProductiveOverhead(teamMember));
            } else {
                teamMember.setOverhead(calculateEmployeeProductiveOverheadResource(teamMember));
            }
        }
    }

    /**
     * Calculate overhead if is a resource
     */
    private BigDecimal calculateEmployeeProductiveOverheadResource(Employee employee) {
        Configuration employeConfig = employee.getActiveConfiguration();
        BigDecimal totalOverHead = calculateEmployeeTotalOverHead(employee);
        BigDecimal utilizationPercentage = employeConfig.getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        return new BigDecimal(String.valueOf(totalOverHead.multiply(BigDecimal.ONE.subtract(utilizationPercentage))));
    }
}
