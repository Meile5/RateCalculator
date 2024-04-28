package easv.employeesPage.employeeMainPage;

import easv.employeesPage.employeeInfo.EmployeeInfoController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeMainPageController implements Initializable {
    @FXML
    private VBox employeesContainer;
   @FXML
   private HBox employeesMainPageContainer;
    @FXML
    private EmployeeInfoController employeeInfoController;

    /*public EmployeeMainPageController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        try {
            employeesMainPageContainer = loader.load();


        } catch (IOException e) {
            //ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public HBox getRoot() {
        return employeesMainPageContainer;
    }*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayEmployees();
    }

    public void displayEmployees() {
       // if (employeesContainer.getScene() != null) {
        employeeInfoController = new EmployeeInfoController(employeesContainer);
        employeesContainer.getChildren().clear();
        employeesContainer.getChildren().add(employeeInfoController.getRoot());
        }
    }



