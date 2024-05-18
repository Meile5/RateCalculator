package easv.ui.components.teamsManagementTeamMembers;

import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;

public class TeamMembersController {
    @FXML
    private HBox teamMembersComponent;
    @FXML
    private Label teamMemberName, utilizationInTeam;
    @FXML
    private MFXCheckbox removeTeamMember;
    private IModel model;
    private Team team;
    private TeamManagementController teamManagementController;

    public TeamMembersController(Team team , IModel model, TeamManagementController teamManagementController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamManagementComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.team = team;
        this.teamManagementController = teamManagementController;

        try {
            teamMembersComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return teamMembersComponent;
    }
}
