package easv.ui.components.distributionPage.distributeToTeamInfo;

import easv.be.Country;
import easv.be.Currency;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.distribution.DistributeToMediator;
import easv.ui.pages.distribution.DistributionController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DistributeToController implements Initializable {
    @FXML
    private final HBox teamComponentDistributeFrom;
    @FXML
    private Label teamRegions;
    @FXML
    private Label teamCountries;
    @FXML
    private Label teamName;
    @FXML
    private Label dayRate, dayCurrency, hourlyRate, hourlyCurrency;
    private IModel model;
    private Team teamToDisplay;
    @FXML
    private MFXTextField distributionPercentage;
    private DistributeToMediator distributeToMediator;
    private final static PseudoClass INVALID_INPUT = PseudoClass.getPseudoClass("inputError");


    public DistributeToController(IModel model, Team teamToDisplay,DistributeToMediator distributeToMediator) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeToTeamInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.teamToDisplay = teamToDisplay;
        this.distributeToMediator = distributeToMediator;
        try {
            teamComponentDistributeFrom = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populateComponentWithValues();
        addInputPercentageListener();
    }

    /**
     * pupulate the component with the team values
     */
    private void populateComponentWithValues() {
        String regions = teamToDisplay.getRegions().stream()
                .map(Region::getRegionName)
                .collect(Collectors.joining(", "));
        this.teamRegions.setText(regions);
        /*add tooltip for the regions to display the hole value*/
        addInfoToolTip(this.teamRegions);
        String countries = teamToDisplay.getCountries().stream()
                .map(Country::getCountryName)
                .collect(Collectors.joining(","));
        this.teamCountries.setText(countries);
        /*add tooltip for the countries to display the whole value*/
        addInfoToolTip(this.teamCountries);
        this.teamName.setText(teamToDisplay.getTeamName());
        if (this.teamToDisplay.getActiveConfiguration() != null) {
            this.dayRate.setText(teamToDisplay.getActiveConfiguration().getTeamDayRate() + "");
            addInfoToolTip(this.dayRate);
            dayCurrency.setText(Currency.USD.toString());
            this.hourlyRate.setText(teamToDisplay.getActiveConfiguration().getTeamHourlyRate() + "");
            addInfoToolTip(this.hourlyRate);
            this.hourlyCurrency.setText(Currency.USD.toString());
        }
    }



    public HBox getRoot() {
        return teamComponentDistributeFrom;
    }
    private void addInfoToolTip(Label label) {
        Tooltip toolTip = new Tooltip();
        toolTip.getStyleClass().add("tooltipinfo");
        toolTip.setText(label.getText());
        label.setTooltip(toolTip);
    }



    public String getOverheadPercentage() {
        return this.distributionPercentage.getText();
    }


    private void addInputPercentageListener() {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        this.distributionPercentage.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((interupt) -> {
                if (!newValue.isEmpty()) {
                    // Validate the new value format
                    if (!newValue.matches("^\\d{0,3}([.,]\\d{1,2})?$")) {
                        ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_OVERHEADVALUE.getValue() + " " + teamToDisplay.getTeamName() + " " + ErrorCode.INVALID_OVERHEAD_MESSAGE.getValue());
                        teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, true);
                        model.setDistributionPercentageTeam(teamToDisplay.getId(), newValue);
                        distributeToMediator.updateTotalOverheadValue();
                    } else {
                        // Convert new value to a double
                        Double overheadInserted = validatePercentageValue(newValue);
                        if (!(overheadInserted >= 0 && overheadInserted <= 100)) {
                            teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, true);
                            model.setDistributionPercentageTeam(teamToDisplay.getId(), newValue);
                            distributeToMediator.updateTotalOverheadValue();
                            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_OVERHEADVALUE.getValue() + " " + teamToDisplay.getTeamName() + " " + ErrorCode.INVALID_OVERHEAD_MESSAGE.getValue());
                        }else{
                            model.setDistributionPercentageTeam(teamToDisplay.getId(), newValue);
                            distributeToMediator.updateTotalOverheadValue();
                            teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, false);
                        }


//                        else {
//                            // Get the previous value from the model
//                            String previousValue = model.getInsertedDistributionPercentageFromTeams().get(teamToDisplay.getId());
//                            Double previousValueValid = validatePercentageValue(previousValue);
//                            // Update the total overhead value based on the difference
//                            if (previousValueValid != null && previousValueValid<100) {
//                                distributeToMediator.updateTotalOverheadValue(overheadInserted - previousValueValid);
//                            } else {
//                                distributeToMediator.removeDistributionPercentage(previousValueValid);
//                                distributeToMediator.updateTotalOverheadValue(overheadInserted);
//                            }
//
//                            teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, false);
//                        }
                    }
                } else {
                    // Handle the case where the input is empty
                    teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, false);
//                    String previousValue = model.getInsertedDistributionPercentageFromTeams().get(teamToDisplay.getId());
//                    Double previousValueValid = validatePercentageValue(previousValue);
//                    System.out.println(model.getInsertedDistributionPercentageFromTeams().get(teamToDisplay.getId()) + "actual value");
//                    if (previousValueValid != null) {
//                        distributeToMediator.removeDistributionPercentage(previousValueValid);
//                    }
                    model.setDistributionPercentageTeam(teamToDisplay.getId(), newValue);
                    distributeToMediator.updateTotalOverheadValue();
                   // model.removeDistributionPercentageTeam(teamToDisplay);
                }
                    });
            pauseTransition.playFromStart();
        });
    }




    private  String convertToDecimalPoint(String value) {
        String validFormat = "";
        if(value== null){
            return validFormat;
        }
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    /**convert string to double , if the input is invalid than the value returned will be null;*/
    private Double validatePercentageValue(String newValue){
        String decimalPoint = convertToDecimalPoint(newValue);
        Double overheadValue= null;
        try{
              overheadValue =  Double.parseDouble(decimalPoint);
        }catch(NumberFormatException e){
            return overheadValue;
        }
        return overheadValue;
    }




    public void changeStyleToError() {
        this.getRoot().pseudoClassStateChanged(INVALID_INPUT,true);
    }

    public void setDayRate(String value) {
        this.dayRate.setText(value);
    }

    public void setHourlyRate(String value) {
        this.hourlyRate.setText(value);
    }
}
