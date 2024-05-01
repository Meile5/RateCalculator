package easv.ui.pages;

import easv.be.Employee;
import javafx.collections.ObservableMap;

import java.sql.SQLException;

public interface IModel {
    ObservableMap<Integer, Employee> returnEmployees() throws SQLException;
}
