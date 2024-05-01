package easv.ui.pages.displayPage;

import easv.ui.pages.IModel;
import easv.ui.pages.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class EmployeeController {
    @FXML
    private Parent employeePage;
    private IModel model;

    public EmployeeController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
        loader.setController(this);
        this.model= model;
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
