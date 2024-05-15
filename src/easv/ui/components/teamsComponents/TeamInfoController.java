package easv.ui.components.teamsComponents;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
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
    private Label teamName, teamRegion, teamCountry, teamDailyRate, teamHourlyRate;

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
        /* Add listener to the team info component*/
        teamInfoComponent.setOnMouseClicked(this::handleTeamInfoComponentClick);
    }

    public void handleTeamInfoComponentClick(MouseEvent event) {
        /* Notify the TeamsPageController about the click event*/
        teamsPageController.handleTeamInfoComponentClick(team);
    }

    public void setLabels() {
        if (team != null) {
            teamName.setText(team.getTeamName());
            teamName.setTooltip(new Tooltip(teamName.getText()));


            teamDailyRate.setText(team.getActiveConfiguration().getTeamDayRate().toString());
            teamHourlyRate.setText(team.getActiveConfiguration().getTeamHourlyRate().toString());

            /*Display multiple region names with tooltip to see all regions*/
            StringBuilder countryNames = new StringBuilder();
            for (Country country : team.getCountries()) {
                if(team.getCountries().size()>1) {
                    countryNames.append(country.getCountryName()).append(", ");
                }
                else{
                    countryNames.append(country.getCountryName());
                }
            }
            teamCountry.setText(countryNames.toString());
            teamCountry.setTooltip(new Tooltip(teamCountry.getText()));

            /*Display multiple region names with tooltip to see all regions*/

            StringBuilder regionNames = new StringBuilder();
            for (Region region : team.getRegions()) {
                if(team.getCountries().size()>1) {
                    countryNames.append(region.getRegionName()).append(", ");
                }
                else{
                    countryNames.append(region.getRegionName());
                }
            }
            teamRegion.setText(countryNames.toString());
            teamRegion.setTooltip(new Tooltip(teamRegion.getText()));

        }
    }
}
