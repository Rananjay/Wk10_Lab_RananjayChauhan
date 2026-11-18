# Wk10_Lab_RananjayChauhan

One Java class: JavaFX employment form + MySQL JDBC PreparedStatement. Eclipse import does not require Maven. The optional pom.xml remains available but is not needed for the steps below.

## Eclipse setup

1. File > Import > Git > Projects from Git > Clone URI. Use https://github.com/Rananjay/Wk10_Lab_RananjayChauhan.git and select main.
2. Select Import existing Eclipse projects when offered and finish. Alternatively, after cloning, use File > Import > General > Existing Projects into Workspace and select the cloned folder.
3. Right-click project > Properties > Java Build Path > Libraries. Add the same JavaFX SDK JARs and MySQL Connector/J JAR used in your working Hospital-MFX project. Use one JavaFX SDK version only and a compatible JDK, matching your working project.
4. Open src/main/java/application/Main.java.
5. Run > Run Configurations > Java Application. Create a configuration for this project, main class application.Main.
6. Copy the JavaFX VM arguments from your working Hospital-MFX launch configuration. They usually look like --module-path "YOUR_JAVAFX_SDK/lib" --add-modules javafx.controls. Replace the path with your actual SDK path.
7. In the Environment tab, add DB_USER and DB_PASSWORD using your local MySQL credentials. Never commit passwords.
8. Apply > Run.

## Database

Start MySQL. Open database.sql in MySQL Workbench and execute it to create wk10_lab and applications. The Java application does not create the database automatically.

## Demo

Use fake data. Enter a name, choose dropdowns and date, enter a numeric salary such as 45000, and type your name for the signature. Click Submit. Each click inserts a new row.

Show the saved data in MySQL Workbench:

```sql
USE wk10_lab;
SELECT * FROM applications;
```

## Explain

start() builds the form. save() opens the connection, prepares an INSERT with 13 question-mark placeholders, binds the field values with setters, and calls executeUpdate(). Try-with-resources closes the connection and statement. No FXML or separate controllers.

This is a classroom demo; database access is synchronous and may briefly freeze the form. Not compiled or tested against your local MySQL server.
