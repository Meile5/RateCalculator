package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Employee;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;

public interface IRateCalculator {


    /**calculate the team salary overhead*/
    BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team);
    /**calculate team total overhead*/
    BigDecimal calculateTeamTotalOverhead(TeamWithEmployees team);
    /**calculate team productive overhead*/
    BigDecimal calculateProductiveOverHead(TeamWithEmployees team);

     /**calculate the day rate for the employee*/
    BigDecimal calculateDayRate(Employee employee);
    /**calculate the hourly rate for an employee*/
    BigDecimal calculateHourlyRate(Employee employee, double configurableHours);
}
