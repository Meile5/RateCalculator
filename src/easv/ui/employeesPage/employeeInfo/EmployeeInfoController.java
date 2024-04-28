package easv.ui.employeesPage.employeeInfo;

import easv.ui.employeesPage.deleteEmployee.DeleteEmployeeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EmployeeInfoController implements Initializable {

   @FXML
   private HBox employeeComponent;
   @FXML
   private VBox employeesContainer;
    @FXML
    protected VBox deleteContainer;

    public EmployeeInfoController(VBox employeesContainer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        try {
            employeeComponent = loader.load();
            this.employeesContainer = employeesContainer;


        } catch (IOException e) {
            //ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return employeeComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayDelete();
    }
    public void displayDelete(){
        DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(deleteContainer);
        deleteContainer.getChildren().clear();
        deleteContainer.getChildren().add(deleteEmployeeController.getRoot());

    }
}
