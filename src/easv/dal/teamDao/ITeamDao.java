package easv.dal.teamDao;
import easv.be.RateType;
import easv.be.Team;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface ITeamDao {
    Map<Integer, Team> getTeams() throws RateException;


    boolean savePerformedDistribution(Map<Team, Map<RateType, BigDecimal>> receivedTeams, Team selectedTeamToDistributeFrom) throws RateException;
}
