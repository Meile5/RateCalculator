package easv.be;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Team {
    private String team;
    private int id;
    private List<Employee> teamMembers;
    private List<TeamConfiguration> teamConfigurationsHistory;
    private TeamConfiguration activeConfiguration;
    private List<Country> countries;
    private List<Region> regions;



    public Team(String team) {
        this.team = team;
    }

    public Team(String team, int id) {
        this.team = team;
        this.id = id;
    }

    public Team(String team, int id, List<Employee> teamMembers, List<TeamConfiguration> teamConfigurationsHistory ) {
        this(team, id);
        this.teamMembers = teamMembers;
        this.teamConfigurationsHistory = teamConfigurationsHistory;

    }


    /**
     * to not be used in equal comparison , it is used for the view only
     */
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
        return teamMembers;
    }

    public void setEmployees(List<Employee> employees) {
        this.teamMembers = employees;
    }

    public boolean addNewTeamMember(Employee employee) {
        return this.teamMembers.add(employee);
    }

    public boolean removeTeamMember(Employee employee) {
        return this.teamMembers.remove(employee);
    }

    public boolean addNewTeamConfiguration(TeamConfiguration teamConfiguration) {
        return this.teamConfigurationsHistory.add(teamConfiguration);
    }

    public boolean removeTeamConfiguration(TeamConfiguration teamConfiguration) {
        return this.teamConfigurationsHistory.remove(teamConfiguration);
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<Employee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}
