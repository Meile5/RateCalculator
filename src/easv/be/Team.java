package easv.be;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Team {
    private String team;
    private int id;
//    private List<Employee> employees;
    private Set<Employee> uniqueEmployees;
    private Set<TeamConfiguration> teamConfigurationsHistory;
    private TeamConfiguration activeConfiguration;


    public Team(String team) {
        this.team = team;
    }

    public Team(String team, int id) {
        this.team = team;
        this.id = id;
    }

    public Team (String team, int id, Set<Employee> teamMembers){
        this(team,id);
        this.uniqueEmployees=teamMembers;
    }




    /** to not be used in equal comparison , it is used for the view only*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team1 = (Team) o;
        return Objects.equals(team, team1.team);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team);
    }

    public Set<Employee> getUniqueEmployees() {
        return uniqueEmployees;
    }


    public void addTeamConfiguration(TeamConfiguration  teamConfiguration){
        this.teamConfigurationsHistory.add(teamConfiguration);
    }

    public void removeTeamConfiguration(TeamConfiguration teamConfiguration){
        this.teamConfigurationsHistory.remove(teamConfiguration);
    }



    public void setUniqueEmployees(Set<Employee> uniqueEmployees) {
        this.uniqueEmployees = uniqueEmployees;
    }

    public void addUniqueEmployee(Employee employee){
        this.uniqueEmployees.add(employee);
    }

    public void removeUniqueEmployee(Employee employee){
        this.uniqueEmployees.remove(employee);
    }

    public Set<TeamConfiguration> getTeamConfigurationsHistory() {
        return teamConfigurationsHistory;
    }

    public void setTeamConfigurationsHistory(Set<TeamConfiguration> teamConfigurationsHistory) {
        this.teamConfigurationsHistory = teamConfigurationsHistory;
    }

    public TeamConfiguration getActiveConfiguration() {
        return activeConfiguration;
    }

    public void setActiveConfiguration(TeamConfiguration activeConfiguration) {
        this.activeConfiguration = activeConfiguration;
    }



    public String getTeamName() {
        return team;
    }

    public void setTeamName(String team) {
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return team;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

//    public List<Employee> getEmployees() {
//        return employees;
//    }
//
//    public void setEmployees(List<Employee> employees) {
//        this.employees = employees;
//    }
}
