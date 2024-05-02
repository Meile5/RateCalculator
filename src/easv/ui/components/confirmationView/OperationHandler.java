package easv.ui.components.confirmationView;

import easv.exception.RateException;

public interface OperationHandler {
    void performOperation() throws RateException;
}
