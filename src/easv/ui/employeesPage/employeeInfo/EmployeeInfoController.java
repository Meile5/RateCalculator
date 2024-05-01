package easv.ui.employeesPage.employeeInfo;

import easv.be.Employee;
import easv.ui.employeesPage.deleteEmployee.DeleteEmployeeController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
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
   private Employee employee;

    public EmployeeInfoController( Employee employee) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        this.employee = employee;
        try {
            System.out.println(employeeComponent +"constructor");
            employeeComponent = loader.load();

            //this.employeesContainer = employeesContainer;
        } catch (IOException e) {
            e.printStackTrace();
            //ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return employeeComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()-> {displayDelete();
        setLabels();
            System.out.println(employeeName.getText()+ "emeployee name");} );

    }
    public void displayDelete(){
        DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(deleteContainer);
        deleteContainer.getChildren().add(deleteEmployeeController.getRoot());

    }
    public void setLabels(){
        employeeName.setText(employee.getName());
        employeeType.setText(employee.getType().toString());
        country.setText(employee.getCountry().getCountryName());
        team.setText(employee.getTeam().getTeam());
        dayRate.setText(employee.getDailyRate().toString());
        hourlyRate.setText(employee.getHourlyRate().toString());
    }
}
