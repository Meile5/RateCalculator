package easv.dal.teamDao;
import easv.be.Team;
import easv.exception.RateException;
import java.util.Map;

public interface ITeamDao {
    Map<Integer, Team> getTeams() throws RateException;
}
