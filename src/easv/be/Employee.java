
package easv.be;
import java.math.BigDecimal;
import java.util.List;


public class Employee {
    private String name;
    private EmployeeType employeeType;
    private Country country;
    private Team team;
    private int id;
    private Currency currency;
    private BigDecimal dailyRate;
    private BigDecimal hourlyRate;
    private List<Configuration> configurations;
    private Configuration latestConfiguration;
    private BigDecimal overhead;



    public void setOverhead(BigDecimal overhead) {
        this.overhead = overhead;
    }

    public Configuration getLatestConfiguration() {
        return latestConfiguration;
    }

    public void setLatestConfiguration(Configuration latestConfiguration) {
        this.latestConfiguration = latestConfiguration;
    }


    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<Configuration> configurations) {
        this.configurations = configurations;
    }

    public Employee(String name, Country country, Team team, EmployeeType employeeType, Currency currency) {
        this.name = name;
        this.country = country;
        this.team = team;
        this.employeeType = employeeType;
        this.currency = currency;
    }

    public Employee(String name, EmployeeType employeeType, Currency currency) {
        this.name = name;
        this.employeeType = employeeType;
        this.currency = currency;
    }
    public Employee(String name,EmployeeType employeeType,Currency currency,List<Configuration> configs){
        this(name,employeeType,currency);
        this.configurations=configs;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public EmployeeType getType() {
        return employeeType;
    }

    public void setType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public Currency getCurrency(){
        return currency;
    }
    public void setCurrency(){
        this.currency = currency;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", employeeType=" + employeeType +
                ", country=" + country +
                ", team=" + team +
                ", id=" + id +
                ", currency=" + currency +
                '}';
    }


    public void addConfiguration(Configuration config){
        this.configurations.add(config);
    }

    public boolean removeConfig(Configuration config){
        return this.configurations.remove(config);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getOverhead() {
        return overhead;
    }
}
