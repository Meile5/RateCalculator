package easv.employeesPage.employeeInfo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class EmployeeInfoController {

   @FXML
   private HBox employeeComponent;
   @FXML
   private VBox employeesContainer;


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
}
