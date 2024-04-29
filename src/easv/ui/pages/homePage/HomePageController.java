package easv.ui.pages.homePage;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.CallBackFactory.CallBackFactory;
import easv.ui.components.homePage.NavigationFactory.NavigationFactory;
import easv.ui.components.homePage.map.WorldMap;
import easv.ui.components.homePage.scrollPane.ScrollPaneContoller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable, PageManager {
    private Parent root;
    @FXML
    private StackPane menu,firstLayout;
    @FXML
    private VBox pageContainer;
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
       // SideNavigationNewAproach sideNavigation = new SideNavigationNewAproach();
        ScrollPaneContoller sde = new ScrollPaneContoller();
        Platform.runLater(()->{ initializeSideMenu(menu,sde.getRoot());});
        Platform.runLater(()->{sde.getRoot().getWidth();});
        initializeWorldMap();
        CallBackFactory.setPageHolder(this);
    }

    private void initializeSideMenu(StackPane stackPane, ScrollPane hBox){
        StackPane.setAlignment(hBox,Pos.CENTER_LEFT);
        stackPane.getChildren().add(hBox);
    }
    private void initializeWorldMap(){
        WorldMap worldMap = new WorldMap(firstLayout);
        this.pageContainer.getChildren().add(worldMap.getRoot());
    }

    @Override
    public void changePage(Parent page) {

                this.pageContainer.getChildren().clear();
                this.pageContainer.getChildren().add(page);
            }
}
