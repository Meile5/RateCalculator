package easv.ui.pages.teamsPage;

import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class TeamsPageController {
    private IModel model;
    @FXML
    private Parent teamPage;
    public TeamsPageController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamsManagementPage.fxml"));
        loader.setController(this);
        this.model=model;
        try {
            teamPage = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public Parent getRoot(){
        return teamPage;
    }
}
