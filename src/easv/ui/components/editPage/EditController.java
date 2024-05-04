package easv.ui.components.editPage;
import easv.Utility.InputValidation;
import easv.Utility.WindowsManagement;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
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

    private StackPane firstLayout;


    public EditController(IModel model,StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
        loader.setController(this);
        this.model=model;
        this.firstLayout= firstLayout;
        try{
            editRoot= loader.load();
        }catch (IOException e){
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
        initializeInputValidationListeners();
    }


/**initialize inputs validation  listeners*/
private void initializeInputValidationListeners(){
    InputValidation.addUtilizationListener(utilPercentageTF);
    InputValidation.addUtilizationListener(multiplierTF);
    InputValidation.addUtilizationListener(markup);
    InputValidation.addUtilizationListener(grossMargin);
}

}
