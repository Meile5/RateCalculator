package easv.ui.pages;

import easv.be.*;
import easv.bll.EmployeeLogic;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.SQLException;

public class Model implements IModel {
    private ObservableMap<Integer, Employee> employees;
    private EmployeeLogic employeeLogic;

    public Model() {
        this.employees = FXCollections.observableHashMap();
        employeeLogic = new EmployeeLogic();

    }

    @Override
    public ObservableMap<Integer, Employee> returnEmployees() throws SQLException {
        employees = employeeLogic.returnEmployees();
        return employees;
    }


}