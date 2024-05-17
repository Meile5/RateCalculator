package easv.ui.pages.distribution;

import easv.be.Team;

public interface DistributionControllerInterface {
    void showTheTeamFromBarchart();
    void updateTotalOverheadValue(Double overheadPercentage);

    void removeOverheadPercentage(Double overheadValue);

    /**add team to distribute from*/
    void addTeamToDistributeFrom(Team team);

    /**add distribute to team in the distribute to list */
    void addDistributeToTeam(Team teamToDisplay);
}
