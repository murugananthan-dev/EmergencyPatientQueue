import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * HospitalGUI.java
 * Java Swing graphical user interface for the Emergency Patient Priority Queue System.
 * Handles all user interactions and delegates business logic to HospitalQueue.
 */
public class HospitalGUI extends JFrame {

    // ── Business Logic ────────────────────────────────────────────────────────
    private final HospitalQueue hospitalQueue = new HospitalQueue();
    private long arrivalCounter = 1;
    private int patientIdCounter = 1; // auto-generates P001, P002, …

    // ── Color Palette ─────────────────────────────────────────────────────────
    private static final Color BG_DARK        = new Color(15,  23,  42);   // deep navy
    private static final Color BG_PANEL       = new Color(22,  33,  62);   // panel navy
    private static final Color BG_CARD        = new Color(30,  41,  82);   // card surface
    private static final Color ACCENT_BLUE    = new Color(56, 139, 253);   // neon blue
    private static final Color ACCENT_TEAL    = new Color(34, 211, 238);   // teal
    private static final Color TEXT_PRIMARY   = new Color(226, 232, 240);  // near-white
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);  // slate
    private static final Color BORDER_COLOR   = new Color(51,  65, 113);   // muted border

    // Severity badge colors
    private static final Color CLR_CRITICAL   = new Color(239,  68,  68);  // red
    private static final Color CLR_SERIOUS    = new Color(249, 115,  22);  // orange
    private static final Color CLR_MODERATE   = new Color(234, 179,   8);  // yellow
    private static final Color CLR_LOW        = new Color( 34, 197,  94);  // green
    private static final Color CLR_MINOR      = new Color(100, 116, 139);  // slate

    // ── Input Components ──────────────────────────────────────────────────────
    private JTextField txtPatientId;  // read-only — shows next auto-generated ID
    private JTextField txtSearchId;   // editable — used by Search Patient
    private JTextField txtName;
    private JTextField txtAge;
    private JComboBox<String> cmbSeverity;

    // ── Table ─────────────────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
    private JTable patientTable;

    // ── Status ────────────────────────────────────────────────────────────────
    private JLabel lblPatientCount;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_SUB     = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_INPUT   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_TABLE   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_HEADER  = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font FONT_STATUS  = new Font("Segoe UI", Font.BOLD,  13);

    // ── Constructor ───────────────────────────────────────────────────────────

    public HospitalGUI() {
        initializeGUI();
    }

    // =========================================================================
    // GUI Initialization
    // =========================================================================

    private void initializeGUI() {
        setTitle("Emergency Patient Priority Queue System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(980, 660);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // Window close → confirm exit
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                confirmExit();
            }
        });

        add(createHeaderPanel(),  BorderLayout.NORTH);
        add(createCenterPanel(),  BorderLayout.CENTER);
        add(createStatusBar(),    BorderLayout.SOUTH);

        setVisible(true);
        txtName.requestFocusInWindow(); // ID field is read-only; focus on Name
    }

    // =========================================================================
    // Header
    // =========================================================================

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(18, 24, 16, 24));

        // Left: title block
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel lblTitle = new JLabel("🏥  EMERGENCY PATIENT PRIORITY QUEUE");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(ACCENT_TEAL);

        JLabel lblSub = new JLabel("Hospital Emergency Management System  ·  Priority: Severity → Age → Arrival");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(TEXT_SECONDARY);

        titleBlock.add(lblTitle);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(lblSub);

        header.add(titleBlock, BorderLayout.WEST);

        // Separator line at bottom
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR),
                new EmptyBorder(18, 24, 16, 24)
        ));

        return header;
    }

    // =========================================================================
    // Center: input + table
    // =========================================================================

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(14, 16, 8, 16));

        center.add(createLeftPanel(),  BorderLayout.WEST);
        center.add(createTablePanel(), BorderLayout.CENTER);

        return center;
    }

    // ── Left Panel (Input + Buttons) ──────────────────────────────────────────

    private JPanel createLeftPanel() {
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(270, 0));

        left.add(createInputPanel());
        left.add(Box.createVerticalStrut(12));
        left.add(createButtonPanel());

        return left;
    }

    private JPanel createInputPanel() {
        JPanel card = createCard("Patient Information");

        GridBagLayout gbl = new GridBagLayout();
        JPanel form = new JPanel(gbl);
        form.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.insets  = new Insets(4, 4, 4, 4);
        gc.weightx = 1.0;

        // Patient ID — read-only, auto-generated
        txtPatientId = makeTextField("Auto-generated");
        txtPatientId.setEditable(false);
        txtPatientId.setText(generatePatientIdPreview()); // show P001 on startup
        txtPatientId.setForeground(ACCENT_TEAL);
        txtPatientId.setToolTipText("Patient ID is automatically generated");
        addFormRow(form, gc, 0, "Patient ID (Auto):", txtPatientId);

        // Name
        addFormRow(form, gc, 1, "Name:", txtName = makeTextField("e.g. Arjun"));

        // Age
        addFormRow(form, gc, 2, "Age:", txtAge = makeTextField("1 – 120"));

        // Severity
        String[] severities = {
            "1 - Minor", "2 - Low", "3 - Moderate", "4 - Serious", "5 - Critical"
        };
        cmbSeverity = new JComboBox<>(severities);
        cmbSeverity.setFont(FONT_INPUT);
        cmbSeverity.setBackground(BG_CARD);
        cmbSeverity.setForeground(TEXT_PRIMARY);
        cmbSeverity.setSelectedIndex(4); // default Critical
        addFormRow(form, gc, 3, "Severity:", cmbSeverity);

        // Search ID — separate editable field for Search Patient
        addFormRow(form, gc, 4, "Search by Patient ID:", txtSearchId = makeTextField("e.g. P003"));

        card.add(form, BorderLayout.CENTER);
        return card;
    }

    private JPanel createButtonPanel() {
        JPanel card = createCard("Actions");

        JPanel btnGrid = new JPanel(new GridLayout(5, 1, 0, 8));
        btnGrid.setOpaque(false);
        btnGrid.setBorder(new EmptyBorder(4, 4, 4, 4));

        btnGrid.add(makeButton("➕  Add Patient",      ACCENT_BLUE,    e -> addPatient()));
        btnGrid.add(makeButton("💉  Treat Next",       new Color(16, 185, 129), e -> treatNextPatient()));
        btnGrid.add(makeButton("🔍  Search Patient",   new Color(139, 92, 246),  e -> searchPatient()));
        btnGrid.add(makeButton("🗑   Clear Fields",    new Color(71, 85, 105),   e -> clearFields()));
        btnGrid.add(makeButton("❌  Exit",             new Color(185, 28, 28),   e -> confirmExit()));

        card.add(btnGrid, BorderLayout.CENTER);
        return card;
    }

    // ── Right Panel (Table) ───────────────────────────────────────────────────

    private JPanel createTablePanel() {
        JPanel card = createCard("Waiting Patients — Priority Order");

        String[] columns = {"Priority", "Patient ID", "Name", "Age", "Severity", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // read-only table
            }
        };

        patientTable = new JTable(tableModel);
        patientTable.setFont(FONT_TABLE);
        patientTable.setBackground(BG_CARD);
        patientTable.setForeground(TEXT_PRIMARY);
        patientTable.setSelectionBackground(new Color(56, 139, 253, 80));
        patientTable.setSelectionForeground(TEXT_PRIMARY);
        patientTable.setGridColor(BORDER_COLOR);
        patientTable.setRowHeight(30);
        patientTable.setShowVerticalLines(true);
        patientTable.setIntercellSpacing(new Dimension(1, 1));
        patientTable.setFillsViewportHeight(true);

        // Column widths
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        patientTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        patientTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        patientTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        patientTable.getColumnModel().getColumn(4).setPreferredWidth(130);
        patientTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Center-align Priority, Patient ID, Age, Status columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        patientTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        patientTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        patientTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        patientTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Severity column: colored renderer
        patientTable.getColumnModel().getColumn(4).setCellRenderer(new SeverityCellRenderer());

        // Header styling
        JTableHeader header = patientTable.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(new Color(30, 41, 82));
        header.setForeground(ACCENT_TEAL);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBackground(BG_CARD);
        scrollPane.getViewport().setBackground(BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // Status Bar
    // =========================================================================

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        bar.setBackground(BG_PANEL);
        bar.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, BORDER_COLOR));

        JLabel icon = new JLabel("📊");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        lblPatientCount = new JLabel("Total Waiting Patients: 0");
        lblPatientCount.setFont(FONT_STATUS);
        lblPatientCount.setForeground(ACCENT_TEAL);

        bar.add(icon);
        bar.add(lblPatientCount);

        return bar;
    }

    // =========================================================================
    // Business Action Handlers
    // =========================================================================

    /**
     * Validates name and age, auto-generates the Patient ID, and adds the patient.
     */
    private void addPatient() {
        String name     = txtName.getText().trim();
        String ageText  = txtAge.getText().trim();
        int severityIdx = cmbSeverity.getSelectedIndex(); // 0-based → severity = idx+1

        // ── Validation (ID is auto-generated, so no ID check needed) ──────────
        if (name.isEmpty()) {
            showError("Patient name cannot be empty.");
            txtName.requestFocusInWindow();
            return;
        }
        if (ageText.isEmpty()) {
            showError("Age cannot be empty.");
            txtAge.requestFocusInWindow();
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException ex) {
            showError("Age must be a valid whole number.");
            txtAge.requestFocusInWindow();
            return;
        }

        if (age <= 0) {
            showError("Age must be greater than 0.");
            txtAge.requestFocusInWindow();
            return;
        }
        if (age > 120) {
            showError("Age must be 120 or less.");
            txtAge.requestFocusInWindow();
            return;
        }

        int severity = severityIdx + 1; // 1-based

        // ── Auto-generate ID and enqueue ──────────────────────────────────────
        String patientId = generatePatientId(); // increments counter
        Patient patient = new Patient(patientId, name, age, severity, arrivalCounter++);
        hospitalQueue.addPatient(patient);

        // ── Update UI ─────────────────────────────────────────────────────────
        refreshTable();
        updatePatientCount();
        clearFields(); // clears name/age/severity; ID field updates to next preview

        showInfo("Patient \"" + name + "\" added as " + patientId + " successfully.");
    }

    /**
     * Treats (removes) the highest-priority patient and shows their details.
     */
    private void treatNextPatient() {
        if (hospitalQueue.isEmpty()) {
            showWarning("No patients are currently waiting in the queue.");
            return;
        }

        // Peek at who is next without removing yet
        Patient next = hospitalQueue.getPatientsInPriorityOrder().get(0);

        String details =
                "<html><body style='font-family:Segoe UI; font-size:13px; padding:6px;'>"
                + "<b style='color:#22d3ee; font-size:15px;'>Next Patient for Treatment</b><br><br>"
                + "<b>Patient ID :</b> " + next.getPatientId() + "<br>"
                + "<b>Name       :</b> " + next.getName()      + "<br>"
                + "<b>Age        :</b> " + next.getAge()       + "<br>"
                + "<b>Severity   :</b> " + next.getSeverityDisplay()
                + "</body></html>";

        int confirm = JOptionPane.showConfirmDialog(
                this, details, "Treat Next Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE
        );

        if (confirm == JOptionPane.OK_OPTION) {
            hospitalQueue.treatNextPatient(); // actually remove
            refreshTable();
            updatePatientCount();
            showInfo("Patient \"" + next.getName() + "\" has been sent for treatment.");
        }
    }

    /**
     * Searches for a patient using the dedicated Search ID field (txtSearchId).
     * The main Patient ID field is read-only (shows next auto ID), so search
     * uses a separate input to avoid confusion.
     */
    private void searchPatient() {
        String id = txtSearchId.getText().trim();

        if (id.isEmpty()) {
            showWarning("Please enter a Patient ID in the 'Search by Patient ID' field.");
            txtSearchId.requestFocusInWindow();
            return;
        }

        Patient found = hospitalQueue.searchPatient(id);

        if (found == null) {
            showWarning("Patient with ID \"" + id + "\" was not found in the queue.");
            return;
        }

        // Highlight matching row in table
        highlightPatientRow(found.getPatientId());

        String details =
                "<html><body style='font-family:Segoe UI; font-size:13px; padding:6px;'>"
                + "<b style='color:#22d3ee; font-size:15px;'>Patient Found</b><br><br>"
                + "<b>Patient ID :</b> " + found.getPatientId() + "<br>"
                + "<b>Name       :</b> " + found.getName()      + "<br>"
                + "<b>Age        :</b> " + found.getAge()       + "<br>"
                + "<b>Severity   :</b> " + found.getSeverityDisplay()
                + "</body></html>";

        JOptionPane.showMessageDialog(this, details, "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Clears Name, Age, Severity, and Search fields.
     * The Patient ID field is NOT cleared — it always shows the next auto ID.
     * The counter is NOT reset.
     */
    private void clearFields() {
        txtName.setText("");
        txtAge.setText("");
        txtSearchId.setText("");
        cmbSeverity.setSelectedIndex(4); // back to Critical default
        updatePatientIdDisplay();        // refresh the preview (counter unchanged)
        txtName.requestFocusInWindow();
    }

    /**
     * Confirms exit and closes the application.
     */
    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit the system?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // =========================================================================
    // Table Helpers
    // =========================================================================

    /**
     * Refreshes the JTable with the current priority-ordered patient list.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // clear all rows

        List<Patient> patients = hospitalQueue.getPatientsInPriorityOrder();
        int priority = 1;
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                priority++,
                p.getPatientId(),
                p.getName(),
                p.getAge(),
                p.getSeverityDisplay(),
                "Waiting"
            });
        }
    }

    /**
     * Updates the patient count label in the status bar.
     */
    private void updatePatientCount() {
        int count = hospitalQueue.getTotalPatients();
        lblPatientCount.setText("Total Waiting Patients: " + count);
    }

    // =========================================================================
    // Patient ID Auto-Generation
    // =========================================================================

    /**
     * Generates the next Patient ID (e.g. P001, P002, …) and increments the counter.
     * Called only when a patient is actually added.
     *
     * @return formatted patient ID string
     */
    private String generatePatientId() {
        return String.format("P%03d", patientIdCounter++);
    }

    /**
     * Returns what the NEXT generated ID will look like, WITHOUT incrementing.
     * Used to preview the upcoming ID in the read-only Patient ID field.
     *
     * @return formatted preview string
     */
    private String generatePatientIdPreview() {
        return String.format("P%03d", patientIdCounter);
    }

    /**
     * Refreshes the read-only Patient ID field to show the next ID that will be assigned.
     */
    private void updatePatientIdDisplay() {
        txtPatientId.setText(generatePatientIdPreview());
    }

    /**
     * Selects and scrolls to the row for the given patient ID.
     */
    private void highlightPatientRow(String patientId) {
        for (int row = 0; row < tableModel.getRowCount(); row++) {
            Object cell = tableModel.getValueAt(row, 1);
            if (patientId.equalsIgnoreCase(String.valueOf(cell))) {
                patientTable.setRowSelectionInterval(row, row);
                patientTable.scrollRectToVisible(patientTable.getCellRect(row, 0, true));
                return;
            }
        }
    }

    // =========================================================================
    // UI Factory Helpers
    // =========================================================================

    /** Creates a styled card panel with a titled border. */
    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 12, 12)
        ));

        // Title label inside card
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        lbl.setText("  " + title.toUpperCase() + "  ");
        card.add(lbl, BorderLayout.NORTH);

        return card;
    }

    /** Creates a dark-themed text field with placeholder hint via tooltip. */
    private JTextField makeTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setBackground(BG_CARD);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(ACCENT_BLUE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                new EmptyBorder(5, 8, 5, 8)
        ));
        field.setToolTipText(placeholder);
        return field;
    }

    /** Creates a styled action button. */
    private JButton makeButton(String text, Color bgColor, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        btn.addActionListener(action);
        return btn;
    }

    /** Adds a label + component row to a GridBagLayout form panel. */
    private void addFormRow(JPanel panel, GridBagConstraints gc, int row,
                            String labelText, JComponent input) {
        gc.gridx = 0; gc.gridy = row * 2;
        gc.gridwidth = 1;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        panel.add(lbl, gc);

        gc.gridx = 0; gc.gridy = row * 2 + 1;
        panel.add(input, gc);
    }

    // =========================================================================
    // JOptionPane Wrappers
    // =========================================================================

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // =========================================================================
    // Custom Severity Cell Renderer
    // =========================================================================

    /**
     * Colors the Severity column cells based on urgency level.
     */
    private class SeverityCellRenderer extends DefaultTableCellRenderer {

        public SeverityCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            setFont(new Font("Segoe UI", Font.BOLD, 12));

            if (!isSelected) {
                String text = String.valueOf(value);
                if (text.contains("Critical")) {
                    setBackground(new Color(127,  29,  29, 200));
                    setForeground(CLR_CRITICAL);
                } else if (text.contains("Serious")) {
                    setBackground(new Color(124,  45,  18, 200));
                    setForeground(CLR_SERIOUS);
                } else if (text.contains("Moderate")) {
                    setBackground(new Color(113,  63,  18, 200));
                    setForeground(CLR_MODERATE);
                } else if (text.contains("Low")) {
                    setBackground(new Color( 20,  83,  45, 200));
                    setForeground(CLR_LOW);
                } else if (text.contains("Minor")) {
                    setBackground(new Color( 30,  41,  82));
                    setForeground(CLR_MINOR);
                } else {
                    setBackground(BG_CARD);
                    setForeground(TEXT_PRIMARY);
                }
            } else {
                setBackground(new Color(56, 139, 253, 80));
                setForeground(TEXT_PRIMARY);
            }

            setOpaque(true);
            return this;
        }
    }
}
