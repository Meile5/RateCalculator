package easv.ui.components.map.popUpInfo.teamComponent;
import easv.be.Employee;
import easv.be.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
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
    private Team team;
    private ObservableList<PieChart.Data> pieChartData;

    public TeamComponentController(Team team) {
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
            //TODO include percentage for the employee based on team total overhead
            for(Employee employee :team.getEmployees()){
                 pieChartData.add(new PieChart.Data(employee.getName(),employee.getActiveConfiguration().getDayRate().doubleValue()));
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

        //TODO if is more time calculate the salaries off the employees, to have the team total
        private void initializeTeamOverheadData(){
//        BigDecimal salaryOverheadValue  = team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.SALARY_OVERHEAD).setScale(2, RoundingMode.HALF_UP);
//        salaryOverhead.setText(salaryOverheadValue + " " + team.getTeamMembers().getFirst().getCurrency());
//        BigDecimal expensesOverheadValue = team.getTeamOverheadValues().get(TeamWithEmployees.TeamOverheadType.EXPENSES_OVERHEAD).setScale(2,RoundingMode.HALF_UP);
//        expensesOverhead.setText(expensesOverheadValue + " " + team.getTeamMembers().getFirst().getCurrency());
        if(team.getActiveConfiguration()!=null){
            BigDecimal totalOverheadValue =   team.getActiveConfiguration().getTeamDayRate();
            totalOverhead.setText(totalOverheadValue + " " + team.getEmployees().get(0).getCurrency());
        }else{
            totalOverhead.setText("n/a");
        }
        }




}
