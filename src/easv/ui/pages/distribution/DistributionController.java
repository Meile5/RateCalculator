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
import java.math.RoundingMode;
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
    private Service<Boolean> saveDistribution;


    private final static PseudoClass OVER_LIMIT = PseudoClass.getPseudoClass("errorLimit");

    public DistributionController(IModel model, StackPane secondLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Distribution.fxml"));
        loader.setController(this);
        this.model = model;
        this.secondLayout = secondLayout;
        this.distributionMediator = new ControllerMediator();
        this.distributionMediator.registerDistributionController(this);
        model.initializeDistributionEntities();
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
        // save the performed distribution operation
        saveDistributionOperation();
    }

    /**
     * add the teams in the  system  to the distribute to teams container
     */
    private void populateDistributeToTeams() {
        distributeToTeams.setCellFactory(listView -> new DistributeToListCell(model, distributionMediator, DistributionType.DISTRIBUTE_TO, secondLayout));
        distributeToTeams.setItems(model.getOperationalTeams());
    }

    private void populateDistributeFromTeams() {
        List<Parent> distributeFromTeamsComponents = new ArrayList<>();
        model.getOperationalTeams().forEach(e -> {
            DistributeFromController distributeToController = new DistributeFromController(model, e, distributionMediator, DistributionType.DISTRIBUTE_FROM, secondLayout);
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


    /**
     * perform distribution simulation
     */
    private void addSimulateButtonListener() {
        this.simulateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // if invalid  values  return
            if (distributionIsInvalid()) return;
            //save in the model that the simulation was performed
            model.setSimulationPerformed(true);
            this.secondLayout.getChildren().add(new MFXProgressSpinner());
            WindowsManagement.showStackPane(secondLayout);
            //start the service that will perform the computation
            initializeSimulateService();
        });
    }


    /**
     * validate the  distribution  inputs
     */
    private boolean distributionIsInvalid() {
        String validateSelectedTeams = validateTeamsDistributionSelection();
        if (!validateSelectedTeams.isEmpty()) {
            showInfoError(validateSelectedTeams);
            return true;
        }

        // validate the user entered inputs for overhead distribution
        DistributionValidation inputValidation = model.validateInputs();

        if (inputValidation.getErrorValues().containsKey(ErrorCode.OVERHEAD_ZERO)) {
            showInfoError(ErrorCode.OVERHEAD_ZERO.getValue());
            return true;
        }


        if (inputValidation.getErrorValues().containsKey(ErrorCode.EMPTY_OVERHEAD)) {
            StringBuilder emptyValues = new StringBuilder();
            emptyValues.append(ErrorCode.EMPTY_OVERHEAD).append("\n");
            inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD).forEach(
                    e -> emptyValues.append(e.getTeamName()).append("\n"));
            showInfoError(emptyValues.toString());
            changeEmptyComponentsStyle(inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD));
            return true;
        }


        if (!inputValidation.getErrorValues().isEmpty()) {
            StringBuilder invalidInput = new StringBuilder();

            if (inputValidation.getErrorValues().containsKey(ErrorCode.INVALID_OVERHEADVALUE)) {
                //apend the invalid  error message
                invalidInput.append(ErrorCode.INVALID_OVERHEADVALUE.getValue())
                        .append(ErrorCode.INVALID_OVERHEAD_MESSAGE).append("\n");
                //append the invalid teams names
                inputValidation.getErrorValues().get(ErrorCode.INVALID_OVERHEADVALUE).forEach(
                        e -> invalidInput.append(e.getTeamName()).append("\n"));
            }
            if (inputValidation.getErrorValues().containsKey(ErrorCode.OVER_LIMIT)) {
                //append the invalid error message
                invalidInput.append(ErrorCode.OVER_LIMIT.getValue()).append("\n");
            }

            //show the error window for the user
            showInfoError(invalidInput.toString());
            return true;
        }
        return false;
    }

    private void updateInsertedTotalValueStyle(double value) {
        totalOverheadContainer.pseudoClassStateChanged(OVER_LIMIT, value < 0 || value > 100);
    }


    @Override
    public void updateTotalOverheadValue() {
        double totalOverhead = model.calculateTeTotalOverheadInserted();
        this.totalOverheadInserted.setText(totalOverhead + "");
        updateInsertedTotalValueStyle(totalOverhead);
    }

    @Override
    public boolean removeTeamFromDistributionView(List<Parent> teamsAfterRemoveOperation) {
        this.selectedToDistributeTo.getChildren().clear();
        if (teamsAfterRemoveOperation.isEmpty()) {
            return true;
        }
        return this.selectedToDistributeTo.getChildren().addAll(teamsAfterRemoveOperation);
    }

    /**
     * add team to distribute from
     */
    @Override
    public void addTeamToDistributeFrom(Team team) {
        if (!this.selectedToDistribute.getChildren().isEmpty()) {
            this.selectedToDistribute.getChildren().clear();
        }
        DistributeFromController distributeFromController = new DistributeFromController(model, team, distributionMediator, DistributionType.DISPLAY, secondLayout);
        distributionMediator.addSelectedTeamToDistributeFromController(distributeFromController);
        selectedToDistribute.getChildren().add(distributeFromController.getRoot());
    }

    /**
     * add team to distribute to into the distribute to list
     */
    @Override
    public void addDistributeToTeam(Team teamToDisplay) {
        DistributeToController selectedTeam = new DistributeToController(model, teamToDisplay, distributionMediator, secondLayout);
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
    private void changeEmptyComponentsStyle(List<Team> teams) {
        for (Team team : teams) {
            distributionMediator.changeComponentStyleToError(team.getId());
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
                updateComponentsOverheadValues(simulateService.getValue());
            });
            pauseTransition.playFromStart();

        });
        this.simulateService.setOnFailed((e) -> {
            simulateService.getException().printStackTrace();
            showInfoError(ErrorCode.SIMULATION_FAILED.getValue());
        });
        simulateService.restart();
    }


    /**
     * display an information  message for the user when the input is invalid
     */
    private void showInfoError(String errorValue) {
        ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout, errorValue);
        secondLayout.getChildren().add(errorWindowController.getRoot());
        WindowsManagement.showStackPane(secondLayout);
    }

    /**save  the  performed distribution operation */
    private void saveDistributionOperation(){
        this.saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            initializeSaveService();
        });
    }



    /**
     * update components values with the new calculation values
     */
    private void updateComponentsOverheadValues(Map<OverheadHistory, List<Team>> performedSimulation) {

          List<Team> performedComputation =  performedSimulation.get(OverheadHistory.CURRENT_OVERHEAD);
          Team teamToDistributeFromNewOverhead= performedComputation.getFirst();
          if(teamToDistributeFromNewOverhead!=null){
              double dayRate = teamToDistributeFromNewOverhead.getActiveConfiguration().getTeamDayRate().setScale(2, RoundingMode.HALF_UP).doubleValue();
              double hourlyRate = teamToDistributeFromNewOverhead.getActiveConfiguration().getTeamHourlyRate().setScale(2,RoundingMode.HALF_UP).doubleValue();
              distributionMediator.updateDistributeFromComponent(dayRate, hourlyRate);
          }


//
//        if (model.getSelectedTeamToDistributeFrom().getActiveConfiguration() != null) {
//            double dayRate = model.getSelectedTeamToDistributeFrom().getActiveConfiguration().getTeamDayRate().setScale(2, RoundingMode.HALF_UP).doubleValue();
//            double hourlyRate = model.getSelectedTeamToDistributeFrom().getActiveConfiguration().getTeamHourlyRate().setScale(2,RoundingMode.HALF_UP).doubleValue();
//            distributionMediator.updateDistributeFromComponent(dayRate, hourlyRate);
//        }

        for(int i=1; i<performedComputation.size();i++){
            Team team =  performedComputation.get(i);
            System.out.println( team.getActiveConfiguration().getTeamDayRate());
            distributionMediator.updateComponentOverheadValues(team.getId(), team.getActiveConfiguration().getTeamDayRate().setScale(2, RoundingMode.HALF_UP).doubleValue(), team.getActiveConfiguration().getTeamHourlyRate().setScale(2, RoundingMode.HALF_UP).doubleValue());
        }

//
//        for (Team team : model.getInsertedDistributionPercentageFromTeams().keySet()) {
//            System.out.println(team.getActiveConfiguration().getTeamDayRate());
//
//
//            distributionMediator.updateComponentOverheadValues(team.getId(), team.getActiveConfiguration().getTeamDayRate().setScale(2, RoundingMode.HALF_UP).doubleValue(), team.getActiveConfiguration().getTeamHourlyRate().setScale(2, RoundingMode.HALF_UP).doubleValue());
//        }


    }


    /**initialize the save service */
    private void initializeSaveService() {
        this.saveDistribution = new Service<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return model.saveDistribution();
                    }
                };
            }
        };
        this.saveDistribution.setOnSucceeded((e) -> {
            // showInfoError(ErrorCode.OPERATION_EXECUTED.getValue());
            // display that the operation was performed
      //  "do logic for graph"    //populateTeamBarChartWithSimulatedOverhead();
           // this.populateDistributeFromTeams();
          //  this.populateDistributeToTeams();
            WindowsManagement.closeStackPane(secondLayout);
        });
        this.saveDistribution.setOnCancelled((e) -> {
            WindowsManagement.closeStackPane(secondLayout);
        });

        saveDistribution.restart();

    }


}
