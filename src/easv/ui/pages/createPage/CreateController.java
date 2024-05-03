package easv.ui.pages.createPage;
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
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


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
    private IModel model;
    @FXML
    private ImageView clearIMG, employeeIMG;


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
            clickClearHandler();
            populateComboBoxes();
    }



    @FXML
    private void saveEmployee() throws RateException {
        String name = nameTF.getText();
        EmployeeType employeeType = EmployeeType.valueOf(overOrResourceCB.getText());

        Country country = null;
        if(countryCB.getSelectedItem() == null){
            country = new Country(countryCB.getText());
        } else {
            country = (Country) countryCB.getSelectedItem();
        }

        Team team = null;
        if(teamCB.getSelectedItem() == null){
            team = new Team(teamCB.getText());
        } else {
            team = (Team) teamCB.getSelectedItem();
        }

        Currency currency = Currency.valueOf(currencyCB.getText());

        BigDecimal annualSalary = new BigDecimal(salaryTF.getText());
        BigDecimal fixedAnnualAmount = new BigDecimal(annualAmountTF.getText());
        BigDecimal overheadMultiplier = new BigDecimal(multiplierTF.getText());
        BigDecimal utilizationPercentage = new BigDecimal(utilPercentageTF.getText());
        BigDecimal workingHours = new BigDecimal(workingHoursTF.getText());
        LocalDateTime savedDate = LocalDateTime.now();

        Employee employee = new Employee(name, country, team, employeeType, currency);
        Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate);
        model.addEmployee(employee, configuration);
        clearFields();
    }

    @FXML
    private void clickClearHandler(){
        Platform.runLater(() -> {
            clearIMG.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
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
        countryCB.clearSelection();
        countryCB.clear();
        teamCB.clearSelection();
        teamCB.clear();
        currencyCB.clearSelection();
        overOrResourceCB.clearSelection();
    }

    public void populateComboBoxes() {
        ObservableList<Country> countries = FXCollections.observableArrayList(model.getCountries().values());
        ObservableList<Team> teams = FXCollections.observableArrayList(model.getTeams().values());
        ObservableList<String> currencies = FXCollections.observableArrayList("$", "€");
        ObservableList<String> overOrResource = FXCollections.observableArrayList("Overhead", "Resource");

        countryCB.setItems(countries);
        teamCB.setItems(teams);
        currencyCB.setItems(currencies);
        overOrResourceCB.setItems(overOrResource);
    }

    public Parent getCreatePage() {
        return createPage;
    }

}
