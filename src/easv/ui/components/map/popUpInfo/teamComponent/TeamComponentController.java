package easv.ui.components.map.popUpInfo.teamComponent;
import easv.be.TeamWithEmployees;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class TeamComponentController  implements Initializable {
    @FXML
    private VBox teamComponent;
    @FXML
    private Label totalOverhead;
    @FXML
    private Label expensesOverhead;
    @FXML
    private Label salaryOverhead;
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
        initializePieChart();
    }

    public VBox getRoot() {
        return teamComponent;
    }


/**initialize chart with the values*/
        private void initializePieChart(){
            for(Map<String, Double> data : team.getEmployeesOverheadPercentage()){
                for (Map.Entry<String, Double> entry : data.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    pieChartData.add(new PieChart.Data(key, value));
                }
            }
            teamChart.setData(pieChartData);
            teamChart.setTitle(team.getTeamName());
            teamChart.setLabelLineLength(10);
            teamChart.setLegendVisible(false);
            for (PieChart.Data data : pieChartData) {
                data.getNode().setOnMouseClicked(event -> {
                    data.setPieValue(data.getPieValue());

                });
            }
            initializeTeamOverheadData();
        }

        /**initialize the team ovearhead values
         * */
        private void initializeTeamOverheadData(){
        BigDecimal salaryOverheadValue  = team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.SALARY_OVERHEAD).setScale(2, RoundingMode.HALF_UP);
        salaryOverhead.setText(salaryOverheadValue + " " + team.getTeamMembers().getFirst().getCurrency());
        BigDecimal expensesOverheadValue = team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.EXPENSES_OVERHEAD).setScale(2,RoundingMode.HALF_UP);
        expensesOverhead.setText(expensesOverheadValue + " " + team.getTeamMembers().getFirst().getCurrency());
        BigDecimal totalOverheadValue =   team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.TOTAL_OVERHEAD).setScale(2,RoundingMode.HALF_UP);
        totalOverhead.setText(totalOverheadValue + " " + team.getTeamMembers().getFirst().getCurrency());
        }




}
