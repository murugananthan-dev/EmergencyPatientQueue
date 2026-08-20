import javax.swing.SwingUtilities;

/**
 * Main.java
 * Application entry point.
 * Launches HospitalGUI on the Swing Event Dispatch Thread (EDT)
 * to ensure thread safety for all UI operations.
 */
public class Main {

    public static void main(String[] args) {
        // Enable native look-and-feel on Windows for better integration
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Fall back to default Swing look-and-feel silently
        }

        SwingUtilities.invokeLater(() -> {
            new HospitalGUI();
        });
    }
}
