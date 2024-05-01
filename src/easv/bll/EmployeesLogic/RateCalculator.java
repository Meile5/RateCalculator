package easv.bll.EmployeesLogic;

import easv.be.Employee;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;
import java.math.MathContext;

public class RateCalculator {

    public BigDecimal calculateDayRate(Employee employee) {
        BigDecimal annualSalary = employee.getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getOverheadMultiplier();
        BigDecimal fixedAnnualAmount = employee.getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getWorkingHours();
        BigDecimal utilizationPercentage = employee.getUtilizationPercentage();
        BigDecimal dayRate = annualSalary
                .add(fixedAnnualAmount)
                .multiply(overheadMultiplier)
                .multiply(utilizationPercentage).divide(annualEffectiveWorkingHours, 2, BigDecimal.ROUND_HALF_UP);

        return dayRate;
    }


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


    /**
     * calculate the overhead of the team by summing the employees  salary overhead */
    public BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team){
        return team.getTeamMembers().stream().map(this::calculateEmployeeSalaryOverhead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**calculate the team total overhead by summing the team member total overhead*/
    public BigDecimal calculateTeamTotalOverhead(TeamWithEmployees team){
        return team.getTeamMembers().stream().map(this::calculateEmployeeTotalOverHead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**calculate the team productive overhead by  summing the  team members productive overhed */
    public BigDecimal calculateProductiveOverHead(TeamWithEmployees team){
        return team.getTeamMembers().stream().map(this::calculateEmployeeProductiveOverhead).reduce(BigDecimal.ZERO, BigDecimal::add);
    }



    //TODO the method needs to be modified to use the procents
    /**calculate the salary overhead for an employee by multiplying the annual salary to the overhead multiplier */
    private BigDecimal calculateEmployeeSalaryOverhead(Employee employee){
     return new BigDecimal(String.valueOf(employee.getAnnualSalary().multiply(employee.getOverheadMultiplier())));
    }

    /**calculate the  total employee overhead by adding the employee annual fixed  amount to the salary overhead   */
    private BigDecimal calculateEmployeeTotalOverHead(Employee employee){
        BigDecimal salaryOverhead = calculateEmployeeSalaryOverhead(employee);
        return new BigDecimal(String.valueOf(salaryOverhead.add(employee.getFixedAnnualAmount())));
    }


    /**calculate the employee productive overhead by dividing the employee total overhead with the utilization percentage*/
    private BigDecimal calculateEmployeeProductiveOverhead(Employee employee){
        BigDecimal totalOverHead =  calculateEmployeeTotalOverHead(employee);
        return new BigDecimal(String.valueOf(totalOverHead.multiply(employee.getWorkingHours().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32))));
    }


}
