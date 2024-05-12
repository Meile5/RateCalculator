package easv.be;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Country {
    private String countryName;
    private int id;
//    private List<Team> teams;

    private Set<Team> uniqueTeams;


    public Country(String country) {
        this.countryName = country;
    }

    public Country(String country, int id) {
        this.countryName = country;
        this.id = id;
    }

    public Country(String countryName, int id, Set<Team> teams) {
        this(countryName, id);
        this.uniqueTeams = teams;
    }



    public Set<Team> getUniqueTeams() {
        return uniqueTeams;
    }

    public void setUniqueTeams(Set<Team> uniqueTeams) {
        this.uniqueTeams = uniqueTeams;
    }

    public void addTeamToCountry(Team team) {
        this.uniqueTeams.add(team);
    }

    public void removeTeamFromCountry(Team team) {
        this.uniqueTeams.remove(team);
    }


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return countryName;

    }


    /**
     * to not be used in equal comparison is used for the view only
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(countryName, country.countryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryName, id);
    }

//    public List<Team> getTeams() {
//        return teams;
//    }
//
//    public void setTeams(List<Team> teams) {
//        this.teams = teams;
//    }
}
