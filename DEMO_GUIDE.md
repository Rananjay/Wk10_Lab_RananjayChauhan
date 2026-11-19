# RC Company - Simple Code Explanation and Demo Guide

## What changed
- Replaced the star with white RC letters in a black square. No image file needed.
- Displayed RC / Company without duplicating the company name.
- Used ToggleGroup directly for gender instead of an invisible ComboBox.
- Removed unused layout methods and reused small helpers.
- Kept the form sections, conditional relatives field, database check, and SQL saving.
- Added salary range and decimal-place checks.

## Run the project
1. Use JDK 21 and the existing Maven configuration.
2. Start MySQL on localhost:3306. Run the existing database.sql if needed.
3. Set DB_USER and DB_PASSWORD in the environment of the process you launch. In Eclipse, use Run Configurations > Environment for that launch configuration.
4. Run application.Main with your configured JavaFX launch, or run `mvn clean javafx:run` from the project root with those environment variables set.

The connection URL is `jdbc:mysql://localhost:3306/wk10_lab`. Do not put passwords in source control. Existing pom.xml and database.sql are unchanged.

## Methods in simple words
- `main()` launches JavaFX.
- `start()` builds the window, lays out the form, and connects Submit to save().
- `paperGrid()` aligns labels and inputs in four columns.
- `section()` creates a black section heading.
- `choice()` creates a radio button in a group.
- `yesNo()` creates Yes and No buttons.
- `selectedText()` reads the selected answer, or returns empty text.
- `openConnection()` reads credentials and connects to MySQL.
- `checkDatabaseConnection()` checks the connection when the window opens.
- `save()` validates inputs and inserts one application.
- `runInBackground()` runs a Task on a daemon thread.

## Layout explanation
HBox places controls horizontally. VBox stacks them vertically. GridPane aligns rows and columns. ScrollPane enables scrolling. ToggleGroup allows one radio-button selection per group. The RC logo is a Label styled with a black background and white text.

## What happens on Submit
1. Check name, date, signature, gender, and both Yes/No answers.
2. Require an explanation when relatives is Yes.
3. Validate salary as a non-negative BigDecimal with at most two decimal places and no more than 9999999999.99.
4. Capture values on the JavaFX application thread.
5. Disable Submit during the save.
6. Insert with PreparedStatement in a background Task.
7. Display success or failure and re-enable Submit.

PreparedStatement binds values to SQL placeholders instead of joining user input into SQL text. try-with-resources closes the statement and connection. Background work avoids blocking the JavaFX interface. Task callbacks update the interface after completion.

## SQL parameter map
| Parameter | Column | Value |
| --- | --- | --- |
| 1 | full_name | values[0] |
| 2 | address | values[1] |
| 3 | phone | values[2] |
| 4 | email | values[3] |
| 5 | education | values[4] |
| 6 | gender | values[5] |
| 7 | available_date | date |
| 8 | position | values[6] |
| 9 | salary | savedSalary |
| 10 | authorized | authorized |
| 11 | relatives | relatives |
| 12 | explanation | values[7] |
| 13 | signature | values[8] |

## Short demo script
This is my RC Company employment application built with JavaFX. My RC logo uses a Label, so no separate image file is required. HBox arranges items horizontally, VBox stacks sections, and GridPane aligns the fields. Radio buttons use ToggleGroup so each question allows one answer. Selecting Yes for relatives enables the explanation field. Submit validates the data and saves it to MySQL using PreparedStatement. Database work runs in a background Task so it does not block the interface.

## Demo checklist
Use fictional data. Show the logo, one-choice radio buttons, and relatives explanation behavior. Submit an empty form, then try a negative salary. Enter a complete valid example with salary 45000.00 and submit. Verify the saved record with:

```sql
SELECT * FROM wk10_lab.applications ORDER BY id DESC;
```

## Common questions
- Why ToggleGroup? It allows only one selected button in a group.
- Why helpers? They avoid repeated setup code.
- Why BigDecimal? It represents salary without binary floating-point rounding.
- Why capture values before Task? JavaFX controls should be accessed on the JavaFX application thread.
- Why environment variables? Database credentials stay out of source code.

## Limits and troubleshooting
This is a simplified classroom demo, not a production hiring system. It follows the reference image's main layout but is not pixel-perfect. The signature is typed text, not a drawn or cryptographic signature. Email/phone formats and all text lengths are not fully validated. Excessively long values can be rejected by MySQL. The form is not cleared after saving; another later click can insert another record. Existing config/dao/model/service packages are not used by this replacement Main.java. Check instructor-specific architecture requirements.

If credentials are missing, set DB_USER and DB_PASSWORD in the actual launch environment. If the connection is refused, check MySQL and port 3306. If access is denied, check credentials and permissions. If the database/table is missing, run database.sql. For JavaFX errors, check JDK 21 and the existing Maven setup.

Verification: prepared by source inspection; not compiled, GUI-tested, or tested against a live MySQL database in the assistant environment. Run locally before presenting.
