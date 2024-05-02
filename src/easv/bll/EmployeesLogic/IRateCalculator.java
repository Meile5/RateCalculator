package easv.bll.EmployeesLogic;

import easv.be.Configuration;
import easv.be.Employee;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;

public interface IRateCalculator {
    BigDecimal calculateDayRate(Employee employee, Configuration latestConfiguration);
    BigDecimal calculateHourlyRate(Employee employee, Configuration latestConfiguration);


    /**calculate the team salary overhead*/
    BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team);
    /**calculate team total overhead*/
    BigDecimal calculateTeamTotalOverhead(TeamWithEmployees team);
    /**calculate team productive overhead*/
    BigDecimal calculateProductiveOverHead(TeamWithEmployees team);
}
