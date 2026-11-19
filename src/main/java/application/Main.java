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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    // Inputs displayed on the form.
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
    private final ToggleGroup gender = new ToggleGroup();
    private final ToggleGroup authorized = new ToggleGroup();
    private final ToggleGroup relatives = new ToggleGroup();
    private final Label databaseStatus = new Label();
    private final Label message = new Label();
    private final Button submit = new Button("Submit Application");
    private final ApplicationService service =
            new ApplicationService(new JdbcApplicationDao());

    @Override
    public void start(Stage stage) {
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(12);
        ColumnConstraints labels = new ColumnConstraints();
        ColumnConstraints inputs = new ColumnConstraints();
        inputs.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(labels, inputs);
        addRow(form, 0, "Full Name", name);
        addRow(form, 1, "Current Address", address);
        addRow(form, 2, "Contact Number", phone);
        addRow(form, 3, "Email Address", email);
        addRow(form, 4, "Highest Education", education);
        addRow(form, 5, "Gender", choices(gender, "Male", "Female", "Other"));
        addRow(form, 6, "Date Available", available);
        addRow(form, 7, "Desired Position", position);
        addRow(form, 8, "Desired Salary", salary);
        addRow(form, 9, "Authorized to work?", choices(authorized, "Yes", "No"));
        addRow(form, 10, "Relatives in company?", choices(relatives, "Yes", "No"));
        addRow(form, 11, "If yes, explain", explanation);
        addRow(form, 12, "Applicant's Signature", signature);
        explanation.setDisable(true);
        relatives.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean yes = "Yes".equals(answer(relatives));
            explanation.setDisable(!yes);
            if (!yes) explanation.clear();
        });

        // Text-based logo: no image file is required.
        Label logo = new Label("RC");
        logo.setMinSize(60, 60);
        logo.setAlignment(Pos.CENTER);
        logo.setStyle("-fx-background-color: black;-fx-text-fill: white;"
                + "-fx-font-size: 25px;-fx-font-weight: bold;");
        Label title = new Label("RC Job Application");
        title.setStyle("-fx-font-size: 25px;-fx-font-weight: bold;");
        HBox header = new HBox(15, logo, title);
        header.setAlignment(Pos.CENTER_LEFT);
        Label declaration = new Label("By submitting, I confirm that my information "
                + "is accurate and agree to company policies.");
        declaration.setWrapText(true);
        databaseStatus.setWrapText(true);
        message.setWrapText(true);
        submit.setDefaultButton(true);
        submit.setOnAction(event -> save());
        VBox page = new VBox(18, header, new Separator(), form,
                declaration, databaseStatus, submit, message);
        page.setPadding(new Insets(25));
        page.setStyle("-fx-background-color: white;-fx-font-family: Arial;-fx-font-size: 13px;");
        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        stage.setTitle("RC Job Application");
        stage.setScene(new Scene(scroll, 650, 800));
        stage.show();
        runTask(() -> {
            Database.checkConnection();
            return null;
        }, databaseStatus, "Database connection successful!");
    }

    // Put a label and its input on one row.
    private void addRow(GridPane form, int row, String text, Node input) {
        form.addRow(row, new Label(text), input);
        if (input instanceof Region) {
            ((Region) input).setMaxWidth(Double.MAX_VALUE);
        }
    }

    // Build radio buttons using the supplied choices.
    private HBox choices(ToggleGroup group, String... options) {
        HBox buttons = new HBox(15);
        for (String option : options) {
            RadioButton button = new RadioButton(option);
            button.setToggleGroup(group);
            button.setUserData(option);
            buttons.getChildren().add(button);
        }
        return buttons;
    }

    private String answer(ToggleGroup group) {
        Toggle selected = group.getSelectedToggle();
        return selected == null ? "" : selected.getUserData().toString();
    }

    // Collect inputs, validate them, and ask the service to save.
    private void save() {
        message.setText("");
        if (answer(gender).isEmpty() || answer(authorized).isEmpty()
                || answer(relatives).isEmpty()) {
            message.setText("Select gender and both Yes/No answers.");
            return;
        }
        try {
            BigDecimal amount = new BigDecimal(salary.getText().trim());
            EmploymentApplication application = new EmploymentApplication(
                    name.getText().trim(), address.getText().trim(),
                    phone.getText().trim(), email.getText().trim(),
                    education.getText().trim(), answer(gender), available.getValue(),
                    position.getText().trim(), amount,
                    "Yes".equals(answer(authorized)), "Yes".equals(answer(relatives)),
                    explanation.getText().trim(), signature.getText().trim());
            service.validate(application);
            runTask(() -> {
                service.save(application);
                return null;
            }, message, "Application saved successfully!");
        } catch (NumberFormatException exception) {
            message.setText("Enter a valid salary, for example 45000.");
        } catch (IllegalArgumentException exception) {
            message.setText(exception.getMessage());
        }
    }

    // Keep database work off the JavaFX screen thread.
    private void runTask(Callable<Void> action, Label output, String successMessage) {
        output.setText("Please wait...");
        submit.setDisable(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return action.call();
            }
        };
        task.setOnSucceeded(event -> {
            output.setText(successMessage);
            submit.setDisable(false);
        });
        task.setOnFailed(event -> {
            output.setText("Error: " + task.getException().getMessage());
            submit.setDisable(false);
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
