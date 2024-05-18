package easv.ui.pages.distribution;

import easv.be.Team;
import javafx.scene.Parent;

import java.util.List;

public interface DistributionControllerInterface {

    void updateTotalOverheadValue();

    boolean removeTeamFromDistributionView(List<Parent> teamsAfterRemoveOperation);

    /**add team to distribute from*/
    void addTeamToDistributeFrom(Team team);

    /**add distribute to team in the distribute to list */
    void addDistributeToTeam(Team teamToDisplay);
}
