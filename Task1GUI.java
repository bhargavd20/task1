import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Task1GUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea outputArea;

    private Connection connection;

    public Task1GUI() {
        setTitle("User Authentication and PNR Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel for the login
        JPanel loginPanel = new JPanel(new BorderLayout(10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel loginLabel = new JLabel("User Login", SwingConstants.CENTER);
        loginLabel.setFont(new Font("Serif", Font.BOLD, 24));
        loginPanel.add(loginLabel, BorderLayout.NORTH);

        JPanel credentialsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        credentialsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        credentialsPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        credentialsPanel.add(usernameField);
        credentialsPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        credentialsPanel.add(passwordField);

        loginPanel.add(credentialsPanel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Serif", Font.PLAIN, 18));
        loginPanel.add(loginButton, BorderLayout.SOUTH);

        add(loginPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Serif", Font.PLAIN, 16));
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        setVisible(true);
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String url = "jdbc:mysql://localhost:3306/dataonline";

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            connection = DriverManager.getConnection(url, username, password);
            outputArea.setText("User Connection Granted.\n");
            showMenu(); // Proceed to show menu if connection successful
        } catch (ClassNotFoundException e) {
            outputArea.setText("JDBC Driver not found: " + e.getMessage() + "\n");
        } catch (SQLException e) {
            outputArea.setText("SQL Exception: " + e.getMessage() + "\n");
            // Print detailed stack trace for diagnosis
            e.printStackTrace();
        }
    }

    private void showMenu() {
        JFrame menuFrame = new JFrame("PNR Management");
        menuFrame.setSize(400, 300);
        menuFrame.setLayout(new GridLayout(4, 1, 10, 10));
        menuFrame.setLocationRelativeTo(null);

        JButton insertButton = new JButton("Insert Record");
        JButton deleteButton = new JButton("Delete Record");
        JButton showButton = new JButton("Show All Records");
        JButton exitButton = new JButton("Exit");

        insertButton.setFont(new Font("Serif", Font.PLAIN, 18));
        deleteButton.setFont(new Font("Serif", Font.PLAIN, 18));
        showButton.setFont(new Font("Serif", Font.PLAIN, 18));
        exitButton.setFont(new Font("Serif", Font.PLAIN, 18));

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertRecord();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
            }
        });

        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRecords();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuFrame.add(insertButton);
        menuFrame.add(deleteButton);
        menuFrame.add(showButton);
        menuFrame.add(exitButton);
        menuFrame.setVisible(true);
    }

    private void insertRecord() {
        JTextField passengerNameField = new JTextField();
        JTextField trainNumberField = new JTextField();
        JTextField classTypeField = new JTextField();
        JTextField journeyDateField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Passenger Name:"));
        panel.add(passengerNameField);
        panel.add(new JLabel("Train Number:"));
        panel.add(trainNumberField);
        panel.add(new JLabel("Class Type:"));
        panel.add(classTypeField);
        panel.add(new JLabel("Journey Date (YYYY-MM-DD):"));
        panel.add(journeyDateField);
        panel.add(new JLabel("From:"));
        panel.add(fromField);
        panel.add(new JLabel("To:"));
        panel.add(toField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Insert Record", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into reservations (passenger_name, train_number, class_type, journey_date, from_location, to_location) values (?, ?, ?, ?, ?, ?)")) {
                preparedStatement.setString(1, passengerNameField.getText());
                preparedStatement.setString(2, trainNumberField.getText());
                preparedStatement.setString(3, classTypeField.getText());
                preparedStatement.setString(4, journeyDateField.getText());
                preparedStatement.setString(5, fromField.getText());
                preparedStatement.setString(6, toField.getText());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    outputArea.setText("Record added successfully.\n");
                } else {
                    outputArea.setText("No records were added.\n");
                }
            } catch (SQLException e) {
                outputArea.setText("SQLException: " + e.getMessage() + "\n");
            }
        }
    }

    private void deleteRecord() {
        String pnrNumber = JOptionPane.showInputDialog("Enter the PNR number to delete the record:");
        if (pnrNumber != null) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM reservations WHERE pnr_number = ?")) {
                preparedStatement.setInt(1, Integer.parseInt(pnrNumber));
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    outputArea.setText("Record deleted successfully.\n");
                } else {
                    outputArea.setText("No records were deleted.\n");
                }
            } catch (SQLException e) {
                outputArea.setText("SQLException: " + e.getMessage() + "\n");
            }
        }
    }

    private void showRecords() {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM reservations");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            StringBuilder records = new StringBuilder();
            while (resultSet.next()) {
                records.append("PNR Number: ").append(resultSet.getString("pnr_number")).append("\n");
                records.append("Passenger Name: ").append(resultSet.getString("passenger_name")).append("\n");
                records.append("Train Number: ").append(resultSet.getString("train_number")).append("\n");
                records.append("Class Type: ").append(resultSet.getString("class_type")).append("\n");
                records.append("Journey Date: ").append(resultSet.getString("journey_date")).append("\n");
                records.append("From Location: ").append(resultSet.getString("from_location")).append("\n");
                records.append("To Location: ").append(resultSet.getString("to_location")).append("\n");
                records.append("--------------\n");
            }
            outputArea.setText(records.toString());
        } catch (SQLException e) {
            outputArea.setText("SQLException: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Task1GUI();
            }
        });
    }
}
