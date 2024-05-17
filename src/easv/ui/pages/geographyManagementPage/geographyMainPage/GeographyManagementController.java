package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeographyManagementController implements Initializable {
    @FXML
    private Parent createPage;

    private IModel model;
    private StackPane pane;

    public GeographyManagementController(IModel model, StackPane pane) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeographyManagementPage.fxml"));
        loader.setController(this);
        this.model=model;
        this.pane = pane;
        //this.firstLayout= firstLayout;
        try {
            createPage=loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Parent getCreatePage() {
        return createPage;
    }
}
