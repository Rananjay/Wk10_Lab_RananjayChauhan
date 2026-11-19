package application;

import application.config.Database;
import application.dao.JdbcApplicationDao;
import application.model.EmploymentApplication;
import application.service.ApplicationService;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private final ApplicationService service = new ApplicationService(new JdbcApplicationDao());
    private final TextField name = new TextField(), address = new TextField();
    private final TextField phone = new TextField(), email = new TextField();
    private final TextField education = new TextField(), position = new TextField();
    private final TextField salary = new TextField(), explanation = new TextField();
    private final TextField signature = new TextField();
    private final DatePicker available = new DatePicker();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private final ToggleGroup authorizedGroup = new ToggleGroup();
    private final ToggleGroup relativesGroup = new ToggleGroup();
    private final Label databaseStatus = new Label("Checking database connection...");
    private final Label message = new Label();
    private final Button submit = new Button("Submit Application");

    @Override
    public void start(Stage stage) {
        HBox genders = new HBox(12);
        for (String value : new String[]{"Male", "Female", "Other"}) {
            RadioButton button = new RadioButton(value);
            button.setToggleGroup(genderGroup);
            button.setUserData(value);
            genders.getChildren().add(button);
        }
        genders.setAlignment(Pos.CENTER_LEFT);
        HBox authorizedChoices = yesNoChoices(authorizedGroup);
        HBox relativeChoices = yesNoChoices(relativesGroup);
        explanation.setDisable(true);
        relativesGroup.selectedToggleProperty().addListener((obs, oldValue, value) -> {
            boolean yes = value != null && Boolean.TRUE.equals(value.getUserData());
            explanation.setDisable(!yes);
            if (!yes) explanation.clear();
        });
        for (TextField field : new TextField[]{name, address, phone, email, education, position, salary, explanation, signature}) {
            field.setPrefHeight(30);
            field.setMaxWidth(Double.MAX_VALUE);
            field.setStyle("-fx-background-color: white;-fx-border-color: #777777;-fx-border-width: 1;-fx-background-radius: 0;-fx-border-radius: 0;");
        }
        available.setPromptText("mm/dd/yyyy");
        available.setPrefHeight(30);
        available.setMaxWidth(Double.MAX_VALUE);
        Label logo = new Label("\u2605");
        logo.setStyle("-fx-font-size: 35px;-fx-text-fill: black;");
        Label companyName = new Label("John");
        companyName.setStyle("-fx-font-weight: bold;");
        HBox company = new HBox(8, logo, new VBox(0, companyName, new Label("Company")));
        company.setAlignment(Pos.CENTER_LEFT);
        company.setPrefWidth(165);
        Label title = new Label("Employment Application");
        title.setAlignment(Pos.CENTER);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setMinHeight(62);
        title.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-font-size: 24px;-fx-font-weight: bold;");
        HBox.setHgrow(title, Priority.ALWAYS);
        HBox header = new HBox(12, company, title);
        header.setAlignment(Pos.CENTER_LEFT);
        GridPane personal = paperGrid();
        personal.add(paperLabel("Full Name"), 0, 0);
        personal.add(name, 1, 0, 3, 1);
        personal.add(paperLabel("Current Address"), 0, 1);
        personal.add(address, 1, 1, 3, 1);
        personal.add(paperLabel("Contact\nNumber"), 0, 2);
        personal.add(phone, 1, 2);
        personal.add(paperLabel("Email\nAddress"), 2, 2);
        personal.add(email, 3, 2);
        personal.add(paperLabel("Highest Educational\nAttainment"), 0, 3);
        personal.add(education, 1, 3);
        personal.add(new VBox(8, new Label("Gender"), genders), 2, 3, 2, 1);
        GridPane employment = paperGrid();
        employment.add(paperLabel("Date\nAvailable"), 0, 0);
        employment.add(available, 1, 0);
        employment.add(paperLabel("Desired\nPosition"), 2, 0);
        employment.add(position, 3, 0);
        employment.add(paperLabel("Desired Salary"), 0, 1);
        employment.add(salary, 1, 1, 3, 1);
        GridPane explanationRow = new GridPane();
        explanationRow.setHgap(12);
        Label explanationLabel = paperLabel("If yes, please explain further");
        explanationLabel.setPrefWidth(190);
        explanationRow.add(explanationLabel, 0, 0);
        explanationRow.add(explanation, 1, 0);
        GridPane.setHgrow(explanation, Priority.ALWAYS);
        VBox body = new VBox(18, paperSection("Personal Information"), personal,
                paperSection("Employment Eligibility"), employment,
                new VBox(9, new Label("Are you legally authorized to work in the country?"), authorizedChoices),
                new VBox(9, new Label("Do you have relatives working for our company?"), relativeChoices), explanationRow);
        body.setPadding(new Insets(24, 22, 24, 22));
        Label declaration = paperLabel("By submitting this application, you agree\nto adhere to company policies and\nprovide accurate information throughout\nthe employment process.");
        declaration.setMaxWidth(Double.MAX_VALUE);
        declaration.setStyle("-fx-line-spacing: 5px;");
        HBox.setHgrow(declaration, Priority.ALWAYS);
        signature.setPrefWidth(170);
        signature.setMaxWidth(170);
        signature.setPrefHeight(60);
        HBox footer = new HBox(16, declaration, new Label("Applicant's\nSignature"), signature);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(20, 22, 20, 22));
        footer.setStyle("-fx-border-color: black;-fx-border-width: 1 0 0 0;");
        VBox paper = new VBox(header, body, footer);
        paper.setMaxWidth(760);
        paper.setStyle("-fx-background-color: white;-fx-border-color: black;-fx-border-width: 1;");
        databaseStatus.setWrapText(true);
        message.setWrapText(true);
        message.setMaxWidth(760);
        submit.setDefaultButton(true);
        submit.setPrefHeight(36);
        submit.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-background-radius: 0;-fx-padding: 8 24;");
        submit.setOnAction(event -> save());
        VBox page = new VBox(12, paper, databaseStatus, submit, message);
        page.setAlignment(Pos.TOP_CENTER);
        page.setPadding(new Insets(20));
        page.setStyle("-fx-background-color: #eeeeee;-fx-font-family: Arial;-fx-font-size: 12px;");
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        stage.setScene(new Scene(scroll, 820, 900));
        stage.setTitle("Employment Application");
        stage.show();
        Task<Void> check = background(() -> { Database.checkConnection(); return null; });
        check.setOnSucceeded(event -> status("Database connection successful!", true));
        check.setOnFailed(event -> status("Database connection failed: " + check.getException().getMessage(), false));
        startTask(check);
    }

    private void save() {
        message.setText("");
        if (genderGroup.getSelectedToggle() == null || authorizedGroup.getSelectedToggle() == null
                || relativesGroup.getSelectedToggle() == null) {
            showMessage("Choose a gender and both Yes/No answers.", false);
            return;
        }
        final EmploymentApplication application;
        try {
            BigDecimal amount = new BigDecimal(salary.getText().trim());
            application = new EmploymentApplication(name.getText().trim(), address.getText().trim(),
                    phone.getText().trim(), email.getText().trim(), education.getText().trim(),
                    (String) genderGroup.getSelectedToggle().getUserData(), available.getValue(),
                    position.getText().trim(), amount,
                    Boolean.TRUE.equals(authorizedGroup.getSelectedToggle().getUserData()),
                    Boolean.TRUE.equals(relativesGroup.getSelectedToggle().getUserData()),
                    explanation.getText().trim(), signature.getText().trim());
            service.validate(application);
        } catch (NumberFormatException exception) {
            showMessage("Enter a valid non-negative salary, for example 45000.", false);
            return;
        } catch (IllegalArgumentException exception) {
            showMessage(exception.getMessage(), false);
            return;
        }
        submit.setDisable(true);
        showMessage("Saving application...", true);
        Task<Void> task = background(() -> { service.save(application); return null; });
        task.setOnSucceeded(event -> {
            submit.setDisable(false);
            status("Database connection successful!", true);
            showMessage("Application saved successfully!", true);
        });
        task.setOnFailed(event -> {
            submit.setDisable(false);
            showMessage("Could not save application: " + task.getException().getMessage(), false);
        });
        startTask(task);
    }

    private HBox yesNoChoices(ToggleGroup group) {
        RadioButton yes = new RadioButton("Yes"), no = new RadioButton("No");
        yes.setToggleGroup(group);
        no.setToggleGroup(group);
        yes.setUserData(Boolean.TRUE);
        no.setUserData(Boolean.FALSE);
        HBox choices = new HBox(24, yes, no);
        choices.setAlignment(Pos.CENTER_LEFT);
        return choices;
    }

    private GridPane paperGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        for (double width : new double[]{17, 30, 17, 36}) {
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
        label.setStyle("-fx-background-color: black;-fx-text-fill: white;-fx-font-weight: bold;");
        return label;
    }

    private void status(String text, boolean success) {
        databaseStatus.setText(text);
        databaseStatus.setStyle("-fx-font-weight: bold;-fx-text-fill: " + (success ? "#15803d;" : "#b91c1c;"));
    }

    private void showMessage(String text, boolean success) {
        message.setText(text);
        message.setStyle("-fx-text-fill: " + (success ? "#15803d;" : "#b91c1c;"));
    }

    private Task<Void> background(Callable<Void> action) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception { return action.call(); }
        };
    }

    private void startTask(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) { launch(args); }
}
