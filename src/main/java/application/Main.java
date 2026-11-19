package application;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    @Override
    public void start(Stage stage) {
        // Header: the RC logo is text, so no image file is needed.
        Label logo = new Label("RC");
        logo.setAlignment(Pos.CENTER);
        logo.setMinSize(48, 48);
        logo.setMaxSize(48, 48);
        logo.setStyle("-fx-background-color: black; -fx-text-fill: white;"
                + "-fx-font-size: 22px; -fx-font-weight: bold;");
        Label companyName = new Label("RC");
        companyName.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox companyText = new VBox(2, companyName, new Label("Company"));
        HBox company = new HBox(8, logo, companyText);
        company.setAlignment(Pos.CENTER_LEFT);
        company.setMinWidth(145);
        Label title = new Label("Employment Application");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setMinHeight(62);
        title.setStyle("-fx-background-color: black; -fx-text-fill: white;"
                + "-fx-font-size: 24px; -fx-font-weight: bold;");
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox header = new HBox(12, company, title);
        header.setAlignment(Pos.CENTER_LEFT);

        for (TextField field : new TextField[]{name, address, phone, email,
                education, position, salary, explanation, signature}) {
            field.setPrefHeight(30);
            field.setMaxWidth(Double.MAX_VALUE);
            field.setStyle("-fx-background-color: white; -fx-border-color: #777;"
                    + "-fx-background-radius: 0; -fx-border-radius: 0;");
        }
        available.setPromptText("mm/dd/yyyy");
        available.setMaxWidth(Double.MAX_VALUE);

        // Personal information: label, input, label, input.
        GridPane personal = paperGrid();
        personal.add(new Label("Full Name"), 0, 0);
        personal.add(name, 1, 0, 3, 1);
        personal.add(new Label("Current Address"), 0, 1);
        personal.add(address, 1, 1, 3, 1);
        personal.add(new Label("Contact\nNumber"), 0, 2);
        personal.add(phone, 1, 2);
        personal.add(new Label("Email\nAddress"), 2, 2);
        personal.add(email, 3, 2);
        personal.add(new Label("Highest Educational\nAttainment"), 0, 3);
        personal.add(education, 1, 3);
        HBox genderChoices = new HBox(12, choice("Male", genderGroup),
                choice("Female", genderGroup), choice("Other", genderGroup));
        personal.add(new VBox(8, new Label("Gender"), genderChoices), 2, 3, 2, 1);

        GridPane employment = paperGrid();
        employment.add(new Label("Date\nAvailable"), 0, 0);
        employment.add(available, 1, 0);
        employment.add(new Label("Desired\nPosition"), 2, 0);
        employment.add(position, 3, 0);
        employment.add(new Label("Desired Salary"), 0, 1);
        employment.add(salary, 1, 1, 3, 1);
        explanation.setDisable(true);
        relativesGroup.selectedToggleProperty().addListener((obs, old, selected) -> {
            boolean hasRelatives = "Yes".equals(selectedText(relativesGroup));
            explanation.setDisable(!hasRelatives);
            if (!hasRelatives) explanation.clear();
        });
        HBox explanationRow = new HBox(12,
                new Label("If yes, please explain further"), explanation);
        explanationRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(explanation, Priority.ALWAYS);
        VBox body = new VBox(18, section("Personal Information"), personal,
                section("Employment Eligibility"), employment,
                new VBox(9, new Label("Are you legally authorized to work in the country?"),
                        yesNo(authorizedGroup)),
                new VBox(9, new Label("Do you have relatives working for our company?"),
                        yesNo(relativesGroup)), explanationRow);
        body.setPadding(new Insets(24, 22, 24, 22));

        Label declaration = new Label("By submitting this application, you agree\n"
                + "to adhere to company policies and\n"
                + "provide accurate information throughout\n"
                + "the employment process.");
        declaration.setWrapText(true);
        HBox.setHgrow(declaration, Priority.ALWAYS);
        signature.setPrefSize(170, 60);
        signature.setMaxWidth(170);
        HBox footer = new HBox(16, declaration,
                new Label("Applicant's\nSignature"), signature);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(20, 22, 20, 22));
        footer.setStyle("-fx-border-color: black; -fx-border-width: 1 0 0 0;");
        VBox paper = new VBox(header, body, footer);
        paper.setMaxWidth(760);
        paper.setStyle("-fx-background-color: white; -fx-border-color: black;");
        databaseStatus.setWrapText(true);
        message.setWrapText(true);
        message.setMaxWidth(760);
        submit.setDefaultButton(true);
        submit.setStyle("-fx-background-color: black; -fx-text-fill: white;"
                + "-fx-background-radius: 0; -fx-padding: 8 24;");
        submit.setOnAction(event -> save());
        VBox page = new VBox(12, paper, databaseStatus, submit, message);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(20));
        page.setStyle("-fx-background-color: #eee; -fx-font-family: Arial;"
                + "-fx-font-size: 12px;");
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        stage.setScene(new Scene(scroll, 820, 900));
        stage.setTitle("RC Company - Employment Application");
        stage.show();
        checkDatabaseConnection();
    }

    private GridPane paperGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        for (double width : new double[]{18, 30, 16, 36}) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            grid.getColumnConstraints().add(column);
        }
        return grid;
    }

    private Label section(String text) {
        Label label = new Label(text);
        label.setPrefWidth(250);
        label.setPadding(new Insets(7, 12, 7, 12));
        label.setStyle("-fx-background-color: black; -fx-text-fill: white;"
                + "-fx-font-weight: bold;");
        return label;
    }

    private RadioButton choice(String text, ToggleGroup group) {
        RadioButton button = new RadioButton(text);
        button.setToggleGroup(group);
        return button;
    }

    private HBox yesNo(ToggleGroup group) {
        return new HBox(24, choice("Yes", group), choice("No", group));
    }

    private String selectedText(ToggleGroup group) {
        RadioButton selected = (RadioButton) group.getSelectedToggle();
        return selected == null ? "" : selected.getText();
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
                    if (!connection.isValid(5)) {
                        throw new SQLException("Connection validation failed.");
                    }
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
        message.setText("");
        if (name.getText().isBlank() || available.getValue() == null
                || selectedText(genderGroup).isEmpty()
                || selectedText(authorizedGroup).isEmpty()
                || selectedText(relativesGroup).isEmpty()
                || signature.getText().isBlank()) {
            message.setText("Enter name, date, signature, gender, and both Yes/No answers.");
            return;
        }
        boolean authorized = "Yes".equals(selectedText(authorizedGroup));
        boolean relatives = "Yes".equals(selectedText(relativesGroup));
        if (relatives && explanation.getText().isBlank()) {
            message.setText("Please enter your relative's name and relationship.");
            return;
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(salary.getText().trim());
            if (amount.signum() < 0 || amount.scale() > 2
                    || amount.compareTo(new BigDecimal("9999999999.99")) > 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            message.setText("Enter salary from 0 to 9999999999.99, with up to 2 decimal places.");
            return;
        }
        // Read controls on the JavaFX thread before starting the database task.
        String[] values = {name.getText().trim(), address.getText().trim(),
                phone.getText().trim(), email.getText().trim(), education.getText().trim(),
                selectedText(genderGroup), position.getText().trim(),
                relatives ? explanation.getText().trim() : "", signature.getText().trim()};
        Date date = Date.valueOf(available.getValue());
        BigDecimal savedSalary = amount;
        String sql = "INSERT INTO applications "
                + "(full_name, address, phone, email, education, gender, available_date, "
                + "position, salary, authorized, relatives, explanation, signature) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        submit.setDisable(true);
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
                    statement.setBigDecimal(9, savedSalary);
                    statement.setBoolean(10, authorized);
                    statement.setBoolean(11, relatives);
                    statement.setString(12, values[7]);
                    statement.setString(13, values[8]);
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("The application was not inserted.");
                    }
                }
                return null;
            }
        };
        task.setOnSucceeded(event -> {
            submit.setDisable(false);
            databaseStatus.setText("Database connection successful!");
            message.setText("Application saved successfully!");
        });
        task.setOnFailed(event -> {
            submit.setDisable(false);
            message.setText("Could not save application: " + task.getException().getMessage());
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
