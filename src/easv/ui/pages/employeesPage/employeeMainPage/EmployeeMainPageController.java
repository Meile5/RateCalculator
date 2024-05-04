package easv.ui.pages.employeesPage.employeeMainPage;
import easv.Utility.DisplayEmployees;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EmployeeMainPageController implements Initializable , DisplayEmployees {
    @FXML
    private VBox employeesContainer;
    @FXML
    private Parent employeePage;

    private IModel model;

    @FXML
    private MFXProgressSpinner progressBar;

    public VBox getEmployeesContainer() {
        return employeesContainer;
    }


    private StackPane firstLayout;


    public EmployeeMainPageController(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        try {
            employeePage = loader.load();
        } catch (IOException e) {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public Parent getRoot() {
        return employeePage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            progressBar.setVisible(true);
            displayEmployees();

        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }
    public void displayEmployees() {

        progressBar.setVisible(true);

        Platform.runLater(() -> {
            employeesContainer.getChildren().clear();

            try {
                model.returnEmployees()
                        .values()
                        .forEach(e -> {
                            DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(firstLayout, model, e);
                            EmployeeInfoController employeeInfoController = new EmployeeInfoController(e, deleteEmployeeController, model, firstLayout);
                            employeesContainer.getChildren().add(employeeInfoController.getRoot());
                        });
            } catch (RateException e) {
                ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_EMPLOYEES_FAILED.getValue());
            } finally {
                // Hide the progress spinner after loading is complete or if an error occurs
                progressBar.setVisible(false);
            }
        });
    }




}




