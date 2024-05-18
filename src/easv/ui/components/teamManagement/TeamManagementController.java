package easv.ui.components.teamManagement;

import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagementEmployeesAdd.EmployeesToAdd;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.components.teamsManagementTeamMembers.TeamMembersController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamManagementController implements Initializable {
    @FXML
    private GridPane teamManagementComponent;
    @FXML
    private VBox teamMembersContainer;
    @FXML
    private VBox allEmployeesContainer;

    private IModel model;
    private StackPane firstLayout;
    private Team team;
    private TeamInfoController teamInfoController;
    private TeamMembersController teamMembersController;




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
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public GridPane getRoot() {
        return teamManagementComponent;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayTeamMembers();
        displayAllEmployees();
    }
   /** displays only team members for selected team*/
    public void displayTeamMembers() {
        teamMembersContainer.getChildren().clear();
        for (Employee employee : team.getTeamMembers()) {
            TeamMembersController teamMembersController = new TeamMembersController(employee, team, model, this);
            teamMembersContainer.getChildren().add(teamMembersController.getRoot());
        }
    }
    /** displays all employees in the system  with their left util*/
    public void displayAllEmployees() {
        allEmployeesContainer.getChildren().clear();
        model.getAllEmployees()
                .forEach(e -> {
                    EmployeesToAdd employeesToAdd = new EmployeesToAdd( e, model, this);
                    allEmployeesContainer.getChildren().add(employeesToAdd.getRoot());

                });
    }


}
