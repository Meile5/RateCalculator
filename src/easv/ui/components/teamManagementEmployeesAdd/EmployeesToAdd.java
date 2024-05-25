package easv.ui.components.teamManagementEmployeesAdd;

import easv.Utility.TeamValidation;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class EmployeesToAdd implements Initializable {
    @FXML
    private HBox employeesToAddComponent;
    @FXML
    private Label employeeName, utilLeft, employeeType;
    @FXML
    private MFXCheckbox addEmployee;
    @FXML
    private MFXTextField utilPercentageToAdd;
    private IModel model;
    private TeamManagementController teamManagementController;
    private Employee employee;
    private TeamValidation teamValidation;


    public EmployeesToAdd(Employee employee, IModel model, TeamManagementController teamManagementController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesToAddInTeam.fxml"));
        loader.setController(this);
        this.model = model;
        this.employee = employee;
        this.teamManagementController = teamManagementController;
        try {
            employeesToAddComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return employeesToAddComponent;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        utilListener();

    }
    private void utilListener() {
        utilPercentageToAdd.textProperty().addListener((observable, oldValue, newValue) -> {
           TeamValidation.isPercentageValid(utilPercentageToAdd, utilLeft);
        });
    }

    public void setLabels() {
        if (employee != null) {
            employeeName.setText(employee.getName());
            employeeName.setTooltip(new Tooltip(employeeName.getText()));
            employeeType.setText(employee.getEmployeeType().toString());
            employeeType.setTooltip(new Tooltip(employeeType.getText()));

            BigDecimal remainingUtilization = calculateRemainingUtilization(employee.getUtilPerTeams());
            if (remainingUtilization != null) {
                utilLeft.setText(remainingUtilization.toString() + "%");
            }else {
                utilLeft.setText("N/A");
            }


        }
    }
    private BigDecimal calculateRemainingUtilization(Map<Integer, BigDecimal> utilPerTeams) {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        for (BigDecimal utilization : utilPerTeams.values()) {
            if (utilization != null) {
                totalUtilization = totalUtilization.add(utilization);
            }
        }
        return BigDecimal.valueOf(100).subtract(totalUtilization);
    }

    public Employee getEditedEmployee(Team team) {
        if (addEmployee.isSelected()) {
            if (TeamValidation.isPercentageValid(utilPercentageToAdd, utilLeft)) {
                Employee editedEmployee = employee;
                String utilPercentageStr = utilPercentageToAdd.getText();
                BigDecimal utilPercentage = new BigDecimal(utilPercentageStr);
                editedEmployee.getUtilPerTeams().put(team.getId(), utilPercentage);
                return editedEmployee;
            }


        }
        return null;
    }

}
