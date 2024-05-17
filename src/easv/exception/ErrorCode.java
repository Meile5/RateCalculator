package easv.exception;

public enum ErrorCode {
    INVALID_INPUT("Invalid Input"),
    CONNECTION_FAILED("Connection failed,please verify your network connection"),
    OPERATION_DB_FAILED("Operation failed,please verify your network connection, or try again"),
    LOADING_FXML_FAILED("Operation failed, problems reading files, please try again or restart the application"),
    POOL_CONNECTION_FAIL("Interrupted while waiting for a connection from the pool"),
    LOADING_EMPLOYEES_FAILED("Operation failed, problems loading employees, please try again or restart the application"),
    DELETING_EMPLOYEES_FAILED("Operation failed, please try again or restart the application"),
    INVALID_OVERHEADVALUE ( "Invalid percentage  value for the teams :"),
    INVALID_OVERHEAD_MESSAGE("provide a value in the following format '00.00' or '00,00' and smaller than 100"),
    UNDO_FAILED("The undo filter operation failed to be executed ,please try again"),
    SEARCH_FAILED("Operation failed, please try again or restart the application"),
    OVER_LIMIT ("Distributed overhead value is over 100 % !"),
    DISTRIBUTE_FROM_EMPTY("No team to distribute from selected !"),
    DISTRIBUTE_TO_EMPTY("No team to distribute to selected !"),
    EMPTY_OVERHEAD("Empty overhead percentage value"),
    SIMULATION_FAILED("Failed to perform simulation computation, please retry!");
    ;





    private String value;

    public String getValue() {
        return value;
    }

    ErrorCode(String value) {
        this.value = value;
    }
}
