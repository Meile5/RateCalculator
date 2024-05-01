package easv.ui.pages.createPage;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.ModelFactory;
import easv.ui.pages.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;



public class CreateController implements Initializable {

    @FXML
    private Parent createPage;
    @FXML
    private MFXTextField nameTF, salaryTF, workingHoursTF, annualAmountTF, utilPercentageTF, multiplierTF;
    @FXML
    private MFXComboBox countryCB, teamCB, currencyCB, overOrResourceCB;
    @FXML
    private IModel model;
    @FXML
    private SVGPath clearSVG;


    // if you decide to use the model like i did, trough the dependency injection , than i made this posibile
    // you just need to initialize it in the constructor.
    public CreateController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        //this.model=model;
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
        try {
            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            clickClearHandler();
            toTest();
        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.CONNECTION_FAILED.getValue());
        }

    }



    @FXML
    private void saveEmployee(){
        String name = nameTF.getText();
        BigDecimal annualSalary = new BigDecimal(salaryTF.getText());
        BigDecimal fixedAnnualAmount = new BigDecimal(annualAmountTF.getText());
        BigDecimal overheadMultiplier = new BigDecimal(multiplierTF.getText());
        BigDecimal utilizationPercentage = new BigDecimal(utilPercentageTF.getText());
        BigDecimal workingHours = new BigDecimal(workingHoursTF.getText());
        EmployeeType employeeType = EmployeeType.valueOf(overOrResourceCB.getText().toUpperCase());
        System.out.println(employeeType);
        Country country = new Country(countryCB.getText());
        Team team = new Team(teamCB.getText());
        Currency currency = Currency.valueOf(currencyCB.getText());
        Employee employee = new Employee(name, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, country, team, employeeType, currency);
        model.addEmployee(employee);
        clearFields();
    }

    @FXML
    private void clickClearHandler(){
        Platform.runLater(() -> {clearSVG.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            clearFields();
        });});
    }



    // a loop can be used here , you can loop over the childrens and apply the clear, otherwise for each new field that will be added,
    // need to modify the method
    @FXML
    private void clearFields() {
        nameTF.clear();
        annualAmountTF.clear();
        multiplierTF.clear();
        salaryTF.clear();
        utilPercentageTF.clear();
        workingHoursTF.clear();
        countryCB.clear();
        teamCB.clear();
        currencyCB.clear();
        overOrResourceCB.clear();
    }

    public void toTest(){
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("Denmark");

        ObservableList<String> teams = FXCollections.observableArrayList();
        teams.add("TEST");

        ObservableList<String> currencies = FXCollections.observableArrayList();
        currencies.add("$");

        ObservableList<String> overOrResource = FXCollections.observableArrayList();
        overOrResource.add("Overhead");

        countryCB.setItems(countries);
        teamCB.setItems(teams);
        currencyCB.setItems(currencies);
        overOrResourceCB.setItems(overOrResource);
    }

    public Parent getCreatePage() {
        return createPage;
    }

   /* private void click (){
        createPage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println(model.returnEmployees());
            System.out.println(model);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ModelFactory.createModel();
        Platform.runLater(() -> {click();});

    }*/

}
