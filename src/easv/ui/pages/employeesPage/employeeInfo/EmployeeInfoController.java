package easv.ui.pages.employeesPage.employeeInfo;

import easv.be.Employee;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

public class EmployeeInfoController implements Initializable {

   @FXML
   private HBox employeeComponent;
   @FXML
   private VBox employeesContainer;
   @FXML
   protected VBox deleteContainer;
  @FXML
   private Label employeeName;
   @FXML
   private Label employeeType;
   @FXML
   private Label country;
   @FXML
   private Label team;
   @FXML
   private Label dayRate;
   @FXML
   private Label hourlyRate;
   @FXML
   private Label hourlyCurrency;
   @FXML
   private Label dayCurrency;
   private Employee employee;
   private StackPane firstLayout;
   private DeleteEmployeeController deleteEmployeeController;

    public EmployeeInfoController( Employee employee, DeleteEmployeeController deleteEmployeeController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        this.employee = employee;
        this.deleteEmployeeController=deleteEmployeeController;

        try {

            employeeComponent = loader.load();

        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return employeeComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteContainer.getChildren().clear();
        this.deleteContainer.getChildren().add(deleteEmployeeController.getRoot());
        //displayDelete();
        setLabels();


    }
    /*public void displayDelete(){
        DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController( firstLayout,  deleteContainer);
        deleteContainer.getChildren().clear();
        deleteContainer.getChildren().add(deleteEmployeeController.getRoot());

    }*/
    public void setLabels(){
        if(employee != null) {
            employeeName.setText(employee.getName());
            employeeType.setText(employee.getType().toString());
            country.setText(employee.getCountry().getCountryName());
            team.setText(employee.getTeam().getTeam());
            dayRate.setText(employee.getDailyRate().toString());
            hourlyRate.setText(employee.getHourlyRate().toString());
            hourlyCurrency.setText(employee.getCurrency().toString());
            dayCurrency.setText(employee.getCurrency().toString());
        }
    }
}
