package easv.ui.pages.createPage;
import easv.be.*;
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
import java.util.ArrayList;
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

    public CreateController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        try {
            createPage=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ModelFactory.createModel(ModelFactory.modelType.ModelOther);
        clickClearHandler();

        toTest();
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

}
