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
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class EditController implements Initializable {

    @FXML
    private StackPane componentParent;
    @FXML
    private HBox closeButton;
    @FXML
    private HBox saveButton;
    @FXML
    private ScrollPane teamsAndCountries;

    private IModel model;
    @FXML
    private MFXTextField utilPercentageTF, multiplierTF;
    @FXML
    private MFXTextField nameInput, salaryTF, workingHoursTF, annualAmountTF, workingHoursInput;

    @FXML
    private MFXComboBox<Region> regionComboBox;
    @FXML
    private MFXComboBox<Country> countryCB;
    @FXML
    private MFXComboBox<String> currencyCB;
    @FXML
    private MFXComboBox<Team> teamComboBox;
    @FXML
    private MFXComboBox<EmployeeType> overOrResourceCB;
    @FXML
    private MFXComboBox<Configuration> configurations;

    @FXML
    private StackPane spinnerLayer;

    private StackPane firstLayout;

    private Employee employee;
    private EmployeeInfoController employeeDisplayer;
    private Service<Boolean> editService;

    private Service<Boolean> calculateEditOperationPerformedEdit;

    public EditController(IModel model, StackPane firstLayout, Employee employee, EmployeeInfoController employeeDisplayer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditStackPane.fxml"));
        loader.setController(this);
        this.model = model;
        this.firstLayout = firstLayout;
        this.employee = employee;
        this.employeeDisplayer = employeeDisplayer;
        try {
            componentParent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
        }
    }

    private void addCloseButtonAction() {
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        { employeeDisplayer.setEmployeesVboxContainerStyleToDefault();
                WindowsManagement.closeStackPane(firstLayout);});
    }

    public StackPane getRoot() {
        return componentParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCloseButtonAction();
        //initialize the percentage inputs listeners
        initializePercentageInputValidationListeners();
        //initialize the digits inputs listeners
        initializeDigitsValidationListeners();
        // initialize the letters inputs listeners
        initializeLettersValidationListeners();
        //populate inputs with the selected employee to edit data
        populateInputs();
        //populate inputs with the values of the selected configuration from history(dropdown menu)
        populateSelectedConfiguration();
        //save the edit configuration
        //saveEdit();

        //initialize region listener
        addRegionSelectionListener(regionComboBox,countryCB);
        //initialize country listener
        addCountrySelectionListener(countryCB,teamComboBox);
    }

    /**
     * populate input fields with the employee data
     */
    private void populateInputs() {
        //set country info

        //TODO this will be used in the Country creation
//        ObservableList<Country>  countries =  FXCollections.observableArrayList();
//        countries.addAll(model.getCountiesValues());
//        countries.addAll(model.getValidCountries());


        //setRegionInfo
        this.regionComboBox.setItems(model.getOperationalRegions());
        if (!employee.getRegions().isEmpty()) {
            String employeeRegionName = employee.getRegions().get(0).getRegionName();

            Region regionToSelect = regionComboBox.getItems().stream()
                    .filter(c -> c.getRegionName().equals(employeeRegionName))
                    .findFirst()
                    .orElse(null);
            this.regionComboBox.selectItem(regionToSelect);
        }


        //set Country Info
        this.countryCB.setItems(model.getOperationalCountries());
        String employeeCountryName = employee.getCountries().get(0).getCountryName();
        Country countryToSelect = countryCB.getItems().stream()
                .filter(c -> c.getCountryName().equals(employeeCountryName))
                .findFirst()
                .orElse(null);
        countryCB.selectItem(countryToSelect);
        this.nameInput.setText(employee.getName());

        //set team info
        this.teamComboBox.setItems(model.getOperationalTeams());
        String employeeTeamName = employee.getTeams().get(0).getTeamName();
        Team teamToSelect = teamComboBox.getItems().stream()
                .filter(c -> c.getTeamName().equals(employeeTeamName))
                .findFirst()
                .orElse(null);
        teamComboBox.selectItem(teamToSelect);
        //set configuration info
        Configuration config = employee.getActiveConfiguration();
        setInputsValuesWithConfiguration(config);
        //set currency inputs
        this.currencyCB.setItems(FXCollections.observableArrayList(Arrays.stream(Currency.values()).map(Enum::name).toList()));
        this.currencyCB.selectItem(employee.getCurrency().name());
        //set resource fields
        this.overOrResourceCB.setItems(FXCollections.observableArrayList(EmployeeType.values()));
        this.overOrResourceCB.selectItem(employee.getType());
        //set configurations items
        this.configurations.setItems(FXCollections.observableArrayList(employee.getConfigurations()));
        this.configurations.selectItem(employee.getActiveConfiguration());
    }


    /**
     * save the edited employee
     */
    //TODO call the method that changes the style off the employee container to default
    /*private void saveEdit() {
        this.saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (EmployeeValidation.areNamesValid(nameInput, countryCB, teamCB) &&
                    EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF) &&
                    EmployeeValidation.arePercentagesValid(utilPercentageTF, multiplierTF) &&
                    EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB)
                    && EmployeeValidation.validateAditionalMultipliers(markup, grossMargin)) {
                Configuration editedConfiguration = getConfiguration();
                Employee editedEmployee = getEmployee(editedConfiguration);
                if (model.isEditOperationPerformed(employee, editedEmployee)) {
                     spinnerLayer.setDisable(false);
                     spinnerLayer.setVisible(true);
                    initializeService(employee,editedEmployee);
                } else {
                    WindowsManagement.closeStackPane(this.firstLayout);
                }
            }
        });
    }


    /**
     * create the employee object with the edited values
     */
    /*private Employee getEmployee(Configuration editedConfiguration) {
        Country country = countryCB.getSelectedItem();
        Currency currency = Currency.valueOf(this.currencyCB.getSelectedItem());
        Team team = getSelectedTeam();
        String name = this.nameInput.getText();
        EmployeeType employeeType = overOrResourceCB.getSelectedItem();
        Employee editedEmployee = new Employee(name, country, team, employeeType, currency);
        editedEmployee.setConfigurations(employee.getConfigurations());
        editedEmployee.setActiveConfiguration(editedConfiguration);
        editedEmployee.setId(employee.getId());
        return editedEmployee;
    }*/


    /**
     * create the Configuration object from the inputs fields
     */
//    private Configuration getConfiguration() {
//        BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText()));
//        BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText()));
//        BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText()));
//        BigDecimal utilizationPercentage = new BigDecimal(convertToDecimalPoint(utilPercentageTF.getText()));
//        BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText()));
//        double markupValue = 0;
//        double grossMarginValue = 0;
//        if (!isTextFieldEmpty(markup)) {
//            markupValue = Double.parseDouble(convertToDecimalPoint(this.markup.getText()));
//        }
//        if (!isTextFieldEmpty(grossMargin)) {
//            grossMarginValue = Double.parseDouble(convertToDecimalPoint(this.grossMargin.getText()));
//        }
//        return new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, LocalDateTime.now(), true, markupValue, grossMarginValue);
//    }


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
     * set the input values with the configuration selected from the history values
     *
     * @param configuration the configuration object that is active for an employee
     */
    private void setInputsValuesWithConfiguration(Configuration configuration) {
        this.utilPercentageTF.setText(String.valueOf(configuration.getUtilizationPercentage()));
        this.multiplierTF.setText(String.valueOf(configuration.getOverheadMultiplier()));
        this.salaryTF.setText(String.valueOf(configuration.getAnnualSalary()));
        this.workingHoursTF.setText(String.valueOf(configuration.getWorkingHours()));
        this.annualAmountTF.setText(String.valueOf(configuration.getFixedAnnualAmount()));
        this.workingHoursInput.setText(configuration.getDayWorkingHours() + "");
    }

    /**
     * call the EmployeeInfoController to update the edited userValues
     */
    private void updateUserValues(Employee employee) {
       /* this.employeeDisplayer.setEmployeeName(employee.getName());
        this.employeeDisplayer.setCountry(employee.getCountry().getCountryName());
        this.employeeDisplayer.setEmployeeType(employee.getEmployeeType());
        this.employeeDisplayer.setTeam(employee.getTeam().getTeamName());
        this.employeeDisplayer.setEmployee(employee);
        this.employeeDisplayer.setDayRate(model.getComputedDayRate(employee).toString());
        this.employeeDisplayer.setHourlyRate(model.getComputedHourlyRate(employee,0).toString());
       // this.employeeDisplayer.refreshRates();
        this.employeeDisplayer.callService();
        WindowsManagement.closeStackPane(this.firstLayout);*/
    }

    private boolean isTextFieldEmpty(MFXTextField textField) {
        return textField.getText().isEmpty();
    }

    private Team getSelectedTeam() {
        Team team = null;
        if (teamComboBox.getSelectedItem() == null) {
        } else {
            team = teamComboBox.getSelectedItem();
        }
        return team;
    }


    /**
     * initialize  percentage inputs validation  listeners
     */
    private void initializePercentageInputValidationListeners() {
        EmployeeValidation.addNonEmptyPercentageListener(utilPercentageTF);
        EmployeeValidation.addNonEmptyPercentageListener(multiplierTF);

    }

    /**
     * initialize digits inputs validation listeners
     */
    private void initializeDigitsValidationListeners() {
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

    private void initializeService(Employee originalEmployee, Employee editedEmployee) {
        this.editService = new Service<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return model.updateEditedEmployee(originalEmployee, editedEmployee);
                    }
                };
            }
        };

        this.editService.setOnSucceeded((edit) -> {
            if (editService.getValue()) {
                updateUserValues(editedEmployee);
            } else {
                this.spinnerLayer.setVisible(false);
                this.spinnerLayer.setDisable(true);
                WindowsManagement.closeStackPane(firstLayout);
                ExceptionHandler.errorAlertMessage(ErrorCode.OPERATION_DB_FAILED.getValue());
            }
        });

        this.editService.setOnFailed((error) -> {
            ExceptionHandler.errorAlertMessage(ErrorCode.OPERATION_DB_FAILED.getValue());
            this.spinnerLayer.setVisible(false);
            this.spinnerLayer.setDisable(true);
        });
        editService.restart();
    }

    /**
     * convert from comma to point
     */
    private String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    private void addRegionSelectionListener(MFXComboBox<Region> region, MFXComboBox<Country> countries) {
        region.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                countries.clearSelection();
                ObservableList<Country> regionCountries= FXCollections.observableArrayList(newValue.getCountries());
                countries.setItems(regionCountries);
                countries.selectItem(regionCountries.get(0));

            }
        });
    }

    private void addCountrySelectionListener(MFXComboBox<Country> country, MFXComboBox<Team> teams) {
        country.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                teams.clearSelection();
                ObservableList<Team> countryTeams = FXCollections.observableArrayList(newValue.getTeams());
                teams.setItems(countryTeams);
                teams.selectItem(countryTeams.get(0));
            }
        });
    }

    //TODO create a separate component for the seleted team tah will be added to the scrollPane , with an remove button
}






