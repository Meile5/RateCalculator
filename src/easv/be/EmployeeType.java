package easv.be;

public enum EmployeeType {
    OVERHEAD("Overhead"),
    RESOURCE("Resource");

    private final String stringValue;

    EmployeeType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }
}
