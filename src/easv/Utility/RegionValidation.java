package easv.Utility;

import easv.be.Country;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.ListView;

public class RegionValidation {


    public static boolean isCountrySelected(MFXComboBox<Country> countriesCB) {
        return countriesCB.getSelectionModel().getSelectedItem() != null;
    }

    public static boolean isCountryToRemoveSelected(ListView<Country> countriesListView) {
        return countriesListView.getSelectionModel().getSelectedItem() != null;
    }

    public static boolean isRegionNameValid(MFXTextField regionNameTF) {
        return true;
    }

    public static boolean isCountryListValid(ListView<Country> countriesListView) {
        return true;
    }
}
