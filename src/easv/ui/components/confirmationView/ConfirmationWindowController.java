package easv.ui.components.confirmationView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;



public class ConfirmationWindowController {

    @FXML
    private Label confirmationTitle;
    @FXML
    private Label entityTitle;
    @FXML
    private Label errorMessage;
    private final StackPane firstLayout;

    public ConfirmationWindowController( StackPane firstLayout) {
        this.firstLayout = firstLayout;

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
    private void confirmOperation(ActionEvent event) {

    }

}
