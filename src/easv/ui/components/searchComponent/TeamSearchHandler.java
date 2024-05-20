package easv.ui.components.searchComponent;
import easv.be.Team;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableList;

public class TeamSearchHandler implements DataHandler<Team>{
   private IModel model ;

//TODO Do this tomorrow when we meet in order to see what Meile is using
    public TeamSearchHandler(IModel model) {
        this.model = model;
    }

    @Override
    public ObservableList<Team> getResultData(String filter) {
      return model.getTeamsFilterResults(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
    }

    @Override
    public void undoSearchOperation() throws RateException {

    }
}
