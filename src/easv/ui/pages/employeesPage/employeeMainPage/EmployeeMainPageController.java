package easv.ui.pages.employeesPage.employeeMainPage;

import easv.Utility.DisplayEmployees;
import easv.Utility.WindowsManagement;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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

   public VBox getEmployeesContainer(){
       return employeesContainer;
   };
   private StackPane firstLayout;


    public EmployeeMainPageController(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        try {
            employeePage = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
           // ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public Parent getRoot() {
        return employeePage;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {

            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            displayEmployees();

        } catch (RateException | SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void displayEmployees() throws RateException, SQLException {
        employeesContainer.getChildren().clear();
       model.returnEmployees()
                .values()
                        .forEach(e -> {
                            DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(firstLayout, model, e);
                            EmployeeInfoController employeeInfoController = new EmployeeInfoController( e, deleteEmployeeController);
                            employeesContainer.getChildren().add(employeeInfoController.getRoot());




                        });


        }
    }





