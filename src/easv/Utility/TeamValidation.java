package easv.Utility;

import easv.be.Employee;
import easv.exception.ExceptionHandler;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.math.BigDecimal;
import java.util.Map;

public class TeamValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    public static boolean isPercentageValid(MFXTextField addUtil, Employee employee) {
        String percentageText = addUtil.getText().trim();

        // Validate input is not empty and is a valid number
        if (percentageText.isEmpty() || !isNumeric(percentageText)) {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            addUtil.setTooltip(new Tooltip("Please enter a valid number."));
            return false;
        }

        BigDecimal inputUtilization = new BigDecimal(percentageText);
        BigDecimal remainingUtilization = calculateRemainingUtilization(employee.getUtilPerTeams());
        if (remainingUtilization != null && inputUtilization.compareTo(remainingUtilization) > 0) {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            addUtil.setTooltip(new Tooltip("Utilization exceeds the remaining available utilization for the employee."));
            return false;
        } else {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
            addUtil.setTooltip(null);
            return true;
        }
    }

    private static BigDecimal calculateRemainingUtilization(Map<Integer, BigDecimal> utilPerTeams) {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        for (BigDecimal utilization : utilPerTeams.values()) {
            if (utilization != null) {
                totalUtilization = totalUtilization.add(utilization);
            }
        }
        return BigDecimal.valueOf(100).subtract(totalUtilization);
    }

    private static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
   /* public static boolean isPercentageValid(MFXTextField addUtil, Employee employee) {
        String percentageText = addUtil.getText();
        BigDecimal inputUtilization = new BigDecimal(percentageText);
            BigDecimal remainingUtilization = calculateRemainingUtilization(employee.getUtilPerTeams());
            if (remainingUtilization != null && inputUtilization.compareTo(remainingUtilization) > 0) {
                addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                addUtil.setTooltip(new Tooltip("Utilization exceeds the remaining available utilization for the employee."));
                return false;
            } else {
                addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
                return true;
            }

    }


    private static BigDecimal calculateRemainingUtilization(Map<Integer, BigDecimal> utilPerTeams) {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        for (BigDecimal utilization : utilPerTeams.values()) {
            if (utilization != null) {
                totalUtilization = totalUtilization.add(utilization);
            }
        }
        return BigDecimal.valueOf(100).subtract(totalUtilization);
*/
}
