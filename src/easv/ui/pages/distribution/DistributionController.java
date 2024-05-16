package easv.ui.pages.distribution;

import easv.be.OverheadComputationPair;
import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToListCell;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class DistributionController implements Initializable, DistributionControllerInterface {
    @FXML
    private Parent distributionPage;
    @FXML
    private VBox distributeFromTeams;

    @FXML
    private ListView<Team> distributeToTeams;
    @FXML
    private MFXTextField percentageToDistribute;
    @FXML
    private BarChart<String, BigDecimal> distributeFromTeamBarChart;


    private IModel model;


    private ControllerMediator distributionMediator;

    private Team selectedTeamToDistributeFrom;

    private final Map<Integer, Double> insertedDistributionPercentageFromTeams = new HashMap<>();

    public DistributionController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model = model;
        this.distributionMediator = new ControllerMediator();
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
        distributeToTeams.setCellFactory(listView -> new DistributeToListCell(model, this));
        distributeToTeams.setItems(model.getOperationalTeams());
    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeToController = new DistributeFromController(model, e, distributionMediator);
            distributeFromTeamsComponents.add(distributeToController.getRoot());
        });
        distributeFromTeams.getChildren().addAll(distributeFromTeamsComponents);
    }

    @Override
    public void setTheSelectedTeam(Team team) {
        this.selectedTeamToDistributeFrom = team;
    }


    @Override
    public void showTheTeamFromBarchart() {
        this.distributeFromTeamBarChart.setVisible(true);
        this.distributeFromTeamBarChart.setDisable(false);
        this.distributeFromTeamBarChart.setTitle(selectedTeamToDistributeFrom.getTeamName());
        this.distributeFromTeamBarChart.setLegendVisible(false);
        this.distributeFromTeamBarChart.getXAxis().setLabel("Team");
        this.distributeFromTeamBarChart.getYAxis().setLabel("Overhead");
// set the barchart with the initial overhead
        populateFromTeamBarChartWithInitialOverhead();
    }


    /**
     * populate team barchart with the initial overhead
     */
    private void populateFromTeamBarChartWithInitialOverhead() {
        this.distributeFromTeamBarChart.getData().clear();

        // Add the team overhead to the chart as a new series
        if (selectedTeamToDistributeFrom.getActiveConfiguration() != null) {
            XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(selectedTeamToDistributeFrom.getTeam(), selectedTeamToDistributeFrom.getActiveConfiguration().getTeamDayRate()));
            this.distributeFromTeamBarChart.getData().add(series);
        }
        System.out.println(model.teamRegionsOverhead(selectedTeamToDistributeFrom.getId()));
        // Iterate over the regions overhead and add each as a new series
        if (!selectedTeamToDistributeFrom.getRegions().isEmpty()) {
            for (OverheadComputationPair<String, BigDecimal> regionOverhead : model.teamRegionsOverhead(selectedTeamToDistributeFrom.getId())) {
                XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>(regionOverhead.getKey(), regionOverhead.getValue()));
                this.distributeFromTeamBarChart.getData().add(series);
            }
        }
    }


    public void addDistributionPercentageToTeam(Integer teamId, Double percentageValue) {
        this.insertedDistributionPercentageFromTeams.put(teamId, percentageValue);
        System.out.println(teamId + " " + percentageValue + "add ");
    }

    public void removeDistributionPercentageToTeam(Integer teamId) {
        System.out.println(teamId + "remove");
        this.insertedDistributionPercentageFromTeams.remove(teamId);
    }


}
