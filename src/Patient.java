/**
 * Patient.java
 * Represents a patient in the emergency queue.
 * Encapsulates all patient data with proper getters.
 */
public class Patient {

    private String patientId;
    private String name;
    private int age;
    private int severity;
    private long arrivalNumber;

    /**
     * Constructs a new Patient with the given details.
     *
     * @param patientId     Unique patient identifier
     * @param name          Patient's full name
     * @param age           Patient's age in years
     * @param severity      Severity level (1=Minor to 5=Critical)
     * @param arrivalNumber Sequential arrival number for tie-breaking
     */
    public Patient(String patientId, String name, int age, int severity, long arrivalNumber) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.severity = severity;
        this.arrivalNumber = arrivalNumber;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getSeverity() {
        return severity;
    }

    public long getArrivalNumber() {
        return arrivalNumber;
    }

    /**
     * Returns a human-readable severity label.
     *
     * @return Severity name string
     */
    public String getSeverityName() {
        switch (severity) {
            case 1: return "Minor";
            case 2: return "Low";
            case 3: return "Moderate";
            case 4: return "Serious";
            case 5: return "Critical";
            default: return "Unknown";
        }
    }

    /**
     * Returns severity in display format, e.g. "5 - Critical"
     */
    public String getSeverityDisplay() {
        return severity + " - " + getSeverityName();
    }

    @Override
    public String toString() {
        return "Patient{id=" + patientId + ", name=" + name
                + ", age=" + age + ", severity=" + getSeverityDisplay()
                + ", arrival=" + arrivalNumber + "}";
    }
}
