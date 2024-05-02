package easv.ui.pages.modelFactory;

import easv.be.Country;
import easv.be.Employee;
import easv.be.TeamWithEmployees;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IModel {


    LinkedHashMap<Integer, Employee> returnEmployees() throws SQLException, RateException;




    void addEmployee(Employee employee);
    Map<String, Country> getCountries();

    /**retrieve the teams with the overhead computed*/
    List<TeamWithEmployees> getCountryTeams();
    /**used to reset the  index of the database retrieval */
    public void resetCurrentIndexToRetrieve();

    /**used for country input validation*/
    void populateValidCountries(List<String> validCountries);

    /**set the country that user has selected from the map*/
    void setSelectedCountry(String selectedCountry);


}
