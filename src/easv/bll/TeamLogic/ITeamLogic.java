package easv.bll.TeamLogic;
import easv.be.OverheadComputationPair;
import easv.be.Region;
import easv.be.Team;
import easv.exception.RateException;


import java.math.BigDecimal;
import java.util.Map;


//TODO do not delete this interface  i Andrei found a usage for
public interface ITeamLogic  {
  Map<Integer, Team> getTeams() throws RateException;


  OverheadComputationPair <String ,BigDecimal> computeRegionOverhead(Region region);
}
