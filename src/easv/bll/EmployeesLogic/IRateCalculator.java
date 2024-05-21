package easv.bll.EmployeesLogic;

import easv.be.Employee;
import easv.be.Team;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;

public interface IRateCalculator {


    /**calculate the team salary overhead*/
    BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team);
    /**calculate team total overhead*/
    BigDecimal calculateTeamOverheadWithoutPercentage(TeamWithEmployees team);
    /**calculate team productive overhead*/
    BigDecimal calculateProductiveOverHead(TeamWithEmployees team);

     /**calculate the day rate for the employee*/
    BigDecimal calculateEmployeeTotalDayRate(Employee employee);
    /**calculate the hourly rate for an employee*/
    BigDecimal calculateEmployeeTotalHourlyRate(Employee employee, double configurableHours);
    BigDecimal calculateTeamDailyRate(Team team);
    BigDecimal calculateTeamHourlyRate(Team team);
    BigDecimal calculateEmployeeDayRateOnTeam(Employee employee,Team  team);
    BigDecimal calculateEmployeeHourlyRateOnTeam(Employee employee,Team  team);

}
