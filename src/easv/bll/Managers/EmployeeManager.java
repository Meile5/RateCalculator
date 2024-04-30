package easv.bll.Managers;

import easv.be.Employee;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;

public class EmployeeManager implements IEmployeeManager{
    private IEmployeeDAO employeeDAO;

    public EmployeeManager() {
        this.employeeDAO = new EmployeesDAO();
    }

    @Override
    public Integer addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }
}
