package easv.be;

public class TeamConfigurationEmployee {
    private String employeeName ;
    private double employeeDailyRate;
    private double employeeHourlyRate;
    private Currency currency;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TeamConfigurationEmployee(String employeeName, double employeeDailyRate, double employeeHourlyRate, Currency currency) {
        this.employeeName = employeeName;
        this.employeeDailyRate = employeeDailyRate;
        this.employeeHourlyRate = employeeHourlyRate;
        this.currency= currency;
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

    @Override
    public String toString() {
        return currency.toString();
    }
}
