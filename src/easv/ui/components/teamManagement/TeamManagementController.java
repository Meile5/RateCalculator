package easv.ui.components.teamManagement;

import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class TeamManagementController {
    @FXML
    private GridPane teamManagementComponent;
    private IModel model;
    private StackPane firstLayout;
    private Team team;
    private TeamInfoController teamInfoController;



    public TeamManagementController(Team team , IModel model, StackPane firstLayout, TeamInfoController teamInfoController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamManagementComponent.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.model = model;
        this.team = team;
        this.teamInfoController = teamInfoController;

        try {
            teamManagementComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public GridPane getRoot() {
        return teamManagementComponent;
    }
}
