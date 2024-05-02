package easv.ui.pages.modelFactory;

import easv.be.*;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableMap;

import java.util.LinkedHashMap;

public class NewModel implements IModel {

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
