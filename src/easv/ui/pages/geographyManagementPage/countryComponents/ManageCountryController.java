package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.CountryValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
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

public class ManageCountryController implements Initializable {
    @FXML
    private VBox manageWindow;
    @FXML
    private MFXFilterComboBox<String> countryNameBox;
    @FXML
    private MFXComboBox<Team> teamsCB;
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
        addTeamListener();
        removeTeamListener();

        saveCountryListener();
        cancelOperationListener();
    }

    private void addTeamListener() {
        addTeamBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(CountryValidation.isTeamSelected(teamsCB)){
                if(teamsCB.getSelectedItem() != null) {
                    Team team = teamsCB.getSelectedItem();
                    existingTeamsList.add(team);
                    teamsListView.getItems().add(team);
                } else {
                    Team team = new Team(teamsCB.getText());
                    existingTeamsList.add(team);
                    teamsListView.getItems().add(team);
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
                    teamsListView.getItems().addAll(country.getTeams());
                    existingTeamsList.addAll(country.getTeams());
                }
            }
        }
        teamsCB.getItems().addAll(model.getTeams());
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
                        Thread.sleep(200);
                        if(isEditOperation)
                            model.updateCountry(country, teams);
                        else
                            model.addNewCountry(country, teams);
                        return null;
                    }
                };
            }
        };

        saveCountry.setOnSucceeded(event -> {
            controller.showOperationStatus("Operation Successful!", Duration.seconds(2));
            controller.updateCountryComponents();
//            if (isEditOperation) {
//                controller.updateCountryComponents();
//            } else {
//                controller.addCountryComponent(country);
//            }
            //WindowsManagement.closeStackPane(secondPane);
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();

        });

        saveCountry.setOnFailed(event -> {
            controller.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            //WindowsManagement.closeStackPane(secondPane);
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
}
