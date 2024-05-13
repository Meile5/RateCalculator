package easv.bll.TeamLogic;
import easv.be.Team;
import easv.exception.RateException;
import java.util.Map;

public interface ITeamLogic  {
  Map<Integer, Team> getTeams() throws RateException;
}
