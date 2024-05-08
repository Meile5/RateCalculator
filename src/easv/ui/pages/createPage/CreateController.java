package easv.ui.pages.createPage;
import easv.Utility.EmployeeValidation;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
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
import java.util.ResourceBundle;



public class CreateController implements Initializable {

    @FXML
    private Parent createPage;
    @FXML
    private MFXTextField nameTF, salaryTF, workingHoursTF, annualAmountTF, utilPercentageTF, multiplierTF;
    @FXML
    private MFXComboBox countryCB, teamCB, currencyCB, overOrResourceCB;
    @FXML
    private ImageView clearIMG, employeeIMG;


    @FXML
    private HBox inputsParent;
    @FXML
    private MFXProgressSpinner operationSpinner;
    @FXML
    private Label spinnerLB;

    private ObservableList<Country> countries;
    private ObservableList<Team> teams;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            populateComboBoxes();
            addListenersToInputs();
            listenerForEmptyFieldsAfterSaving();
            addTooltips();
            disableSpinner();
    }

    @FXML
    private void saveEmployee() throws RateException {
        parseCountriesAndTeamsToValidator();

        if(EmployeeValidation.areNamesValid(nameTF, countryCB,teamCB) &&
           EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF) &&
           EmployeeValidation.arePercentagesValid(utilPercentageTF, multiplierTF) &&
           EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB))
        {
            enableSpinner();
            String name = nameTF.getText().trim();
            EmployeeType employeeType = EmployeeType.valueOf(overOrResourceCB.getText().trim());

            Country country = getSelectedCountry();

            Team team = getSelectedTeam();

            Currency currency = Currency.valueOf(currencyCB.getText().trim());
            BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText().trim()));
            BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText().trim()));
            BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText().trim()));
            BigDecimal utilizationPercentage = new BigDecimal(convertToDecimalPoint(utilPercentageTF.getText().trim()));
            BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText().trim()));
            LocalDateTime savedDate = LocalDateTime.now();
            Employee employee = new Employee(name, country, team, employeeType, currency);
            Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate,true);
            saveEmployeeOperation(employee, configuration);
        }
    }

    private void saveEmployeeOperation(Employee employee, Configuration configuration) {
        saveEmployee = new Service<Void>(){
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(200);
                        model.addEmployee(employee, configuration);
                        disableFields();
                        return null;
                    }
                };
            }
        };

        saveEmployee.setOnSucceeded(event -> {
            showOperationStatus("Operation Successful!", Duration.seconds(1));
            enableFields();
            clearFields();
        });

        saveEmployee.setOnFailed(event ->
                showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5)));
                enableFields();

        saveEmployee.restart();
    }

    private void showOperationStatus(String message, Duration duration) {
        spinnerLB.setText(message);

        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> Platform.runLater(() -> disableSpinner()));
        delay.play();
    }

    private Team getSelectedTeam() {
        Team team = null;
        if(teamCB.getSelectedItem() == null){
            team = new Team(teamCB.getText().trim());
            teams.add(team);
            teamCB.setItems(teams);
        } else {
            team = (Team) teamCB.getSelectedItem();
        }
        return team;
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
            countries = model.getCountiesValues();
            teams = FXCollections.observableArrayList(model.getTeams().values());
            ObservableList<String> currencies = FXCollections.observableArrayList("USD", "EUR");

            ObservableList<String> overOrResource = FXCollections.observableArrayList("Overhead", "Resource");

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

    private void addTooltips(){
        EmployeeValidation.addNameToolTip(nameTF);
        EmployeeValidation.addCountryToolTip(countryCB);
        EmployeeValidation.addTeamToolTip(teamCB);
        EmployeeValidation.addOverOrResourceToolTip(overOrResourceCB);
        EmployeeValidation.addCurrencyToolTip(currencyCB);
        EmployeeValidation.addValueToolTip(salaryTF, workingHoursTF, annualAmountTF);
        EmployeeValidation.addPercentageToolTip(utilPercentageTF, multiplierTF);
    }

    private void parseCountriesAndTeamsToValidator(){
        EmployeeValidation.getCountries(model.getValidCountries());
        EmployeeValidation.getTeams(model.getTeams());
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

}
