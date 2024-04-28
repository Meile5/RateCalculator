package easv.ui.pages.homePage;
import easv.ui.components.homePage.sideNavigation.SideNavigation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {
    private Parent root;
    @FXML
    private StackPane menu;
    public HomePageController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        loader.setController(this);
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Parent getRoot() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SideNavigation  sideNavigation = new SideNavigation();
        Platform.runLater(()->{ initializeSideMenu(menu,sideNavigation.getRoot());});
    }

    private void initializeSideMenu(StackPane stackPane, HBox hBox){
        StackPane.setAlignment(hBox,Pos.CENTER_LEFT);
        stackPane.getChildren().add(hBox);
    }
}
