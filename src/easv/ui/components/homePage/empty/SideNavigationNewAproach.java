package easv.ui.components.homePage.empty;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SideNavigationNewAproach implements Initializable {
    @FXML
    private HBox sideNavigationContainer;
    private boolean isDisplayed;
    @FXML
    private HBox distribution;
    @FXML
    private VBox iconsContainer;

    public SideNavigationNewAproach() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewAproach.fxml"));
        loader.setController(this);
        try {
            sideNavigationContainer = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HBox getRoot() {
        return sideNavigationContainer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
            addEnterListener(sideNavigationContainer,iconsContainer);
            addExitListener(sideNavigationContainer,iconsContainer);
    }

    private void addEnterListener( HBox navigationParent, VBox iconsContainer) {

        iconsContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            if (isDisplayed) {
                return;
            }
            Timeline timeline = new Timeline();
            KeyValue kvContainer = new KeyValue(sideNavigationContainer.prefWidthProperty(),sideNavigationContainer.getWidth()+200);
            KeyFrame kfContainer = new KeyFrame(Duration.millis(500), kvContainer);
            KeyValue kvIcons = new KeyValue(iconsContainer.prefWidthProperty(),iconsContainer.getWidth()+200);
            KeyFrame kfIcons = new KeyFrame(Duration.millis(500),kvIcons);
            iconsContainer.getChildren().stream().filter(elem-> elem instanceof HBox).forEach(elem->{
                KeyValue kvHbox = new KeyValue(((HBox)elem).prefWidthProperty(), ((HBox)elem).getWidth() + 200);
                KeyFrame kfHbox = new KeyFrame(Duration.millis(500), kvHbox);
                timeline.getKeyFrames().add(kfHbox);
            });
            timeline.getKeyFrames().addAll(kfContainer,kfIcons);

            timeline.setOnFinished((e)->{
                List<HBox>  controls = iconsContainer.getChildren().stream().filter(elem-> elem instanceof HBox).map(elem->(HBox)elem).toList();
                int count = 0;
                for (NavigationLabels navigationLabel: NavigationLabels.values()){
                    if(count>controls.size()){
                        break;
                    }
                    SVGPath svgPath = new SVGPath();
                    svgPath.setContent("M97.64,44.1,64.72,11.18a8.06,8.06,0,1,0-11.4,11.39L72.78,42H8.06a8.06,8.06,0,0,0,0,16.12H72.6L53.32,77.43a8.06,8.06,0,0,0,11.4,11.39L97.64,55.9A8,8,0,0,0,100,50.2a1.27,1.27,0,0,0,0-.2,1.41,1.41,0,0,0,0-.2A8.07,8.07,0,0,0,97.64,44.1Z");
                    svgPath.getStyleClass().add("navArrow");
                    svgPath.setScaleX(0.2);
                    svgPath.setScaleY(0.2);
                    Label label = new Label();
                    label.setText(navigationLabel.value);
                    label.getStyleClass().add("navText");
                    label.setPrefWidth(100);
                    label.setMinWidth(100);
                    HBox hbox = controls.get(count);
                    hbox.getChildren().addAll(label,svgPath);
                    hbox.setSpacing(5); // Set the space between the children to 20 pixels
                    count++;
                }
            });

            timeline.play();
            isDisplayed = true;
        });
    }

    private void addExitListener(HBox navigationParent, VBox iconsContainer){
        iconsContainer.addEventHandler(MouseEvent.MOUSE_EXITED,event -> {
            Timeline timeline = new Timeline();
            KeyValue kvContainer = new KeyValue(sideNavigationContainer.prefWidthProperty(),sideNavigationContainer.getWidth()-200);
            KeyFrame kfContainer = new KeyFrame(Duration.millis(500), kvContainer);
            KeyValue kvIcons = new KeyValue(iconsContainer.prefWidthProperty(),iconsContainer.getWidth()-200);
            KeyFrame kfIcons = new KeyFrame(Duration.millis(500),kvIcons);
            iconsContainer.getChildren().stream().filter(elem-> elem instanceof HBox).forEach(elem->{
                KeyValue kvHbox = new KeyValue(((HBox)elem).prefWidthProperty(), ((HBox)elem).getWidth()-200);
                KeyFrame kfHbox = new KeyFrame(Duration.millis(500), kvHbox);
                timeline.getKeyFrames().add(kfHbox);
            });
            timeline.getKeyFrames().addAll(kfContainer,kfIcons);
            timeline.play();
            isDisplayed=false;
        });
    }



    public enum NavigationLabels{
      DISTRIBUTION("Distribution"),
      CREATE("Create"),
      EMPLOYEES("Employees"),
      MODELING("Modeling"),
      PROFILE("Profile");
   private final String  value;
      NavigationLabels(String value) {
      this.value=value;
      }
  }
}
