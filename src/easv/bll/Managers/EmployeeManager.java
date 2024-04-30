package easv.bll.Managers;

import easv.be.Employee;
import easv.dal.EmployeesDAO;
import easv.dal.IEmployeeDAO;
import easv.exception.RateException;

public class EmployeeManager implements IEmployeeManager{
    private IEmployeeDAO employeeDAO;

    public EmployeeManager() throws RateException {
        this.employeeDAO = new EmployeesDAO();
    }

    @Override
    public Integer addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }
}
