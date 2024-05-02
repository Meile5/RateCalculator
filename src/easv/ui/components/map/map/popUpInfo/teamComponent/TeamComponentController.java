package easv.ui.components.map.map.popUpInfo.teamComponent;
import easv.be.TeamWithEmployees;
import easv.ui.components.homePage.openPageObserver.Observable;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class TeamComponentController  implements Initializable {
    @FXML
    private VBox teamComponent;
    @FXML
    private Label teamName;
    @FXML
    private PieChart teamChart;
    private TeamWithEmployees team;
    private ObservableList<PieChart.Data> pieChartData;

    public TeamComponentController(TeamWithEmployees team) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamComponent.fxml"));
        loader.setController(this);
         this.team=team;
         pieChartData= FXCollections.observableArrayList();
        try {
     loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("initialized");
        initializePieChart();
    }

    public VBox getRoot() {
        return teamComponent;
    }



        private void initializePieChart(){
            this.teamName.setText(team.getTeamName());
            for(Map<String, Double> data : team.getEmployeesOverheadPercentage()){
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    pieChartData.add(new PieChart.Data(key, value));
                }
            }
            teamChart.setData(pieChartData);
            teamChart.setTitle(team.getTeamName());
        }

}
