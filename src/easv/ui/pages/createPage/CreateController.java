package easv.ui.pages.createPage;
import easv.ui.ModelFactory;
import easv.ui.pages.IModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateController implements Initializable {
    @FXML
    private Parent createPage;
    private IModel model;


    public CreateController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        try {
            createPage=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getCreatePage() {
        return createPage;
    }
    private void click (){
        createPage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println(model.returnEmployees());
            System.out.println(model);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = ModelFactory.createModel(ModelFactory.modelType.ModelOther);
        Platform.runLater(() -> {click();});

    }
}
