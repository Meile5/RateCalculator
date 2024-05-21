package easv.ui.components.teamManagement;

import easv.Utility.WindowsManagement;
import easv.be.Configuration;
import easv.be.Employee;
import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.teamManagementEmployeesAdd.EmployeesToAdd;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.components.teamsManagementTeamMembers.TeamMembersController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TeamManagementController implements Initializable {
    @FXML
    private GridPane teamManagementComponent;
    @FXML
    private VBox teamMembersContainer;
    @FXML
    private VBox allEmployeesContainer;
    @FXML
    private TextField grossMargin, markUp;
    @FXML
    private HBox closeButton, saveButton;

    private IModel model;
    private StackPane firstLayout;
    private Team team;
    private TeamInfoController teamInfoController;
    private TeamMembersController teamMembersController;
    private TeamsPageController teamsPageController;
    private EmployeesToAdd employeesToAdd;
    private List<EmployeesToAdd> employeesToAddList;
    private List<TeamMembersController> teamMembersToAddList;
    private Service<Void> saveTeam;


    public TeamManagementController(Team team, IModel model, StackPane firstLayout, TeamInfoController teamInfoController, EmployeesToAdd employeesToAdd) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamManagementComponent.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.model = model;
        this.team = team;
        this.teamInfoController = teamInfoController;
        this.employeesToAdd = employeesToAdd;
        employeesToAddList = new ArrayList<EmployeesToAdd>();
        teamMembersToAddList = new ArrayList<TeamMembersController>();
        this.teamsPageController = teamsPageController;
       // teamsPageController = new TeamsPageController(model, firstLayout); //?
       // this.employeesToAdd = employeesToAdd;
        employeesToAddList = new ArrayList<>();
        teamMembersToAddList = new ArrayList<>();
       teamsPageController = teamInfoController.getTeamsPageController();
        System.out.println(teamInfoController + "  teamInfoController");

       // teamsPageController = new TeamsPageController(model, firstLayout);

        try {
            teamManagementComponent = loader.load();
        } catch (IOException e) {
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
        populateTextFields();
        addCloseButtonAction();
        Platform.runLater(this::editAction);
    }

    /**
     * displays only team members for selected team
     */
//    public void displayTeamMembers() {
//        teamMembersContainer.getChildren().clear();
//        System.out.println(this + " team management Controller at the time of the teamMembersControllers initialization ");
//
//        System.out.println( teamMembersToAddList.size() + "size off the list that holds the team members");
//        for (Employee employee : team.getTeamMembers()) {
//            TeamMembersController teamMembersController = new TeamMembersController(employee, team, model, this);
//            teamMembersContainer.getChildren().add(teamMembersController.getRoot());
//            teamMembersToAddList.add(teamMembersController);
//            System.out.println( teamMembersToAddList.size() + "size off the list that holds the team members after ");
//            System.out.println( teamMembersController + "team members controller ");
//        }
//    }

    public void displayTeamMembers() {
        teamMembersContainer.getChildren().clear();
        teamMembersToAddList.clear(); // Clear the list before adding new members
        System.out.println(team.getEmployees().size() + " from the team Page ");
        for (Employee employee : team.getTeamMembers()) {
            TeamMembersController teamMembersController = new TeamMembersController(employee, team, model, this);
            teamMembersContainer.getChildren().add(teamMembersController.getRoot());
            teamMembersToAddList.add(teamMembersController);
        }
    }

    /**
     * displays all employees in the system  with their left util
     */
    public void displayAllEmployees() {
        allEmployeesContainer.getChildren().clear();
        employeesToAddList.clear();
        System.out.println(this + " team management Controller at the time of the EmployeesToAdd initialization ");
        employeesToAddList.forEach(EmployeesToAdd::changeName);
        model.getAllEmployees()
                .forEach(e -> {
                    EmployeesToAdd employeesToAdd = new EmployeesToAdd(e, model, this);
                    allEmployeesContainer.getChildren().add(employeesToAdd.getRoot());
                    employeesToAddList.add(employeesToAdd);
                    System.out.println(employeesToAdd+"employees to add controllers");
                });
    }

    private void editAction() {
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            System.out.println("on save");
            returnAllEmployees();
            returnAllEmployees().forEach((e)->{System.out.println(e.getName() + " " + e.getId() +"" + " testing");});
            returnEmployeesToDelete();
            getTeam();
          /*  try {
                model.performEditTeam(returnAllEmployees(), returnEmployeesToDelete(), getNewTeam(), team);
            } catch (RateException e) {}*/
            saveTeamOperation(returnAllEmployees(), returnEmployeesToDelete(), getTeam(), team);
         //

        });
    }

    private void saveTeamOperation(List<Employee> employees, List<Employee> employeesToDelete, Team editedTeam, Team originalTeam) {
        saveTeam = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.performEditTeam(employees, employeesToDelete, editedTeam, originalTeam);
                        return null;
                    }
                };
            }
        };

        saveTeam.setOnSucceeded(event -> {
            System.out.println("success");
          //  teamsPageController.clearTeams();
            teamsPageController.displayNewTeams();
            //showOperationStatus("Operation Successful!", Duration.seconds(2));
            WindowsManagement.closeStackPane(firstLayout);
            // closeWindowSpinner(firstLayout);

        });

        saveTeam.setOnFailed(event -> {
            saveTeam.getException().printStackTrace();
            //showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            WindowsManagement.closeStackPane(firstLayout);
            //closeWindowSpinner(firstLayout);
        });
        saveTeam.restart();
    }

    public Team getTeam() {
        String grossMarginString = grossMargin.getText();
        double grossMargin = 0.0; // Default value if null or empty
        if (grossMarginString != null && !grossMarginString.isEmpty()) {
            grossMargin = Double.parseDouble(grossMarginString);
        }

        String markUpString = markUp.getText();
        double markUp = 0.0; // Default value if null or empty
        if (markUpString != null && !markUpString.isEmpty()) {
            markUp = Double.parseDouble(markUpString);
        }
        Team editedTeam = new Team(team);
        editedTeam.setGrossMarginTemporary(grossMargin);
        editedTeam.setMarkupMultiplierTemporary(markUp);
        return editedTeam;
    }


    public List<Employee> returnEmployeesToDelete() {
        List<Employee> employeesToDeleteList = new ArrayList<Employee>();
        for (TeamMembersController teamMembersToAdd : teamMembersToAddList) {
            Employee employeesToDelete = teamMembersToAdd.membersToDelete();
            if (employeesToDelete != null) {
                employeesToDeleteList.add(employeesToDelete);
            }
        }
        System.out.println(employeesToDeleteList + "employeesToDelete");
        return employeesToDeleteList;
    }

    public List<Employee> returnAllEmployees() {
        List<Employee> editedEmployeesList = new ArrayList<Employee>();
        for (EmployeesToAdd employeesToAdd : employeesToAddList) {
            Employee editedEmployee = employeesToAdd.getEditedEmployee(team);
            if (editedEmployee != null) {
                editedEmployeesList.add(editedEmployee);
            }
        }
        List<Employee> editedTeamMembersList = new ArrayList<Employee>();
        for (TeamMembersController teamMembersToAdd : teamMembersToAddList) {
            Employee editedTeamMember = teamMembersToAdd.getEditedTeamMember(team);
            if (editedTeamMember != null) {
                editedTeamMembersList.add(editedTeamMember);
            }

        }
        List<Employee> employeesList = new ArrayList<Employee>();
        employeesList.addAll(editedTeamMembersList);
        employeesList.addAll(editedEmployeesList);
        return employeesList;

    }


    public void populateTextFields() {
        if (team != null && team.getActiveConfiguration() != null) {
            double margin = team.getActiveConfiguration().getGrossMargin();
            grossMargin.setText(String.valueOf(margin));  /* Convert double to String*/
            double markup = team.getActiveConfiguration().getMarkupMultiplier();
            markUp.setText(String.valueOf(markup));  /* Convert double to String*/

        } else {
            grossMargin.setText("0");
            markUp.setText("0");
        }
    }

    /**
     * closes manage popup
     */
    private void addCloseButtonAction() {
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            WindowsManagement.closeStackPane(firstLayout);
        });

    }


}
