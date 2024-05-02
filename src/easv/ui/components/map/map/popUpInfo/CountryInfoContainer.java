package easv.ui.components.map.map.popUpInfo;
import easv.Utility.WindowsManagement;
import easv.be.TeamWithEmployees;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.map.map.popUpInfo.teamComponent.TeamComponentController;
import easv.ui.pages.modelFactory.IModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CountryInfoContainer implements Initializable {
    @FXML
    private VBox countryPopUp;
    @FXML
    private HBox closeButton;

    @FXML
    private VBox teamsContainer;
    private IModel model;
    private StackPane parent;
    private Service<List<TeamWithEmployees>> teamsInitializer;


    public CountryInfoContainer(IModel model, StackPane parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.parent = parent;
        try {
            countryPopUp = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeWindow();
       initializeService();
    }

    public VBox getRoot() {
        return countryPopUp;
    }

    private void closeWindow() {
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            WindowsManagement.closeStackPane(this.parent);
            model.resetCurrentIndexToRetrieve();
            this.teamsContainer.getChildren().clear();
        });
    }

    public void populatePieChart(List<TeamWithEmployees> countryTeams) {

        List<Parent> teamComponentControllers = new ArrayList<>();
        for (TeamWithEmployees team : countryTeams) {
            System.out.println(team.getTeamName());
            TeamComponentController teamComponentController = new TeamComponentController(team);
            teamComponentControllers.add(teamComponentController.getRoot());
        }
        teamsContainer.getChildren().addAll(teamComponentControllers);
    }

    public void initializeService() {
        if (teamsInitializer != null) {
            if (teamsInitializer.isRunning()) {
                teamsInitializer.cancel();
            }
            teamsInitializer.reset();
        }

        teamsInitializer = new Service<>() {
            @Override
            protected Task<List<TeamWithEmployees>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<TeamWithEmployees> call() throws RateException {
                        return model.getCountryTeams();
                    }
                };
            }
        };

        teamsInitializer.setOnSucceeded(event -> {
            teamsInitializer.getValue().forEach(e-> System.out.println(e.getTeamName()));
            populatePieChart(teamsInitializer.getValue());
        });

        teamsInitializer.setOnFailed(event -> {
            teamsInitializer.getException().printStackTrace();
            ExceptionHandler.errorAlertMessage(ErrorCode.CONNECTION_FAILED.getValue());
        });
        teamsInitializer.start();
    }


}
