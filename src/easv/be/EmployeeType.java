package easv.be;

public enum EmployeeType {
    Overhead("overhead"),
    Resource("resource");

    private final String stringValue;

    EmployeeType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }
}
