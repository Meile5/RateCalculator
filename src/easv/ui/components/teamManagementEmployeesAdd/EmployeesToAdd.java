package easv.ui.components.teamManagementEmployeesAdd;

import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class EmployeesToAdd implements Initializable {
    @FXML
    private HBox employeesToAddComponent;
    @FXML
    private Label employeeName, utilLeft;
    @FXML
    private MFXCheckbox addEmployee;
    private IModel model;
    private TeamManagementController teamManagementController;
    private Employee employee;


    public EmployeesToAdd(Employee employee, IModel model, TeamManagementController teamManagementController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesToAddInTeam.fxml"));
        loader.setController(this);
        this.model = model;
        this.employee = employee;
        this.teamManagementController = teamManagementController;

        try {
            employeesToAddComponent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return employeesToAddComponent;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
    }

    public void setLabels() {
        if (employee != null) {
            employeeName.setText(employee.getName());
            employeeName.setTooltip(new Tooltip(employeeName.getText()));

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

}