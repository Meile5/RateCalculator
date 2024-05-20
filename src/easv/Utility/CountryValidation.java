package easv.Utility;

import easv.be.Team;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.scene.control.ListView;

public class CountryValidation {


    public static boolean isTeamSelected(MFXComboBox<Team> teamsCB) {
        return true;
    }

    public static boolean isTeamToRemoveSelected(ListView<Team> teamsListView) {
        return true;
    }

    public static boolean isCountryNameValid(MFXTextField countryNameTF) {
        return true;
    }

    public static boolean isTeamsListValid(ListView<Team> teamsListView) {
        return true;
    }
}
