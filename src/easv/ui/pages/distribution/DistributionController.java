package easv.ui.pages.distribution;

import easv.Utility.WindowsManagement;
import easv.be.DistributionValidation;
import easv.be.OverheadHistory;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToListCell;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.PauseTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

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
    private BarChart<String, BigDecimal> barchartAfterTheSimulation;
    private IModel model;
    private ControllerMediator distributionMediator;
    private DistributeToMediator distributeToMediator;
    @FXML
    private Button simulateButton, saveButton;
    @FXML
    private Circle circleValue;
    @FXML
    private Label totalOverheadInserted;
    @FXML
    private HBox totalOverheadContainer;
    private StackPane secondLayout;
    private Service<Map<OverheadHistory, List<Team>>> simulateService;


    private final static PseudoClass OVER_LIMIT = PseudoClass.getPseudoClass("errorLimit");

    public DistributionController(IModel model, StackPane secondLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model = model;
        this.secondLayout = secondLayout;
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
        // add simulate button listener
        addSimulateButtonListener();
    }

    /**
     * add the teams in the  system  to the distribute to teams container
     */
    private void populateDistributeToTeams() {
        distributeToTeams.setCellFactory(listView -> new DistributeToListCell(model, distributionMediator, DistributionType.DISTRIBUTE_TO));
        distributeToTeams.setItems(model.getOperationalTeams());
    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeToController = new DistributeFromController(model, e, distributionMediator, DistributionType.DISTRIBUTE_FROM);
            distributeFromTeamsComponents.add(distributeToController.getRoot());
        });
        distributeFromTeams.getChildren().addAll(distributeFromTeamsComponents);
    }

    /**
     * display the chart when simulate button is pressed
     */

    public void showThesimulationBarChart(Map<OverheadHistory, List<Team>> overHeadSimulationTeams) {
        this.barchartAfterTheSimulation.setVisible(true);
        this.barchartAfterTheSimulation.setDisable(false);
        this.barchartAfterTheSimulation.setTitle("Simulation values");
        this.barchartAfterTheSimulation.getXAxis().setLabel("Team");
        this.barchartAfterTheSimulation.getYAxis().setLabel("Overhead");
// set the barchart with the initial overhead
        populateTeamBarChartWithSimulatedOverhead(overHeadSimulationTeams);
    }


    /**
     * populate team barchart with the initial overhead
     */
    private void populateTeamBarChartWithSimulatedOverhead(Map<OverheadHistory, List<Team>> overheadSimulationValues) {
        this.barchartAfterTheSimulation.getData().clear();
        //display the previous values
        XYChart.Series<String, BigDecimal> previousOverheadSeries = new XYChart.Series<>();
        previousOverheadSeries.setName("Previous overhead");
        //display the current values
        XYChart.Series<String, BigDecimal> currentOverheadSeries = new XYChart.Series<>();
        currentOverheadSeries.setName("Current overhead");

        // populate the chart with previous data
        for (Team team : overheadSimulationValues.get(OverheadHistory.PREVIOUS_OVERHEAD)) {
            previousOverheadSeries.getData().add(new XYChart.Data<>(team.getTeamName(), team.getActiveConfiguration().getTeamDayRate()));
        }

        // populate the chart with current data
        for (Team team : overheadSimulationValues.get(OverheadHistory.CURRENT_OVERHEAD)) {
            currentOverheadSeries.getData().add(new XYChart.Data<>(team.getTeamName(), team.getActiveConfiguration().getTeamDayRate()));
        }
        overheadSimulationValues.get(OverheadHistory.CURRENT_OVERHEAD).forEach(e -> System.out.println(e.getTeamName() + " " + e.getActiveConfiguration().getTeamDayRate()));
        overheadSimulationValues.get(OverheadHistory.PREVIOUS_OVERHEAD).forEach(e -> {
            System.out.println(e.getTeamName() + " " + e.getActiveConfiguration().getTeamDayRate());
        });
        this.barchartAfterTheSimulation.getData().add(previousOverheadSeries);
        this.barchartAfterTheSimulation.getData().add(currentOverheadSeries);
    }


    private void addSimulateButtonListener() {
        this.simulateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println(model.getInsertedDistributionPercentageFromTeams());
            DistributionValidation inputValidation = model.validateInputs();
            String validateSelectedTeams = validateTeamsDistributionSelection();

            if (!validateSelectedTeams.isEmpty()) {
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout, validateSelectedTeams);
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                return;
            }


            if (inputValidation.getErrorValues().containsKey(ErrorCode.EMPTY_OVERHEAD)) {
                StringBuilder emptyValues = new StringBuilder();
                emptyValues.append(ErrorCode.EMPTY_OVERHEAD).append("\n");
                inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD).forEach(
                        e -> emptyValues.append(model.getTeamName(e)).append("\n"));
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout, emptyValues.toString());
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                changeEmptyComponentsStyle(inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD));
                return;
            }


            if (!inputValidation.getErrorValues().isEmpty()) {
                StringBuilder invalidInput = new StringBuilder();


                if (inputValidation.getErrorValues().containsKey(ErrorCode.INVALID_OVERHEADVALUE)) {
                    //apend the invalid  error message
                    invalidInput.append(ErrorCode.INVALID_OVERHEADVALUE.getValue())
                            .append(ErrorCode.INVALID_OVERHEAD_MESSAGE).append("\n");
                    //append the invalid teams names
                    inputValidation.getErrorValues().get(ErrorCode.INVALID_OVERHEADVALUE).forEach(
                            e -> invalidInput.append(model.getTeamName(e)).append("\n"));
                }
                if (inputValidation.getErrorValues().containsKey(ErrorCode.OVER_LIMIT)) {
                    //append the invalid error message
                    invalidInput.append(ErrorCode.OVER_LIMIT.getValue()).append("\n");
                }

//                if(inputValidation.getErrorValues().containsKey(ErrorCode.EMPTY_OVERHEAD)){
//                    invalidInput.append(ErrorCode.EMPTY_OVERHEAD.getValue()).append("\n");
//                }

                //show the error window for the user
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout, invalidInput.toString());
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                return;
            }
            this.secondLayout.getChildren().add(new MFXProgressSpinner());
            WindowsManagement.showStackPane(secondLayout);
            //start the service that will perform the computation
            initializeSimulateService();
        });
    }


    /**
     * change circle stroke color based on the user input
     */
//    private void updateCircleColor(Double coverPercentage) {
//        System.out.println(coverPercentage);
//        if (coverPercentage > 100) {
//            circleValue.pseudoClassStateChanged(OVER_LIMIT, true);
//            return;
//        }
//
//        if (coverPercentage < 100) {
//            circleValue.pseudoClassStateChanged(OVER_LIMIT, false);
//        }
//
//        // Calculate circumference
//        double circumference = 2 * Math.PI * this.circleValue.getRadius();
//        double coveredLength = circumference * (coverPercentage / 100);
//        double uncoveredLength = circumference - coveredLength;
//        // Set the stroke dash array
//        circleValue.getStrokeDashArray().addAll(coveredLength, uncoveredLength);
//        // Adjust stroke dash offset to start from the right (0 degrees)
//        circleValue.setStrokeDashOffset(-circumference / 4);
//    }
    private void updateInsertedTotalValueStyle(double value) {
        if (value < 0 || value > 100) {
            totalOverheadContainer.pseudoClassStateChanged(OVER_LIMIT, true);
        } else {
            totalOverheadContainer.pseudoClassStateChanged(OVER_LIMIT, false);
        }
    }

    @Override
    public void updateTotalOverheadValue() {
        double totalOverhead = model.calculateTeTotalOverheadInserted();
        this.totalOverheadInserted.setText(totalOverhead + "");
        updateInsertedTotalValueStyle(totalOverhead);
    }

    @Override
    public void removeOverheadPercentage(Double overheadValue) {
        Double totalOverhead = 0.0;
        if (!totalOverheadInserted.getText().isEmpty()) {
            totalOverhead = Double.parseDouble(this.totalOverheadInserted.getText());
            totalOverheadInserted.setText(totalOverhead - overheadValue + "");
        }
        //  updateCircleColor(totalOverhead);
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
        distributionMediator.addDistributeToController(selectedTeam, teamToDisplay.getId());
        this.selectedToDistributeTo.getChildren().add(selectedTeam.getRoot());
    }


    /**
     * Check if the user selected both teams , team to distribute from , and team(teams) to distribute to
     */
    private String validateTeamsDistributionSelection() {
        StringBuilder invalidTeams = new StringBuilder();
        if (this.selectedToDistribute.getChildren().isEmpty()) {
            invalidTeams.append(ErrorCode.DISTRIBUTE_FROM_EMPTY.getValue()).append("\n");
        }
        if (this.selectedToDistributeTo.getChildren().isEmpty()) {
            invalidTeams.append(ErrorCode.DISTRIBUTE_TO_EMPTY.getValue()).append("\n");
        }
        return invalidTeams.toString();
    }

    /**
     * if the inputs are empty  return the error message and change the components style to error pseudo class
     */
    private void changeEmptyComponentsStyle(List<Integer> teamIds) {
        for (Integer teamId : teamIds) {
            distributionMediator.changeComponentStyleToError(teamId);
        }
    }


    /**
     * initialize the simulate service
     */

    private void initializeSimulateService() {
        this.simulateService = new Service<>() {
            @Override
            protected Task<Map<OverheadHistory, List<Team>>> createTask() {
                return new Task<>() {
                    @Override
                    protected Map<OverheadHistory, List<Team>> call() throws RateException {
                        return model.performSimulation();
                    }
                };
            }
        };

        this.simulateService.setOnSucceeded((e) -> {
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
            pauseTransition.setOnFinished((event) -> {
                WindowsManagement.closeStackPane(secondLayout);
                showThesimulationBarChart(simulateService.getValue());
            });
            pauseTransition.playFromStart();

        });
        this.simulateService.setOnFailed((e) -> {
            secondLayout.getChildren().clear();
            simulateService.getException().printStackTrace();
            ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout, ErrorCode.SIMULATION_FAILED.getValue());
            secondLayout.getChildren().add(errorWindowController.getRoot());
        });
        simulateService.restart();
    }


    /**update components values with the new calculation values*/


}
