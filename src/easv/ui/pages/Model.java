package easv.ui.pages;

import easv.be.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

public class Model implements IModel {
    private ObservableMap<Integer, Employee> employees;

    public Model() {
        this.employees = FXCollections.observableHashMap();
        test();
    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {
        return employees;
    }

    private void test() {
        Employee employee = new Employee("Meile", new BigDecimal(200000), new BigDecimal(10000), new BigDecimal(10), new BigDecimal(20), new BigDecimal(2000), new Country("Denmark"), new Team("IT"), EmployeeType.OVERHEAD, Currency.$);
        employee.setId(20);
        employees.put(employee.getId(), employee);
    }
}