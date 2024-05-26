package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.CountryValidation;
import easv.Utility.WindowsManagement;
import easv.be.Currency;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import io.github.palexdev.materialfx.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageTeamController implements Initializable {

    @FXML
    private VBox createTeamWindow;
    @FXML
    private MFXTextField teamNameTF;
    @FXML
    private MFXComboBox<String> currencyCB;
    @FXML
    private Button saveBTN, cancelBTN, deleteBTN;
    @FXML
    private MFXProgressSpinner progressSpinner;


    private StackPane pane, secondPane;
    private GeographyManagementController geographyManagementController;
    private ManageCountryController manageCountryController;
    private Service<Void> saveTeam;
    private Team selectedTeam;
    private boolean isEditOperation;

    public ManageTeamController(StackPane pane, GeographyManagementController geographyManagementController, Team selectedTeam, StackPane secondPane, boolean isEditOperation, ManageCountryController manageCountryController){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageTeamPage.fxml"));
        loader.setController(this);
        this.pane = pane;
        this.secondPane = secondPane;
        this.geographyManagementController = geographyManagementController;
        this.manageCountryController = manageCountryController;
        this.selectedTeam = selectedTeam;
        this.isEditOperation = isEditOperation;
        try {
            createTeamWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFields();

        saveTeamListener();
        deleteTeamListener();
        cancelOperationListener();
    }

    private void saveTeamListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamNameValid(teamNameTF) && CountryValidation.isCurrencySelected(currencyCB)){
                if(selectedTeam == null) {
                    Team newTeam = new Team(teamNameTF.getText(), getCurrency());
                    saveTeamOperation(newTeam);
                    manageCountryController.updateTeamComboBox(newTeam);
                } else {
                    Team updatedTeam = new Team(teamNameTF.getText(), getCurrency());
                    updatedTeam.setId(selectedTeam.getId());
                    saveTeamOperation(updatedTeam);
                    manageCountryController.updateTeamComboBox(updatedTeam);
                }
            }
        });

    }

    private void deleteTeamListener() {
        deleteBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
                if(selectedTeam != null) {
                    try {
                        manageCountryController.deleteTeam(selectedTeam);
                    } catch (RateException ex) {
                        throw new RuntimeException(ex);
                    }
                    WindowsManagement.closeStackPane(secondPane);
                    WindowsManagement.showStackPane(pane);
                }
        });
//        firstLayout.getChildren().clear();
//        ConfirmationWindowController confirmationWindowController = new ConfirmationWindowController(firstLayout, this);
//        firstLayout.getChildren().add(confirmationWindowController.getRoot());
//        firstLayout.setDisable(false);
//        firstLayout.setVisible(true);
    }

    private void saveTeamOperation(Team team) {
        saveTeam = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (isEditOperation) {
                            Platform.runLater(() -> {
                                manageCountryController.getUpdatedTeam(team, selectedTeam);
                            });
                        } else {
                            Platform.runLater(() -> {
                                manageCountryController.getNewTeam(team);
                            });
                        }
                        return null;
                    }
                };
            }
        };

        saveTeam.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                WindowsManagement.closeStackPane(secondPane);
                WindowsManagement.showStackPane(pane);
            });
        });

        saveTeam.setOnFailed(event -> {
            Platform.runLater(() -> {
                WindowsManagement.closeStackPane(secondPane);
                WindowsManagement.showStackPane(pane);
            });
        });

        saveTeam.restart();
    }

    private void cancelOperationListener() {
        cancelBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            WindowsManagement.closeStackPane(secondPane);
            WindowsManagement.showStackPane(pane);
        });
    }

    private void setFields(){
        pane.setVisible(false);
        ObservableList<String> currencies = FXCollections.observableArrayList(Currency.EUR.name(), Currency.USD.name());
        currencyCB.setItems(currencies);
        if(selectedTeam != null){
            teamNameTF.setText(selectedTeam.getTeamName());
            currencyCB.getSelectionModel().selectItem(selectedTeam.getCurrency().name());
        }
    }

    private Currency getCurrency() {
        if (currencyCB.getSelectedItem().equals(Currency.EUR.name())) {
            return Currency.EUR;
        } else {
            return Currency.USD;
        }
    }

    private void clearFields(){
        teamNameTF.clear();
        currencyCB.clearSelection();
    }

    public VBox getRoot(){
        return createTeamWindow;
    }
}
