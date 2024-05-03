package easv.dal;

import easv.be.Configuration;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public interface IEmployeeDAO {
    LinkedHashMap<Integer, Employee> returnEmployees() throws RateException;

    Integer addEmployee(Employee employee, boolean newCountry, boolean newTeam, Configuration configuration) throws RateException;
    Boolean deleteEmployee(Employee employee) throws RateException;
    void addNewCountryOrTeam(Employee employee, boolean newCountry, boolean newTeam, Connection conn) throws RateException, SQLException;
    Integer addCountry(Country country, Connection conn) throws RateException, SQLException;
    Integer addTeam(Team team, Connection conn) throws RateException, SQLException;
    Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException;
    void addEmployeeConfiguration(int employeeID, int configurationID, Connection conn) throws RateException, SQLException;

}
