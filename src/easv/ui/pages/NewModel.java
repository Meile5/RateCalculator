package easv.ui.pages;

import easv.be.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

public class NewModel implements IModel{

    private ObservableMap<Integer, Employee> employees;

    public NewModel() {
        this.employees = FXCollections.observableHashMap();

    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {
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
