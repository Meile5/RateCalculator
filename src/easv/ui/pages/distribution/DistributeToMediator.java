package easv.ui.pages.distribution;

import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToInterface;

public class DistributeToMediator {
    private DistributionControllerInterface distributionController;
    private DistributeToInterface distributeToInterface;

    public DistributeToMediator() {
    }


    public void registerMainController(DistributionControllerInterface distributionController){
        this.distributionController= distributionController;
    }

    public void registerSmallController(DistributeToInterface distributeToInterface){
        this.distributeToInterface= distributeToInterface;
    }

    public void updateTotalOverheadValue(Double overheadPercentage){
        distributionController.updateTotalOverheadValue(overheadPercentage);
    }


    public void removeDistributionPercentage(Double overheadValue) {
        distributionController.removeOverheadPercentage(overheadValue);
    }
}
