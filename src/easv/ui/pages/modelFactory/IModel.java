package easv.ui.pages.modelFactory;

import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IModel {
    LinkedHashMap<Integer, Employee> returnEmployees() throws SQLException, RateException;
    void deleteEmployee(Employee employee) throws RateException ;

    void addEmployee(Employee employee, Configuration configuration) throws RateException;
    ObservableMap<String, Country> getCountries();

    /**retrieve the teams with the overhead computed*/
    Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(String country);
    /**used to reset the  index of the database retrieval */
    public void resetCurrentIndexToRetrieve();

    ObservableMap<Integer, Team> getTeams();
    /**used for country input validation*/
    void populateValidCountries(List<String> validCountries);

    /**set the country that user has selected from the map*/
    void setSelectedCountry(String selectedCountry);

}
