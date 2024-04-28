package easv.employeesPage.deleteEmployee;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DeleteEmployeeController {

    @FXML
    private VBox deleteButtonContainer;
    private VBox deleteContainer;

    public DeleteEmployeeController(VBox deleteContainer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        try {
            deleteButtonContainer = loader.load();
            this.deleteContainer = deleteContainer;

        } catch (IOException e) {
            //ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public VBox getRoot() {
        return deleteButtonContainer;
    }
}
