package easv.ui.pages.displayPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class EmployeeController {
    @FXML
    private Parent employeePage;

    public EmployeeController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
        loader.setController(this);
        try {
            employeePage=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getEmployeePage() {
        return employeePage;
    }

}
