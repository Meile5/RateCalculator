package easv.bll.TeamLogic;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


//TODO do not delete this interface  i Andrei found a usage for
public interface ITeamLogic  {
  Map<Integer, Team> getTeams() throws RateException;


  OverheadComputationPair <String ,BigDecimal> computeRegionOverhead(Region region);

    DistributionValidation  validateDistributionInputs(Map<Integer, String> insertedDistributionPercentageFromTeams);

    /**calculate the total overhead inserted for the valid inputs*/
    double calculateTotalOverheadInsertedForValidInputs(Map<Integer, String> insertedDistributionPercentageFromTeams);


    /**perform the simulation computation*/
    Map<OverheadHistory, List<Team>> performSimulationComputation(Team selectedTeamToDistributeFrom, Map<Integer, String> insertedDistributionPercentageFromTeams, ObservableMap<Integer, Team> teamsWithEmployees);
}
