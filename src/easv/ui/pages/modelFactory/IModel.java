package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IModel {
<<<<<<< HEAD


    ObservableMap<Integer, Employee> returnEmployees() throws SQLException, RateException;
    void deleteEmployee(Employee employee) throws RateException ;

    void setDisplayer(DisplayEmployees displayEmployees);


=======
    LinkedHashMap<Integer, Employee> returnEmployees() throws SQLException, RateException;
    void deleteEmployee(Employee employee) throws RateException ;
    void addEmployee(Employee employee);
    Map<String, Country> getCountries();
>>>>>>> 0b10669f03d0d147c2f6d6384563fc76eff51de0

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


}
