package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CountryComponent implements Initializable {

    @FXML
    private HBox countryInfoComponent;
    @FXML
    private Label nameLB, teamLB, employeeLB;
    @FXML
    private VBox addButton, editButton, deleteContainer;

    private IModel model;

    public CountryComponent(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryComponent.fxml"));
        loader.setController(this);
        this.model= model;
        try {
            countryInfoComponent=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public HBox getRoot() {
        return countryInfoComponent;
    }
}
