package easv.ui.components.teamsComponents;


import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;;
import easv.ui.pages.employeesPage.employeeMainPage.EmployeeMainPageController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamInfoController implements Initializable {
    @FXML
    private HBox teamInfoComponent;
    private IModel model;
    private Team team;
    private TeamsPageController teamsPageController;

    public TeamInfoController(Team team /*, DeleteEmployeeController deleteEmployeeController*/, IModel model,/* StackPane firstLayout,*/ TeamsPageController teamsPageController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        this.team = team;
       // this.deleteEmployeeController = deleteEmployeeController;
        //this.firstLayout = firstLayout;
        this.model = model;
        this.teamsPageController = teamsPageController;
        try {
            teamInfoComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return teamInfoComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
