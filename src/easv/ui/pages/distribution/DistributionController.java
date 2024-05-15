package easv.ui.pages.distribution;

import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToListCell;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
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
    private VBox distributeFromTeams;

    @FXML
    private ListView<Team> distributeToTeams;


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
        distributeToTeams.setCellFactory(listView -> new DistributeToListCell(model));
        distributeToTeams.setItems(model.getOperationalTeams());



    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeToController = new DistributeFromController(model, e,distributionMediator);
            distributeFromTeamsComponents.add(distributeToController.getRoot());
        });
        distributeFromTeams.getChildren().addAll(distributeFromTeamsComponents);
    }

    @Override
    public void setTheSelectedTeam(Team team ) {
        this.selectedTeamToDistributeFrom= team;
    }

}
