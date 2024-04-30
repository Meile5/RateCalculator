package easv.ui.pages;

import easv.be.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;

public class NewModel implements IModel{

    private ObservableMap<Integer, Employee> employees;

    public NewModel() {
        this.employees = FXCollections.observableHashMap();
        test();
    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() {
        return employees;
    }

    @Override
    public void addEmployee(Employee employee) {

    }

    private void test() {
        Employee employee = new Employee("Nelson", new BigDecimal(200000), new BigDecimal(10000), new BigDecimal(10), new BigDecimal(20), new BigDecimal(2000), new Country("Denmark"), new Team("IT"), EmployeeType.OVERHEAD, Currency.$);
        employee.setId(20);
        employees.put(employee.getId(), employee);
    }
}
