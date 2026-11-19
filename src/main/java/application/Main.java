package application;

import application.config.Database;
import application.dao.JdbcApplicationDao;
import application.model.EmploymentApplication;
import java.math.BigDecimal;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    private final JdbcApplicationDao applicationDao = new JdbcApplicationDao();
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
    private final Button saveButton = new Button("Save");
    private final Button clearButton = new Button("Clear");
    private final Label message = new Label();
    private final Label databaseStatus = new Label("Checking database...");
    private GridPane grid;
    private boolean saving;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RC Company - Employment Application");
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));
        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(170);
        ColumnConstraints inputs = new ColumnConstraints();
        inputs.setPrefWidth(350);
        inputs.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labels, inputs);

        // A plain title and GridPane, following the classroom example.
        Label logo = new Label("RC");
        logo.setStyle("-fx-font-weight: bold; -fx-border-color: gray; -fx-padding: 6;");
        HBox company = new HBox(10, logo, new Label("RC Company"));
        company.setAlignment(Pos.CENTER_LEFT);
        grid.add(company, 0, 0, 2, 1);
        Text sceneTitle = new Text("Employment Application");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 1, 2, 1);
        grid.add(section("Personal Information"), 0, 2, 2, 1);
        grid.add(new Label("Full Name:"), 0, 3);
        grid.add(name, 1, 3);
        grid.add(new Label("Current Address:"), 0, 4);
        grid.add(address, 1, 4);
        grid.add(new Label("Contact Number:"), 0, 5);
        grid.add(phone, 1, 5);
        grid.add(new Label("Email Address:"), 0, 6);
        grid.add(email, 1, 6);
        grid.add(new Label("Highest Educational\nAttainment:"), 0, 7);
        grid.add(education, 1, 7);
        grid.add(new Label("Gender:"), 0, 8);
        grid.add(new HBox(12, choice("Male", genderGroup),
                choice("Female", genderGroup), choice("Other", genderGroup)), 1, 8);
        grid.add(new Separator(), 0, 9, 2, 1);
        grid.add(section("Employment Eligibility"), 0, 10, 2, 1);
        grid.add(new Label("Date Available:"), 0, 11);
        grid.add(available, 1, 11);
        available.setMaxWidth(Double.MAX_VALUE);
        grid.add(new Label("Desired Position:"), 0, 12);
        grid.add(position, 1, 12);
        grid.add(new Label("Desired Salary:"), 0, 13);
        grid.add(salary, 1, 13);
        grid.add(new Label("Are you legally authorized to work in the country?"), 0, 14, 2, 1);
        grid.add(yesNo(authorizedGroup), 1, 15);
        grid.add(new Label("Do you have relatives working for our company?"), 0, 16, 2, 1);
        grid.add(yesNo(relativesGroup), 1, 17);
        grid.add(new Label("If yes, please explain further:"), 0, 18);
        grid.add(explanation, 1, 18);
        explanation.setDisable(true);
        relativesGroup.selectedToggleProperty().addListener((obs, old, selected) -> {
            boolean yes = "Yes".equals(selectedText(relativesGroup));
            explanation.setDisable(!yes);
            if (!yes) explanation.clear();
        });
        grid.add(new Separator(), 0, 19, 2, 1);
        Label declaration = new Label("By submitting this application, you agree to adhere to "
                + "company policies and provide accurate information throughout the employment process.");
        declaration.setWrapText(true);
        declaration.setMaxWidth(540);
        grid.add(declaration, 0, 20, 2, 1);
        grid.add(new Label("Applicant's Signature:"), 0, 21);
        grid.add(signature, 1, 21);
        HBox buttons = new HBox(10, saveButton, clearButton);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        grid.add(buttons, 1, 22);
        message.setWrapText(true);
        databaseStatus.setWrapText(true);
        grid.add(message, 0, 23, 2, 1);
        grid.add(databaseStatus, 0, 24, 2, 1);
        saveButton.setOnAction(event -> saveApplication());
        clearButton.setOnAction(event -> {
            if (!saving) {
                clearForm();
                message.setText("");
            }
        });
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        primaryStage.setScene(new Scene(scroll, 680, 900));
        primaryStage.setMinWidth(620);
        primaryStage.show();
        checkDatabaseConnection();
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

    private TextField[] fields() {
        return new TextField[]{name, address, phone, email, education,
                position, salary, explanation, signature};
    }

    // The first invalid value is shown before any database call.
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

    // Like createUserObject in the teacher example, capture the form in a model.
    private EmploymentApplication createApplicationObject() {
        boolean relatives = "Yes".equals(selectedText(relativesGroup));
        return new EmploymentApplication(name.getText().trim(), address.getText().trim(),
                phone.getText().trim(), email.getText().trim(), education.getText().trim(),
                selectedText(genderGroup), available.getValue(), position.getText().trim(),
                new BigDecimal(salary.getText().trim()),
                "Yes".equals(selectedText(authorizedGroup)), relatives,
                relatives ? explanation.getText().trim() : "", signature.getText().trim());
    }

    private void saveApplication() {
        if (saving) return;
        String error = validateForm();
        if (!error.isEmpty()) {
            message.setText(error);
            alert("Check form", error, Alert.AlertType.WARNING);
            return;
        }
        EmploymentApplication application = createApplicationObject();
        setSaving(true);
        message.setText("Saving application...");
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                applicationDao.save(application);
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            clearForm();
            message.setText("Application saved successfully! The form has been cleared.");
            try {
                alert("Save", "Successful! Your application was saved and the form cleared.",
                        Alert.AlertType.INFORMATION);
            } finally {
                setSaving(false);
                name.requestFocus();
            }
        });
        task.setOnFailed(event -> {
            message.setText("Save could not be confirmed. Your inputs have been kept.");
            try {
                alert("Error", task.getException().getMessage()
                        + "\nYour inputs have been kept. If the connection failed during saving,"
                        + " check Workbench before retrying to avoid a duplicate.", Alert.AlertType.ERROR);
            } finally {
                setSaving(false);
            }
        });
        runInBackground(task);
    }

    private void setSaving(boolean busy) {
        saving = busy;
        grid.setDisable(busy);
        saveButton.setText(busy ? "Saving..." : "Save");
    }

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

    private void alert(String title, String text, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.initOwner(saveButton.getScene().getWindow());
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void checkDatabaseConnection() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Database.checkConnection();
                return null;
            }
        };
        task.setOnSucceeded(event -> databaseStatus.setText("Database connection successful!"));
        task.setOnFailed(event -> databaseStatus.setText(
                "Database connection failed: " + task.getException().getMessage()));
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
