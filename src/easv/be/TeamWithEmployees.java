package easv.be;
import java.util.List;

public class TeamWithEmployees {
    private String teamName;
    private int id;

    private  List<Employee> teamMembers;
    public TeamWithEmployees(String team, int id, List<Employee> teamMembers) {
        this.teamName = team;
        this.id = id;
        this.teamMembers = teamMembers;
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
}
