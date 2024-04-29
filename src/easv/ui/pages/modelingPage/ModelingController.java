package easv.ui.pages.modelingPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ModelingController {
    @FXML
    private Parent modelingPage;

    public ModelingController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Modeling.fxml"));
        loader.setController(this);
        try {
            modelingPage =loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getModelingPage() {
        return modelingPage;
    }

}
