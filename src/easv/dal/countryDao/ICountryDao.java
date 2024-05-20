package easv.dal.countryDao;

import easv.be.Country;
import easv.be.Team;
import easv.exception.RateException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ICountryDao {
    Map<String, Country> getCountries() throws RateException;

    Integer addCountry(Country country, List<Team> teams, List<Team> newTeams) throws RateException;

    void addTeamToCountry(Integer countryID, List<Team> teams, Connection conn) throws SQLException;

    List<Integer> addTeams(List<Team> teams, Connection conn) throws RateException, SQLException;

    void updateCountry(Country country, List<Team> teamsToAdd, List<Team> teamsToRemove, List<Team> newTeams) throws RateException;

    boolean deleteCountry(Country country) throws RateException;
}
