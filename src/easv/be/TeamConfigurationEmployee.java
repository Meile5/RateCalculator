package easv.be;

public class TeamConfigurationEmployee {
    private String employeeName ;
    private double employeeDailyRate;
    private double employeeHourlyRate;

    public TeamConfigurationEmployee(String employeeName, double employeeDailyRate, double employeeHourlyRate) {
        this.employeeName = employeeName;
        this.employeeDailyRate = employeeDailyRate;
        this.employeeHourlyRate = employeeHourlyRate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getEmployeeDailyRate() {
        return employeeDailyRate;
    }

    public void setEmployeeDailyRate(double employeeDailyRate) {
        this.employeeDailyRate = employeeDailyRate;
    }

    public double getEmployeeHourlyRate() {
        return employeeHourlyRate;
    }

    public void setEmployeeHourlyRate(double employeeHourlyRate) {
        this.employeeHourlyRate = employeeHourlyRate;
    }
}
