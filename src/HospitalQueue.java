import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * HospitalQueue.java
 * Contains the core problem-solving logic using PriorityQueue and a custom Comparator.
 *
 * Priority Rules (in order):
 *   1. Higher severity treated first
 *   2. If severity equal → older patient treated first
 *   3. If severity and age equal → earlier arrival treated first
 */
public class HospitalQueue {

    /** Custom comparator implementing the three-level priority rule. */
    private static final Comparator<Patient> PATIENT_COMPARATOR = (p1, p2) -> {
        // Rule 1: Higher severity first
        if (p1.getSeverity() != p2.getSeverity()) {
            return Integer.compare(p2.getSeverity(), p1.getSeverity());
        }
        // Rule 2: Older age first
        if (p1.getAge() != p2.getAge()) {
            return Integer.compare(p2.getAge(), p1.getAge());
        }
        // Rule 3: Earlier arrival first
        return Long.compare(p1.getArrivalNumber(), p2.getArrivalNumber());
    };

    private final PriorityQueue<Patient> patientQueue;

    public HospitalQueue() {
        patientQueue = new PriorityQueue<>(PATIENT_COMPARATOR);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Adds a patient to the priority queue.
     *
     * @param patient Patient to add
     */
    public void addPatient(Patient patient) {
        patientQueue.offer(patient);
    }

    /**
     * Removes and returns the highest-priority patient.
     *
     * @return The next patient for treatment, or null if queue is empty
     */
    public Patient treatNextPatient() {
        return patientQueue.poll();
    }

    /**
     * Searches for a patient by ID without removing them.
     *
     * @param patientId The ID to search for
     * @return The matching Patient, or null if not found
     */
    public Patient searchPatient(String patientId) {
        for (Patient p : patientQueue) {
            if (p.getPatientId().equalsIgnoreCase(patientId.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns all waiting patients in actual treatment priority order
     * WITHOUT modifying the main queue.
     *
     * Works by draining a copy of the PriorityQueue.
     *
     * @return Ordered list of waiting patients
     */
    public List<Patient> getPatientsInPriorityOrder() {
        // Copy the queue so the original remains untouched
        PriorityQueue<Patient> copy = new PriorityQueue<>(patientQueue);
        List<Patient> ordered = new ArrayList<>();
        while (!copy.isEmpty()) {
            ordered.add(copy.poll());
        }
        return ordered;
    }

    /**
     * Returns the number of patients currently waiting.
     */
    public int getTotalPatients() {
        return patientQueue.size();
    }

    /**
     * Checks whether a patient ID already exists in the queue.
     *
     * @param patientId ID to check
     * @return true if the ID is already present
     */
    public boolean patientIdExists(String patientId) {
        for (Patient p : patientQueue) {
            if (p.getPatientId().equalsIgnoreCase(patientId.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if no patients are currently waiting.
     */
    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }
}
