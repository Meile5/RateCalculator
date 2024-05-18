package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.be.Country;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javax.swing.text.html.ListView;
import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ManageRegionController implements Initializable {

    @FXML
    private MFXTextField regionNameTF;
    @FXML
    private MFXComboBox<Country> countriesCB;
    @FXML
    private ListView countriesListView;
    @FXML
    private Button addCountryBTN, removeCountryBTN;
    @FXML
    private MFXButton saveBTN, cancelBTN;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
