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
    private MFXButton saveBTN, cancelBTN;
    @FXML
    private MFXProgressSpinner progressSpinner;


    private IModel model;
    private StackPane pane, secondPane;
    private Country country;
    private GeographyManagementController geographyManagementController;
    private ManageCountryController manageCountryController;
    private List<Team> teamsList;
    private Service<Void> saveTeam;
    private Team team;
    private boolean isEditOperation;

    public CreateTeamController(IModel model, StackPane pane, Country country, GeographyManagementController geographyManagementController, Team team, StackPane secondPane, boolean isEditOperation, ManageCountryController manageCountryController){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateTeamPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.secondPane = secondPane;
        this.country = country;
        this.geographyManagementController = geographyManagementController;
        this.manageCountryController = manageCountryController;
        this.team = team;
        this.isEditOperation = isEditOperation;
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

        saveTeamListener();
        cancelOperationListener();
    }

    private void saveTeamListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamNameValid(teamNameTF) && CountryValidation.isCurrencySelected(currencyCB)){
                enableProgressBar();
                if(team == null) {
                    Team newTeam = new Team(teamNameTF.getText(), getCurrency());
                    saveTeamOperation(country, newTeam);
                } else {
                    saveTeamOperation(country, team);
                }
            }
        });

    }

    private void saveTeamOperation(Country country, Team team) {
        saveTeam = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(200);
                        if (isEditOperation){
                            manageCountryController.getNewTeam(team);
                        }
                            model.addNewTeam(country, team);
                        return null;
                    }
                };
            }
        };

        saveTeam.setOnSucceeded(event -> {
            geographyManagementController.showOperationStatus("Operation Successful!", Duration.seconds(2));
            geographyManagementController.updateCountryComponents();
            checkPaneToClose();
            disableProgressBar();

        });

        saveTeam.setOnFailed(event -> {
            geographyManagementController.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            checkPaneToClose();
            disableProgressBar();
        });
        saveTeam.restart();
    }

    private void cancelOperationListener() {
        cancelBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            checkPaneToClose();
        });
    }

    private void checkPaneToClose() {
        if(secondPane != null)
            WindowsManagement.closeStackPane(secondPane);
        else
            WindowsManagement.closeStackPane(pane);
    }

    private void setFields(){
        ObservableList<String> currencies = FXCollections.observableArrayList(Currency.EUR.name(), Currency.USD.name());
        currencyCB.setItems(currencies);
        if(team != null){
            teamNameTF.setText(team.getTeamName());
            currencyCB.getSelectionModel().selectItem(team.getCurrency().name());
        }
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
