package easv.Utility;

import easv.be.Employee;
import easv.be.Team;
import easv.exception.ExceptionHandler;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableMap;
import javafx.css.PseudoClass;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmployeeValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final String VALID_NAME_FORMAT = "Please enter the name of the Employee (e.g., Nelson Mandela).";
    private static final String VALID_COUNTRY_FORMAT = "Please select or enter the Country where the Employee works (e.g., Denmark).";
    private static final String VALID_TEAM_FORMAT = "Please select or enter the Team of the Employee (e.g., Project Management).";
    private static final String VALID_VALUE_FORMAT = "Please enter the value in this format XXXX or XXXX.XX (e.g., 1000 or 150000.50).";
    private static final String VALID_PERCENTAGE_FORMAT = "Please enter the percentage in this format XX or XX.XX (e.g., 59 or 70.50).";
    private static final String VALID_OVERHEAD_COST_FORMAT = "Please select one of the options (e.g., Overhead or Resource).";
    private static final String VALID_CURRENCY_FORMAT = "Please select the currency to apply (e.g., EUR or USD).";
    private final static String validNamePattern = "^[A-Za-z]+(\\s[A-Za-z]+)*$";
    private static List<String> countries = new ArrayList<>();
    private static List<Team> teams;




    /**
     * validate the user inputs for the add operation
     */
    public static boolean isEmployeeValid(Employee employee) {
        boolean isValid = true;

        if (employee.getName().isEmpty()) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("This field cannot be empty.");
        }

        if (!countries.contains(employee.getCountry().getCountryName())) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("Country Not Found: We couldn't find the country you entered. Please check your spelling and try again.");
        } else if (employee.getCountry().getCountryName().isEmpty()) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("This field cannot be empty.");
        }

        if (!teams.contains(employee.getTeam()) && employee.getTeam().getTeam().isEmpty()) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("This field cannot be empty.");
        }

        if (employee.getActiveConfiguration().getAnnualSalary() == null || employee.getActiveConfiguration().getAnnualSalary().compareTo(BigDecimal.ZERO) < 0) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("The Salary should be equal or greater than 0.");
        }

        if (employee.getActiveConfiguration().getWorkingHours() == null || employee.getActiveConfiguration().getWorkingHours().compareTo(BigDecimal.ZERO) < 0) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("The Working Hours should be equal or greater than 0.");
        }

        if (employee.getActiveConfiguration().getFixedAnnualAmount() == null || employee.getActiveConfiguration().getFixedAnnualAmount().compareTo(BigDecimal.ZERO) < 0) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("The Fixed Amount should be equal or greater than 0.");
        }

        if (employee.getActiveConfiguration().getUtilizationPercentage() == null || employee.getActiveConfiguration().getUtilizationPercentage().compareTo(BigDecimal.ZERO) < 0 || employee.getActiveConfiguration().getUtilizationPercentage().compareTo(new BigDecimal("100")) > 0) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("The Utilization % should be greater than or equal to 0 and less than or equal to 100.");
        }

        if (employee.getActiveConfiguration().getOverheadMultiplier() == null || employee.getActiveConfiguration().getOverheadMultiplier().compareTo(BigDecimal.ZERO) < 0 || employee.getActiveConfiguration().getOverheadMultiplier().compareTo(new BigDecimal("100")) > 0) {
            isValid = false;
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be greater than or equal to 0 and less than or equal to 100.");
        }

        if(employee.getCurrency() == null){
            isValid = false;
            ExceptionHandler.errorAlertMessage("Please choose a Currency.");
        }

        if(employee.getType() == null){
            isValid = false;
            ExceptionHandler.errorAlertMessage("Please choose between 'Overhead' or 'Resource'.");
        }
        return isValid;
    }

    /*
    /**
     * listeners for the inputs on textfields

    public static void addTicketListeners(MFXTextField typeTF, MFXTextField priceTF, MFXTextField quantityTF) {
        typeTF.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                typeTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            } else
                typeTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
        });

        priceTF.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue.isEmpty() || new BigDecimal(newValue).compareTo(BigDecimal.ZERO) < 0) {
                    priceTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                } else {
                    priceTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
                }
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle invalid input (not a number)
                priceTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            }
        });

        quantityTF.textProperty().addListener(((observable, oldValue, newValue) -> {
            try {
                if (newValue.isEmpty() || Integer.parseInt(newValue) <= 0) {
                    quantityTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                } else {
                    quantityTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
                }
            } catch (NumberFormatException | NullPointerException ex) {
                // Handle invalid input (not a number)
                quantityTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            }
        }));
    }
    */

    public static void addNameToolTip(MFXTextField nameTF) {
        Tooltip errorTooltip = new Tooltip(VALID_NAME_FORMAT);
        nameTF.setTooltip(errorTooltip);
    }

    public static void addCountryToolTip(MFXComboBox countryCB) {
        Tooltip tooltip = new Tooltip(VALID_COUNTRY_FORMAT);
        countryCB.setTooltip(tooltip);
    }

    public static void addTeamToolTip(MFXComboBox teamCB) {
        Tooltip tooltip = new Tooltip(VALID_TEAM_FORMAT);
        teamCB.setTooltip(tooltip);
    }

    public static void addValueToolTip(MFXTextField salaryTF, MFXTextField workingHoursTF, MFXTextField fixedValueTF) {
        Tooltip errorTooltip = new Tooltip(VALID_VALUE_FORMAT);
        salaryTF.setTooltip(errorTooltip);
        workingHoursTF.setTooltip(errorTooltip);
        fixedValueTF.setTooltip(errorTooltip);
    }

    public static void addValueToolTip(MFXTextField utilizationPercentageTF, MFXTextField overheadMultiplierTF) {
        Tooltip errorTooltip = new Tooltip(VALID_PERCENTAGE_FORMAT);
        utilizationPercentageTF.setTooltip(errorTooltip);
        overheadMultiplierTF.setTooltip(errorTooltip);
    }

    public static void addOverOrResourceToolTip(MFXComboBox overOrResourceCB) {
        Tooltip tooltip = new Tooltip(VALID_OVERHEAD_COST_FORMAT);
        overOrResourceCB.setTooltip(tooltip);
    }

    public static void addCurrencyToolTip(MFXComboBox currencyCB) {
        Tooltip tooltip = new Tooltip(VALID_CURRENCY_FORMAT);
        currencyCB.setTooltip(tooltip);
    }

    public static void getCountries(List<String> listCountries){
        countries = listCountries;
    }

    public static void getTeams(ObservableMap<Integer, Team> listTeams){
        teams = new ArrayList<>(listTeams.values());
    }

    /**add validation for the utilization percentage */
    public static void addUtilizationListener(MFXTextField percentageDisplayer){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        percentageDisplayer.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue.isEmpty()){
                pauseTransition.setOnFinished((event)->{
                    try{
                        double value =  Double.parseDouble(newValue);
                        if(value>100){
                            percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS,true);
                            return;
                        }
                        percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS,false);
                    }catch(NumberFormatException e){
                        percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS,true);
                    }
                });
                pauseTransition.playFromStart();
            }
        }));
    }
    /**add validation listeners for the input fields, to nopt be empty and to be digits
     *  if later we need to check for the cap than we need to modify the method*/

    public static void addInputDigitsListeners(MFXTextField normalDigitInputs){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        normalDigitInputs.textProperty().addListener((observable, oldValue, newValue) -> {

            pauseTransition.setOnFinished((event -> {
                double value  = parseFormattedNumber(newValue, Locale.GERMANY);
                if(Double.isNaN(value)){
                    value=parseFormattedNumber(newValue,Locale.US);
                }
                normalDigitInputs.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, Double.isNaN(value));
            }));

            pauseTransition.playFromStart();
        });
    }


    private static double parseFormattedNumber(String numberStr, Locale locale) {

        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        format.setDecimalFormatSymbols(symbols);

        try {
            Number number = format.parse(numberStr);
            return number.doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }


    /**Add validation for name and country*/
    public static void addLettersOnlyInputListener(MFXTextField input) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        input.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((e) -> {
                input.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isInputValid(newValue));
            });
            pauseTransition.playFromStart();
        }));
    }

    /**check if the inserted value contains only letters and has no empty space at the end*/
    private static boolean isInputValid(String value) {
        return !value.isEmpty() && value.matches(validNamePattern);
    }
}