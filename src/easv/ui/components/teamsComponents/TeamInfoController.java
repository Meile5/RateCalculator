package easv.ui.components.teamsComponents;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
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

public class TeamInfoController implements Initializable {
    @FXML
    private HBox teamInfoComponent;
    private IModel model;
    private Team team;
    private TeamsPageController teamsPageController;
    @FXML
    private Label teamName, teamRegion, teamCountry, teamDailyRate, teamHourlyRate, teamDayCurrency, teamHourlyCurrency;


    public TeamInfoController(Team team , IModel model, TeamsPageController teamsPageController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamInfoComponent.fxml"));
        loader.setController(this);
        this.team = team;
        this.model = model;
        this.teamsPageController = teamsPageController;
        try {
            teamInfoComponent = loader.load();
        } catch (IOException e) {
             ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getRoot() {
        return teamInfoComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        Platform.runLater(this::addClickListener);
    }


    private void addClickListener(){

        teamInfoComponent.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {

            teamInfoComponent.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"),false);
            teamInfoComponent.getStyleClass().add("teamComponentClicked");

            teamsPageController.setConfigurations(team);
            teamsPageController.handleTeamInfoComponentClick(team);
            teamsPageController.populateComboBoxWithYears(team);

        });
    }



    public void setLabels() {
        if (team != null) {
            teamName.setText(team.getTeamName());
            teamName.setTooltip(new Tooltip(teamName.getText()));
            teamDayCurrency.setText(team.getCurrency().toString());
            teamHourlyCurrency.setText(team.getCurrency().toString());

            TeamConfiguration activeConfiguration = team.getActiveConfiguration();
            if (activeConfiguration != null) {
                teamDailyRate.setText(activeConfiguration.getTeamDayRate().toString());
                teamHourlyRate.setText(activeConfiguration.getTeamHourlyRate().toString());
            } else {
                teamDailyRate.setText("N/A");
                teamHourlyRate.setText("N/A");
            }

            if (team.getCountries() != null && !team.getCountries().isEmpty()) {
                StringBuilder countryNames = new StringBuilder();
                for (Country country : team.getCountries()) {
                    countryNames.append(country.getCountryName()).append(", ");
                }
                teamCountry.setText(countryNames.toString());
                teamCountry.setTooltip(new Tooltip(teamCountry.getText()));
            } else {
                teamCountry.setText("N/A");
                teamCountry.setTooltip(new Tooltip("N/A"));
            }

            if (team.getRegions() != null && !team.getRegions().isEmpty()) {
                StringBuilder regionNames = new StringBuilder();
                for (Region region : team.getRegions()) {
                    regionNames.append(region.getRegionName()).append(", ");
                }
                teamRegion.setText(regionNames.toString());
                teamRegion.setTooltip(new Tooltip(teamRegion.getText()));
            } else {
                teamRegion.setText("N/A");
                teamRegion.setTooltip(new Tooltip("N/A"));
            }
        }
    }

}
