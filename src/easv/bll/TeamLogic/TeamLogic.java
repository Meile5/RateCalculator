package easv.bll.TeamLogic;


import easv.be.*;
import easv.bll.EmployeesLogic.IRateCalculator;
import easv.bll.EmployeesLogic.RateCalculator;
import easv.dal.teamDao.ITeamDao;
import easv.dal.teamDao.TeamDao;
import easv.exception.RateException;


import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO do not delete this class   Andrei found a usage for
public class TeamLogic implements ITeamLogic {
    private final ITeamDao teamDao;
    private final IRateCalculator rateCalculator;

    public TeamLogic() throws RateException {
        this.teamDao = new TeamDao();
        this.rateCalculator = new RateCalculator();
    }


    /**
     * compute the overhead for a team*
     *
     * @param team the team to calculate for
     *             returns a List that contains salaryOverhead,totalOverhead,productiveOverhead
     */
    private Map<TeamWithEmployees.TeamOverheadType, BigDecimal> calculateTeamOverhead(TeamWithEmployees team) {
        Map<TeamWithEmployees.TeamOverheadType, BigDecimal> teamOverhead = new HashMap<>();
        BigDecimal salaryOverhead = rateCalculator.calculateTeamSalaryOverhead(team);
        BigDecimal expensesOverhead = rateCalculator.calculateTeamOverheadWithoutPercentage(team);
        BigDecimal productiveOverhead = rateCalculator.calculateProductiveOverHead(team);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.SALARY_OVERHEAD, salaryOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.EXPENSES_OVERHEAD, expensesOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD, productiveOverhead);
        return teamOverhead;
    }

    private List<Map<String, Double>> calculateTeamPercentage(TeamWithEmployees team) {
        List<Map<String, Double>> teamPercentagePerEmployee = team.getTeamMembers().stream().map(e -> employeePercentage(e, team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD))).toList();
        return teamPercentagePerEmployee;
    }


    private Map<String, Double> employeePercentage(Employee employee, BigDecimal totalOverhead) {
        Map<String, Double> emplPercentage = new HashMap<>();
        BigDecimal employeeOverhead = employee.getOverhead();
        if (employeeOverhead.compareTo(BigDecimal.ZERO) == 0) {
            emplPercentage.put(employee.getName(), 0.0);
            return emplPercentage;
        }
        double percentage = employeeOverhead.divide(totalOverhead, MathContext.DECIMAL32).doubleValue() * 100;
        String formattedPercentage = String.format("%.2f", percentage);
        emplPercentage.put(employee.getName() + " " + formattedPercentage, percentage);
        return emplPercentage;
    }

    public Map<Integer, Team> getTeams() throws RateException {
        return teamDao.getTeams();
    }


    /**calculate the overhead off a region , if is zero return an  OverHeadComputationPair object
     * populated with the country name and the value zero
     * @param region the region to calculate for.*/

    @Override
    public OverheadComputationPair<String, BigDecimal> computeRegionOverhead(Region region) {
        BigDecimal regionOverhead = BigDecimal.ZERO;
        if (region.getCountries() != null) {
            for (Country country : region.getCountries()) {
                for (Team team : country.getTeams()) {
                    if (team.getActiveConfiguration() != null) {
                        regionOverhead = regionOverhead.add(team.getActiveConfiguration().getTeamDayRate());
                    }
                }
            }
            return new OverheadComputationPair<>(region.getRegionName(), regionOverhead);
        }
        return new OverheadComputationPair<>(region.getRegionName(),BigDecimal.ZERO);
    }
}
