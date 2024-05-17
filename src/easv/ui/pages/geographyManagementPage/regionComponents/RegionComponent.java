package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegionComponent implements Initializable {

    @FXML
    private HBox regionInfoComponent;
    @FXML
    private Label nameLB, countryLB, teamLB;
    @FXML
    private VBox editButton, deleteContainer;

    private IModel model;

    public RegionComponent(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegionComponent.fxml"));
        loader.setController(this);
        this.model= model;
        try {
            regionInfoComponent =loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public HBox getRoot() {
        return regionInfoComponent;
    }
}
