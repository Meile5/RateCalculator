package easv.ui.components.distributionPage.distributeToTeamInfo;


import easv.be.Team;
import easv.ui.pages.distribution.DistributeToMediator;
import easv.ui.pages.distribution.DistributionController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;

import java.awt.color.ColorSpace;

public class DistributeToListCell extends ListCell<Team> {
    private IModel model;
    private DistributeToController controller;
    private DistributeToMediator distributeToMediator;


    public DistributeToListCell(IModel model,DistributeToMediator distributeToMediator) {
        this.model = model;
        this.distributeToMediator= distributeToMediator;
    }

    @Override
    protected void updateItem(Team team, boolean empty) {
        super.updateItem(team, empty);

        if (empty || team == null) {
            setGraphic(null);
            setText(null);
        } else {
            if(isSelected()){
                setTextFill(Color.BLACK);
            }
            if (controller == null) {
                controller = new DistributeToController(model, team,distributeToMediator);
            } else {
                controller.setTeamToDisplay(team);
            }
            setGraphic(controller.getRoot());
        }
    }
}

