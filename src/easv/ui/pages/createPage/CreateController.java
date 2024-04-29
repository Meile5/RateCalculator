package easv.ui.pages.createPage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class CreateController {
    @FXML
    private Parent createPage;

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
}
