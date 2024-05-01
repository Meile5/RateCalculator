package easv.ui.pages;

import easv.be.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

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
    public void addEmployee(Employee employee) {

    }

    @Override
    public ObservableMap<Integer, Country> getCountries() {
        return null;
    }


}
