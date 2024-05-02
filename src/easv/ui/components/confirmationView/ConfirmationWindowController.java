package easv.ui.components.confirmationView;

import easv.exception.RateException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class ConfirmationWindowController {

    @FXML
    private Label confirmationTitle;
    @FXML
    private Label entityTitle;
    @FXML
    private Label errorMessage;
    @FXML
    private VBox confirmationWindow;
    private  StackPane firstLayout;
    private OperationHandler operationHandler;

    public ConfirmationWindowController(StackPane firstLayout, OperationHandler operationHandler) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Confirmation.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.operationHandler=operationHandler;
        try {
            confirmationWindow = loader.load();

        } catch (IOException e) {

        }
    }
    public VBox getRoot() {
        return confirmationWindow;
    }

    public Label getConfirmationTitle() {
        return confirmationTitle;
    }

    public void setConfirmationTitle(String confirmationTitle) {
        this.confirmationTitle.setText(confirmationTitle);
    }

    public Label getEntityTitle() {
        return entityTitle;
    }

    public void setEntityTitle(String entityTitle) {
        this.entityTitle.setText(entityTitle);
    }

    public void setErrorMessage(String message){
        this.errorMessage.setText(message);
    }
    @FXML
    private void cancelOperation(ActionEvent event) {
        firstLayout.getChildren().clear();
        firstLayout.setDisable(true);
        firstLayout.setVisible(false);;
    }

    @FXML
    private void confirmOperation(ActionEvent event) throws RateException {
        operationHandler.performOperation();

    }

}
