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
    ObservableMap<Integer, Country> getCountries();

    /**retrieve the teams with the overhead computed*/
    Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(String country);
    /**used to reset the  index of the database retrieval */
    public void resetCurrentIndexToRetrieve();
}
