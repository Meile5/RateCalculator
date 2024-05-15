package easv.ui.pages.distribution;

import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DistributionController implements Initializable,DistributionControllerInterface {
    @FXML
    private Parent distributionPage;
    @FXML
    private VBox distributeToTeams;

    @FXML
    private VBox distributeFromTeams;


    private IModel model;

     private ControllerMediator distributionMediator;

    private Team selectedTeamToDistributeFrom;


    public DistributionController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model = model;
        this.distributionMediator= new ControllerMediator();
        this.distributionMediator.registerDistributionController(this);
        try {
            distributionPage = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Parent getDistributionPage() {
        return distributionPage;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateDistributeToTeams();
        populateDistributeFromTeams();
    }


    /**
     * add the teams in the  system  to the distribute to teams container
     */
    private void populateDistributeToTeams() {
        List<Parent> distributeToTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeToController distributeToController = new DistributeToController(model, e);
            distributeToTeamsComponents.add(distributeToController.getRoot());
        });
        distributeToTeams.getChildren().addAll(distributeToTeamsComponents);

    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeFromController = new DistributeFromController(model,e,distributionMediator);
            distributeFromComponents.add(distributeFromController.getRoot());
        });
        distributeFromTeams.getChildren().addAll(distributeFromComponents);
    }

    @Override
    public void setTheSelectedTeam(Team team ) {
        this.selectedTeamToDistributeFrom= team;
    }

}
