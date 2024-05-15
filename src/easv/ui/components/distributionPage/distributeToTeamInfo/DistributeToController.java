package easv.ui.components.distributionPage.distributeToTeamInfo;

import easv.be.Team;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DistributeToController implements Initializable {
    private HBox distributeToRoot;
    private IModel model;
    private Team teamToDisplay ;


    public DistributeToController(IModel model,Team teamToDisplay) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeToTeamInfo.fxml"));
        this.model= model;
        this.teamToDisplay= teamToDisplay;
        try {
            distributeToRoot=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialized the distribute to ");
    }

    public HBox getRoot() {
        return distributeToRoot;
    }
}
