package easv.ui.pages.distribution;

import easv.be.OverheadComputationPair;
import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToListCell;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class DistributionController implements Initializable, DistributionControllerInterface {
    @FXML
    private Parent distributionPage;
    @FXML
    private VBox distributeFromTeams, selectedToDistribute, selectedToDistributeTo;

    @FXML
    private ListView<Team> distributeToTeams;
    @FXML
    private MFXTextField percentageToDistribute;
    @FXML
    private BarChart<String, BigDecimal> distributeFromTeamBarChart;
    private IModel model;
    private ControllerMediator distributionMediator;
    private DistributeToMediator distributeToMediator;
    @FXML
    private Button simulateButton;
    @FXML
    private Circle circleValue;
    @FXML
    private Label totalOverheadInserted;

    private final static PseudoClass OVER_LIMIT = PseudoClass.getPseudoClass("errorLimit");

    public DistributionController(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model = model;
        this.distributionMediator = new ControllerMediator();
        this.distributionMediator.registerDistributionController(this);
        this.distributeToMediator = new DistributeToMediator();
        this.distributeToMediator.registerMainController(this);
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
        distributeToTeams.setCellFactory(listView -> new DistributeToListCell(model,distributionMediator,DistributionType.DISTRIBUTE_TO));
        distributeToTeams.setItems(model.getOperationalTeams());
        List<Parent> distributeToTeamsComponents = new ArrayList<>();
//        model.getOperationalTeams().forEach(e -> {
//            DistributeFromController distributeToController = new DistributeFromController(model, e, distributionMediator, DistributionType.DISTRIBUTE_TO);
//            distributeToTeamsComponents.add(distributeToController.getRoot());
//        });
//        distributeFromTeams.getChildren().addAll(distributeToTeamsComponents);

    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeToController = new DistributeFromController(model, e, distributionMediator, DistributionType.DISTRIBUTE_FROM);
            distributeFromTeamsComponents.add(distributeToController.getRoot());
        });
        distributeFromTeams.getChildren().addAll(distributeFromTeamsComponents);
    }


    @Override
    public void showTheTeamFromBarchart() {
        this.distributeFromTeamBarChart.setVisible(true);
        this.distributeFromTeamBarChart.setDisable(false);
        this.distributeFromTeamBarChart.setTitle(model.getDistributeFromTeam().getTeamName());
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
        if (model.getDistributeFromTeam().getActiveConfiguration() != null) {
            XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
            series.getData().add(new XYChart.Data<>(model.getDistributeFromTeam().getTeam(), model.getDistributeFromTeam().getActiveConfiguration().getTeamDayRate()));
            this.distributeFromTeamBarChart.getData().add(series);
        }
        // Iterate over the regions overhead and add each as a new series
        if (!model.getDistributeFromTeam().getRegions().isEmpty()) {
            for (OverheadComputationPair<String, BigDecimal> regionOverhead : model.teamRegionsOverhead(model.getDistributeFromTeam().getId())) {
                XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
                series.getData().add(new XYChart.Data<>(regionOverhead.getKey(), regionOverhead.getValue()));
                this.distributeFromTeamBarChart.getData().add(series);
            }
        }
    }


    private void addSimulateButtonListener() {
        this.simulateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Map<Team, String> validateInputs = model.validateInputs();

        });
    }


    /**
     * change circle stroke color based on the user input
     */
    private void updateCircleColor(Double coverPercentage) {
        System.out.println(coverPercentage);
        if (coverPercentage > 100) {
            circleValue.pseudoClassStateChanged(OVER_LIMIT, true);
            return;
        }

        if (coverPercentage < 100) {
            circleValue.pseudoClassStateChanged(OVER_LIMIT, false);
        }

        // Calculate circumference
        double circumference = 2 * Math.PI * this.circleValue.getRadius();
        double coveredLength = circumference * (coverPercentage / 100);
        double uncoveredLength = circumference - coveredLength;
        // Set the stroke dash array
        circleValue.getStrokeDashArray().addAll(coveredLength, uncoveredLength);
        // Adjust stroke dash offset to start from the right (0 degrees)
        circleValue.setStrokeDashOffset(-circumference / 4);
    }

    @Override
    public void updateTotalOverheadValue(Double overheadPercentage) {
        Double totalOverhead = 0.0;
        if (!totalOverheadInserted.getText().isEmpty()) {
            totalOverhead = Double.parseDouble(this.totalOverheadInserted.getText());
            totalOverheadInserted.setText(totalOverhead + overheadPercentage + "");
        } else {
            totalOverhead = overheadPercentage;
            totalOverheadInserted.setText(totalOverhead + "");
        }
        updateCircleColor(totalOverhead);
    }

    @Override
    public void removeOverheadPercentage(Double overheadValue) {
        Double totalOverhead = 0.0;
        if (!totalOverheadInserted.getText().isEmpty()) {
            totalOverhead = Double.parseDouble(this.totalOverheadInserted.getText());
            totalOverheadInserted.setText(totalOverhead - overheadValue + "");
        }
        updateCircleColor(totalOverhead);
    }


    /**
     * add team to distribute from
     */
    @Override
    public void addTeamToDistributeFrom(Team team) {
        if (!this.selectedToDistribute.getChildren().isEmpty()) {
            this.selectedToDistribute.getChildren().clear();
        }
        this.selectedToDistribute.getChildren().add(new DistributeFromController(model, team, distributionMediator, DistributionType.DISPLAY).getRoot());
    }

    /**
     * add team to distribute to into the distribute to list
     */
    @Override
    public void addDistributeToTeam(Team teamToDisplay) {
        DistributeToController selectedTeam = new DistributeToController(model, teamToDisplay, distributeToMediator);
        this.selectedToDistributeTo.getChildren().add(selectedTeam.getRoot());
    }

}
