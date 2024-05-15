package easv.ui.pages.teamsPage;

import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.teamsComponents.TeamInfoController;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.modelFactory.ModelFactory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamsPageController implements Initializable {
    private IModel model;
    @FXML
    private Parent teamPage;
    @FXML
    private VBox teamsContainer;
    private Service<Void> loadTeamsFromDB;
    private TeamInfoController teamInfoController;
    public TeamsPageController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamsManagementPage.fxml"));
        loader.setController(this);
        this.model=model;
        try {
            teamPage = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public Parent getRoot(){
        return teamPage;

    }

    public void displayTeams() {
        teamsContainer.getChildren().clear();
        model.getOperationalTeams()
                .forEach(t -> {
                    TeamInfoController teamInfoController = new TeamInfoController( t, model, this);
                    teamsContainer.getChildren().add(teamInfoController.getRoot());
                });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            System.out.println(model);
            displayTeams();

        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
        displayTeams();
    }
}
