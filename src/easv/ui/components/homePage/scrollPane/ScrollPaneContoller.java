package easv.ui.components.homePage.scrollPane;

import easv.ui.components.homePage.CallBackFactory.CallBackFactory;
import easv.ui.components.homePage.NavigationFactory.NavigationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class ScrollPaneContoller implements Initializable {
    @FXML
    private VBox iconsContainer;
    @FXML
    private ScrollPane sideNavigationContainer;
    private boolean isExpanded;
    private double expandedWidth=300;
    private double originalWidth = 90;



    public ScrollPaneContoller() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewAproach.fxml"));
        loader.setController(this);
        try {
            sideNavigationContainer =loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateNavigation(this.iconsContainer);

    }

    private void populateNavigation(VBox vBox) {
        HBox distribution = NavigationFactory.getNavigationComponent(NavigationFactory.Navigation.DISTRIBUTION, CallBackFactory.createCallBack(CallBackFactory.PageTo.DISTRIBUTION));
        HBox create = NavigationFactory.getNavigationComponent(NavigationFactory.Navigation.CREATE, CallBackFactory.createCallBack(CallBackFactory.PageTo.CREATE));
        HBox employees = NavigationFactory.getNavigationComponent(NavigationFactory.Navigation.EMPLOYEES, CallBackFactory.createCallBack(CallBackFactory.PageTo.EMPLOYEES));
        HBox modeling = NavigationFactory.getNavigationComponent(NavigationFactory.Navigation.MODELING, CallBackFactory.createCallBack(CallBackFactory.PageTo.MODELING));
        HBox profile = NavigationFactory.getNavigationComponent(NavigationFactory.Navigation.PROFILE, CallBackFactory.createCallBack(CallBackFactory.PageTo.PROFILE));
        vBox.getChildren().add(1, distribution);
        vBox.getChildren().add(2, create);
        vBox.getChildren().add(3, employees);
        vBox.getChildren().add(4, modeling);
        vBox.getChildren().add(8, profile);
        addOnEnterListener();
        addOnExitListener();
    }
    public ScrollPane getRoot() {
        return sideNavigationContainer;
    }


    private void addOnEnterListener(){
       sideNavigationContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, event->{
           if(isExpanded){
               return;
           }
           Timeline timeline  = new Timeline();
           KeyValue keyValue= new KeyValue(sideNavigationContainer.prefWidthProperty(),expandedWidth);
           KeyFrame keyFrame = new KeyFrame(Duration.millis(500),keyValue);
           timeline.getKeyFrames().add(keyFrame);
           timeline.play();
           isExpanded=true;
       });
    }
    private void addOnExitListener(){
        sideNavigationContainer.addEventHandler(MouseEvent.MOUSE_EXITED, event->{
            Timeline timeline  = new Timeline();
            KeyValue keyValue= new KeyValue(sideNavigationContainer.prefWidthProperty(),originalWidth);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(500),keyValue);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            isExpanded=false;
        });
    }


}
