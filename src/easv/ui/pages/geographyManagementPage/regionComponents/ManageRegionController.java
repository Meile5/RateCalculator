package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.Utility.RegionValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageRegionController implements Initializable {

    @FXML
    private VBox manageWindow;
    @FXML
    private MFXTextField regionNameTF;
    @FXML
    private MFXComboBox<Country> countriesCB;
    @FXML
    private ListView<Country> countriesListView;
    @FXML
    private Button addCountryBTN, removeCountryBTN;
    @FXML
    private MFXButton saveBTN, cancelBTN;
    @FXML
    private MFXProgressSpinner progressSpinner;

    private IModel model;
    private StackPane pane;
    private StackPane secondPane;
    private Region region;
    private List<Country> countries;
    private Service<Void> saveRegion;
    private GeographyManagementController controller;
    private boolean isEditOperation = false;

    public ManageRegionController(IModel model, StackPane pane, StackPane secondPane, Region region, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageRegionPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.secondPane = secondPane;
        this.region = region;
        this.controller = controller;
        try {
            manageWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        countries = new ArrayList<>();

        setFields();
        addCountryListener();
        removeCountryListener();

        saveRegionListener();
        cancelOperationListener();
    }

    private void addCountryListener() {
        addCountryBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isCountrySelected(countriesCB)){
                Country country = countriesCB.getSelectedItem();
                countries.add(country);
                countriesListView.getItems().add(country);

                countriesCB.clearSelection();
            }
        });
    }

    private void removeCountryListener() {
        removeCountryBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isCountryToRemoveSelected(countriesListView)){
                countries.remove(countriesListView.getSelectionModel().getSelectedIndex());
                countriesListView.getItems().remove(countriesListView.getSelectionModel().getSelectedIndex());
            }});
    }

    private void setFields() {
        if(region != null){
            regionNameTF.setText(region.getRegionName());
            countriesListView.getItems().addAll(region.getCountries());
            countriesCB.getItems().addAll(model.getOperationalCountries());

            countries.addAll(region.getCountries());
            isEditOperation = true;
        }
    }

    private void saveRegionListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isRegionNameValid(regionNameTF) && RegionValidation.isCountryListValid(countriesListView)){
                if(isEditOperation) {
                    enableProgressBar();
                    region.setRegionName(regionNameTF.getText());
                    secondPane.getChildren().add(progressSpinner);
                    WindowsManagement.showStackPane(secondPane);

                    saveRegionOperation(region, countries);
                } else {
                    enableProgressBar();
                    secondPane.getChildren().add(progressSpinner);
                    WindowsManagement.showStackPane(secondPane);

                    String name = regionNameTF.getText();
                    region = new Region(name);
                    saveRegionOperation(region, countries);
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

    private void saveRegionOperation(Region region, List<Country> countries) {
        saveRegion = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(200);
                        if(isEditOperation)
                            model.updateRegion(region, countries);
                        else
                            model.addNewRegion(region, countries);
                        return null;
                    }
                };
            }
        };

        saveRegion.setOnSucceeded(event -> {
            controller.showOperationStatus("Operation Successful!", Duration.seconds(2));
            WindowsManagement.closeStackPane(secondPane);
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();

        });

        saveRegion.setOnFailed(event -> {
            controller.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            WindowsManagement.closeStackPane(secondPane);
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();
        });
        saveRegion.restart();
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
