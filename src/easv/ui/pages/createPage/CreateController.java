package easv.ui.pages.createPage;
import easv.ui.ModelFactory;
import easv.ui.pages.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;


import java.io.IOException;
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
    }

    @FXML
    private void saveEmployee(){

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

    public Parent getCreatePage() {
        return createPage;
    }

}
