package easv.ui.components.homePage.sideNavigation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SideNavigation implements Initializable {
    @FXML
    private HBox sideNavigationContainer;
    @FXML
    private VBox navigationIcons;
    @FXML
    private VBox navigationLabels;
    private boolean isDisplayed=false;
    @FXML
    private Button test;

    public SideNavigation() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SideNavigation.fxml"));
        loader.setController(this);
        try {
            sideNavigationContainer = loader.load();
            sideNavigationContainer.setAlignment(Pos.CENTER_LEFT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            addEnterListener(navigationIcons, navigationLabels, sideNavigationContainer);
            addEnterListener(navigationIcons,navigationIcons);
            addExitListener(sideNavigationContainer, sideNavigationContainer, navigationLabels);
            System.out.println(navigationIcons.getChildren());
        });
        test.setOnAction((ev)->{
            System.out.println("Clicked");
        });

    }

    private void addEnterListener(VBox hbox,VBox vbox){
        hbox.addEventHandler(MouseEvent.MOUSE_ENTERED,event -> {
            System.out.println("alooo");
        });
    }

//    private void addEnterListener(VBox vBox1, VBox vBox2, HBox hbox) {
//        vBox1.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
//            System.out.println("over hover");
//            // rest of your code
//        });
//    }
//
//    private void addExitListener(HBox vBox2, HBox hbox, VBox vbox2) {
//        vBox2.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
//            System.out.println("executed");
//            // rest of your code
//        });
//    }

    private void addEnterListener(HBox hbox,VBox vbox){
        hbox.addEventHandler(MouseEvent.MOUSE_ENTERED,event -> {
            System.out.println("alooo");
        });
    }

    private void addEnterListener(VBox vBox1, VBox vBox2, HBox hbox) {
        vBox1.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            System.out.println("over hover");
            if (isDisplayed) {
                return;
            }
            vBox1.setMaxWidth(vBox1.getWidth());
            Timeline timeline = new Timeline();
            KeyValue kvHbox = new KeyValue(hbox.prefWidthProperty(), hbox.getWidth() + 120);
            KeyFrame kfHbox = new KeyFrame(Duration.millis(500), kvHbox);
            timeline.getKeyFrames().add(kfHbox);
            KeyValue kvVbox2 = new KeyValue(vBox2.prefWidthProperty(), vBox2.getWidth() + 100);
            KeyFrame kfVbox2 = new KeyFrame(Duration.millis(500), kvVbox2);
            timeline.getKeyFrames().add(kfVbox2);
            timeline.play();
            vBox2.setVisible(true);
            isDisplayed = true;
        });
    }

    private void addExitListener(HBox vBox2, HBox hbox, VBox vbox2) {
        vBox2.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            System.out.println("exuted");
            if (!isDisplayed) {
                return;
            }
            Timeline timeline = new Timeline();
            KeyValue kvHbox = new KeyValue(hbox.prefWidthProperty(), hbox.getWidth() - 120);
            KeyFrame kfHbox = new KeyFrame(Duration.millis(500), kvHbox);
            timeline.getKeyFrames().add(kfHbox);
            KeyValue kvVbox2 = new KeyValue(vBox2.prefWidthProperty(), vBox2.getWidth() - 100);
            KeyFrame kfVbox2 = new KeyFrame(Duration.millis(500), kvVbox2);
            timeline.getKeyFrames().add(kfVbox2);
            timeline.play();
            vbox2.setVisible(false);
            isDisplayed = false;
        });
    }

    public HBox getRoot() {
        return sideNavigationContainer;
    }


}
