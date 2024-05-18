package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.geographyManagementPage.countryComponents.CountryComponent;
import easv.ui.pages.geographyManagementPage.countryComponents.DeleteCountryController;
import easv.ui.pages.geographyManagementPage.regionComponents.DeleteRegionController;
import easv.ui.pages.geographyManagementPage.regionComponents.RegionComponent;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeographyManagementController implements Initializable {
    @FXML
    private Parent createPage;
    @FXML
    private VBox regionsVBox, countriesVBox;
    @FXML
    private MFXProgressSpinner progressBar;

    private IModel model;
    private StackPane pane;
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

    }

    private void initializeRegionsLoadingService() {
        enableProgressBar();
        loadRegionsAndCountriesFromDB = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        regions = model.getOperationalRegions();
                        countries = model.getOperationalCountries();
                        teams = model.getOperationalTeams();
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
        regions.forEach(r -> {
            DeleteRegionController deleteRegionController = new DeleteRegionController(pane, model, r);
            RegionComponent regionComponent = new RegionComponent(model, pane, r, deleteRegionController);
            regionsVBox.getChildren().add(regionComponent.getRoot());
        });
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
}
