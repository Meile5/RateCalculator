package easv.ui.employeesPage.employeeMainPage;

import easv.exception.RateException;
import easv.ui.ModelFactory;
import easv.ui.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.IModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class EmployeeMainPageController implements Initializable {
    @FXML
    private VBox employeesContainer;
   @FXML
   private HBox employeesMainPageContainer;
   private IModel model;



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
        try {

            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            displayEmployees();
        } catch (SQLException | RateException e) {
            throw new RuntimeException(e);
        }
    }



    public void displayEmployees() throws SQLException {
        employeesContainer.getChildren().clear();
        model.returnEmployees()
                .values()
                        .forEach(e -> {
                            EmployeeInfoController employeeInfoController = new EmployeeInfoController(employeesContainer, e);
                            employeesContainer.getChildren().add(employeeInfoController.getRoot());

                        });


        }
    }





