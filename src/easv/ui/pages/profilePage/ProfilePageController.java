package easv.ui.pages.profilePage;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ProfilePageController {
    @FXML
    private Parent profilePage;

    public ProfilePageController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
        loader.setController(this);
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
