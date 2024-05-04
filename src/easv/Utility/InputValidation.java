package easv.Utility;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.util.Duration;

public class InputValidation {

    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

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








}
