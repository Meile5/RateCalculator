package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.CountryValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Currency;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
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

public class CreateTeamController implements Initializable {

    @FXML
    private VBox createTeamWindow;
    @FXML
    private MFXTextField teamNameTF;
    @FXML
    private MFXComboBox<String> currencyCB;
    @FXML
    private ListView<Team> teamsListView;
    @FXML
    private Button addTeamBTN, removeTeamBTN;
    @FXML
    private MFXButton saveBTN, cancelBTN;
    @FXML
    private MFXProgressSpinner progressSpinner;


    private IModel model;
    private StackPane pane;
    private Country country;
    private GeographyManagementController controller;
    private List<Team> teamsList;
    private Service<Void> saveTeam;

    public CreateTeamController(IModel model, StackPane pane, Country country, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateTeamController.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.country = country;
        this.controller = controller;
        try {
            createTeamWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamsList = new ArrayList<>();
        setFields();
        addTeamListener();
        removeTeamListener();

        saveTeamListener();
        cancelOperationListener();
    }

    private void addTeamListener() {
        addTeamBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamNameValid(teamNameTF) && CountryValidation.isCurrencySelected(currencyCB)){
                    Team team = new Team(teamNameTF.getText(), getCurrency());
                    teamsList.add(team);
                    teamsListView.getItems().add(team);
            }
            clearFields();
        });
    }

    private void removeTeamListener() {
        removeTeamBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamToRemoveSelected(teamsListView)){
                teamsList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
            }});
    }

    private void saveTeamListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamNameValid(teamNameTF) && CountryValidation.isTeamsListValid(teamsListView)){
                enableProgressBar();
                saveTeamOperation(country, teamsList);
            }
        });

    }

    private void saveTeamOperation(Country country, List<Team> teams) {
        saveTeam = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(200);
                            model.addNewTeams(country, teams);
                        return null;
                    }
                };
            }
        };

        saveTeam.setOnSucceeded(event -> {
            controller.showOperationStatus("Operation Successful!", Duration.seconds(2));
            controller.updateCountryComponents();
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();

        });

        saveTeam.setOnFailed(event -> {
            controller.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();
        });
        saveTeam.restart();
    }

    private void cancelOperationListener() {
        cancelBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            WindowsManagement.closeStackPane(pane);
        });
    }

    private void setFields(){
        ObservableList<String> currencies = FXCollections.observableArrayList(Currency.EUR.name(), Currency.USD.name());
        currencyCB.setItems(currencies);
    }

    private Currency getCurrency() {
        if (currencyCB.getSelectedItem().equals(Currency.EUR.name())) {
            return Currency.EUR;
        } else {
            return Currency.USD;
        }
    }

    private void enableProgressBar() {
        progressSpinner.setVisible(true);
        progressSpinner.setDisable(false);
    }

    private void disableProgressBar() {
        progressSpinner.setVisible(false);
        progressSpinner.setDisable(true);
    }

    private void clearFields(){
        teamNameTF.clear();
        currencyCB.clearSelection();
    }

    public VBox getRoot(){
        return createTeamWindow;
    }
}
