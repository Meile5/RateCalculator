package easv.ui.pages.createPage;
import easv.Utility.EmployeeValidation;
import easv.be.*;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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
    private VBox vBox1, vBox2, vBox3;
    @FXML
    private HBox inputsParent;

    private ObservableList<Country> countries;
    private ObservableList<Team> teams;
    private IModel model;

    public CreateController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        this.model=model;
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
            clickClearHandler();
            addListenersToInputs();
            addTooltips();
    }



    @FXML
    private void saveEmployee() throws RateException {
        parseCountriesAndTeamsToValidator();

        if(EmployeeValidation.areNamesValid(nameTF, countryCB,teamCB) &&
           EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF) &&
           EmployeeValidation.arePercentagesValid(utilPercentageTF, multiplierTF) &&
           EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB))
        {
            String name = nameTF.getText();
            EmployeeType employeeType = EmployeeType.valueOf(overOrResourceCB.getText());

            Country country = getSelectedCountry();

            Team team = getSelectedTeam();

            Currency currency = Currency.valueOf(currencyCB.getText());
            BigDecimal annualSalary = new BigDecimal(salaryTF.getText());
            BigDecimal fixedAnnualAmount = new BigDecimal(annualAmountTF.getText());
            BigDecimal overheadMultiplier = new BigDecimal(multiplierTF.getText());
            BigDecimal utilizationPercentage = new BigDecimal(utilPercentageTF.getText());
            BigDecimal workingHours = new BigDecimal(workingHoursTF.getText());
            LocalDateTime savedDate = LocalDateTime.now();
            Employee employee = new Employee(name, country, team, employeeType, currency);
            Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate,true);
            employee.setActiveConfiguration(configuration);
            model.addEmployee(employee, configuration);
            clearFields();
        }
    }

    private Team getSelectedTeam() {
        Team team = null;
        if(teamCB.getSelectedItem() == null){
            team = new Team(teamCB.getText());
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
            country = new Country(countryCB.getText());
            countries.add(country);
            countryCB.setItems(countries);
        } else {
            country = (Country) countryCB.getSelectedItem();
        }
        return country;
    }

    private void clickClearHandler(){
        Platform.runLater(() -> {
            clearIMG.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clearFields();
        });});
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
                   }
               });
           }
         });
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

    public Parent getCreatePage() {
        return createPage;
    }
}
