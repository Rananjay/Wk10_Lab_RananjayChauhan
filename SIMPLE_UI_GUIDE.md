# Simple RC Company form

This update uses the supplied college-registration screenshot as a visual direction: a standard desktop background, normal JavaFX text fields and buttons, plain labels, and modest spacing. It is not an exact copy. No Dashboard or Courses navigation is added because those features do not exist in this application.

## What stays
All 13 submitted values remain: full name, address, phone, email, education, gender, available date, desired position, salary, authorization, relatives, explanation, and signature. Personal Information, Employment Eligibility, the questions, declaration, and signature are kept in the same sequence. The database schema and records are untouched. The previous validation rules, result popups, background save, and clear-after-success behavior are retained.

## What is simpler
- No black title banner, paper border, custom input borders, or custom button colors.
- A small outlined RC text badge and RC Company name.
- Plain bold section labels separated with Separator.
- Submit Application and Clear buttons use standard JavaFX appearance.
- Clear empties the screen, not the database. Both buttons and the form are disabled during saving.
- Status messages use plain text rather than custom colors. This replaces the green/red status-text description in VALIDATION_GUIDE.md; the warning/error/success popups still distinguish outcomes.

## Explain it in the demo
The interface uses normal JavaFX controls. GridPane lines up labels and inputs, HBox makes rows, VBox stacks sections, and BorderPane arranges the header. I kept styling small and reused a few helper methods. validateForm checks the first invalid value, save runs a PreparedStatement in a Task, and clearForm resets the inputs. The same clearForm method is used by the Clear button and after a successful save.

## Test locally
Run git pull origin main, rebuild, and restart the application. Confirm all fields appear. Test Clear, an invalid email, an invalid phone, and a valid save. Verify the success popup and cleared inputs, then confirm the saved row in Workbench. The assistant has not compiled or run this UI; platform fonts and default control rendering may differ from the screenshot. This is readable beginner-oriented code, not evidence or a guarantee of any particular authorship.
