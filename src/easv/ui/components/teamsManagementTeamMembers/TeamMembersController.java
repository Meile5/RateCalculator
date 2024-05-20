package easv.ui.components.teamsManagementTeamMembers;

import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamMembersController implements Initializable {
    @FXML
    private HBox membersComponent;
    @FXML
    private Label teamMemberName, utilizationInTeam;
    @FXML
    private MFXCheckbox removeTeamMember;
    @FXML
    private MFXTextField utilPercentageToAdd;
    private IModel model;
    private TeamManagementController teamManagementController;
    private Employee employee;
    private Team team;

    public TeamMembersController(Employee employee, Team team, IModel model, TeamManagementController teamManagementController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamMembersComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.employee = employee;
        this.team = team;
        this.teamManagementController = teamManagementController;

        try {
            membersComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return membersComponent;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
    }

    public void setLabels() {
        if (employee != null) {
            teamMemberName.setText(employee.getName());
            teamMemberName.setTooltip(new Tooltip(teamMemberName.getText()));

            BigDecimal utilization = employee.getUtilPerTeams().get(team.getId());
            if (utilization != null) {
                utilizationInTeam.setText(utilization.toString() + "%");
            } else {
                utilizationInTeam.setText("N/A");
            }

        }
    }


}