package easv.ui.components.map.map.popUpInfo;

import easv.Utility.WindowsManagement;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.IModel;
import javafx.application.Platform;
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

public class CountryInfoContainer implements Initializable {
    @FXML
    private VBox countryPopUp;
    @FXML
    private HBox closeButton;
    private IModel model;
    private StackPane parent;
    public CountryInfoContainer(IModel model, StackPane parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryInfo.fxml"));
        loader.setController(this);
        this.model= model;
        this.parent=parent;
        try {
           countryPopUp= loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("I am initialized");
      closeWindow();

    }
    public VBox getRoot() {
        return countryPopUp;
    }

    private void  closeWindow(){
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
            WindowsManagement.closeStackPane(this.parent);
            System.out.println("aloo");
        });
    }
}
