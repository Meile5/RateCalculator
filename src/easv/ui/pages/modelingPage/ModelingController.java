package easv.ui.pages.modelingPage;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ModelingController {
    @FXML
    private Parent modelingPage;
    private IModel model;
    public ModelingController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Modeling.fxml"));
        loader.setController(this);
        this.model= model;
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
