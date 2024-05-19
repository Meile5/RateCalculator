package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.geographyManagementPage.countryComponents.CountryComponent;
import easv.ui.pages.geographyManagementPage.countryComponents.DeleteCountryController;
import easv.ui.pages.geographyManagementPage.regionComponents.DeleteRegionController;
import easv.ui.pages.geographyManagementPage.regionComponents.ManageRegionController;
import easv.ui.pages.geographyManagementPage.regionComponents.RegionComponent;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeographyManagementController implements Initializable {
    @FXML
    private Parent createPage;
    @FXML
    private VBox regionsVBox, countriesVBox;
    @FXML
    private Label operationStatusLB;
    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private Button addRegionBTN, addCountryBTN;

    private IModel model;
    private StackPane pane;
    private StackPane secondPane;
    private Service<Void> loadRegionsAndCountriesFromDB;
    private ObservableList<Region> regions;
    private ObservableList<Country> countries;
    private ObservableList<Team> teams;

    public GeographyManagementController(IModel model, StackPane pane) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeographyManagementPage.fxml"));
        loader.setController(this);
        this.model=model;
        this.pane = pane;
        try {
            createPage=loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeRegionsLoadingService();
        addRegionButtonListener();

    }

    private void addRegionButtonListener() {
        addRegionBTN.setOnAction(event -> {
            ManageRegionController manageRegionController = new ManageRegionController(model, pane, secondPane, null, this);
            this.pane.getChildren().add(manageRegionController.getRoot());
            WindowsManagement.showStackPane(pane);
        });
    }

    private void initializeRegionsLoadingService() {
        enableProgressBar();
        loadRegionsAndCountriesFromDB = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(1000);
                        regions = FXCollections.observableArrayList(model.getOperationalRegions());
                        countries = FXCollections.observableArrayList(model.getOperationalCountries());
                        teams = FXCollections.observableArrayList(model.getOperationalTeams());
                        return null;
                    }
                };
            }
        };

        loadRegionsAndCountriesFromDB.setOnSucceeded((event) -> {
            // Update the UI with loaded Regions and Countries
            displayRegions();
            displayCountries();

            // Hide the progress bar
            disableProgressBar();
        });
        loadRegionsAndCountriesFromDB.setOnFailed((event) -> {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_GEOGRAPHY_FAILED.getValue());


        });
        loadRegionsAndCountriesFromDB.restart();
    }

    private void displayCountries() {
        countriesVBox.getChildren().clear();
        countries.forEach(c -> {
            DeleteCountryController deleteCountryController = new DeleteCountryController(pane, model, c);
            CountryComponent countryComponent = new CountryComponent(model, pane, c, deleteCountryController);
            countriesVBox.getChildren().add(countryComponent.getRoot());
        });
    }

    private void displayRegions() {
        regionsVBox.getChildren().clear();
        regions.forEach(this::addRegionComponent);
    }

    public void addRegionComponent(Region region){
        DeleteRegionController deleteRegionController = new DeleteRegionController(pane, model, region, this);
        RegionComponent regionComponent = new RegionComponent(model, pane, region, deleteRegionController, this);
        regionsVBox.getChildren().add(regionComponent.getRoot());
        if(!regions.contains(region))
            regions.add(region);
    }

    //TODO: ON UPDATE, ADD AND REMOVE COMPONENT ! - NELSON

    public void removeRegionComponent(Region region){
        for (Node node : regionsVBox.getChildren()) {
            System.out.println("Region: " + region);
            System.out.println(node.getClass());
            System.out.println(node instanceof RegionComponent); // FALSE
            System.out.println(node instanceof HBox); //TRUE
            System.out.println(node.getParent());

            if (node instanceof RegionComponent) {
                RegionComponent regionComponent = (RegionComponent) node;
                if (regionComponent.getRegion().equals(region)) {
                    regionsVBox.getChildren().remove(regionComponent);
                    break;
                }
            }
        }
    }

    private void enableProgressBar(){
        progressBar.setDisable(false);
        progressBar.setVisible(true);
        enableStackPane(progressBar);
    }

    private void disableProgressBar(){
        progressBar.setDisable(true);
        progressBar.setVisible(false);
        disableStackPane();
    }

    private void enableStackPane(Node node){
        pane.getChildren().clear();
        pane.getChildren().add(node);
        pane.setDisable(false);
        pane.setVisible(true);
    }

    private void disableStackPane(){
        pane.getChildren().clear();
        pane.setDisable(true);
        pane.setVisible(false);
    }

    public Parent getCreatePage() {
        return createPage;
    }

    public void showOperationStatus(String value, Duration duration) {
        operationStatusLB.setText(value);
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> operationStatusLB.setText(""));
        delay.play();
    }
}
