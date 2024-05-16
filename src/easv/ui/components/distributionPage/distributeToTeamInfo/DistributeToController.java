package easv.ui.components.distributionPage.distributeToTeamInfo;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.ui.pages.distribution.DistributionController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
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
    private Team teamToDisplay ;
    @FXML
    private MFXTextField distributionPercentage;
    private DistributionController mainDistributionController;

    public DistributeToController(IModel model, Team teamToDisplay, DistributionController mainDistributionController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeToTeamInfo.fxml"));
        loader.setController(this);
        this.model= model;
        this.teamToDisplay= teamToDisplay;
        this.mainDistributionController = mainDistributionController;
        try {
            teamComponentDistributeFrom =loader.load();
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

    public void setTeamToDisplay(Team team ){
        this.teamToDisplay=team;
    }
    public String  getOverheadPercentage(){
        return this.distributionPercentage.getText();
    }


    //TODO add validation for the input percentage to not allow to be a string
    private void  addInputPercentageListener(){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        this.distributionPercentage.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((interupt)->{
                if(!newValue.isEmpty()) {
                    mainDistributionController.addDistributionPercentageToTeam(teamToDisplay.getId(), Double.parseDouble(newValue));
                }else if(!oldValue.isEmpty()){
                    mainDistributionController.removeDistributionPercentageToTeam(teamToDisplay.getId());
                }
            });
           pauseTransition.playFromStart();
        });
    }



}
