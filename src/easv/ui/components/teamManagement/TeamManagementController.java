package easv.ui.components.teamManagement;

import easv.Utility.WindowsManagement;
import easv.be.Employee;
import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagementEmployeesAdd.EmployeesToAdd;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.components.teamsManagementTeamMembers.TeamMembersController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.application.Platform;
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
    private EmployeesToAdd employeesToAdd;
    private List<EmployeesToAdd> employeesToAddList;




    public TeamManagementController(Team team , IModel model, StackPane firstLayout, TeamInfoController teamInfoController, EmployeesToAdd employeesToAdd) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamManagementComponent.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.model = model;
        this.team = team;
        this.teamInfoController = teamInfoController;
        this.employeesToAdd = employeesToAdd;
        employeesToAddList = new ArrayList<EmployeesToAdd>();

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
                    employeesToAddList.add(employeesToAdd);

                });
    }

    private void editAction() {
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            List<Employee> editedEmployeesList = new ArrayList<Employee>();
            for(EmployeesToAdd employeesToAdd : employeesToAddList){
               Employee editedEmployee = employeesToAdd.getEditedEmployee(team);
               if(editedEmployee != null){
                   editedEmployeesList.add(editedEmployee);
               }

           }

        });

    }

  /*  private Employee getTeam(TeamConfiguration editedConfiguration) {
        Country country = countryCB.getSelectedItem();
        Currency currency = Currency.valueOf(this.currencyCB.getSelectedItem());
        Team team = getSelectedTeam();
        String name = this.nameInput.getText();
        EmployeeType employeeType = overOrResourceCB.getSelectedItem();
        Team editedTeam = new Team(name, country, team, employeeType, currency);
        editedEmployee.setConfigurations(employee.getConfigurations());
        editedEmployee.setActiveConfiguration(editedConfiguration);
        editedEmployee.setId(employee.getId());
        return editedEmployee;
    }*/

    //TODO call the method that changes the style off the employee container to default
  /*  private void saveEdit() {
        this.saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (EmployeeValidation.areNamesValid(nameInput, countryCB, teamCB) &&
                    EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF) &&
                    EmployeeValidation.arePercentagesValid(utilPercentageTF, multiplierTF) &&
                    EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB)
                    && EmployeeValidation.validateAditionalMultipliers(markup, grossMargin)) {
                Configuration editedConfiguration = getConfiguration();
                Employee editedEmployee = getEmployee(editedConfiguration);
                if (model.isEditOperationPerformed(employee, editedEmployee)) {
                     spinnerLayer.setDisable(false);
                     spinnerLayer.setVisible(true);
                    initializeService(employee,editedEmployee);
                } else {
                    WindowsManagement.closeStackPane(this.firstLayout);
                }
            }
        });
    }*/


    /**
     * create the employee object with the edited values
     */
   /* private Employee getEmployee(Configuration editedConfiguration) {
        Country country = countryCB.getSelectedItem();
        Currency currency = Currency.valueOf(this.currencyCB.getSelectedItem());
        Team team = getSelectedTeam();
        String name = this.nameInput.getText();
        EmployeeType employeeType = overOrResourceCB.getSelectedItem();
        Employee editedEmployee = new Employee(name, country, team, employeeType, currency);
        editedEmployee.setConfigurations(employee.getConfigurations());
        editedEmployee.setActiveConfiguration(editedConfiguration);
        editedEmployee.setId(employee.getId());
        return editedEmployee;
    }*/


    /**
     * create the Configuration object from the inputs fields
     */
   /* private Configuration getConfiguration() {
        BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText()));
        BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText()));
        BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText()));
        BigDecimal utilizationPercentage = new BigDecimal(convertToDecimalPoint(utilPercentageTF.getText()));
       BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText()));
        double markupValue = 0;
        double grossMarginValue = 0;
        if (!isTextFieldEmpty(markup)) {
            markupValue = Double.parseDouble(convertToDecimalPoint(this.markup.getText()));
        }
       if (!isTextFieldEmpty(grossMargin)) {
           grossMarginValue = Double.parseDouble(convertToDecimalPoint(this.grossMargin.getText()));
       }
        return new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, LocalDateTime.now(), true, markupValue, grossMarginValue);
    }*/

    public void populateTextFields(){
        if (team != null && team.getActiveConfiguration() != null) {
            double margin = team.getActiveConfiguration().getGrossMargin();
            grossMargin.setText(String.valueOf(margin));  /* Convert double to String*/
            double markup = team.getActiveConfiguration().getMarkupMultiplier();
            markUp.setText(String.valueOf(markup));  /* Convert double to String*/

        } else {
            grossMargin.setText("N/A");
            markUp.setText("N/A");
        }
    }

    /** closes manage popup*/
    private void addCloseButtonAction() {
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            WindowsManagement.closeStackPane(firstLayout);});

    }


}
