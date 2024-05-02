package easv.ui.pages.profilePage;

import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ProfilePageController {
    @FXML
    private Parent profilePage;
    private IModel model;

    public ProfilePageController(IModel imodel) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
        loader.setController(this);
        this.model=model;
        try {
            profilePage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getProfilePage() {
        return profilePage;
    }
}
