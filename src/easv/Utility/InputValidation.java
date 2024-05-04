package easv.Utility;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.util.Duration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class InputValidation {

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private final static String validNamePattern = "^[A-Za-z]+(\\s[A-Za-z]+)*$";

    public static void  addNameListener(MFXTextField textField){

    }

    public static void addCountryListenerForText(MFXComboBox<String> country){
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        country.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((e) -> {
                country.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !newValue.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"));
            });
            pauseTransition.playFromStart();
        }));
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
                double value  = parseFormattedNumber(newValue,Locale.GERMANY);
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
    private static boolean isInputValid(String value) {
        return !value.isEmpty() && value.matches(validNamePattern);
    }







}
