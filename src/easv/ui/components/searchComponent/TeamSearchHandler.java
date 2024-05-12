package easv.ui.components.searchComponent;

import easv.be.Employee;
import easv.be.Team;
import easv.exception.RateException;
import javafx.collections.ObservableList;

public class TeamSearchHandler implements DataHandler<Team>{
    @Override
    public ObservableList getResultData(String filter) {
        return null;
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {

    }

    @Override
    public void undoSearchOperation() throws RateException {

    }
}
