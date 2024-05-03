package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface IModel {


    ObservableMap<Integer, Employee> returnEmployees() throws SQLException, RateException;
    void deleteEmployee(Employee employee) throws RateException ;

    void setDisplayer(DisplayEmployees displayEmployees);



    void addEmployee(Employee employee, Configuration configuration) throws RateException;
    ObservableMap<String, Country> getCountries();

    /**retrieve the teams with the overhead computed*/
    Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(String country);
    /**used to reset the  index of the database retrieval */
    public void resetCurrentIndexToRetrieve();

    ObservableMap<Integer, Team> getTeams();


}
