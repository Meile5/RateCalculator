package easv.bll.EmployeesLogic;
import easv.be.Configuration;
import easv.be.Employee;
import easv.be.TeamWithEmployees;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class RateCalculator implements IRateCalculator {

    public BigDecimal calculateDayRate(Employee employee, Configuration latestConfiguration) {
        BigDecimal annualSalary = latestConfiguration.getAnnualSalary();
        BigDecimal overheadMultiplier = latestConfiguration.getOverheadMultiplier(); // Convert to percentage;
        BigDecimal fixedAnnualAmount = latestConfiguration.getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = latestConfiguration.getWorkingHours();
        BigDecimal utilizationPercentage = latestConfiguration.getUtilizationPercentage(); // Convert to percentage;

        BigDecimal dayRate = annualSalary
                .add(fixedAnnualAmount)
                .multiply(overheadMultiplier)
                .multiply(utilizationPercentage)
                .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP);

        return dayRate;
    }

    public BigDecimal calculateHourlyRate(Employee employee, Configuration latestConfiguration) {
        BigDecimal annualEffectiveWorkingHours = latestConfiguration.getWorkingHours();

        BigDecimal hourlyRate = calculateDayRate(employee, latestConfiguration)
                .divide(annualEffectiveWorkingHours, RoundingMode.HALF_UP);

        return hourlyRate;
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
    //TODO the method needs to be modified to use the procents



    /**calculate the salary overhead for an employee by multiplying the annual salary to the overhead multiplier*/
    private BigDecimal calculateEmployeeSalaryOverhead(Employee employee) {
        Configuration config = employee.getConfigurations().getFirst();
        return new BigDecimal(String.valueOf(config.getAnnualSalary().multiply(config.getOverheadMultiplier())));}



    /**calculate the  total employee overhead by adding the employee annual fixed  amount to the salary overhead*/
    private BigDecimal calculateEmployeeTotalOverHead(Employee employee) {
        Configuration configuration = employee.getConfigurations().getFirst();
        BigDecimal salaryOverhead = calculateEmployeeSalaryOverhead(employee);
        return new BigDecimal(String.valueOf(salaryOverhead.add(configuration.getFixedAnnualAmount())));}




   /** calculate the employee (total overhead) productive overhead by dividing the employee total overhead with the utilization percentage*/
    private BigDecimal calculateEmployeeProductiveOverhead(Employee employee) {
        Configuration employeConfig = employee.getConfigurations().getFirst();
        BigDecimal totalOverHead = calculateEmployeeTotalOverHead(employee);
        return  new BigDecimal(String.valueOf(totalOverHead.multiply(employeConfig.getWorkingHours().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32))));}

    /**calculate total overhead for each employee*/
    private  void setEmployeesTotalOverhead(TeamWithEmployees team){
        for (Employee teamMember : team.getTeamMembers()) {
            teamMember.setOverhead(calculateEmployeeProductiveOverhead(teamMember));
        }
    }





}
