package easv.be;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.format.DateTimeFormatter;

public class Configuration {
    private int configurationId;
    private BigDecimal annualSalary;
    private BigDecimal fixedAnnualAmount;
    private BigDecimal overheadMultiplier;
    private BigDecimal utilizationPercentage;
    private BigDecimal workingHours;
    private LocalDateTime savedDate;
    private boolean active;





    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return savedDate.format(formatter);
    }


    /**do not use the equal for comparison , is used only for the view
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(savedDate.toString(), that.savedDate.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedDate);
    }

    public LocalDateTime getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(LocalDateTime savedDate) {
        this.savedDate = savedDate;
    }

    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours) {
        this.annualSalary = annualSalary;
        this.fixedAnnualAmount = fixedAnnualAmount;
        this.overheadMultiplier = overheadMultiplier;
        this.utilizationPercentage = utilizationPercentage;
        this.workingHours = workingHours;
    }
    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours,LocalDateTime savedDate,boolean active) {
      this(annualSalary,fixedAnnualAmount,overheadMultiplier,utilizationPercentage,workingHours);
        this.savedDate= savedDate;
        this.active= active;
    }

    public Configuration(int configurationId ,BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours,LocalDateTime savedDate, boolean active) {
        this(annualSalary,fixedAnnualAmount,overheadMultiplier,utilizationPercentage,workingHours,savedDate,active);
        this.configurationId=configurationId;

    }



    public Configuration(int configurationId , BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, boolean active) {
        this(annualSalary,fixedAnnualAmount,overheadMultiplier,utilizationPercentage,workingHours);
        this.configurationId=configurationId;
        this.active=active;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
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
}
