package easv.ui.pages.editPage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class EditController {
    @FXML
    private Parent editPage;

    public EditController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Edit.fxml"));
        loader.setController(this);
        try {
            editPage=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getEditPage() {
        return editPage;
    }

}
