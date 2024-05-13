package easv.ui.pages.createPage;
import easv.Utility.EmployeeValidation;
import easv.Utility.WindowsManagement;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;



public class CreateController implements Initializable {

    @FXML
    private Parent createPage;
    @FXML
    private MFXTextField nameTF, salaryTF, workingHoursTF, annualAmountTF, utilPercentageTF, multiplierTF, dayWorkingHours;
    @FXML
    private MFXComboBox countryCB, teamCB, regionCB, currencyCB, overOrResourceCB;
    @FXML
    private ImageView clearIMG, employeeIMG;
    @FXML
    private HBox inputsParent;
    @FXML
    private MFXProgressSpinner operationSpinner;
    @FXML
    private Label spinnerLB;
    @FXML
    private Button addTeamBT, removeTeamBT;
    @FXML
    private ListView teamsListView;

    private ObservableList<Country> countries;
    private ObservableList<Team> teams;
    private ObservableList<Region> regions;
    private List<Team> teamsList = new ArrayList<>();
    private List<Integer> teamsUtilizationList = new ArrayList<>();
    private IModel model;
    private Service<Void> saveEmployee;
    private StackPane firstLayout;


    public CreateController(IModel model, StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        this.model=model;
        this.firstLayout= firstLayout;
        try {
            createPage=loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    //TOdo Nelsson i added the stack pane that blocks the ui while saving, you can have a look , if is ok ,

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            populateComboBoxes();
            addListenersToInputs();
            addTeamListener();
            removeTeamListener();
            listenerForEmptyFieldsAfterSaving();
            addTooltips();
            disableSpinner();
            addRegionSelectionListener(regionCB, countryCB);
            addCountrySelectionListener(countryCB, teamCB);
    }

    @FXML
    private void saveEmployee() throws RateException {
        if(EmployeeValidation.areNamesValid(nameTF, countryCB,teamCB) &&
           EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF) &&
           EmployeeValidation.arePercentagesValid(utilPercentageTF, multiplierTF) &&
           EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB))
        {
            enableSpinner();
            firstLayout.getChildren().add(operationSpinner);
            WindowsManagement.showStackPane(firstLayout);

            String name = nameTF.getText().trim();
            EmployeeType employeeType = (EmployeeType) overOrResourceCB.getSelectedItem();
            List<Team> teams = getSelectedTeams();
            Currency currency = Currency.valueOf(currencyCB.getText().trim());
            BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText().trim()));
            BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText().trim()));
            BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText().trim()));
            BigDecimal utilizationPercentage = new BigDecimal(convertToDecimalPoint(utilPercentageTF.getText().trim()));
            BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText().trim()));
            LocalDateTime savedDate = LocalDateTime.now();
            boolean isActive = true;
            int dailyWorkingHours = Integer.parseInt(dayWorkingHours.getText());

            Employee employee = new Employee(name, employeeType, currency);
            Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate, isActive);
            employee.setActiveConfiguration(configuration);

            BigDecimal dayRate = model.getComputedDayRate(employee);
            BigDecimal hourlyRate = model.getComputedHourlyRate(employee, dailyWorkingHours);
            configuration.setDayRate(dayRate);
            configuration.setHourlyRate(hourlyRate);

            saveEmployeeOperation(employee, configuration, teams);
        }
    }

    @FXML
    private void clearInputs(){
        clearFields();
    }

    private void saveEmployeeOperation(Employee employee, Configuration configuration, List<Team> teams) {
        saveEmployee = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        // Thread.sleep(200);
                        model.addEmployee(employee, configuration, teams);
                        return null;
                    }
                };
            }
        };

        saveEmployee.setOnSucceeded(event -> {
            showOperationStatus("Operation Successful!", Duration.seconds(1));
            WindowsManagement.closeStackPane(firstLayout);
            closeWindowSpinner(firstLayout);

        });

        saveEmployee.setOnFailed(event -> {
                showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
                WindowsManagement.closeStackPane(firstLayout);
        });
        saveEmployee.restart();
    }

    private void showOperationStatus(String message, Duration duration) {
        spinnerLB.setText(message);
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> Platform.runLater(this::disableSpinner));
        delay.play();
    }

    private List<Team> getSelectedTeams() {
        return teamsList;
    }

    private void addTeamListener(){
        addTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(teamCB.getSelectedItem() != null){
                teamsList.add((Team) teamCB.getSelectedItem());
                teamsUtilizationList.add(Integer.valueOf(utilPercentageTF.getText()));
                String teamWithUtilization = ((Team) teamCB.getSelectedItem()).getTeamName() + ", " + utilPercentageTF.getText() + "%";
                teamsListView.getItems().add(teamWithUtilization);
            }
        });
    }

    private void removeTeamListener(){
        removeTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
                teamsList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsUtilizationList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
                System.out.println(teamsList);
                System.out.println(teamsUtilizationList);
        });
    }

    private Country getSelectedCountry() {
        Country country = null;
        if(countryCB.getSelectedItem() == null){
            country = new Country(countryCB.getText().trim());
            countries.add(country);
            countryCB.setItems(countries);
        } else {
            country = (Country) countryCB.getSelectedItem();
        }
        return country;
    }

    private void clearFields(){
         inputsParent.getChildren().forEach((child)->{
           if(child instanceof VBox){
               ((VBox) child).getChildren().forEach((input)->{
                   if(input instanceof  MFXTextField){
                       ((MFXTextField) input).clear();
                   }
                   if(input instanceof MFXComboBox<?>){
                       ((MFXComboBox<?>) input).clear();
                       ((MFXComboBox<?>) input).clearSelection();
                   }
               });
           }
         });
         teamCB.clear();
         teamCB.clearSelection();
         teamsListView.getItems().clear();
         teamsList.clear();
         teamsUtilizationList.clear();
    }

    private void disableFields(){
        inputsParent.getChildren().forEach((child)->{
            if(child instanceof VBox){
                ((VBox) child).getChildren().forEach((input)->{
                    if(input instanceof  MFXTextField){
                        ((MFXTextField) input).setEditable(false);
                    }
                    if(input instanceof MFXComboBox<?>){
                        ((MFXComboBox<?>) input).setSelectable(false);
                    }
                });
            }
        });
    }

    private void enableFields(){
        inputsParent.getChildren().forEach((child)->{
            if(child instanceof VBox){
                ((VBox) child).getChildren().forEach((input)->{
                    if(input instanceof  MFXTextField){
                        input.setDisable(false);
                    }
                    if(input instanceof MFXComboBox<?>){
                        input.setDisable(false);
                    }
                });
            }
        });
    }

    private void listenerForEmptyFieldsAfterSaving(){
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(nameTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(annualAmountTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(workingHoursTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(multiplierTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(utilPercentageTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(salaryTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(countryCB);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(currencyCB);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(teamCB);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(overOrResourceCB);
    }

    private void populateComboBoxes() {
            regions = model.getOperationalRegions();
            countries = model.getOperationalCountries();
            teams = model.getOperationalTeams();
            ObservableList<String> currencies = FXCollections.observableArrayList(Currency.EUR.name(), Currency.USD.name());
            ObservableList<EmployeeType> overOrResource = FXCollections.observableArrayList(EmployeeType.Overhead, EmployeeType.Resource);
            regionCB.setItems(regions);
            countryCB.setItems(countries);
            teamCB.setItems(teams);
            currencyCB.setItems(currencies);
            overOrResourceCB.setItems(overOrResource);
    }

    private void addListenersToInputs(){
        //Listeners for the percentages
        EmployeeValidation.addNonEmptyPercentageListener(utilPercentageTF);
        EmployeeValidation.addNonEmptyPercentageListener(multiplierTF);
        //
        EmployeeValidation.addInputDigitsListeners(salaryTF);
        EmployeeValidation.addInputDigitsListeners(workingHoursTF);
        EmployeeValidation.addInputDigitsListeners(annualAmountTF);
        //
        EmployeeValidation.addLettersOnlyInputListener(nameTF);
        EmployeeValidation.addLettersOnlyInputListener(countryCB);
        EmployeeValidation.addLettersOnlyInputListener(teamCB);
        EmployeeValidation.addLettersOnlyInputListener(overOrResourceCB);
        EmployeeValidation.addLettersOnlyInputListener(currencyCB);
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

    private void addTooltips(){
        EmployeeValidation.addNameToolTip(nameTF);
        EmployeeValidation.addCountryToolTip(countryCB);
        EmployeeValidation.addTeamToolTip(teamCB);
        EmployeeValidation.addOverOrResourceToolTip(overOrResourceCB);
        EmployeeValidation.addCurrencyToolTip(currencyCB);
        EmployeeValidation.addValueToolTip(salaryTF, workingHoursTF, annualAmountTF);
        EmployeeValidation.addPercentageToolTip(utilPercentageTF, multiplierTF);
    }

    private void enableSpinner() {
        spinnerLB.setText("Processing...");
        operationSpinner.setVisible(true);
        operationSpinner.setDisable(false);
    }

    private void disableSpinner() {
        operationSpinner.setVisible(false);
        operationSpinner.setDisable(true);
        spinnerLB.setText("");
    }

    //convert form comma decimal to point decimal
    private String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    public Parent getCreatePage() {
        return createPage;
    }


    private void closeWindowSpinner(StackPane stackPane){
        PauseTransition pauseTransition =  new PauseTransition(Duration.millis(2000));
        pauseTransition.setOnFinished((e)->{
            WindowsManagement.closeStackPane(firstLayout);
            clearFields();
        });
        pauseTransition.playFromStart();
    }
}
