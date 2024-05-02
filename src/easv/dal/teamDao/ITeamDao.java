package easv.dal.teamDao;

import easv.be.Country;
import easv.be.Team;
import easv.be.TeamWithEmployees;
import easv.exception.RateException;

import java.util.List;
import java.util.Map;

public interface ITeamDao {
    /**get teams by country
     * @param country country to retrieve for
     * @param offset the index from where to retrieve
     * @param numberOfElements how manny elements to retrieve*/
    List<TeamWithEmployees> getTeamsByCountry(Country country, int offset, int numberOfElements);

    Map<Integer, Team> getTeams() throws RateException;
}
