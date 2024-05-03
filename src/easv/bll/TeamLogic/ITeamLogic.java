package easv.bll.TeamLogic;

import easv.be.Country;
import easv.be.Team;
import easv.be.TeamWithEmployees;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ITeamLogic  {


  Map<Integer, Team> getTeams() throws RateException;
    /**

     retrieve the teams and related team overhead for a specific country*
     @param country          country to retrieve for
     @param offset           the index from where to retrieve
     @param numberOfElements how manny elements to retrieve
     */
    List<TeamWithEmployees> getTeamsOverheadByCountry(Country country, int offset, int numberOfElements);

}
