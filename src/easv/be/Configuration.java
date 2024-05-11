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
    private double markupMultiplier;
    private double grossMargin;
    private boolean active;
    private BigDecimal dayRate;
    private BigDecimal hourlyRate;



    /**
     * Constructor used to create Configuration objects with the configuration formula and rates.

     */
    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime createdDate, boolean active, BigDecimal dayRate, BigDecimal hourlyRate) {
        this(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, active);
        this.dayRate = dayRate;
        this.hourlyRate = hourlyRate;
        this.savedDate = createdDate;
    }
    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours) {
        this.annualSalary = annualSalary;
        this.fixedAnnualAmount = fixedAnnualAmount;
        this.overheadMultiplier = overheadMultiplier;
        this.utilizationPercentage = utilizationPercentage;
        this.workingHours = workingHours;
    }


    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime savedDate, boolean active) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours);
        this.savedDate = savedDate;
        this.active = active;
    }


    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime savedDate, boolean active) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate, active);
        this.configurationId = configurationId;

    }


    /**
     * Constructor used to create Configuration objects with active configuration, without marginMultiplier and grossMargin.
     *
     * @param configurationId       Unique identifier for the configuration.
     * @param annualSalary          The annual salary.
     * @param fixedAnnualAmount     The fixed annual amount.
     * @param overheadMultiplier    The overhead multiplier.
     * @param utilizationPercentage The utilization percentage.
     * @param workingHours          The working hours.
     * @param active                The active status of the configuration.
     */

    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, boolean active) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours);
        this.configurationId = configurationId;
        this.active = active;
    }


    /**
     * Constructor used to create Configuration objects with active configuration, without marginMultiplier and grossMargin , without configurationId.
     *
     * @param annualSalary          The annual salary.
     * @param fixedAnnualAmount     The fixed annual amount.
     * @param overheadMultiplier    The overhead multiplier.
     * @param utilizationPercentage The utilization percentage.
     * @param workingHours          The working hours.
     * @param active                The active status of the configuration.
     * @param markup                The markup multiplier.
     * @param grossMargin           The gross margin.
     */
    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime savedDate, boolean active, double markup, double grossMargin) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours);
        this.markupMultiplier = markup;
        this.grossMargin = grossMargin;
        this.savedDate = savedDate;
        this.active = active;
    }


    /**
     * Constructor used to create Configuration objects with the markupMultiplier and grossMargin.
     *
     * @param configurationId       Unique identifier for the configuration.
     * @param annualSalary          The annual salary.
     * @param fixedAnnualAmount     The fixed annual amount.
     * @param overheadMultiplier    The overhead multiplier.
     * @param utilizationPercentage The utilization percentage.
     * @param workingHours          The working hours.
     * @param active                The active status of the configuration.
     * @param createdDate           date of creation
     * @param markup                The markup multiplier.
     * @param grossMargin           The gross margin.
     */
    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime createdDate, boolean active, double markup, double grossMargin) {
        this(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, active);
        this.markupMultiplier = markup;
        this.grossMargin = grossMargin;
        this.savedDate = createdDate;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String activeStatus = this.isActive() ? "active" : "";
        return savedDate.format(formatter) + " " + activeStatus;
    }


    /**
     * do not use the equal for comparison , is used only for the view
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

/**used to compare to configuration objects if are equal */
public boolean isEqualTo(Configuration other) {
    if (other == null) {
        return false;
    }
    return Objects.equals(this.getAnnualSalary(), other.getAnnualSalary())
            && Objects.equals(this.getFixedAnnualAmount(), other.getFixedAnnualAmount())
            && Objects.equals(this.getOverheadMultiplier(), other.getOverheadMultiplier())
            && Objects.equals(this.getUtilizationPercentage(), other.getUtilizationPercentage())
            && Objects.equals(this.getWorkingHours(), other.getWorkingHours())
            && Objects.equals(this.getMarkupMultiplier(), other.getMarkupMultiplier())
            && Objects.equals(this.getGrossMargin(), other.getGrossMargin());
}


public String printConfiguration(){

        return "Configuration{" +
                "configurationId=" + configurationId +
                ", annualSalary=" + annualSalary +
                ", fixedAnnualAmount=" + fixedAnnualAmount +
                ", overheadMultiplier=" + overheadMultiplier +
                ", utilizationPercentage=" + utilizationPercentage +
                ", workingHours=" + workingHours +
                ", savedDate=" + savedDate +
                ", markupMultiplier=" + markupMultiplier +
                ", grossMargin=" + grossMargin +
                ", active=" + active +
                '}';

    }


    public LocalDateTime getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(LocalDateTime savedDate) {
        this.savedDate = savedDate;
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

    public double getMarkupMultiplier() {
        return markupMultiplier;
    }

    public void setMarkupMultiplier(double markupMultiplier) {
        this.markupMultiplier = markupMultiplier;
    }

    public double getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(double grossMargin) {
        this.grossMargin = grossMargin;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
