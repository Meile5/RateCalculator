package easv.exception;

public enum ErrorCode {
    INVALID_INPUT("Invalid Input"),
    CONNECTION_FAILED("Connection failed,please verify your network connection"),
    OPERATION_DB_FAILED("Operation failed,please verify your network connection, or try again"),
    LOADING_FXML_FAILED("Operation failed, problems reading files, please try again or restart the application"),
    POOL_CONNECTION_FAIL("Interrupted while waiting for a connection from the pool"),
    LOADING_EMPLOYEES_FAILED("Operation failed, problems loading employees, please try again or restart the application"),
    DELETING_EMPLOYEES_FAILED("Operation failed, please try again or restart the application"),

    UNDO_FAILED("The undo filter operation failed to be executed ,please try again");


    private String value;

    public String getValue() {
        return value;
    }

    ErrorCode(String value) {
        this.value = value;
    }
}
