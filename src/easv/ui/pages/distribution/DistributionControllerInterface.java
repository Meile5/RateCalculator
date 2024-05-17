package easv.ui.pages.distribution;

import easv.be.Team;

public interface DistributionControllerInterface {
    void showTheTeamFromBarchart();
    void updateTotalOverheadValue(Double overheadPercentage);

    void removeOverheadPercentage(Double overheadValue);
}
