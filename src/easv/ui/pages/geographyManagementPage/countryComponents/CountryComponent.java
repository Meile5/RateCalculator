package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.be.Country;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
    private StackPane pane;

    public CountryComponent(IModel model, StackPane pane, Country country, DeleteCountryController deleteCountryController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryComponent.fxml"));
        loader.setController(this);
        this.model= model;
        this.pane = pane;
        try {
            countryInfoComponent=loader.load();
        } catch (IOException e) {
            e.printStackTrace();
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
