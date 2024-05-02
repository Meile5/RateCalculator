package easv.ui.pages.modelFactory;

import easv.be.*;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NewModel implements IModel{

    private LinkedHashMap<Integer, Employee> employees;

    public NewModel() {
        this.employees = new LinkedHashMap<>();

    }

    @Override
    public LinkedHashMap<Integer, Employee> returnEmployees() {
        return employees;
    }

    @Override
    public void deleteEmployee(Employee employee) throws RateException {

    }

    @Override
    public void addEmployee(Employee employee) {

    }

    @Override
    public ObservableMap<Integer, Country> getCountries() {
        return null;
    }

    @Override
    public Map<TeamWithEmployees, List<BigDecimal>> getCountryTeams(String country) {
        return null;
    }

    @Override
    public void resetCurrentIndexToRetrieve() {

    }

    @Override
    public ObservableMap<Integer, Team> getTeams() {
        return null;
    }


}
