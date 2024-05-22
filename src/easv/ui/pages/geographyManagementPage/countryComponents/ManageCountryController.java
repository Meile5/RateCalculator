package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.CountryValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageCountryController implements Initializable {
    @FXML
    private VBox manageWindow;
    @FXML
    private MFXFilterComboBox<String> countryNameBox;
    @FXML
    private MFXComboBox<Team> teamsCB;
    @FXML
    private ListView<String> teamsListView;
    @FXML
    private Button addTeamBTN, removeTeamBTN;
    @FXML
    private MFXButton saveBTN, cancelBTN, editTeamBT, addNewTeamBT;
    @FXML
    private MFXProgressSpinner progressSpinner;

    private IModel model;
    private StackPane pane;
    private StackPane secondPane;
    private Country country;
    private List<Team> existingTeamsList;
    private Service<Void> saveCountry;
    private GeographyManagementController controller;
    private boolean isEditOperation = false;

    public ManageCountryController(IModel model, StackPane pane, StackPane secondPane, Country country, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageCountryPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.secondPane = secondPane;
        this.country = country;
        this.controller = controller;
        try {
            manageWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        existingTeamsList = new ArrayList<>();

        setFields();
        addNewTeamListener();
        addTeamToListListener();
        editTeamListener();
        removeTeamListener();
        saveCountryListener();
        cancelOperationListener();
    }

    private void addTeamToListListener() {
        addTeamBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamSelected(teamsCB, null)){
                if(teamsCB.getSelectedItem() != null) {
                    Team team = teamsCB.getSelectedItem();
                    existingTeamsList.add(team);
                    String teamAndCurrency = team.getTeamName() + " - " + team.getCurrency().name();
                    teamsListView.getItems().add(teamAndCurrency);
                }
                teamsCB.clearSelection();
            }
        });
    }

    private void removeTeamListener() {
        removeTeamBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamToRemoveSelected(teamsListView)){
                existingTeamsList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
            }});
    }

    private void setFields() {
        ObservableList<String> countries = FXCollections.observableArrayList(model.getValidCountries());
        countryNameBox.setItems(countries);
        if(country != null){
            isEditOperation = true;
            countryNameBox.setText(country.getCountryName());
            if(!country.getTeams().isEmpty()) {
                if(country.getTeams().getFirst() != null) {
                    teamsListView.getItems().addAll(getTeams(country.getTeams()));
                    existingTeamsList.addAll(country.getTeams());
                }
            }
        }
        teamsCB.getItems().addAll(model.getOperationalTeams());
    }

    private void saveCountryListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isCountryNameValid(countryNameBox) && CountryValidation.isTeamsListValid(teamsListView)){
                enableProgressBar();
                if(isEditOperation) {
                    country.setCountryName(countryNameBox.getText());
//                    secondPane.getChildren().add(progressSpinner);
//                    WindowsManagement.showStackPane(secondPane);

                    saveCountryOperation(country, existingTeamsList);
                } else {
//                    secondPane.getChildren().add(progressSpinner);
//                    WindowsManagement.showStackPane(secondPane);

                    String name = countryNameBox.getText();
                    country = new Country(name);
                    saveCountryOperation(country, existingTeamsList);
                }
            }
        });

    }

    private void addNewTeamListener() {
        addNewTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
                CreateTeamController createTeamController = new CreateTeamController (pane, controller, null, secondPane, false, this);
                secondPane.getChildren().add(createTeamController.getRoot());
                WindowsManagement.showStackPane(secondPane);

        });
    }

    private void editTeamListener() {
        editTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamSelected(null, teamsListView)){
                Team team = getSelectedTeam();
                CreateTeamController createTeamController = new CreateTeamController(pane, controller, team, secondPane, true, this);
                secondPane.getChildren().add(createTeamController.getRoot());
                WindowsManagement.showStackPane(secondPane);
            }
        });
    }

    private Team getSelectedTeam() {
        if(teamsListView.getSelectionModel().getSelectedItem() == null) {
            return null;
        } else {
            String teamAndCurrency = teamsListView.getSelectionModel().getSelectedItem();
            String[] teamAndCurrencyArray = teamAndCurrency.split(" - ");
            String teamName = teamAndCurrencyArray[0];
            return existingTeamsList.stream()
                    .filter(team -> team.getTeamName().equals(teamName))
                    .findFirst()
                    .orElse(null);
        }
    }

    private List<String> getTeams(List<Team> teams) {
        String teamAndCurrency = null;
        List<String> teamsList = new ArrayList<>();
        for(Team team : teams){
            teamAndCurrency = team.getTeamName() + " - " + team.getCurrency().name();
            teamsList.add(teamAndCurrency);
        }
        return teamsList;
    }

    private void enableProgressBar() {
        progressSpinner.setVisible(true);
        progressSpinner.setDisable(false);
    }

    private void disableProgressBar() {
        progressSpinner.setVisible(false);
        progressSpinner.setDisable(true);
    }

    private void saveCountryOperation(Country country, List<Team> teams) {
        saveCountry = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if(isEditOperation) {
                            System.out.println("Updating country " + country + " with teams " + teams);
                            model.updateCountry(country, teams);
                        } else {
                            System.out.println("Updating country " + country + " with teams " + teams);
                            model.addNewCountry(country, teams);
                        }
                        return null;
                    }
                };
            }
        };

        saveCountry.setOnSucceeded(event -> {
            controller.showOperationStatus("Operation Successful!", Duration.seconds(2));
            controller.updateCountryComponents();
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();

        });

        saveCountry.setOnFailed(event -> {
            controller.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();
        });
        saveCountry.restart();
    }

    private void cancelOperationListener() {
        cancelBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            WindowsManagement.closeStackPane(pane);
        });
    }

    public VBox getRoot(){
        return manageWindow;
    }

    public void getUpdatedTeam(Team team) {
        Team selectedTeam = getSelectedTeam();
        if (selectedTeam != null) {
            selectedTeam.setTeamName(team.getTeamName());
            selectedTeam.setCurrency(team.getCurrency());
        }
        teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
        String teamAndCurrency = team.getTeamName() + " - " + team.getCurrency().name();
        teamsListView.getItems().add(teamAndCurrency);
    }

    public void getNewTeam(Team team) {
        if (team != null) {
            existingTeamsList.add(team);
            String teamAndCurrency = team.getTeamName() + " - " + team.getCurrency().name();
            teamsListView.getItems().add(teamAndCurrency);
        }
    }

    public void deleteTeam(Team team) throws RateException {
        if (team != null) {
            model.deleteTeam(team);
            existingTeamsList.remove(team);
            teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
        }
    }
}
