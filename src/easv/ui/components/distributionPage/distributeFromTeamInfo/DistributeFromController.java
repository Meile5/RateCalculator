package easv.ui.components.distributionPage.distributeFromTeamInfo;

import easv.be.Country;
import easv.be.Currency;
import easv.be.Region;
import easv.be.Team;
import easv.ui.pages.distribution.ControllerMediator;
import easv.ui.pages.distribution.DistributionType;
import easv.ui.pages.modelFactory.IModel;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DistributeFromController implements Initializable, DistributionComponentInterface {
    @FXML
    private final HBox teamComponent;
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
    private ControllerMediator controllerMediator;
    private DistributionType distributionType;

    public DistributeFromController(IModel model, Team teamToDisplay, ControllerMediator distributionControllerMediator, DistributionType distributionType) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeFromTeamInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.teamToDisplay = teamToDisplay;
        this.controllerMediator = distributionControllerMediator;
        this.distributionType = distributionType;
        try {
            teamComponent = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateComponentWithValues();

        Platform.runLater(this::addClickListener);
    }


    //TODO add the  team day rate and hourly rate  when is finished

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


        //dayRate, dayCurrency, hourlyRate, hourlyCurrency
    }

    public HBox getRoot() {
        return teamComponent;
    }

    private void addInfoToolTip(Label label) {
        Tooltip toolTip = new Tooltip();
        toolTip.getStyleClass().add("tooltipinfo");
        toolTip.setText(label.getText());
        label.setTooltip(toolTip);
    }

    private void addClickListener() {
        this.teamComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (this.distributionType.equals(DistributionType.DISTRIBUTE_FROM)) {
                model.setDistributeFromTeam(teamToDisplay);
                controllerMediator.addTeamToDistributeFrom(teamToDisplay);
                this.teamComponent.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
                this.controllerMediator.setTheSelectedComponentToDistributeFrom(this);
                this.teamComponent.getStyleClass().add("teamComponentClicked");
                return;
            }

            if (this.distributionType.equals(DistributionType.DISTRIBUTE_TO)) {
                model.addDistributionPercentageTeam(teamToDisplay, "");
                controllerMediator.addDistributeToTeam(teamToDisplay);
                return;
            }

            //   this.controllerMediator.showTeamToDistributeFromBarChart();
        });
    }

    @Override
    public void setTheStyleClassToDefault() {
        this.teamComponent.getStyleClass().remove("teamComponentClicked");
    }

    public void setTeamToDisplay(Team team) {
        this.teamToDisplay = team;
    }

}
