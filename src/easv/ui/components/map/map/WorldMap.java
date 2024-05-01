package easv.ui.components.map.map;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.ModelFactory;
import easv.ui.components.map.map.popUpInfo.CountryInfoContainer;
import easv.ui.pages.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.WorldMapView;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorldMap implements Initializable {

    @FXML
    private WorldMapView worldMap;
    private StackPane firstLayout;
    private IModel model;
    private CountryInfoContainer countryInfoContainer;

    public WorldMap(StackPane firstLayout,IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorldMap.fxml"));
        loader.setController(this);
        this.model= model;
        this.firstLayout=firstLayout;
        try {
            worldMap = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public WorldMap(IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorldMap.fxml"));
        loader.setController(this);
        this.model= model;
        try {
            worldMap = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public WorldMapView getRoot() {
        return worldMap;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.model= ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
        } catch (RateException e) {
        }
        addCountryClickHandler();
        changeColor(model.getCountries().values());
    }



    private void addCountryClickHandler() {
        worldMap.setOnMouseClicked(event -> {
            if (!worldMap.getSelectedCountries().isEmpty()) {
//                List<WorldMapView.Country> countries = worldMap.getSelectedCountries();
//                List<WorldMapView.Location> locations = worldMap.getLocations();
//                this.firstLayout.setVisible(true);
//                this.firstLayout.setDisable(false);
                countryInfoContainer= new CountryInfoContainer(model,firstLayout);
                firstLayout.getChildren().add(countryInfoContainer.getRoot());
                WindowsManagement.showStackPane(firstLayout);
                System.out.println("I am clicked");
            }
        });
    }


/**
 *change the color of the countries that are operational */
private void changeColor(Collection<Country> countries) {
    worldMap.setCountryViewFactory(param -> {
        Optional<WorldMapView.CountryView> countryView = getCountryView(countries, param);
        return countryView.orElseGet(() -> new WorldMapView.CountryView(param));
    });
}
    private Optional<WorldMapView.CountryView> getCountryView(Collection<Country> countries, WorldMapView.Country country) {
        return countries.stream()
                .filter(e -> e.getCountryName().equals(country.getLocale().getDisplayCountry()))
                .map(e -> {
                    WorldMapView.CountryView operationalCountry = new WorldMapView.CountryView(country);
                    operationalCountry.getStyleClass().add("country_operational");
                    return operationalCountry;
                })
                .findFirst();
    }




}




