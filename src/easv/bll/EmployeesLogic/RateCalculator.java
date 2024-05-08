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

    /**
     * method can be improved for readability and testing
     * this method is calculating the day rate for an employee
     *
     * @param employee the employee to calculate for
     *                 if no values are present for the employee returns BigDecimal.ZERO
     */

    public BigDecimal calculateDayRate(Employee employee) {
        //System.out.println(employee + " " + employee.getEmployeeType()+ "  heck employee");
        //System.out.println(employee.getActiveConfiguration().printConfiguration());

        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();
        BigDecimal utilizationPercentage = employee.getActiveConfiguration().getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal dayRate = BigDecimal.ZERO;

if(annualSalary==null){
    System.out.println("anual salary" + "nulled");
}

if(overheadMultiplier == null){
    System.out.println("overhead nulll");
}

if(fixedAnnualAmount ==  null){
    System.out.println("fixed annual ammount ");
}

if(annualEffectiveWorkingHours == null ){
    System.out.println(" anuaEfective ahours");
}

if(utilizationPercentage == null){
    System.out.println("utilization Perentage");
}


        if (employee.getEmployeeType() == EmployeeType.Overhead) {
          dayRate = (((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)).multiply(utilizationPercentage));
            //calculate markup multiplier
            dayRate = calculateEmployeeOverheadMarkupMultiplier(employee.getActiveConfiguration().getMarkupMultiplier(), dayRate);

            //calculate grossMargin multiplier
            dayRate = calculateEmployeeOverheadWithGrossMargin(employee.getActiveConfiguration().getGrossMargin(), dayRate)
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(HoursInDay));
        } else {
            dayRate = ((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)
                    .multiply(BigDecimal.ONE.subtract(utilizationPercentage)));
            //add markup multiplier
            dayRate = calculateEmployeeOverheadMarkupMultiplier(employee.getActiveConfiguration().getMarkupMultiplier(), dayRate);
            //calculate grossMargin multiplier
            dayRate = calculateEmployeeOverheadWithGrossMargin(employee.getActiveConfiguration().getGrossMargin(), dayRate)
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(HoursInDay));
        }
        return dayRate.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateHourlyRate(Employee employee) {

        BigDecimal hourlyRate = calculateDayRate(employee)
                .divide(BigDecimal.valueOf(HoursInDay));

        return hourlyRate;
    }

    /**
     * calculate hourly rate for an employee based on the hours of a configurable day
     *
     * @param employee          the employee to calculate for
     * @param configurableHours the configurable hours  of an working day
     *                          if no configuration is present for the employee it returns BigDecimal.ZERO
     */
    public BigDecimal calculateHourlyRate(Employee employee, double configurableHours) {
        BigDecimal hourlyRate = BigDecimal.ZERO;
        if (configurableHours == 0) {
            hourlyRate = calculateDayRate(employee)
                    .divide(BigDecimal.valueOf(HoursInDay), RoundingMode.HALF_UP);
        } else {
            hourlyRate = calculateDayRate(employee)
                    .divide(BigDecimal.valueOf(configurableHours), RoundingMode.HALF_UP);
        }
        return hourlyRate.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * calculate the overhead of the team by summing the employees  salary overhead
     */
    public BigDecimal calculateTeamSalaryOverhead(TeamWithEmployees team) {
        return team.getTeamMembers().stream().map((e) -> {
            if (e.getEmployeeType() == EmployeeType.Overhead) {
                return calculateEmployeeOverHeadWithoutUtilizationPercentage(e);
            } else {
                return calculateEmployeeProductiveOverheadResource(e);
            }
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    /**
     * calculate the team total overhead by summing the team member total overhead
     */
    public BigDecimal calculateTeamOverheadWithoutPercentage
    (TeamWithEmployees team) {
        return team.getTeamMembers().stream().map(this::calculateEmployeeOverHeadWithoutUtilizationPercentage).reduce(BigDecimal.ZERO, BigDecimal::add);
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
        return config.getAnnualSalary().multiply(overheadMultiplier);
    }


    /**
     * calculate the  employee overhead by adding the employee annual fixed  amount to the salary overhead
     */
    private BigDecimal calculateEmployeeOverHeadWithoutUtilizationPercentage(Employee employee) {
        Configuration configuration = employee.getActiveConfiguration();
        BigDecimal salaryOverhead = calculateEmployeeSalaryOverhead(employee);
        return salaryOverhead.add(configuration.getFixedAnnualAmount());
    }

    /**
     * calculate the employee (total overhead) productive overhead by dividing the employee total overhead with the utilization percentage,
     * if is an overhead
     */
    private BigDecimal calculateEmployeeProductiveOverhead(Employee employee) {
        Configuration employeeConfig = employee.getActiveConfiguration();
        BigDecimal totalOverHead = calculateEmployeeOverHeadWithoutUtilizationPercentage(employee);
        BigDecimal utilizationPercentage = employeeConfig.getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal totalProductiveOverhead = totalOverHead.multiply(utilizationPercentage);
        //calculate markup multiplier
        totalProductiveOverhead = calculateEmployeeOverheadMarkupMultiplier(employeeConfig.getMarkupMultiplier(), totalProductiveOverhead);

//calculate grossMargin multiplier
        totalProductiveOverhead = calculateEmployeeOverheadWithGrossMargin(employeeConfig.getGrossMargin(), totalProductiveOverhead);

        return totalProductiveOverhead;
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
        Configuration employeeConfig = employee.getActiveConfiguration();
        BigDecimal totalOverHead = calculateEmployeeOverHeadWithoutUtilizationPercentage(employee);
        BigDecimal utilizationPercentage = employeeConfig.getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal totalProductiveOverhead = new BigDecimal(String.valueOf(totalOverHead.multiply(BigDecimal.ONE.subtract(utilizationPercentage))));
        //calculate markup multiplier
        totalProductiveOverhead = calculateEmployeeOverheadMarkupMultiplier(employeeConfig.getMarkupMultiplier(), totalProductiveOverhead);

//calculate grossMargin multiplier
        totalProductiveOverhead = calculateEmployeeOverheadWithGrossMargin(employeeConfig.getGrossMargin(), totalProductiveOverhead);

        return totalProductiveOverhead;
    }


    /**
     * calculate the employee Overhead based on the additional markupMultiplier , if is present
     *
     * @param markup        the markup multiplier to be applied for the employee
     * @param totalOverhead the overhead  to apply markup
     */

    public BigDecimal calculateEmployeeOverheadMarkupMultiplier(double markup, BigDecimal totalOverhead) {
        BigDecimal markupMultiplier = BigDecimal.valueOf(1 + (markup / 100));
        return totalOverhead.multiply(markupMultiplier);
    }


    /**
     * calculate the employee overhead based on the additional GrossMargin
     */
    private BigDecimal calculateEmployeeOverheadWithGrossMargin(double grossMarginPercentage, BigDecimal totalOverhead) {
        if (grossMarginPercentage == 0) {
            return totalOverhead;
        }
        BigDecimal marginMultiplier = BigDecimal.valueOf(1 - (grossMarginPercentage / 100));
        return totalOverhead.divide(marginMultiplier, MathContext.DECIMAL32);
    }
}
