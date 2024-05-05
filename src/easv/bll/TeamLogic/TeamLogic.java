package easv.bll.TeamLogic;


import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.be.TeamWithEmployees;
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

public class TeamLogic implements ITeamLogic {
    private final ITeamDao teamDao;
    private final IRateCalculator rateCalculator;

    public TeamLogic() throws RateException {
        this.teamDao = new TeamDao();
        this.rateCalculator = new RateCalculator();
    }

    /**

     retrieve the teams and related team overhead for a specific country
     @param country country to retrieve for
     @param offset the index from where to retrieve
     @param numberOfElements how manny elements to retrieve
     */
    public List<TeamWithEmployees> getTeamsOverheadByCountry(Country country, int offset, int numberOfElements) {
        List<TeamWithEmployees> teams = teamDao.getTeamsByCountry(country, offset, numberOfElements);
        for (TeamWithEmployees team : teams) {
            team.setTeamOverheadValues(calculateTeamOverhead(team));
            team.setEmployeesOverheadPercentage(calculateTeamPercentage(team));}
        return teams;}

    /**

     compute the overhead for a team*
     @param team the team to calculate for
     returns a List that contains salaryOverhead,totalOverhead,productiveOverhead*/
    private Map<TeamWithEmployees.TeamOverheadType, BigDecimal> calculateTeamOverhead(TeamWithEmployees team) {
        Map<TeamWithEmployees.TeamOverheadType,BigDecimal> teamOverhead = new HashMap<>();
        BigDecimal salaryOverhead =rateCalculator.calculateTeamSalaryOverhead(team);
        BigDecimal expensesOverhead = rateCalculator.calculateTeamTotalOverhead(team);
        BigDecimal productiveOverhead =  rateCalculator.calculateProductiveOverHead(team);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.SALARY_OVERHEAD,salaryOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.EXPENSES_OVERHEAD,expensesOverhead);
        teamOverhead.put(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD,productiveOverhead);
        return teamOverhead;
    }

    private List<Map<String,Double>> calculateTeamPercentage(TeamWithEmployees team){
        List<Map<String,Double>> teamPercentagePerEmployee =team.getTeamMembers().stream().map(e-> employeePercentage(e,team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD))).toList();
        return teamPercentagePerEmployee;
    }


    private Map<String,Double> employeePercentage(Employee employee, BigDecimal totalOverhead){
        Map<String,Double> emplPercentage = new HashMap<>();
        BigDecimal employeeOverhead = employee.getOverhead();
        double percentage = employeeOverhead.divide(totalOverhead, MathContext.DECIMAL32).doubleValue() * 100;
        emplPercentage.put(employee.getName(), percentage);
        return emplPercentage;
    }

    public Map<Integer, Team> getTeams() throws RateException {
        return teamDao.getTeams();
    }

}
