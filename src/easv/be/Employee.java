
package easv.be;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


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
    private BigDecimal overhead;
    private Configuration  activeConfiguration;

    //static because it will be available for all objects
    private static final int  workingHours = 8;


    public void setOverhead(BigDecimal overhead) {
        this.overhead = overhead;
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



    /**equals method is used to compare  if the original employee is equal with the
     * edited employee based on name,country, team, employee type ,currency and id */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id && Objects.equals(name, employee.name) && employeeType == employee.employeeType && Objects.equals(country, employee.country) && Objects.equals(team, employee.team) && currency == employee.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, employeeType, country, team, id, currency, configurations, activeConfiguration);
    }

    public Configuration getActiveConfiguration() {
        return activeConfiguration;
    }

    public void setActiveConfiguration(Configuration activeConfiguration) {
        deactivateOldConfiguration();
        this.activeConfiguration = activeConfiguration;
    }
    private void deactivateOldConfiguration() {
        for (Configuration config : this.configurations) {
            if (config.isActive()) {
                config.setActive(false);
            }
        }
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
        return name;
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
