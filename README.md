# Wk10_Lab_RananjayChauhan

Simple employment application: one Java class, JavaFX form, JDBC, MySQL, and PreparedStatement. No FXML or module-info.java.

## 1. Database

Start your local MySQL server. Open database.sql in MySQL Workbench and execute it. This creates wk10_lab and applications. No database is created just by running the Java app.

## 2. Eclipse

1. Clone this repository using Eclipse Git, or clone it locally.
2. File > Import > Maven > Existing Maven Projects. Select the folder containing pom.xml, then Finish.
3. Use JDK 21 for the project (Project > Properties > Java Build Path).
4. Right-click the project > Run As > Maven build...
5. Goals: clean compile javafx:run
6. On the Environment tab, add DB_USER (your local MySQL username) and DB_PASSWORD (your local password). Do not commit passwords.
7. Click Run. Maven downloads JavaFX and MySQL Connector/J; you do not need to copy JARs or add JavaFX VM arguments when using this Maven run configuration.

## 3. Demo

Fill the form with fake example information. Choose the dropdown values, date, and a numeric salary. Type your name for the signature and click Submit.

In MySQL Workbench run:

```sql
USE wk10_lab;
SELECT * FROM applications;
```

Each click of Submit inserts a new row. The signature is typed text, not a handwritten image. Layout is intentionally basic.

## Explain the code

- start(): creates the fields and Submit button using GridPane.
- save(): checks basic input and opens a JDBC connection.
- prepareStatement(sql): prepares INSERT with 13 question-mark placeholders.
- setString/setDate/setBigDecimal/setBoolean: bind form values to placeholders, numbered from 1.
- executeUpdate(): inserts the row.
- try-with-resources closes the connection and statement automatically.

The database connection runs directly in the button handler for this small classroom demo, so it may briefly freeze while connecting. This is not a production application. Use fake data only.

## Command line alternative

Set DB_USER and DB_PASSWORD in your shell, then run:

```bash
mvn clean compile javafx:run
```

## Verification status

Source and configuration prepared for the lab. Not compiled or runtime-tested against your local MySQL server.
