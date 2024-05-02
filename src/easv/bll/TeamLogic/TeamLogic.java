package easv.bll.TeamLogic;

import easv.be.Country;
import easv.be.Team;
import easv.be.TeamWithEmployees;
import easv.bll.IRateCalculator;
import easv.bll.RateCalculator;
import easv.dal.teamDao.ITeamDao;
import easv.dal.teamDao.TeamDao;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.util.*;

public class TeamLogic implements ITeamLogic {
    private final ITeamDao teamDao;
    private final IRateCalculator rateCalculator;

    public TeamLogic() throws RateException {
        this.teamDao = new TeamDao();
        this.rateCalculator = new RateCalculator();
    }

    /**
     * retrieve the teams and related team overhead for a specific country
     * @param country country to retrieve for
     * @param offset the index from where to retrieve
     * @param numberOfElements how manny elements to retrieve
     */
    public Map<TeamWithEmployees, List<BigDecimal>> getTeamsOverheadByCountry(Country country, int offset, int numberOfElements) {
        List<TeamWithEmployees> teams = teamDao.getTeamsByCountry(country, offset, numberOfElements);
        Map<TeamWithEmployees, List<BigDecimal>> teamsOverhead = new HashMap<>();

        for (TeamWithEmployees team : teams) {
            teamsOverhead.put(team, calculateTeamOverhead(team));
        }
        return teamsOverhead;
    }

    /**
     * compute the overhead for a team
     *
     * @param team the team to calculate for
     *             returns a List that contains salaryOverhead,totalOverhead,productiveOverhead
     */
    private List<BigDecimal> calculateTeamOverhead(TeamWithEmployees team) {
        BigDecimal salaryOverhead = null;
        BigDecimal totalOverhead = null;
        BigDecimal productiveOverhead = null;
        salaryOverhead = rateCalculator.calculateTeamSalaryOverhead(team);
        totalOverhead = rateCalculator.calculateTeamTotalOverhead(team);
        productiveOverhead = rateCalculator.calculateProductiveOverHead(team);
        List<BigDecimal> teamOverhead = new ArrayList<>();
        Collections.addAll(teamOverhead, salaryOverhead, totalOverhead, productiveOverhead);
        return teamOverhead;
    }

}
