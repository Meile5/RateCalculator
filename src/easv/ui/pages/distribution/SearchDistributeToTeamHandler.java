package easv.ui.pages.distribution;

import easv.be.Team;
import easv.exception.RateException;
import easv.ui.components.searchComponent.DataHandler;
import javafx.collections.ObservableList;

public class SearchDistributeToTeamHandler implements DataHandler<Team> {
    private DistributionControllerInterface dataHandler;

    public SearchDistributeToTeamHandler(DistributionControllerInterface dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public ObservableList<Team> getResultData(String filter) {
        return dataHandler.getResultData(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
        dataHandler.performSelectSearchOperationTo(entityId);
    }

    @Override
    public void undoSearchOperation() throws RateException {
        dataHandler.undoSearchOperationTo();
    }
}
