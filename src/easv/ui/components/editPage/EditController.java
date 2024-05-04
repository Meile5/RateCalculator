package easv.ui.components.editPage;

import easv.Utility.EmployeeValidation;
import easv.Utility.WindowsManagement;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    @FXML
    private VBox editRoot;
    @FXML
    private HBox closeButton;
    @FXML
    private HBox saveButton;

    private IModel model;
    @FXML
    private MFXTextField utilPercentageTF, multiplierTF, markup, grossMargin;
    @FXML
    private MFXTextField salaryTF, workingHoursTF, annualAmountTF;
    @FXML
    private MFXTextField nameInput;
    @FXML
    private MFXComboBox<Country> countryCB;
    @FXML
    private MFXComboBox<Currency> currencyCB;
    @FXML
    private MFXComboBox<Team> teamCB;
    @FXML
    private MFXComboBox<EmployeeType> overOrResourceCB;
    @FXML
    private MFXComboBox<Configuration> configurations;

    private StackPane firstLayout;

    private Employee employee;
    private EmployeeInfoController employeeDisplayer;

    public EditController(IModel model, StackPane firstLayout, Employee employee, EmployeeInfoController employeeDisplayer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
        loader.setController(this);
        this.model = model;
        this.firstLayout = firstLayout;
        this.employee = employee;
        this.employeeDisplayer = employeeDisplayer;
        try {
            editRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
        }
    }

    private void addCloseButtonAction() {
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                WindowsManagement.closeStackPane(firstLayout));
    }

    public VBox getRoot() {
        return editRoot;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCloseButtonAction();
        //initialize the percentage inputs listeners
        initializePercentageInputValidationListeners();
        //initialize the digits inputs listeners
        initializeDigitsValidationListaners();
        // initialize the letters inputs listeners
        initializeLettersValidationListeners();
        //populate inputs with the selected employee to edit data
        populateInputs();
        //populate inputs with the values of the selected configuration from history(dropdown menu)
        populateSelectedConfiguration();
        //save the edit configuration
        saveEdit();

    }


    /**
     * initialize  percentage inputs validation  listeners
     */
    private void initializePercentageInputValidationListeners() {
        EmployeeValidation.addNonEmptyPercentageListener(utilPercentageTF);
        EmployeeValidation.addNonEmptyPercentageListener(multiplierTF);
        EmployeeValidation.addAdditionalMarkupsListeners(markup);
        EmployeeValidation.addAdditionalMarkupsListeners(grossMargin);
    }

    /**
     * initialize digits inputs validation listeners
     */
    private void initializeDigitsValidationListaners() {
        EmployeeValidation.addInputDigitsListeners(salaryTF);
        EmployeeValidation.addInputDigitsListeners(workingHoursTF);
        EmployeeValidation.addInputDigitsListeners(annualAmountTF);
    }

    /**
     * initialize letters inputs validation listeners
     */
    private void initializeLettersValidationListeners() {
        EmployeeValidation.addLettersOnlyInputListener(nameInput);
        EmployeeValidation.addLettersOnlyInputListener(countryCB);
    }

    private void populateInputs() {
        this.countryCB.setItems(model.getCountiesValues());
        this.countryCB.selectItem(employee.getCountry());
        this.nameInput.setText(employee.getName());
        ObservableList<Team> teams = FXCollections.observableArrayList(model.getTeams().values());
        Configuration config = employee.getActiveConfiguration();
        setInputsValuesWithConfiguration(config);
        this.currencyCB.setItems(FXCollections.observableArrayList(Currency.values()));
        this.currencyCB.selectItem(employee.getCurrency());
        this.teamCB.setItems(teams);
        this.teamCB.selectItem(employee.getTeam());
        this.overOrResourceCB.setItems(FXCollections.observableArrayList(EmployeeType.values()));
        this.overOrResourceCB.selectItem(employee.getType());
        this.configurations.setItems(FXCollections.observableArrayList(employee.getConfigurations()));
        this.configurations.selectItem(employee.getActiveConfiguration());
    }


    /**
     * save the edited employee
     */
    private void saveEdit() {
        this.saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            BigDecimal annualSalary = new BigDecimal(salaryTF.getText());
            BigDecimal fixedAnnualAmount = new BigDecimal(annualAmountTF.getText());
            BigDecimal overheadMultiplier = new BigDecimal(multiplierTF.getText());
            BigDecimal utilizationPercentage = new BigDecimal(utilPercentageTF.getText());
            BigDecimal workingHours = new BigDecimal(workingHoursTF.getText());
            double markupValue = 0;
            double grossMarginValue = 0;

            if (EmployeeValidation.validateAditionalMultipliers(markup, grossMargin).isEmpty()) {
                if (!isTextFieldEmpty(markup)) {
                    markupValue = Double.parseDouble(this.markup.getText());
                }
                if (!isTextFieldEmpty(grossMargin)) {
                    grossMarginValue = Double.parseDouble(this.grossMargin.getText());
                }
            } else {
                return;
            }

            Configuration editedConfiguration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, LocalDateTime.now(), true, markupValue, grossMarginValue);
            String name = this.nameInput.getText();
            Country country = this.countryCB.getSelectedItem();
            Currency currency = this.currencyCB.getSelectedItem();
            Team team = this.teamCB.getSelectedItem();
            EmployeeType employeeType = overOrResourceCB.getSelectedItem();
            Employee editedEmployee = new Employee(name, country, team, employeeType, currency);
            editedEmployee.setActiveConfiguration(editedConfiguration);
            if (model.updateEditedEmployee(this.employee, editedEmployee)) {
                updateUserValues(editedEmployee);
            }
        });
    }


    /**
     * populate the input fields with the selected configuration values
     */
    private void populateSelectedConfiguration() {
        this.configurations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setInputsValuesWithConfiguration(newValue);
            }
        });

    }

    /**
     * set the input values with the configuration values
     *
     * @param configuration the configuration object that is active for an employee
     */
    private void setInputsValuesWithConfiguration(Configuration configuration) {
        this.utilPercentageTF.setText(String.valueOf(configuration.getUtilizationPercentage()));
        this.multiplierTF.setText(String.valueOf(configuration.getOverheadMultiplier()));
        this.salaryTF.setText(String.valueOf(configuration.getAnnualSalary()));
        this.workingHoursTF.setText(String.valueOf(configuration.getWorkingHours()));
        this.annualAmountTF.setText(String.valueOf(configuration.getFixedAnnualAmount()));
        if (configuration.getMarkupMultiplier() != 0) {
            this.markup.setText(String.valueOf(configuration.getMarkupMultiplier()));
        }
        if (configuration.getGrossMargin() != 0) {
            this.grossMargin.setText(String.valueOf(configuration.getGrossMargin()));
        }
    }

    /**
     * call the EmployeeInfoControllerTo update the edited userValues
     */
    private void updateUserValues(Employee employee) {
        this.employeeDisplayer.setEmployeeName(employee.getName());
        this.employeeDisplayer.setCountry(employee.getCountry().getCountryName());
        this.employeeDisplayer.setEmployeeType(employee.getEmployeeType());
        this.employeeDisplayer.setTeam(employee.getTeam().getTeamName());
        WindowsManagement.closeStackPane(this.firstLayout);
    }

    private boolean isTextFieldEmpty(MFXTextField textField) {
        return textField.getText().isEmpty();
    }
}

