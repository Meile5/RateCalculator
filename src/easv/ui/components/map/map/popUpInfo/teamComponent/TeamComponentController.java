package easv.ui.components.map.map.popUpInfo.teamComponent;

import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamComponentController  implements Initializable {
    @FXML
    private VBox teamComponent;
    @FXML
    private Label teamName;
    @FXML
    private PieChart teamChart;
    private IModel model;

    public TeamComponentController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamComponent.fxml"));
        loader.setController(this);
        this.model = model;
        try {
     loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialized");
    }

    public VBox getRoot() {
        return teamComponent;
    }
}
