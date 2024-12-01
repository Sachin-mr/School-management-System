import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.DecimalFormat;

public class StudentManagementSystem extends JFrame {
    private JPanel sidePanel, mainPanel, currentPanel;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color bgColor = new Color(236, 240, 241);
    private ArrayList<Student> students = new ArrayList<>();
    private String dataFile = "students.txt";

    public StudentManagementSystem() {
        setTitle("Student Management Syste");
        setTitle("created BY");
        setTitle("MR SACHIN");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loadData();
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Side Panel
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, getHeight()));
        sidePanel.setBackground(primaryColor);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Main Panel
        mainPanel = new JPanel();
        mainPanel.setBackground(bgColor);
        mainPanel.setLayout(new CardLayout());

        // Add modules to side panel
        addModuleButton("Student Enrollment", new EnrollmentPanel());
        addModuleButton("Student Information", new InformationPanel());
        addModuleButton("Attendance Management", new AttendancePanel());
        addModuleButton("Mark Management", new MarkPanel());
        addModuleButton("Fee Management", new FeePanel());

        // Add panels to frame
        add(sidePanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        // Welcome Panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setBackground(bgColor);
        welcomePanel.setLayout(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("Welcome to Student Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel);
        mainPanel.add(welcomePanel, "WELCOME");
    }

    private void addModuleButton(String name, JPanel panel) {
        JButton button = new JButton(name);
        button.setMaximumSize(new Dimension(250, 50));
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });

        button.addActionListener(e -> {
            if (currentPanel != null) {
                mainPanel.remove(currentPanel);
            }
            currentPanel = panel;
            mainPanel.add(panel, name);
            CardLayout cl = (CardLayout) mainPanel.getLayout();
            cl.show(mainPanel, name);
        });

        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(button);
    }

    // Student Class
    class Student {
        String rollNo, name, className;
        double fees, feesPaid;
        Map<String, Integer> attendance = new HashMap<>();
        Map<String, Integer> marks = new HashMap<>();

        public Student(String rollNo, String name, String className) {
            this.rollNo = rollNo;
            this.name = name;
            this.className = className;
            this.fees = 50000; // Default fees
            this.feesPaid = 0;
        }
    }

    // Enrollment Panel
    class EnrollmentPanel extends JPanel {
        private JTextField nameField, rollNoField, classField;
        
        public EnrollmentPanel() {
            setBackground(bgColor);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);

            // Create components
            nameField = createStyledTextField();
            rollNoField = createStyledTextField();
            classField = createStyledTextField();
            JButton submitButton = createStyledButton("Enroll Student");

            // Add components
            gbc.gridx = 0; gbc.gridy = 0;
            add(createStyledLabel("Student Name:"), gbc);
            gbc.gridx = 1;
            add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            add(createStyledLabel("Roll Number:"), gbc);
            gbc.gridx = 1;
            add(rollNoField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            add(createStyledLabel("Class:"), gbc);
            gbc.gridx = 1;
            add(classField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            add(submitButton, gbc);

            submitButton.addActionListener(e -> {
                String name = nameField.getText();
                String rollNo = rollNoField.getText();
                String className = classField.getText();
                
                if (name.isEmpty() || rollNo.isEmpty() || className.isEmpty()) {
                    showMessage("Please fill all fields", "Error");
                    return;
                }

                Student student = new Student(rollNo, name, className);
                students.add(student);
                saveData();
                showMessage("Student enrolled successfully!", "Success");
                clearFields();
            });
        }

        private void clearFields() {
            nameField.setText("");
            rollNoField.setText("");
            classField.setText("");
        }
    }

    // Information Panel
    class InformationPanel extends JPanel {
        private JTextField searchField;
        private JTextArea infoArea;
        
        public InformationPanel() {
            setBackground(bgColor);
            setLayout(new BorderLayout(10, 10));
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            searchPanel.setBackground(bgColor);
            searchField = createStyledTextField();
            JButton searchButton = createStyledButton("Search");
            searchPanel.add(createStyledLabel("Enter Roll Number:"));
            searchPanel.add(searchField);
            searchPanel.add(searchButton);
            
            // Info panel
            infoArea = new JTextArea(15, 40);
            infoArea.setEditable(false);
            infoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(infoArea);
            
            add(searchPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            
            searchButton.addActionListener(e -> {
                String rollNo = searchField.getText();
                Student student = findStudent(rollNo);
                if (student != null) {
                    displayStudentInfo(student);
                } else {
                    showMessage("Student not found", "Error");
                }
            });
        }
        
        private void displayStudentInfo(Student student) {
            StringBuilder info = new StringBuilder();
            info.append("Student Information:\n\n");
            info.append("Name: ").append(student.name).append("\n");
            info.append("Roll Number: ").append(student.rollNo).append("\n");
            info.append("Class: ").append(student.className).append("\n");
            info.append("Fees Paid: ").append(student.feesPaid).append("\n");
            info.append("Fees Pending: ").append(student.fees - student.feesPaid).append("\n\n");
            
            if (!student.marks.isEmpty()) {
                info.append("Marks:\n");
                student.marks.forEach((subject, mark) -> 
                    info.append(subject).append(": ").append(mark).append("\n"));
            }
            
            if (!student.attendance.isEmpty()) {
                info.append("\nAttendance:\n");
                student.attendance.forEach((date, present) -> 
                    info.append(date).append(": ").append(present).append("\n"));
            }
            
            infoArea.setText(info.toString());
        }
    }

    // Attendance Panel
    class AttendancePanel extends JPanel {
        private JTextField rollNoField, dateField;
        private JComboBox<String> statusCombo;
        
        public AttendancePanel() {
            setBackground(bgColor);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            rollNoField = createStyledTextField();
            dateField = createStyledTextField();
            statusCombo = new JComboBox<>(new String[]{"Present", "Absent"});
            JButton submitButton = createStyledButton("Mark Attendance");
            
            gbc.gridx = 0; gbc.gridy = 0;
            add(createStyledLabel("Roll Number:"), gbc);
            gbc.gridx = 1;
            add(rollNoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            add(createStyledLabel("Date (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1;
            add(dateField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            add(createStyledLabel("Status:"), gbc);
            gbc.gridx = 1;
            add(statusCombo, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            add(submitButton, gbc);
            
            submitButton.addActionListener(e -> {
                String rollNo = rollNoField.getText();
                String date = dateField.getText();
                int status = statusCombo.getSelectedItem().equals("Present") ? 1 : 0;
                
                Student student = findStudent(rollNo);
                if (student != null) {
                    student.attendance.put(date, status);
                    saveData();
                    showMessage("Attendance marked successfully!", "Success");
                    clearFields();
                } else {
                    showMessage("Student not found", "Error");
                }
            });
        }
        
        private void clearFields() {
            rollNoField.setText("");
            dateField.setText("");
        }
    }

    // Mark Panel
    class MarkPanel extends JPanel {
        private JTextField rollNoField, subjectField, marksField;
        
        public MarkPanel() {
            setBackground(bgColor);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            rollNoField = createStyledTextField();
            subjectField = createStyledTextField();
            marksField = createStyledTextField();
            JButton submitButton = createStyledButton("Add Marks");
            
            gbc.gridx = 0; gbc.gridy = 0;
            add(createStyledLabel("Roll Number:"), gbc);
            gbc.gridx = 1;
            add(rollNoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            add(createStyledLabel("Subject:"), gbc);
            gbc.gridx = 1;
            add(subjectField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            add(createStyledLabel("Marks:"), gbc);
            gbc.gridx = 1;
            add(marksField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 2;
            add(submitButton, gbc);
            
            submitButton.addActionListener(e -> {
                try {
                    String rollNo = rollNoField.getText();
                    String subject = subjectField.getText();
                    int marks = Integer.parseInt(marksField.getText());
                    
                    if (marks < 0 || marks > 100) {
                        showMessage("Marks should be between 0 and 100", "Error");
                        return;
                    }
                    
                    Student student = findStudent(rollNo);
                    if (student != null) {
                        student.marks.put(subject, marks);
                        saveData();
                        showMessage("Marks added successfully!", "Success");
                        clearFields();
                    } else {
                        showMessage("Student not found", "Error");
                    }
                } catch (NumberFormatException ex) {
                    showMessage("Please enter valid marks", "Error");
                }
            });
        }
        
        private void clearFields() {
            rollNoField.setText("");
            subjectField.setText("");
            marksField.setText("");
        }
    }

    // Fee Panel
    class FeePanel extends JPanel {
        private JTextField rollNoField, amountField;
        private JLabel pendingFeesLabel;
        
        public FeePanel() {
            setBackground(bgColor);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            
            rollNoField = createStyledTextField();
            amountField = createStyledTextField();
            pendingFeesLabel = createStyledLabel("");
            JButton checkButton = createStyledButton("Check Fees");
            JButton payButton = createStyledButton("Pay Fees");
            
            gbc.gridx = 0; gbc.gridy = 0;
            add(createStyledLabel("Roll Number:"), gbc);
            gbc.gridx = 1;
            add(rollNoField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            add(createStyledLabel("Amount:"), gbc);
            gbc.gridx = 1;
            add(amountField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            add(pendingFeesLabel, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            gbc.gridwidth = 1;
            add(checkButton, gbc);
            gbc.gridx = 1;
            add(payButton, gbc);
            
            checkButton.addActionListener(e -> {
                String rollNo = rollNoField.getText();
                Student student = findStudent(rollNo);
                if (student != null) {
                    double pending = student.fees - student.feesPaid;
                    pendingFeesLabel.setText("Pending Fees: " + pending);
                } else {
                    showMessage("Student not found", "Error");
                }
            });
            
            payButton.addActionListener(e -> {
                try {
                    String rollNo = rollNoField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    
                    Student student = findStudent(rollNo);
                    if (student != null) {
                        double pending = student.fees - student.feesPaid;
                        if (amount > pending) {
                            showMessage("Amount is greater than pending fees", "Error");
                            return;
                        }
                        student.feesPaid += amount;
                        saveData();
                        showMessage("Fees paid successfully!", "Success");
                        pendingFeesLabel.setText("Pending Fees: " + (student.fees - student.feesPaid));
                        clearFields();
                    } else {
                        showMessage("Student not found", "Error");
                    }
                } catch (NumberFormatException ex) {
                    showMessage("Please enter valid amount", "Error");
                }
            });
        }
        
        private void clearFields() {
            amountField.setText("");
            pendingFeesLabel.setText("");
        }
    }

    // Utility Methods
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(primaryColor),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(secondaryColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });
        return button;
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, 
            title.equals("Error") ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }

    private Student findStudent(String rollNo) {
        return students.stream()
            .filter(s -> s.rollNo.equals(rollNo))
            .findFirst()
            .orElse(null);
    }

    // Data Persistence Methods
    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            for (Student student : students) {
                writer.println("START_STUDENT");
                writer.println(student.rollNo);
                writer.println(student.name);
                writer.println(student.className);
                writer.println(student.fees);
                writer.println(student.feesPaid);
                
                // Write attendance
                writer.println("START_ATTENDANCE");
                for (Map.Entry<String, Integer> entry : student.attendance.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
                writer.println("END_ATTENDANCE");
                
                // Write marks
                writer.println("START_MARKS");
                for (Map.Entry<String, Integer> entry : student.marks.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
                writer.println("END_MARKS");
                
                writer.println("END_STUDENT");
            }
        } catch (IOException e) {
            showMessage("Error saving data: " + e.getMessage(), "Error");
        }
    }

    private void loadData() {
        File file = new File(dataFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Student currentStudent = null;
            boolean readingAttendance = false;
            boolean readingMarks = false;

            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "START_STUDENT":
                        String rollNo = reader.readLine();
                        String name = reader.readLine();
                        String className = reader.readLine();
                        currentStudent = new Student(rollNo, name, className);
                        currentStudent.fees = Double.parseDouble(reader.readLine());
                        currentStudent.feesPaid = Double.parseDouble(reader.readLine());
                        break;
                        
                    case "START_ATTENDANCE":
                        readingAttendance = true;
                        break;
                        
                    case "END_ATTENDANCE":
                        readingAttendance = false;
                        break;
                        
                    case "START_MARKS":
                        readingMarks = true;
                        break;
                        
                    case "END_MARKS":
                        readingMarks = false;
                        break;
                        
                    case "END_STUDENT":
                        if (currentStudent != null) {
                            students.add(currentStudent);
                            currentStudent = null;
                        }
                        break;
                        
                    default:
                        if (readingAttendance && currentStudent != null) {
                            String[] parts = line.split(",");
                            currentStudent.attendance.put(parts[0], Integer.parseInt(parts[1]));
                        } else if (readingMarks && currentStudent != null) {
                            String[] parts = line.split(",");
                            currentStudent.marks.put(parts[0], Integer.parseInt(parts[1]));
                        }
                }
            }
        } catch (IOException e) {
            showMessage("Error loading data: " + e.getMessage(), "Error");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            StudentManagementSystem sms = new StudentManagementSystem();
            sms.setVisible(true);
        });
    }
}