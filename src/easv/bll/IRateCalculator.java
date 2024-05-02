package easv.bll;

import easv.be.Employee;
import easv.be.TeamWithEmployees;

import java.math.BigDecimal;

public interface IRateCalculator {
    BigDecimal calculateDayRate(Employee employee);
    BigDecimal calculateHourlyRate(Employee employee);


    /**calculate the team salary overhead*/
    BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team);
    /**calculate team total overhead*/
    BigDecimal calculateTeamTotalOverhead(TeamWithEmployees team);
    /**calculate team productive overhead*/
    BigDecimal calculateProductiveOverHead(TeamWithEmployees team);
}
