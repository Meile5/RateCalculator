package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IModel {



    ObservableMap<Integer, Employee> returnEmployees() throws RateException;
    void deleteEmployee(Employee employee) throws RateException ;

    void setDisplayer(DisplayEmployees displayEmployees);



    Map<String, Country> getCountries();


    void addEmployee(Employee employee, Configuration configuration) throws RateException;


    /**retrieve the teams with the overhead computed*/
    List<TeamWithEmployees> getCountryTeams();
    /**used to reset the  index of the database retrieval */
    public void resetCurrentIndexToRetrieve();

    ObservableMap<Integer, Team> getTeams();
    /**used for country input validation*/
    void populateValidCountries(List<String> validCountries);

    /**set the country that user has selected from the map*/
    void setSelectedCountry(String selectedCountry);

    /**get countries values as a Observable list */
    ObservableList<Country> getCountiesValues();


    /**save the updated employee to the database*/
    boolean updateEditedEmployee(Employee employee, Employee editedEmployee);
}
