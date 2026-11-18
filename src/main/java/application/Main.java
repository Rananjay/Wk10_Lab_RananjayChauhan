package application;

import java.math.BigDecimal;
import java.sql.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    TextField name = new TextField(), address = new TextField();
    TextField phone = new TextField(), email = new TextField();
    TextField education = new TextField(), position = new TextField();
    TextField salary = new TextField(), explanation = new TextField();
    TextField signature = new TextField();
    DatePicker available = new DatePicker();
    ComboBox<String> gender = new ComboBox<>();
    ComboBox<String> authorized = new ComboBox<>();
    ComboBox<String> relatives = new ComboBox<>();
    Label message = new Label();

    @Override
    public void start(Stage stage) {
        gender.getItems().addAll("Male", "Female", "Other");
        authorized.getItems().addAll("Yes", "No");
        relatives.getItems().addAll("Yes", "No");
        GridPane form = new GridPane();
        form.setPadding(new Insets(15));
        form.setHgap(12);
        form.setVgap(8);
        form.add(new Label("Employment Application"), 0, 0, 2, 1);
        form.addRow(1, new Label("Full Name"), name);
        form.addRow(2, new Label("Current Address"), address);
        form.addRow(3, new Label("Contact Number"), phone);
        form.addRow(4, new Label("Email Address"), email);
        form.addRow(5, new Label("Highest Education"), education);
        form.addRow(6, new Label("Gender"), gender);
        form.addRow(7, new Label("Date Available"), available);
        form.addRow(8, new Label("Desired Position"), position);
        form.addRow(9, new Label("Desired Salary"), salary);
        form.addRow(10, new Label("Legally authorized to work?"), authorized);
        form.addRow(11, new Label("Relatives in company?"), relatives);
        form.addRow(12, new Label("If yes, explain"), explanation);
        form.addRow(13, new Label("Signature (type your name)"), signature);
        form.add(new Label("By submitting, I agree to company policies\nand confirm the information is accurate."), 0, 14, 2, 1);
        Button submit = new Button("Submit");
        submit.setOnAction(event -> save());
        form.add(submit, 1, 15);
        form.add(message, 0, 16, 2, 1);
        message.setWrapText(true);
        message.setMaxWidth(440);
        stage.setScene(new Scene(new ScrollPane(form), 520, 660));
        stage.setTitle("Wk10 Lab - Rananjay Chauhan");
        stage.show();
    }

    void save() {
        if (name.getText().isBlank() || available.getValue() == null
                || gender.getValue() == null || authorized.getValue() == null
                || relatives.getValue() == null || signature.getText().isBlank()) {
            message.setText("Enter name, date, signature and choose all dropdowns.");
            return;
        }
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (user == null || password == null) {
            message.setText("Set DB_USER and DB_PASSWORD in Run Configurations > Environment.");
            return;
        }
        String sql = "INSERT INTO applications (full_name, address, phone, email, education, gender, available_date, position, salary, authorized, relatives, explanation, signature) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            BigDecimal amount = new BigDecimal(salary.getText().trim());
            if (amount.signum() < 0) throw new NumberFormatException();
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wk10_lab", user, password);
                 PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name.getText());
                statement.setString(2, address.getText());
                statement.setString(3, phone.getText());
                statement.setString(4, email.getText());
                statement.setString(5, education.getText());
                statement.setString(6, gender.getValue());
                statement.setDate(7, Date.valueOf(available.getValue()));
                statement.setString(8, position.getText());
                statement.setBigDecimal(9, amount);
                statement.setBoolean(10, authorized.getValue().equals("Yes"));
                statement.setBoolean(11, relatives.getValue().equals("Yes"));
                statement.setString(12, explanation.getText());
                statement.setString(13, signature.getText());
                statement.executeUpdate();
                message.setText("Application saved successfully!");
            }
        } catch (NumberFormatException exception) {
            message.setText("Enter a valid non-negative salary, for example 45000.");
        } catch (SQLException exception) {
            message.setText("Database error: " + exception.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
