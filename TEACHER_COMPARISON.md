# Following the teacher's JavaFX/JDBC example

Reference: https://github.com/vahuja8/Wk10_FxJdbc_Java21_crud
Inspected files: Wk10JavaFx/src/application/Main.java, User.java, UserDao.java, Database.java. The inspected Main screen is an Add User form; despite the repository name, that screen is not a complete read/update/delete interface.

## Main idea
The teacher separates the interface, a data model, SQL operations, and the database connection. This employment form follows that responsibility split using the equivalent files already present in this project rather than adding another unused set of classes.

| Teacher example | Employment project |
| --- | --- |
| Main builds a centered GridPane | Main builds a centered two-column GridPane |
| Text title, Tahoma normal 20 | Same title style, with RC branding |
| Labels and fields added row by row | All employment fields added explicitly row by row |
| HBox aligned BOTTOM_RIGHT with Save | Same arrangement with Save and Clear |
| createUserObject creates a User | createApplicationObject creates EmploymentApplication |
| UserDao.saveUser handles INSERT | JdbcApplicationDao.save handles INSERT |
| Database.getDBConnection opens JDBC connection | application.config.Database.openConnection opens JDBC connection |
| alert helper displays an Alert | alert helper displays an Alert |

## What remains different, and why
- The subject is employment applications, not user accounts. All 13 submitted values, both sections, the questions, declaration, and signature remain.
- EmploymentApplication remains the existing Java 21 record, not a mutable bean with setters. It is an immutable data carrier populated by its constructor. Its accessor is fullName(), for example, rather than getFullName(). Keeping it avoids breaking the existing DAO/service code.
- JdbcApplicationDao.save returns void and throws SQLException if exactly one row was not inserted. The teacher DAO returns a generated ID. This UI reports success when its DAO Task completes normally; it does not display a generated ID.
- Credentials remain in DB_USER and DB_PASSWORD, with optional DB_URL. No teacher credentials are copied. The default database is wk10_lab.
- Background Tasks are retained to avoid blocking the interface, rather than doing the SQL directly inside the button handler.
- The existing DAO uses try-with-resources and the connection's default auto-commit behavior. The teacher DAO uses an explicit transaction and manual cleanup. This update does not copy exception-swallowing behavior.
- The teacher checks username uniqueness. Employment applications have no corresponding unique username. No email-only duplicate rule or schema constraint is invented. The save guard and cleared form reduce accidental repeated submissions; separate valid submissions can still create multiple rows.
- Existing email/phone/date/salary/required-field validation, success/error popups, input preservation after errors, and automatic clearing after success remain.
- The screen is taller and scrollable because it has more fields. This is a two-column layout, not the earlier side-by-side four-column form.
- Earlier guide descriptions of Main performing SQL directly or using BorderPane are superseded by this guide. SQL now goes through the existing DAO; Main uses GridPane. Existing service classes remain unchanged and are not used by this Main.

## Explain this flow during the demo
1. start() creates the GridPane, labels, fields, radio buttons, and buttons.
2. The Save button calls saveApplication().
3. validateForm() returns the first error before any insert.
4. createApplicationObject() copies the form into an EmploymentApplication model on the JavaFX thread.
5. A background Task calls applicationDao.save(application).
6. The DAO obtains a connection from Database, binds 13 SQL parameters, and inserts one row.
7. On success, Main clears the form and shows an information alert. On failure, it keeps the inputs and shows an error.
8. Clear resets the screen only. It never deletes database records.

## Run and test
Keep the existing pom.xml and database.sql. Use JDK 21 and the configured JavaFX dependencies. Set DB_USER and DB_PASSWORD in the actual launch environment. DB_URL is optional; if absent, jdbc:mysql://localhost:3306/wk10_lab is used. Pull main, rebuild, and restart. Test invalid inputs, Clear, a successful save, and a database error. Verify saved rows in Workbench.

The teacher repository was inspected through GitHub. This refactor has not been compiled, GUI-tested, or tested against live MySQL in the assistant environment. It adapts the teaching pattern; it is not a claim of identical code, complete CRUD functionality, or a guarantee of a grade. No stored rows or database schema are modified by this commit.
