package easv.be;

import java.util.List;
import java.util.Objects;

public class Team {
    private String team;
    private int id;
    private List<Employee> employees;
    private List<TeamConfiguration> teamConfigurationsHistory;
    private TeamConfiguration activeConfiguration;


    public Team(String team) {
        this.team = team;
    }

    public Team(String team, int id) {
        this.team = team;
        this.id = id;
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

    public List<TeamConfiguration> getTeamConfigurationsHistory() {
        return teamConfigurationsHistory;
    }

    public void setTeamConfigurationsHistory(List<TeamConfiguration> teamConfigurationsHistory) {
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

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}
