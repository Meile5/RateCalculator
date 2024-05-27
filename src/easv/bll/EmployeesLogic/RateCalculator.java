package easv.bll.EmployeesLogic;

import easv.be.*;

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

    //TODO modify to take the employee working hours
    public BigDecimal calculateEmployeeTotalDayRate(Employee employee) {
        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();
        BigDecimal utilizationPercentage = employee.getActiveConfiguration().getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal dayRate = BigDecimal.ZERO;

        if (employee.getEmployeeType() == EmployeeType.Overhead) {
          dayRate = (((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)).multiply(utilizationPercentage))
            //calculate markup multiplier
           // dayRate = calculateEmployeeOverheadMarkupMultiplier(employee.getActiveConfiguration().getMarkupMultiplier(), dayRate);

            //calculate grossMargin multiplier
           // dayRate = calculateEmployeeOverheadWithGrossMargin(employee.getActiveConfiguration().getGrossMargin(), dayRate)
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(HoursInDay));
        } else {
            dayRate = ((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)
                    .multiply(BigDecimal.ONE.subtract(utilizationPercentage)))
            //add markup multiplier
           // dayRate = calculateEmployeeOverheadMarkupMultiplier(employee.getActiveConfiguration().getMarkupMultiplier(), dayRate);
            //calculate grossMargin multiplier
          //  dayRate = calculateEmployeeOverheadWithGrossMargin(employee.getActiveConfiguration().getGrossMargin(), dayRate)
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(HoursInDay));
        }
        return dayRate.setScale(2, RoundingMode.HALF_UP);
    }




    /** calculate employee day rate without the utilization percentage */
    public BigDecimal calculateEmployeeDayRateWithoutUtilization(Employee employee) {
        BigDecimal dayEmployeeConfigHours = employee.getActiveConfiguration().getWorkingHours() != null
                ? BigDecimal.valueOf(employee.getActiveConfiguration().getDayWorkingHours())
                : BigDecimal.valueOf(HoursInDay);

        BigDecimal hourlyRate = calculateEmployeeHourlyRateWithoutUtilization(employee);
        BigDecimal dayRate = hourlyRate.multiply(dayEmployeeConfigHours);

        return dayRate.setScale(2, RoundingMode.HALF_UP);
    }

    /** calculate employee hour  rate without the utilization percentage  */
    public BigDecimal calculateEmployeeHourlyRateWithoutUtilization(Employee employee) {
        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier()
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();

        BigDecimal hourlyRate = BigDecimal.ZERO;

        if (employee.getEmployeeType() == EmployeeType.Overhead) {
            hourlyRate = (annualSalary.multiply(overheadMultiplier).add(fixedAnnualAmount))
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP);
    }
        else {
            hourlyRate = annualSalary
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP);
        }
        return hourlyRate.setScale(2, RoundingMode.HALF_UP);
    }




    public BigDecimal calculateHourlyRate(Employee employee) {

        BigDecimal hourlyRate = calculateEmployeeTotalDayRate(employee)
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
    public BigDecimal calculateEmployeeTotalHourlyRate(Employee employee, double configurableHours) {
        BigDecimal hourlyRate = BigDecimal.ZERO;
        if (configurableHours == 0) {
            hourlyRate = calculateEmployeeTotalDayRate(employee)
                    .divide(BigDecimal.valueOf(HoursInDay), RoundingMode.HALF_UP);
        } else {
            hourlyRate = calculateEmployeeTotalDayRate(employee)
                    .divide(BigDecimal.valueOf(configurableHours), RoundingMode.HALF_UP);
        }
        return hourlyRate.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployeeDayRateOnTeam(Employee employee, Team team){
        BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeam(employee, team);
        return hourlyRate.multiply(BigDecimal.valueOf(HoursInDay)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployeeHourlyRateOnTeam(Employee employee, Team team){
        BigDecimal hourlyRate = employee.getActiveConfiguration().getHourlyRate();
        BigDecimal utilizationPercentage = employee.getUtilPerTeams().get(team.getId()).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        System.out.println("utilizationPercentage: " + utilizationPercentage);
        return (hourlyRate.multiply(utilizationPercentage)).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal calculateEmployeeHourlyRateOnTeamE(Employee employee, Team team){
        BigDecimal hourlyRate = employee.getActiveConfiguration().getHourlyRate();
        BigDecimal utilizationPercentage = employee.getUtilPerTeams().get(team.getId()).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        return (hourlyRate.multiply(utilizationPercentage)).setScale(2, RoundingMode.HALF_UP);

    }
    public BigDecimal calculateEmployeeDayRateOnTeamE(Employee employee, Team team){
        BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeamE(employee, team);

        return hourlyRate.multiply(BigDecimal.valueOf(HoursInDay)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTeamDailyRate(Team team) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getActiveConfiguration().getMarkupMultiplier();
            grossMargin = team.getActiveConfiguration().getGrossMargin();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal dayRate = calculateEmployeeDayRateOnTeam(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = dayRate.multiply(BigDecimal.valueOf(markupMultiplier/100));
                totalDayRate = totalDayRate.add(markedUpHourlyRate);
            } else {
                totalDayRate = totalDayRate.add(dayRate);
            }
        }
        if(grossMargin > 0){
            BigDecimal hourlyRateWithMargin = totalDayRate.divide(BigDecimal.valueOf(grossMargin/100), 2, RoundingMode.HALF_UP);
            return hourlyRateWithMargin;
        }
        return totalDayRate;
    }


    public BigDecimal calculateTeamHourlyRate(Team team) {
        BigDecimal totalHourlyRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getActiveConfiguration().getMarkupMultiplier();
            grossMargin = team.getActiveConfiguration().getGrossMargin();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeam(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = hourlyRate.multiply(BigDecimal.valueOf(markupMultiplier/100));
                totalHourlyRate = totalHourlyRate.add(markedUpHourlyRate);
            } else {
                totalHourlyRate = totalHourlyRate.add(hourlyRate);
            }
        }
        if(grossMargin > 0){
            BigDecimal hourlyRateWithMargin = totalHourlyRate.divide(BigDecimal.valueOf(grossMargin/100), 2, RoundingMode.HALF_UP);
            return hourlyRateWithMargin;
        }
        return totalHourlyRate;
    }
    public BigDecimal calculateTeamDailyRateE(Team team) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getMarkupMultiplierTemporary();
            grossMargin = team.getGrossMarginTemporary();
        }
        for (Employee employee : team.getEmployees()) {
            System.out.println(employee + "in calculator");
            BigDecimal dayRate = calculateEmployeeDayRateOnTeamE(employee, team);
            if (markupMultiplier > 0) {
               /* 1 represents the additional cost added to the base rate*/
                BigDecimal markedUpHourlyRate = dayRate.multiply(BigDecimal.valueOf(1 + markupMultiplier/100));
                totalDayRate = totalDayRate.add(markedUpHourlyRate);
            } else {
                totalDayRate = totalDayRate.add(dayRate);
            }
        }
        if(grossMargin > 0){
            BigDecimal hourlyRateWithMargin = totalDayRate.divide(BigDecimal.valueOf(1 - grossMargin/100), 2, RoundingMode.HALF_UP);
            return hourlyRateWithMargin;
        }
        return totalDayRate;
    }

    public BigDecimal calculateTeamHourlyRateE(Team team) {
        BigDecimal totalHourlyRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getMarkupMultiplierTemporary();
            grossMargin = team.getGrossMarginTemporary();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeamE(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = hourlyRate.multiply(BigDecimal.valueOf(1 + markupMultiplier/100));
                totalHourlyRate = totalHourlyRate.add(markedUpHourlyRate);
            } else {
                totalHourlyRate = totalHourlyRate.add(hourlyRate);
            }
        }
        if(grossMargin > 0){
            BigDecimal hourlyRateWithMargin = totalHourlyRate.divide(BigDecimal.valueOf(1 - grossMargin/100), 2, RoundingMode.HALF_UP);
            return hourlyRateWithMargin;
        }
        return totalHourlyRate;
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
      //  totalProductiveOverhead = calculateEmployeeOverheadMarkupMultiplier(employeeConfig.getMarkupMultiplier(), totalProductiveOverhead);

//calculate grossMargin multiplier
      //  totalProductiveOverhead = calculateEmployeeOverheadWithGrossMargin(employeeConfig.getGrossMargin(), totalProductiveOverhead);

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
     //   totalProductiveOverhead = calculateEmployeeOverheadMarkupMultiplier(employeeConfig.getMarkupMultiplier(), totalProductiveOverhead);

//calculate grossMargin multiplier
     //   totalProductiveOverhead = calculateEmployeeOverheadWithGrossMargin(employeeConfig.getGrossMargin(), totalProductiveOverhead);

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
