package easv.ui.components.homePage.navigation;
import easv.ui.components.homePage.CallBackFactory.CallBack;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {
    @FXML
    private HBox navComponent;
    @FXML
    private HBox navIcon;
    @FXML
    private Label navText;
    private CallBack callback;
    private SVGPath icon;
    private String iconText;

    public NavigationController(SVGPath icon, CallBack callback, String iconText) {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("Navigation.fxml"));
        loader.setController(this);
        this.callback= callback;
        this.icon = icon;
        this.iconText=iconText;
        try {
            navComponent= loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     navIcon.getChildren().add(icon);
     navText.setText(iconText);
     addOnClickListener();
    }
    public HBox getNavComponent() {
        return navComponent;
    }

    private void addOnClickListener(){
        navComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
            callback.call();
        });
    }
}
