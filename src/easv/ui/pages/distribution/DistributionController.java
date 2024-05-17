package easv.ui.pages.distribution;

import easv.Utility.WindowsManagement;
import easv.be.DistributionValidation;
import easv.be.OverheadComputationPair;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToListCell;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
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
    private BarChart<String, BigDecimal> distributeFromTeamBarChart;
    private IModel model;
    private ControllerMediator distributionMediator;
    private DistributeToMediator distributeToMediator;
    @FXML
    private Button simulateButton,saveButton;
    @FXML
    private Circle circleValue;
    @FXML
    private Label totalOverheadInserted;
    @FXML
    private HBox totalOverheadContainer;
    private StackPane secondLayout;




    private final static PseudoClass OVER_LIMIT = PseudoClass.getPseudoClass("errorLimit");

    public DistributionController(IModel model ,StackPane secondLayout) {
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
        //     List<Parent> distributeToTeamsComponents = new ArrayList<>();
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

    /**
     * display the chart when simulate button is pressed
     */
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
            series.getData().add(new XYChart.Data<>(model.getDistributeFromTeam().getTeamName(), model.getDistributeFromTeam().getActiveConfiguration().getTeamDayRate()));
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
            System.out.println(model.getInsertedDistributionPercentageFromTeams());
            DistributionValidation inputValidation = model.validateInputs();
            String validateSelectedTeams = validateTeamsDistributionSelection();

            if(!validateSelectedTeams.isEmpty()){
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout,validateSelectedTeams);
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                return;
            }


            if(inputValidation.getErrorValues().containsKey(ErrorCode.EMPTY_OVERHEAD)){
                StringBuilder emptyValues = new StringBuilder();
                emptyValues.append(ErrorCode.EMPTY_OVERHEAD).append("\n");
                inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD).forEach(
                        e -> emptyValues.append(model.getTeamName(e)).append("\n"));
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout,emptyValues.toString());
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                changeEmptyComponentsStyle(inputValidation.getErrorValues().get(ErrorCode.EMPTY_OVERHEAD));
                return;
            }





            if (!inputValidation.getErrorValues().isEmpty()) {
                StringBuilder invalidInput = new StringBuilder();


                if(inputValidation.getErrorValues().containsKey(ErrorCode.INVALID_OVERHEADVALUE)){
                    //apend the invalid  error message
                    invalidInput.append(ErrorCode.INVALID_OVERHEADVALUE.getValue())
                            .append(ErrorCode.INVALID_OVERHEAD_MESSAGE).append("\n");
                    //append the invalid teams names
                    inputValidation.getErrorValues().get(ErrorCode.INVALID_OVERHEADVALUE).forEach(
                            e -> invalidInput.append(model.getTeamName(e)).append("\n"));
                }
                if(inputValidation.getErrorValues().containsKey(ErrorCode.OVER_LIMIT)){
                    //append the invalid error message
                    invalidInput.append(ErrorCode.OVER_LIMIT.getValue()).append("\n");
                }

//                if(inputValidation.getErrorValues().containsKey(ErrorCode.EMPTY_OVERHEAD)){
//                    invalidInput.append(ErrorCode.EMPTY_OVERHEAD.getValue()).append("\n");
//                }

                //show the error window for the user
                ErrorWindowController errorWindowController = new ErrorWindowController(secondLayout,invalidInput.toString());
                secondLayout.getChildren().add(errorWindowController.getRoot());
                WindowsManagement.showStackPane(secondLayout);
                return;
            }



             this.secondLayout.getChildren().add(new MFXProgressSpinner());
             WindowsManagement.showStackPane(secondLayout);





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
        distributionMediator.addDistributeToController(selectedTeam,teamToDisplay.getId());
        this.selectedToDistributeTo.getChildren().add(selectedTeam.getRoot());
    }


    /**Check if the user selected both teams , team to distribute from , and team(teams) to distribute to*/
    private String  validateTeamsDistributionSelection(){
        StringBuilder invalidTeams = new StringBuilder();
        if(this.selectedToDistribute.getChildren().isEmpty()){
            invalidTeams.append(ErrorCode.DISTRIBUTE_FROM_EMPTY.getValue()).append("\n");
        }
        if(this.selectedToDistributeTo.getChildren().isEmpty()){
            invalidTeams.append(ErrorCode.DISTRIBUTE_TO_EMPTY.getValue()).append("\n");
        }
        return invalidTeams.toString();
    }

    /**if the inputs are empty  return the error message and change the components style to error pseudo class*/
    private void changeEmptyComponentsStyle(List<Integer> teamIds) {
        System.out.println(model.getInsertedDistributionPercentageFromTeams() + "from style");
        for(Integer teamId: teamIds){
            distributionMediator.changeComponentStyleToError(teamId);
        }
    }

}
