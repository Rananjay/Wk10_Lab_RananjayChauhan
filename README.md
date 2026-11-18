# Wk10_Lab_RananjayChauhan

Simple JavaFX employment form using MySQL and PreparedStatement.

## Setup

1. Clone and import as an existing Eclipse project.
2. Add your JavaFX and MySQL Connector/J JARs to the build path.
3. Run database.sql in MySQL Workbench.
4. In Run Configurations, copy the JavaFX VM arguments from your working FX project.
5. Add DB_USER and DB_PASSWORD in the Environment tab.
6. Run application.Main, fill the form, and click Submit.

## Check saved data

```sql
USE wk10_lab;
SELECT * FROM applications;
```
