package easv.ui.pages.teamsPage;

import easv.Utility.WindowsManagement;
import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.be.TeamConfigurationEmployee;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.modelFactory.ModelFactory;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TeamsPageController implements Initializable {
    private IModel model;
    @FXML
    private Parent teamPage;
    @FXML
    private VBox teamsContainer;
    @FXML
    private LineChart<String, BigDecimal> lineChart;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<TeamConfiguration> teamsHistory;
    @FXML
    private PieChart teamsPieChart;
    private StackPane firstLayout;

    private TeamInfoController selectedTeam;
    public TeamsPageController(IModel model, StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamsManagementPage.fxml"));
        loader.setController(this);
        this.model=model;
        this.firstLayout = firstLayout;
        try {
            teamPage = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public Parent getRoot(){
        return teamPage;

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {


            displayTeams();

    }

    public void displayTeams() {
        teamsContainer.getChildren().clear();
        ObservableList<HBox> teamInfoControllers = FXCollections.observableArrayList();
            model.getOperationalTeams()
                .forEach(t -> {
                TeamInfoController teamInfoController = new TeamInfoController(t, model, this, firstLayout);
                    teamInfoControllers.add(teamInfoController
                            .getRoot());
            });
            teamsContainer.getChildren().setAll(teamInfoControllers);
    }





    public void clearTeams(){
        teamsContainer.getChildren().clear();
    }


    /* adds green border to selected team and removes it after another is selected*/
    public void setSelectedComponentStyleToSelected(TeamInfoController selectedTeam) {
        if (this.selectedTeam != null) {
            this.selectedTeam.getRoot().getStyleClass().remove("teamComponentClicked");
        }
        this.selectedTeam = selectedTeam;
        this.selectedTeam.getRoot().getStyleClass().add("teamComponentClicked");
    }

    /* listener that listens changes in selected years of combobox and calls a method to populate pieChart*/
    public void yearsComboBoxListener(Team team) {
        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateChartForYear(team, newValue);

            }
        });
    }

    /**
     * populates the lineChart with history from a selected year, it includes day rates and months
     * initializes a new series for an XYChart with String as the X-axis type and BigDecimal as the Y-axis type
     * format String into "Jan 01"
     * @param selectedYear is the year that is selected from a combobox
     */
    private void populateChartForYear(Team team, int selectedYear) {
        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.setName(team.getTeamName());
        /* Get the configurations for the selected year*/
        List<TeamConfiguration> configurations = team.getTeamConfigurationsHistory().stream()
                .filter(config -> config.getSavedDateWithoutTime().getYear() == selectedYear)
                .sorted(Comparator.comparing(TeamConfiguration::getSavedDateWithoutTime))
                .toList();
        /* Populate the series with sorted data from configurations*/
        for (TeamConfiguration config : configurations) {
            series.getData().add(new XYChart.Data<>(config.getSavedDateWithoutTime().format(DateTimeFormatter.ofPattern("MMM dd")), config.getTeamDayRate()));
        }
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }
    /**
     * populates the ComboBox with years based on the team's configurations history
     * if configurations exist extracts only years from the configurations, sorts them in descending order
     * sets the latest year as the initial value of the ComboBox
     * @param team the team whose configurations history is used to populate the ComboBox
     */
    public void populateComboBoxWithYears(Team team) {
        List<TeamConfiguration> configurations = team.getTeamConfigurationsHistory();
        ObservableList<Integer> yearOptions = FXCollections.observableArrayList();
        if (configurations != null) {
            /*Collect years from configurations*/
            configurations.stream()
                    .map(config -> config.getSavedDateWithoutTime().getYear())
                    .distinct()
                    .sorted(Collections.reverseOrder())
                    .forEach(yearOptions::add);
        }
        yearComboBox.setItems(yearOptions);
        /* Set the latest year as the initial value of the ComboBox*/
        if (!yearOptions.isEmpty()) {
            yearComboBox.setValue(yearOptions.get(0));
        }
    }

    /** sets a list of team history dates in combobox of pieChart
     * @param team is being passed from teamInfo controller to get the selected team component team
     */
    public void setTeamHistoryDatesInComboBox(Team team){
        List<TeamConfiguration> teamConfigurations = team.getTeamConfigurationsHistory();
        teamConfigurations.sort(Comparator.comparing(TeamConfiguration::getSavedDate).reversed());
        teamsHistory.getItems().clear();
        teamsHistory.getItems().addAll(teamConfigurations);
        /* Set the latest configuration as the default value*/
        if (!teamConfigurations.isEmpty()) {
            teamsHistory.setValue(teamConfigurations.get(0));
        }

    }
    /** sets a list of team history dates in combobox of pieChart
     * adds listener in order to display pieChart info based on selected history configuration
     */
    public void historyComboBoxListener(Team team){
        teamsHistory.setOnAction(event -> {
            TeamConfiguration selectedConfig = teamsHistory.getValue();
            if (selectedConfig != null) {
                displayEmployeesForDate(team, selectedConfig);
            }
        });
    }

    /** displays pieChart data which is teamMembers of teamHistory configurations
     * gets employee name and rate for each to display in pieChart slice
     * sets team name into pieChart label
     */
    private void displayEmployeesForDate(Team team, TeamConfiguration selectedConfig){
        String currency = team.getCurrency().toString();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<TeamConfigurationEmployee> teamMembers = selectedConfig.getTeamMembers();
        for (TeamConfigurationEmployee employee : teamMembers) {
            String label = employee.getEmployeeName() + " " + currency+ " ";
            pieChartData.add(new PieChart.Data(label, employee.getEmployeeDailyRate()));
        }
        /* binds each PieChart.Data object's name property to a concatenated string
         containing the name and day rate, ensuring that both are displayed in the pie chart.*/
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(data.getName(), " ", data.pieValueProperty())
                )
                );
        teamsPieChart.setData(pieChartData);
        teamsPieChart.setTitle(team.getTeamName());
        teamsPieChart.setLabelLineLength(10);
        teamsPieChart.setLegendVisible(false);
        for (PieChart.Data data : pieChartData) {
            data.setPieValue(data.getPieValue());
        }
    }









}
