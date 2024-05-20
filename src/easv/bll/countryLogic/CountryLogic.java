package easv.bll.countryLogic;

import easv.be.Country;
import easv.be.Team;
import easv.dal.countryDao.CountryDao;
import easv.dal.countryDao.ICountryDao;
import easv.exception.RateException;

import java.util.*;

public class CountryLogic implements ICountryLogic {

    private ICountryDao countryDao;


    public CountryLogic() throws RateException {
        this.countryDao = new CountryDao();
    }

    @Override
    public Map<String, Country> getCountries() throws RateException {
        return countryDao.getCountries();
    }


    /**extract the countries to be used for the  map view component*/
    public Map<String, Country> getCountriesForMap(Map<Integer, Country> operationalCountries) {
        Map<String, Country> countriesForMap = new HashMap<>();
        operationalCountries.values().forEach(e -> {
            countriesForMap.put(e.getCountryName(), e);
        });
        return countriesForMap;
    }

    @Override
    public Country addCountry(Country country, List<Team> teams, List<Team> newTeams) throws RateException {
        int regionID = countryDao.addCountry(country, teams, newTeams);
        if (regionID > 0) {
            country.setId(regionID);
            country.setTeams(teams);
        }
        return country;
    }

    @Override
    public Country updateCountry(Country country, List<Team> currentTeams, List<Team> newTeams) throws RateException {
        List<Team> teamsBeforeUpdate = country.getTeams();

        Set<Team> existingSet = new HashSet<>(teamsBeforeUpdate);
        Set<Team> newSet = new HashSet<>(currentTeams);

        Set<Team> addedTeams = new HashSet<>(newSet);
        addedTeams.removeAll(existingSet);

        Set<Team> removedTeams = new HashSet<>(existingSet);
        removedTeams.removeAll(newSet);

        countryDao.updateCountry(country, addedTeams.stream().toList(), removedTeams.stream().toList(), newTeams);
        country.setTeams(currentTeams);
        return country;
    }

    @Override
    public boolean deleteCountry(Country country) throws RateException {
        return countryDao.deleteCountry(country);
    }

    @Override
    public List<Team> checkNewTeams(List<Team> teamsToCheck, Map<Integer, Team> teams){
        List<Team> newTeams = new ArrayList<>();
        for (Team team : teamsToCheck) {
            if (team != null && !teams.containsValue(team)) {
                newTeams.add(team);
            }
        }
        return newTeams;
    }

    @Override
    public List<Team> checkExistingTeams(List<Team> teamsToCheck, ObservableMap<Integer, Team> teams) {
        List<Team> existingTeams = new ArrayList<>();
        for (Team team : teamsToCheck) {
            if (teams.containsValue(team)) {
                existingTeams.add(team);
            }
        }
        return existingTeams;
    }


}
