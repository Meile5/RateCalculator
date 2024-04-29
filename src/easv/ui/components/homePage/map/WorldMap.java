package easv.ui.components.homePage.map;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import org.controlsfx.control.WorldMapView;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorldMap implements Initializable {

    private Map<String, Double> countryData;
    private List<String> countries;

    private List<WorldMapView.Country> operationalCountries;
    @FXML
    private WorldMapView worldMap;
    private StackPane firstLayout;

    public WorldMap(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("WorldMap.fxml"));
        loader.setController(this);
        countryData = new HashMap<>();
        try {
            worldMap = loader.load();
            this.firstLayout=firstLayout;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WorldMapView getRoot() {
        return worldMap;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateCountries();
        addCountryClickHandler();
        populateCountries();
        populateOperationalCountries();
        changeColor(countries);
        Platform.runLater(() -> {
            for (WorldMapView.Country c : WorldMapView.Country.values())
                System.out.println(c.getLocale().getDisplayCountry());
        });

    }

    private void populateCountries() {
        operationalCountries = new ArrayList<>();
        operationalCountries.add(WorldMapView.Country.US);
    }

    private void addCountryClickHandler() {
        worldMap.setOnMouseClicked(event -> {
            if (!worldMap.getSelectedCountries().isEmpty()) {
                List<WorldMapView.Country> countries = worldMap.getSelectedCountries();
                List<WorldMapView.Location> locations = worldMap.getLocations();
                this.firstLayout.setVisible(true);
                this.firstLayout.setDisable(false);
            }
        });
    }



    private void changeColor(List<String> countries) {
        worldMap.setCountryViewFactory(new Callback<WorldMapView.Country, WorldMapView.CountryView>() {
            @Override
            public WorldMapView.CountryView call(WorldMapView.Country param) {
                if (countries.contains(param.getLocale().getDisplayCountry())) {
                    WorldMapView.CountryView operationalCountry = new WorldMapView.CountryView(param);
                    operationalCountry.getStyleClass().add("country_operational");
                    return operationalCountry;
                }
                return new WorldMapView.CountryView(param);
            }
        });
    }


    private void populateOperationalCountries() {
        countries = new ArrayList<>();
        String norway = "Norway";
        String denmark = "Denmark";
        String usa = "United States of America";
        String china = "China";
        String india = "India";
        String germany = "Germany";
        String brazil = "Brazil";
        Collections.addAll(countries, norway, denmark, usa, china, india, germany, brazil);
    }


}




