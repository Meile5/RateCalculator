package easv.ui.components.distributionPage.distributeToTeamInfo;


import easv.be.Team;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.control.ListCell;

public class DistributeToListCell extends ListCell<Team> {
    private IModel model;
    private DistributeToController controller;

    public DistributeToListCell(IModel model) {
        this.model = model;
    }

    @Override
    protected void updateItem(Team team, boolean empty) {
        super.updateItem(team, empty);

        if (empty || team == null) {
            setGraphic(null);
            setText(null);
        } else {
            if (controller == null) {
                controller = new DistributeToController(model, team);
            } else {
                controller.setTeamToDisplay(team);
            }
            setGraphic(controller.getRoot());
        }
    }
}

