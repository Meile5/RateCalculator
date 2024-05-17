package easv.be;

import easv.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** * The  DistributionValidation class is used to store and manage error codes
 * and their associated integer values. Each error code is mapped to a list of integers
 * representing specific teams input with invalid  values.*/

public class DistributionValidation {
     private Map<ErrorCode, List<Integer>> errorValues;


    public DistributionValidation() {
        this.errorValues =  new HashMap<>();
    }

    public Map<ErrorCode, List<Integer>> getErrorValues() {
        return errorValues;
    }

    public void setErrorValues(Map<ErrorCode, List<Integer>> errorValues) {
        this.errorValues = errorValues;
    }

}
