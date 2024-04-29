package easv.be;

import java.math.BigDecimal;

public class Employee {
    private String name;
    private BigDecimal annualSalary;
    private BigDecimal fixedAnnualAmount;
    private BigDecimal overheadMultiplier;
    private BigDecimal utilizationPercentage;
    private BigDecimal workingHours;
    private EmployeeType employeeType;
    private Country country;
    private Team team;
    private int id;
    private Currency currency;


    public Employee(String name, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours,  Country country, Team team, EmployeeType employeeType, Currency currency) {
        this.name = name;
        this.annualSalary = annualSalary;
        this.fixedAnnualAmount = fixedAnnualAmount;
        this.overheadMultiplier = overheadMultiplier;
        this.utilizationPercentage = utilizationPercentage;
        this.workingHours = workingHours;
        this.country = country;
        this.team = team;
        this.employeeType = employeeType;
        this.currency = currency;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getFixedAnnualAmount() {
        return fixedAnnualAmount;
    }

    public void setFixedAnnualAmount(BigDecimal fixedAnnualAmount) {
        this.fixedAnnualAmount = fixedAnnualAmount;
    }

    public BigDecimal getOverheadMultiplier() {
        return overheadMultiplier;
    }

    public void setOverheadMultiplier(BigDecimal overheadMultiplier) {
        this.overheadMultiplier = overheadMultiplier;
    }

    public BigDecimal getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public BigDecimal getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(BigDecimal workingHours) {
        this.workingHours = workingHours;
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

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", annualSalary=" + annualSalary +
                ", fixedAnnualAmount=" + fixedAnnualAmount +
                ", overheadMultiplier=" + overheadMultiplier +
                ", utilizationPercentage=" + utilizationPercentage +
                ", workingHours=" + workingHours +
                ", employeeType=" + employeeType +
                ", country=" + country +
                ", team=" + team +
                ", id=" + id +
                ", currency=" + currency +
                '}';
    }
}
