package easv.ui.components.editPage;
import easv.Utility.EmployeeValidation;
import easv.Utility.InputValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditController implements Initializable {
    @FXML
    private VBox editRoot;
     @FXML
     private HBox closeButton;

    private IModel model;
    @FXML
    private MFXTextField utilPercentageTF,multiplierTF,markup,grossMargin;
    @FXML
    private MFXTextField salaryTF, workingHoursTF,annualAmountTF;
    @FXML
    private MFXTextField nameInput;
    @FXML
    private MFXComboBox<Country> countryCB;

    private StackPane firstLayout;


    public EditController(IModel model,StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
        loader.setController(this);
        this.model=model;
        this.firstLayout= firstLayout;
        try{
            editRoot= loader.load();
        }catch (IOException e){
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
        }
    }

   private void addCloseButtonAction(){
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED,event->
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
    }


/**initialize  percentage inputs validation  listeners */
private void initializePercentageInputValidationListeners(){
    EmployeeValidation.addUtilizationListener(utilPercentageTF);
    EmployeeValidation.addUtilizationListener(multiplierTF);
    EmployeeValidation.addUtilizationListener(markup);
    EmployeeValidation.addUtilizationListener(grossMargin);
}

/**initialize digits inputs validation listeners*/
private void initializeDigitsValidationListaners(){
    EmployeeValidation.addInputDigitsListeners(salaryTF);
    EmployeeValidation.addInputDigitsListeners(workingHoursTF);
    EmployeeValidation.addInputDigitsListeners(annualAmountTF);
}
/**initialize letters inputs validation listeners */
private void initializeLettersValidationListeners(){
    EmployeeValidation.addLettersOnlyInputListener(nameInput);
    EmployeeValidation.addLettersOnlyInputListener(countryCB);
}

}

