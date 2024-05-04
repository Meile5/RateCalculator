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
    private IModel model;
    @FXML
    private HBox inputsParent;


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
        Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate,true);
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

    @FXML
    private void clearFields() {
        for (int i = 0; i < vBox1.getChildren().size(); i++) {
            if (vBox1.getChildren().get(i) instanceof MFXTextField) {
                MFXTextField textField = (MFXTextField) vBox1.getChildren().get(i);
                textField.clear();
            }
            if(vBox1.getChildren().get(i) instanceof MFXComboBox){
                MFXComboBox comboBox = (MFXComboBox) vBox1.getChildren().get(i);
                comboBox.clearSelection();
                comboBox.clear();
            }
        }

        for (int i = 0; i < vBox2.getChildren().size(); i++) {
            if (vBox2.getChildren().get(i) instanceof MFXTextField) {
                MFXTextField textField = (MFXTextField) vBox2.getChildren().get(i);
                textField.clear();
            }
            if(vBox2.getChildren().get(i) instanceof MFXComboBox){
                MFXComboBox comboBox = (MFXComboBox) vBox2.getChildren().get(i);
                comboBox.clearSelection();
                comboBox.clear();
            }
        }

        for (int i = 0; i < vBox3.getChildren().size(); i++) {
            if (vBox3.getChildren().get(i) instanceof MFXTextField) {
                MFXTextField textField = (MFXTextField) vBox3.getChildren().get(i);
                textField.clear();
            }
            if(vBox3.getChildren().get(i) instanceof MFXComboBox){
                MFXComboBox comboBox = (MFXComboBox) vBox3.getChildren().get(i);
                comboBox.clearSelection();
                comboBox.clear();
            }
        }
    }

    //Todo  the bellow method is it how i imagined that needs to be implemented, when i told you about
    //it is much shorter and simpler, if you want you can use it,

    private void clearFields2(){
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
