package application;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private final TextField name = new TextField();
    private final TextField address = new TextField();
    private final TextField phone = new TextField();
    private final TextField email = new TextField();
    private final TextField education = new TextField();
    private final TextField position = new TextField();
    private final TextField salary = new TextField();
    private final TextField explanation = new TextField();
    private final TextField signature = new TextField();
    private final DatePicker available = new DatePicker();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private final ToggleGroup authorizedGroup = new ToggleGroup();
    private final ToggleGroup relativesGroup = new ToggleGroup();
    private final Label databaseStatus = new Label("Checking database...");
    private final Label message = new Label();
    private final Button submit = new Button("Submit Application");
    private final Button clear = new Button("Clear");
    private VBox form;
    private boolean saving;

    @Override
    public void start(Stage stage) {
        // Keep the standard JavaFX controls and only a small amount of styling.
        Label logo = new Label("RC");
        logo.setPadding(new Insets(7));
        logo.setStyle("-fx-border-color: #777; -fx-font-weight: bold;");
        HBox company = new HBox(8, logo, new Label("RC Company"));
        company.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Employment Application");
        title.setStyle("-fx-font-size: 16px;");
        BorderPane header = new BorderPane();
        header.setLeft(company);
        header.setRight(title);
        BorderPane.setAlignment(title, Pos.CENTER_RIGHT);

        for (TextField field : fields()) {
            field.setPrefHeight(30);
            field.setMaxWidth(Double.MAX_VALUE);
        }
        available.setMaxWidth(Double.MAX_VALUE);
        available.setPromptText("Select a date");

        // Labels and inputs stay in the same groups as the original form.
        GridPane personal = createGrid();
        personal.add(new Label("Full Name:"), 0, 0);
        personal.add(name, 1, 0, 3, 1);
        personal.add(new Label("Current Address:"), 0, 1);
        personal.add(address, 1, 1, 3, 1);
        personal.add(new Label("Contact Number:"), 0, 2);
        personal.add(phone, 1, 2);
        personal.add(new Label("Email Address:"), 2, 2);
        personal.add(email, 3, 2);
        personal.add(new Label("Highest Educational\nAttainment:"), 0, 3);
        personal.add(education, 1, 3);
        personal.add(new Label("Gender:"), 2, 3);
        personal.add(new HBox(10, choice("Male", genderGroup),
                choice("Female", genderGroup), choice("Other", genderGroup)), 3, 3);

        GridPane employment = createGrid();
        employment.add(new Label("Date Available:"), 0, 0);
        employment.add(available, 1, 0);
        employment.add(new Label("Desired Position:"), 2, 0);
        employment.add(position, 3, 0);
        employment.add(new Label("Desired Salary:"), 0, 1);
        employment.add(salary, 1, 1, 3, 1);

        explanation.setDisable(true);
        relativesGroup.selectedToggleProperty().addListener((obs, old, selected) -> {
            boolean yes = "Yes".equals(selectedText(relativesGroup));
            explanation.setDisable(!yes);
            if (!yes) explanation.clear();
        });
        HBox explanationRow = new HBox(12,
                new Label("If yes, please explain further:"), explanation);
        explanationRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(explanation, Priority.ALWAYS);
        Label declaration = new Label("By submitting this application, you agree to adhere to "
                + "company policies and provide accurate information throughout the employment process.");
        declaration.setWrapText(true);
        HBox signatureRow = new HBox(12, new Label("Applicant's Signature:"), signature);
        signatureRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(signature, Priority.ALWAYS);

        form = new VBox(14, section("Personal Information"), personal,
                new Separator(), section("Employment Eligibility"), employment,
                new VBox(7, new Label("Are you legally authorized to work in the country?"),
                        yesNo(authorizedGroup)),
                new VBox(7, new Label("Do you have relatives working for our company?"),
                        yesNo(relativesGroup)), explanationRow,
                new Separator(), declaration, signatureRow);
        submit.setOnAction(event -> save());
        clear.setOnAction(event -> {
            if (!saving) {
                clearForm();
                message.setText("");
            }
        });
        HBox buttons = new HBox(10, submit, clear);
        buttons.setAlignment(Pos.CENTER_LEFT);
        message.setWrapText(true);
        databaseStatus.setWrapText(true);
        VBox page = new VBox(18, header, new Separator(), form,
                buttons, message, databaseStatus);
        page.setPadding(new Insets(24));
        page.setStyle("-fx-font-family: Arial; -fx-font-size: 13px;");
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        stage.setScene(new Scene(scroll, 820, 820));
        stage.setMinWidth(800);
        stage.setTitle("RC Company - Employment Application");
        stage.show();
        checkDatabaseConnection();
    }

    private TextField[] fields() {
        return new TextField[]{name, address, phone, email, education,
                position, salary, explanation, signature};
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        for (double width : new double[]{20, 27, 17, 36}) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            grid.getColumnConstraints().add(column);
        }
        return grid;
    }

    private Label section(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    private RadioButton choice(String text, ToggleGroup group) {
        RadioButton button = new RadioButton(text);
        button.setToggleGroup(group);
        return button;
    }

    private HBox yesNo(ToggleGroup group) {
        return new HBox(20, choice("Yes", group), choice("No", group));
    }

    private String selectedText(ToggleGroup group) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        return selected == null ? "" : selected.getText();
    }

    // Return the first problem, or an empty string when the form is valid.
    private String validateForm() {
        TextField[] required = {name, address, phone, email, education, position, signature};
        String[] captions = {"Full name", "Address", "Phone", "Email", "Education",
                "Desired position", "Signature"};
        int[] limits = {150, 255, 40, 150, 150, 150, 150};
        for (int i = 0; i < required.length; i++) {
            String value = required[i].getText().trim();
            if (value.isBlank()) return captions[i] + " is required.";
            if (value.length() > limits[i]) {
                return captions[i] + " must be at most " + limits[i] + " characters.";
            }
            if (value.codePoints().anyMatch(Character::isISOControl)) {
                return captions[i] + " must not contain control characters.";
            }
        }
        if (name.getText().trim().codePoints().noneMatch(Character::isLetter)) {
            return "Full name must contain at least one letter.";
        }
        String mail = email.getText().trim();
        if (!mail.matches("[A-Za-z0-9_%+\\-]+(?:\\.[A-Za-z0-9_%+\\-]+)*@"
                + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?\\.)+"
                + "[A-Za-z]{2,63}") || mail.indexOf('@') > 64) {
            return "Enter an email such as name@example.com (no spaces).";
        }
        String number = phone.getText().trim();
        if (!number.matches("[+0-9() .-]+")
                || number.indexOf('+') > 0
                || number.chars().filter(c -> c == '+').count() > 1) {
            return "Phone may contain digits, spaces, brackets, dots, dashes, and a leading +1.";
        }
        String digits = number.replaceAll("[^0-9]", "");
        if (digits.length() == 11 && digits.startsWith("1")) digits = digits.substring(1);
        if (!digits.matches("[2-9][0-9]{2}[2-9][0-9]{6}")) {
            return "Enter a Canadian/US number such as 416-555-0123 or +1 416-555-0123.";
        }
        if (number.startsWith("+") && !number.replaceAll("[^0-9]", "").matches("1[0-9]{10}")) {
            return "Use +1 followed by a 10-digit Canadian/US phone number.";
        }
        String plainNumber = number.replaceAll("[ .-]", "");
        if (!plainNumber.matches("(?:\\+?1)?(?:[0-9]{10}|\\([0-9]{3}\\)[0-9]{7})")) {
            return "Check phone brackets: use (416) 555-0123 or 416-555-0123.";
        }
        try {
            available.commitValue();
        } catch (RuntimeException exception) {
            return "Enter a valid available date or select it from the calendar.";
        }
        if (available.getValue() == null) return "Select your available date.";
        if (available.getValue().isBefore(LocalDate.now())) {
            return "Available date must be today or a future date.";
        }
        if (available.getValue().getYear() > 9999) return "Available date year must not exceed 9999.";
        if (selectedText(genderGroup).isEmpty()) return "Select a gender option.";
        if (selectedText(authorizedGroup).isEmpty()) return "Answer the work authorization question.";
        if (selectedText(relativesGroup).isEmpty()) return "Answer the relatives question.";
        if ("Yes".equals(selectedText(relativesGroup)) && explanation.getText().isBlank()) {
            return "Enter your relative's name and relationship.";
        }
        if (explanation.getText().length() > 4000) return "Keep the relatives explanation within 4000 characters.";
        String pay = salary.getText().trim();
        if (!pay.matches("[0-9]{1,10}(?:\\.[0-9]{1,2})?")) {
            return "Salary must be digits only, with up to 2 decimal places; example: 45000.00.";
        }
        if (new BigDecimal(pay).compareTo(new BigDecimal("9999999999.99")) > 0) {
            return "Salary must not exceed 9999999999.99.";
        }
        return "";
    }

    private void popup(Alert.AlertType type, String heading, String text) {
        Alert alert = new Alert(type);
        alert.initOwner(submit.getScene().getWindow());
        alert.setTitle("RC Company");
        alert.setHeaderText(heading);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void setSaving(boolean busy) {
        saving = busy;
        form.setDisable(busy);
        submit.setDisable(busy);
        clear.setDisable(busy);
        submit.setText(busy ? "Saving..." : "Submit Application");
    }

    // Clears only the screen, never the database.
    private void clearForm() {
        for (TextField field : fields()) field.clear();
        available.setValue(null);
        available.getEditor().clear();
        genderGroup.selectToggle(null);
        authorizedGroup.selectToggle(null);
        relativesGroup.selectToggle(null);
        explanation.setDisable(true);
        name.requestFocus();
    }

    private Connection openConnection() throws SQLException {
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (user == null || password == null) {
            throw new SQLException("Set DB_USER and DB_PASSWORD in your run environment.");
        }
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wk10_lab", user, password);
    }

    private void checkDatabaseConnection() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection connection = openConnection()) {
                    if (!connection.isValid(5)) throw new SQLException("Connection validation failed.");
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> databaseStatus.setText("Database connection successful!"));
        task.setOnFailed(event -> databaseStatus.setText(
                "Database connection failed: " + task.getException().getMessage()));
        runInBackground(task);
    }

    private void save() {
        if (saving) return;
        String error = validateForm();
        if (!error.isEmpty()) {
            message.setText(error);
            popup(Alert.AlertType.WARNING, "Please correct the form", error);
            return;
        }
        boolean authorized = "Yes".equals(selectedText(authorizedGroup));
        boolean relatives = "Yes".equals(selectedText(relativesGroup));
        String[] values = {name.getText().trim(), address.getText().trim(),
                phone.getText().trim(), email.getText().trim(), education.getText().trim(),
                selectedText(genderGroup), position.getText().trim(),
                relatives ? explanation.getText().trim() : "", signature.getText().trim()};
        Date date = Date.valueOf(available.getValue());
        BigDecimal amount = new BigDecimal(salary.getText().trim());
        String sql = "INSERT INTO applications "
                + "(full_name, address, phone, email, education, gender, available_date, "
                + "position, salary, authorized, relatives, explanation, signature) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        setSaving(true);
        message.setText("Saving application...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection connection = openConnection();
                     PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, values[0]);
                    statement.setString(2, values[1]);
                    statement.setString(3, values[2]);
                    statement.setString(4, values[3]);
                    statement.setString(5, values[4]);
                    statement.setString(6, values[5]);
                    statement.setDate(7, date);
                    statement.setString(8, values[6]);
                    statement.setBigDecimal(9, amount);
                    statement.setBoolean(10, authorized);
                    statement.setBoolean(11, relatives);
                    statement.setString(12, values[7]);
                    statement.setString(13, values[8]);
                    if (statement.executeUpdate() != 1) throw new SQLException("Expected one inserted row.");
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            clearForm();
            message.setText("Application saved successfully! The form has been cleared.");
            try {
                popup(Alert.AlertType.INFORMATION, "Application saved successfully",
                        "Your application was saved to MySQL. The form has been cleared.");
            } finally {
                setSaving(false);
                name.requestFocus();
            }
        });
        task.setOnFailed(event -> {
            message.setText("Save could not be confirmed. Your inputs have been kept.");
            try {
                popup(Alert.AlertType.ERROR, "Save could not be confirmed",
                        task.getException().getMessage()
                        + "\nYour inputs have been kept. If the connection failed during saving,"
                        + " check Workbench before retrying to avoid a duplicate.");
            } finally {
                setSaving(false);
            }
        });
        runInBackground(task);
    }

    private void runInBackground(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
