package easv;

import easv.be.Configuration;
import easv.be.Currency;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.bll.EmployeesLogic.RateCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RateCalculatorTest {

        private RateCalculator rateCalculator;
        private Employee employee;
        private Configuration configuration;

        @BeforeEach
        void setUp() {
            rateCalculator = new RateCalculator();
            configuration = new Configuration();
            configuration.setAnnualSalary(new BigDecimal("50000"));
            configuration.setOverheadMultiplier(new BigDecimal("20"));
            configuration.setFixedAnnualAmount(new BigDecimal("10000"));
            configuration.setWorkingHours(new BigDecimal("2000"));
           // configuration.setUtilizationPercentage(new BigDecimal("80"));
            employee = new Employee("Test employee" ,EmployeeType.Overhead, Currency.USD);
            employee.setActiveConfiguration(configuration);
        }




    @Test
    void calculateEmployeeTotalDayRate() {
    }

    @Test
    void calculateEmployeeDayRateWithoutUtilization() {
    }

    @Test
    void calculateEmployeeHourlyRateWithoutUtilization() {
    }

    @Test
    void calculateHourlyRate() {
    }

    @Test
    void calculateEmployeeTotalHourlyRate() {
    }

    @Test
    void calculateEmployeeDayRateOnTeam() {
    }

    @Test
    void calculateEmployeeHourlyRateOnTeam() {
    }

    @Test
    void calculateEmployeeHourlyRateOnTeamE() {
    }

    @Test
    void calculateEmployeeDayRateOnTeamE() {
    }

    @Test
    void calculateTeamDailyRate() {
    }

    @Test
    void calculateTeamHourlyRate() {
    }

    @Test
    void calculateTeamDailyRateE() {
    }

    @Test
    void calculateTeamHourlyRateE() {
    }

    @Test
    void calculateTeamSalaryOverhead() {
    }

    @Test
    void calculateTeamOverheadWithoutPercentage() {
    }

    @Test
    void calculateProductiveOverHead() {
    }

    @Test
    void calculateEmployeeOverheadMarkupMultiplier() {
    }
}