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

    private final static String INVALID_MARKUP = "The  multiplier should be between 0 and 100";
    private final static String  INVALID_FORMAT = "Please insert a value in the following formats : '0 00 00,0 00,00 00.0 00.00'";

    private static List<String> countries = new ArrayList<>();
    private static List<Team> teams;


    /**
     * validate the user inputs for the add operation
     */
    public static boolean arePercentagesValid(MFXTextField utilization, MFXTextField overheadMultiplier) {
        boolean isValid = true;

        String utilizationText = utilization.getText();
        if (utilizationText.isEmpty()) {
            utilization.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Utilization % field cannot be empty.");
            return false;
        } else {
            BigDecimal utilizationValue = new BigDecimal(utilizationText);
            if (utilizationValue.compareTo(BigDecimal.ZERO) < 0 || utilizationValue.compareTo(new BigDecimal("100")) > 0) {
                utilization.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Utilization % should be equal or greater than 0.");
                return false;
            }
        }

        String overheadMultiplierText = overheadMultiplier.getText();
        if (overheadMultiplierText.isEmpty()) {
            overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % field cannot be empty.");
            return false;
        } else {
            BigDecimal multiplierValue = new BigDecimal(overheadMultiplierText);
            if (multiplierValue.compareTo(BigDecimal.ZERO) < 0 || multiplierValue.compareTo(new BigDecimal("100")) > 0) {
                overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be equal or greater than 0.");
                return false;
            }
        }
        return isValid;
    }


    public static boolean areNumbersValid(MFXTextField salary, MFXTextField hours, MFXTextField fixedAmount) {
        boolean isValid = true;

        String salaryText = salary.getText();
        if (salaryText.isEmpty()) {
            salary.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Salary field cannot be empty.");
            return false;
        } else {
            BigDecimal salaryValue = new BigDecimal(salaryText);
            if (salaryValue.compareTo(BigDecimal.ZERO) < 0) {
                salary.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Salary should be equal or greater than 0.");
                return false;
            }
        }

        String hoursText = hours.getText();
        if (hoursText.isEmpty()) {
            hours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Working Hours field cannot be empty.");
            return false;
        } else {
            BigDecimal hoursValue = new BigDecimal(hoursText);
            if (hoursValue.compareTo(BigDecimal.ZERO) < 0) {
                hours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Working Hours should be equal or greater than 0.");
                return false;
            }
        }

        String fixedAmountText = fixedAmount.getText();
        if (fixedAmountText.isEmpty()) {
            fixedAmount.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Fixed Annual Amount field cannot be empty.");
            return false;
        } else {
            BigDecimal fixedAmountValue = new BigDecimal(fixedAmountText);
            if (fixedAmountValue.compareTo(BigDecimal.ZERO) < 0) {
                fixedAmount.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Fixed Annual Amount should be equal or greater than 0.");
                return false;
            }
        }
        return isValid;
    }

    public static boolean areNamesValid(MFXTextField name, MFXComboBox country, MFXComboBox team) {
        boolean isValid = true;

        if (name.getText().isEmpty()) {
            name.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Name field cannot be empty.");
            return false;
        }

        if (!countries.contains(country.getText())) {
            country.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Country Not Found: We couldn't find the country you entered. Please check your spelling and try again.");
            return false;
        } else if (country.getText().isEmpty()) {
            ExceptionHandler.errorAlertMessage("Country  field cannot be empty.");
            return false;
        }
        if (!teams.contains((Team) team.getSelectedItem()) && team.getText().isEmpty()) {
            team.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Team field cannot be empty.");
            return false;
        }
        return isValid;
    }

    public static boolean isItemSelected(MFXComboBox currency, MFXComboBox overOrResource) {
        boolean isValid = true;

        if (currency.getSelectedItem() == null || currency.getText().isEmpty()) {
            currency.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Please select a currency.");
            return false;
        }

        if (overOrResource.getSelectedItem() == null || overOrResource.getText().isEmpty()) {
            overOrResource.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Please select Overhead or Resource.");
            return false;
        }
        return isValid;
    }


    /**
     * validate the format of the  markupMultiplier and grossMarginMultiplier
     *
     * @param markup
     * @param grossMargin
     */
    public static boolean validateAditionalMultipliers(MFXTextField markup, MFXTextField grossMargin) {

        System.out.println(markup.getText().matches("^\\d{0,3}([.,]\\d{1,2})?$"));

        if (!markup.getText().isEmpty()) {
            if(!markup.getText().matches("^\\d{0,3}([.,]\\d{1,2})?$")){
                ExceptionHandler.errorAlertMessage(INVALID_FORMAT);
                return false;
            }
            boolean isValid = isValueWithinValidRange(markup.getText());
            markup.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValid);
            ExceptionHandler.errorAlertMessage(INVALID_MARKUP);
            System.out.println("Markup not valid");
            return false;
        }
        if (!grossMargin.getText().isEmpty()) {

            if(!grossMargin.getText().matches("^\\d{0,3}([.,]\\d{1,2})?$")){
                ExceptionHandler.errorAlertMessage(INVALID_FORMAT);
                return false;
            }
            boolean isValid = isValueWithinValidRange(grossMargin.getText());
            grossMargin.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValid);
            ExceptionHandler.errorAlertMessage(INVALID_MARKUP);
            System.out.println("Markupgross not valid");
            return false;
        }

        System.out.println("all valid");
        return true;
    }


    /**
     * check if the  input is a valid number between 0 an 100
     */
    private static boolean isValueWithinValidRange(String inputValue) {
        double value = Double.parseDouble(convertToDecimalPoint(inputValue));
        return value > 0 && value <= 100;
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

    //TODO the overOrResource field need to be disabled from editing , we only supoort overhead and resource
    //if the user enters another value we will have massive errors
    public static void addOverOrResourceToolTip(MFXComboBox overOrResourceCB) {
        Tooltip tooltip = new Tooltip(VALID_OVERHEAD_COST_FORMAT);
        overOrResourceCB.setTooltip(tooltip);
    }

    //TODO the currency field needs to be disabled from editing, we only support usd and dollars
    //  if the user enter another value we will have massive errors
    public static void addCurrencyToolTip(MFXComboBox currencyCB) {
        Tooltip tooltip = new Tooltip(VALID_CURRENCY_FORMAT);
        currencyCB.setTooltip(tooltip);
    }

    public static void getCountries(List<String> listCountries) {
        countries = listCountries;
    }

    public static void getTeams(ObservableMap<Integer, Team> listTeams) {
        teams = new ArrayList<>(listTeams.values());
    }


    /**
     * add validation for the  percentage input fields that can not be empty
     *
     * @param percentageDisplayer the container off the percentage values
     */
    public static void addNonEmptyPercentageListener(MFXTextField percentageDisplayer) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        percentageDisplayer.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event) -> {
                try {
                    if (!newValue.matches("^\\d{0,3}([.,]\\d{1,2})?$")) {
                        percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                        return;
                    }
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueWithinValidRange(newValue));
                } catch (NumberFormatException e) {
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                }
            });
            pauseTransition.playFromStart();
        }));

    }


    /**
     * add validation listeners for the markup and the grossMargin multipliers,
     * they can be empty;
     */
    public static void addAdditionalMarkupsListeners(MFXTextField percentageDisplayer) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        percentageDisplayer.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event) -> {
                if (newValue.isEmpty()) {
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
                    return;
                }
                if (!newValue.matches("^\\d{1,3}([.,]\\d{1,2})?$")) {
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                    return;
                }
                percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueWithinValidRange(newValue));
            });
            pauseTransition.playFromStart();
        }));

    }


    /**
     * if the inputs are not empty  check if they are  in the following formats
     * 0 00 00.00  and between 0 and 100
     *
     * @param percentageDisplayer the input that is accepting percentage values
     * @param pauseTransition     the transition that will change the color off the input
     * @param value               input value that needs to be validated
     */
    private static void percentageInputsValidation(MFXTextField percentageDisplayer, PauseTransition pauseTransition, String value) {
        pauseTransition.setOnFinished((event) -> {
            try {
                if (!value.matches("^\\d{1,3}([.,]\\d{1,2})?$")) {
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                    return;
                }
                percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueWithinValidRange(value));
            } catch (NumberFormatException e) {
                percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            }
        });
    }


    /**
     * add validation listeners for the input fields, to not be empty and to be digits
     * if later we need to check for the cap than we need to modify the method
     */

    public static void addInputDigitsListeners(MFXTextField normalDigitInputs) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        normalDigitInputs.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event -> {
                double value = parseMultiplierToNumber(newValue, Locale.GERMANY);
                if (Double.isNaN(value)) {
                    value = parseMultiplierToNumber(newValue, Locale.US);
                }
                boolean isValueValid = !Double.isNaN(value) && value > 0;
                normalDigitInputs.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueValid);
            }));
            pauseTransition.playFromStart();
        });
    }


    /**
     * convert the format of the input strings to  accept US and Europe values
     */
    private static double parseMultiplierToNumber(String numberStr, Locale locale) {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        if (locale.equals(Locale.GERMANY)) {
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');
        } else if (locale.equals(Locale.US)) {
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
        }
        format.setDecimalFormatSymbols(symbols);
        try {
            Number number = format.parse(numberStr);
            return number.doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }


    //convert form comma decimal to point decimal
    private static String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }


    /**
     * Add validation for name and country
     */
    public static void addLettersOnlyInputListener(MFXTextField input) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        input.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((e) -> {
                input.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isInputValid(newValue));
            });
            pauseTransition.playFromStart();
        }));
    }

    /**
     * check if the inserted value contains only letters and has no empty space at the end
     */
    private static boolean isInputValid(String value) {
        return !value.isEmpty() && value.matches(validNamePattern);
    }
}
