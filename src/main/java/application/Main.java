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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/wk10_lab";

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
    private final ComboBox<String> gender = new ComboBox<>();

    private final ToggleGroup authorizedGroup = new ToggleGroup();
    private final ToggleGroup relativesGroup = new ToggleGroup();

    private final Label databaseStatus =
            new Label("Checking database connection...");
    private final Label message = new Label();
    private final Button submit = new Button("Submit Application");

    @Override
    public void start(Stage stage) {
        // Gender radio buttons update the existing gender value
        // so your save() method does not need to change.
        ToggleGroup genderGroup = new ToggleGroup();

        RadioButton male = new RadioButton("Male");
        RadioButton female = new RadioButton("Female");
        RadioButton other = new RadioButton("Other");

        male.setToggleGroup(genderGroup);
        female.setToggleGroup(genderGroup);
        other.setToggleGroup(genderGroup);

        male.setOnAction(event -> gender.setValue("Male"));
        female.setOnAction(event -> gender.setValue("Female"));
        other.setOnAction(event -> gender.setValue("Other"));

        HBox genderChoices = new HBox(12, male, female, other);
        genderChoices.setAlignment(Pos.CENTER_LEFT);

        HBox authorizedChoices = yesNoChoices(authorizedGroup);
        HBox relativesChoices = yesNoChoices(relativesGroup);

        explanation.setDisable(true);

        relativesGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    boolean yes = newValue != null
                            && Boolean.TRUE.equals(newValue.getUserData());

                    explanation.setDisable(!yes);

                    if (!yes) {
                        explanation.clear();
                    }
                }
        );

        TextField[] fields = {
                name, address, phone, email, education,
                position, salary, explanation, signature
        };

        for (TextField field : fields) {
            field.setPromptText("");
            field.setPrefHeight(30);
            field.setMaxWidth(Double.MAX_VALUE);
            field.setStyle(
                    "-fx-background-color: white;"
                    + "-fx-border-color: #777777;"
                    + "-fx-border-width: 1;"
                    + "-fx-background-radius: 0;"
                    + "-fx-border-radius: 0;"
            );
        }

        available.setPromptText("mm/dd/yyyy");
        available.setPrefHeight(30);
        available.setMaxWidth(Double.MAX_VALUE);

        // Header and simple text logo
        Label logo = new Label("\u2605");
        logo.setStyle("-fx-font-size: 35px; -fx-text-fill: black;");

        Label companyName = new Label("John");
        companyName.setStyle("-fx-font-weight: bold;");

        Label companySubtitle = new Label("Company");

        VBox companyText = new VBox(0, companyName, companySubtitle);

        HBox company = new HBox(8, logo, companyText);
        company.setAlignment(Pos.CENTER_LEFT);
        company.setPrefWidth(165);

        Label title = new Label("Employment Application");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setMinHeight(62);
        title.setStyle(
                "-fx-background-color: black;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 24px;"
                + "-fx-font-weight: bold;"
        );

        HBox.setHgrow(title, Priority.ALWAYS);

        HBox header = new HBox(12, company, title);
        header.setAlignment(Pos.CENTER_LEFT);

        // Personal information
        GridPane personal = paperGrid();

        personal.add(paperLabel("Full Name"), 0, 0);
        personal.add(name, 1, 0, 3, 1);

        personal.add(paperLabel("Current Address"), 0, 1);
        personal.add(address, 1, 1, 3, 1);

        personal.add(paperLabel("Contact\nNumber"), 0, 2);
        personal.add(phone, 1, 2);
        personal.add(paperLabel("Email\nAddress"), 2, 2);
        personal.add(email, 3, 2);

        personal.add(
                paperLabel("Highest Educational\nAttainment"),
                0, 3
        );
        personal.add(education, 1, 3);

        VBox genderBox = new VBox(
                8, new Label("Gender"), genderChoices
        );
        personal.add(genderBox, 2, 3, 2, 1);

        // Employment eligibility
        GridPane employment = paperGrid();

        employment.add(paperLabel("Date\nAvailable"), 0, 0);
        employment.add(available, 1, 0);
        employment.add(paperLabel("Desired\nPosition"), 2, 0);
        employment.add(position, 3, 0);

        employment.add(paperLabel("Desired Salary"), 0, 1);
        employment.add(salary, 1, 1, 3, 1);

        VBox authorization = new VBox(
                9,
                new Label(
                        "Are you legally authorized to work in the country?"
                ),
                authorizedChoices
        );

        VBox relativeQuestion = new VBox(
                9,
                new Label(
                        "Do you have relatives working for our company?"
                ),
                relativesChoices
        );

        GridPane explanationRow = new GridPane();
        explanationRow.setHgap(12);

        Label explanationLabel =
                new Label("If yes, please explain further");
        explanationLabel.setWrapText(true);
        explanationLabel.setPrefWidth(190);

        explanationRow.add(explanationLabel, 0, 0);
        explanationRow.add(explanation, 1, 0);
        GridPane.setHgrow(explanation, Priority.ALWAYS);

        VBox body = new VBox(
                18,
                paperSection("Personal Information"),
                personal,
                paperSection("Employment Eligibility"),
                employment,
                authorization,
                relativeQuestion,
                explanationRow
        );
        body.setPadding(new Insets(24, 22, 24, 22));

        // Declaration and signature
        Label declaration = new Label(
                "By submitting this application, you agree\n"
                + "to adhere to company policies and\n"
                + "provide accurate information throughout\n"
                + "the employment process."
        );
        declaration.setWrapText(true);
        declaration.setMaxWidth(Double.MAX_VALUE);
        declaration.setStyle("-fx-line-spacing: 5px;");
        HBox.setHgrow(declaration, Priority.ALWAYS);

        Label signatureLabel = new Label("Applicant's\nSignature");

        signature.setPrefWidth(170);
        signature.setMaxWidth(170);
        signature.setPrefHeight(60);

        HBox footer = new HBox(
                16, declaration, signatureLabel, signature
        );
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(20, 22, 20, 22));
        footer.setStyle(
                "-fx-border-color: black;"
                + "-fx-border-width: 1 0 0 0;"
        );

        VBox paper = new VBox(header, body, footer);
        paper.setMaxWidth(760);
        paper.setStyle(
                "-fx-background-color: white;"
                + "-fx-border-color: black;"
                + "-fx-border-width: 1;"
        );

        // Keep status messages outside the paper form.
        databaseStatus.setWrapText(true);
        message.setWrapText(true);
        message.setMaxWidth(760);

        submit.setText("Submit Application");
        submit.setDefaultButton(true);
        submit.setPrefHeight(36);
        submit.setStyle(
                "-fx-background-color: black;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 0;"
                + "-fx-padding: 8 24;"
        );
        submit.setOnAction(event -> save());

        VBox page = new VBox(
                12, paper, databaseStatus, submit, message
        );
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(20));
        page.setStyle(
                "-fx-background-color: #eeeeee;"
                + "-fx-font-family: Arial;"
                + "-fx-font-size: 12px;"
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 820, 900));
        stage.setTitle("Employment Application");
        stage.show();

        checkDatabaseConnection();
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(22);
        grid.setVgap(16);

        for (int i = 0; i < 2; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(50);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }

        return grid;
    }

    private VBox field(String caption, Node control) {
        Label label = new Label(caption);
        label.setWrapText(true);
        label.setStyle("-fx-font-weight: bold;");

        if (control instanceof Region) {
            ((Region) control).setMaxWidth(Double.MAX_VALUE);
        }

        return new VBox(7, label, control);
    }

    private void addField(
            GridPane grid, int column, int row,
            String caption, Node control) {

        grid.add(field(caption, control), column, row);
    }

    private Label sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #17365d;"
        );
        return label;
    }

    private HBox yesNoChoices(ToggleGroup group) {
        RadioButton yes = new RadioButton("Yes");
        RadioButton no = new RadioButton("No");

        yes.setToggleGroup(group);
        no.setToggleGroup(group);

        yes.setUserData(Boolean.TRUE);
        no.setUserData(Boolean.FALSE);

        HBox choices = new HBox(24, yes, no);
        choices.setAlignment(Pos.CENTER_LEFT);
        return choices;
    }

    private boolean selectedBoolean(ToggleGroup group) {
        return Boolean.TRUE.equals(
                group.getSelectedToggle().getUserData()
        );
    }

    private Connection openConnection() throws SQLException {
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (user == null || password == null) {
            throw new SQLException(
                    "Set DB_USER and DB_PASSWORD in "
                    + "Run Configurations > Environment."
            );
        }

        return DriverManager.getConnection(DB_URL, user, password);
    }

    private void checkDatabaseConnection() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (Connection connection = openConnection()) {
                    if (!connection.isValid(5)) {
                        throw new SQLException(
                                "Database connection validation failed."
                        );
                    }
                }
                return null;
            }
        };

        task.setOnSucceeded(event ->
                showDatabaseStatus(
                        "Database connection successful!", true
                )
        );

        task.setOnFailed(event ->
                showDatabaseStatus(
                        "Database connection failed: "
                                + task.getException().getMessage(),
                        false
                )
        );

        runInBackground(task);
    }

    private void save() {
        message.setText("");

        if (name.getText().isBlank()
                || available.getValue() == null
                || gender.getValue() == null
                || authorizedGroup.getSelectedToggle() == null
                || relativesGroup.getSelectedToggle() == null
                || signature.getText().isBlank()) {

            showMessage(
                    "Enter your name, date, signature, gender, "
                    + "and both Yes/No answers.",
                    false
            );
            return;
        }

        final boolean authorized = selectedBoolean(authorizedGroup);
        final boolean relatives = selectedBoolean(relativesGroup);

        if (relatives && explanation.getText().isBlank()) {
            showMessage(
                    "Please enter your relative's name and relationship.",
                    false
            );
            return;
        }

        final BigDecimal amount;

        try {
            amount = new BigDecimal(salary.getText().trim());

            if (amount.signum() < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            showMessage(
                    "Enter a valid non-negative salary, for example 45000.",
                    false
            );
            return;
        }

        // Capture form values before starting the background task.
        final String fullName = name.getText().trim();
        final String currentAddress = address.getText().trim();
        final String contact = phone.getText().trim();
        final String emailAddress = email.getText().trim();
        final String qualification = education.getText().trim();
        final String selectedGender = gender.getValue();
        final Date availableDate = Date.valueOf(available.getValue());
        final String desiredPosition = position.getText().trim();
        final String relativeExplanation =
                relatives ? explanation.getText().trim() : "";
        final String signedName = signature.getText().trim();

        String sql =
                "INSERT INTO applications "
                + "(full_name, address, phone, email, education, "
                + "gender, available_date, position, salary, "
                + "authorized, relatives, explanation, signature) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        submit.setDisable(true);
        showMessage("Saving application...", true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try (Connection connection = openConnection();
                     PreparedStatement statement =
                             connection.prepareStatement(sql)) {

                    statement.setString(1, fullName);
                    statement.setString(2, currentAddress);
                    statement.setString(3, contact);
                    statement.setString(4, emailAddress);
                    statement.setString(5, qualification);
                    statement.setString(6, selectedGender);
                    statement.setDate(7, availableDate);
                    statement.setString(8, desiredPosition);
                    statement.setBigDecimal(9, amount);
                    statement.setBoolean(10, authorized);
                    statement.setBoolean(11, relatives);
                    statement.setString(12, relativeExplanation);
                    statement.setString(13, signedName);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "The application was not inserted."
                        );
                    }
                }

                return null;
            }
        };

        task.setOnSucceeded(event -> {
            submit.setDisable(false);
            showDatabaseStatus(
                    "Database connection successful!", true
            );
            showMessage("Application saved successfully!", true);
        });

        task.setOnFailed(event -> {
            submit.setDisable(false);
            showMessage(
                    "Could not save application: "
                            + task.getException().getMessage(),
                    false
            );
        });

        runInBackground(task);
    }

    private void showDatabaseStatus(String text, boolean success) {
        databaseStatus.setText(text);
        databaseStatus.setStyle(
                "-fx-font-weight: bold; -fx-text-fill: "
                + (success ? "#15803d;" : "#b91c1c;")
        );
    }

    private void showMessage(String text, boolean success) {
        message.setText(text);
        message.setStyle(
                "-fx-text-fill: "
                + (success ? "#15803d;" : "#b91c1c;")
        );
    }

    private void runInBackground(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private GridPane paperGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);

        double[] widths = {17, 30, 17, 36};

        for (double width : widths) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(width);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }

        return grid;
    }

    private Label paperLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private Label paperSection(String text) {
        Label label = new Label(text);
        label.setPrefWidth(250);
        label.setPadding(new Insets(7, 12, 7, 12));
        label.setStyle(
                "-fx-background-color: black;"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
        );
        return label;
    }
}