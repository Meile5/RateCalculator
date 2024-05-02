package easv.be;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class TeamWithEmployees {

    private String teamName;
    private int id;
    private  List<Employee> teamMembers;
    /**holds the team overhead related expenses*/
    private Map<TeamOverheadType,BigDecimal> teamOverheadValues;
    /**
     *holds the data related to the employees percentage in the total team overhead
     */
    private List<Map<String,Double>> employeesOverheadPercentage;
    public TeamWithEmployees(String team, int id, List<Employee> teamMembers) {
        this.teamName = team;
        this.id = id;
        this.teamMembers = teamMembers;
    }

    public TeamWithEmployees(String team, int id) {
        this.teamName = team;
        this.id = id;
    }

    public enum TeamOverheadType{
        SALARY_OVERHEAD("Salary overhead"),
        EXPENSES_OVERHEAD("Expenses overhead"),
        TOTAL_OVERHEAD("Total overhead");

        private final String value;
        TeamOverheadType(String value) {
        this.value=value;
        }
        public String getValue() {
            return value;
        }
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Employee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public void removeTeamMember(Employee employee){
        this.teamMembers.remove(employee);
    }

    public void addEmployee(Employee employee){
        this.teamMembers.add(employee);
    }

    public Map<TeamOverheadType,BigDecimal> getTeamOverheadValues() {
        return teamOverheadValues;
    }

    public void setTeamOverheadValues(Map<TeamOverheadType,BigDecimal> teamOverheadValues) {
        this.teamOverheadValues = teamOverheadValues;
    }

    public List<Map<String, Double>> getEmployeesOverheadPercentage() {
        return employeesOverheadPercentage;
    }

    public void setEmployeesOverheadPercentage(List<Map<String, Double>> employeesOverheadPercentage) {
        this.employeesOverheadPercentage = employeesOverheadPercentage;
    }
}
