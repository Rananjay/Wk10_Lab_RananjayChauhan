# Validation and save feedback - updated behavior

This guide supersedes the validation and form-reset limitations in DEMO_GUIDE.md.

## Successful save
The app requires exactly one affected row from the INSERT. A success popup appears and a green message remains. Inputs, date, and radio selections are cleared only after successful completion. Submit and all form inputs are disabled while saving, with an additional saving flag to block repeated calls. The form stays disabled until the result popup is dismissed.

## Invalid form or database error
The app shows a popup and keeps inputs. A validation error prevents the INSERT from running. Database failures say the save could not be confirmed; a lost connection during an INSERT can leave the outcome uncertain, so check Workbench before retrying.

## Validation rules for this classroom demo
- Name, address, phone, email, education, position, signature, salary, available date, gender, and both Yes/No answers are required.
- Name needs at least one letter; names are not restricted to English letters or exactly two words.
- Text limits match the existing VARCHAR columns: name/email/education/position/signature 150, address 255, phone 40 characters. The relatives explanation has an application limit of 4000 characters.
- Basic email format: name@example.com, dotted domain, no spaces, no consecutive local-part dots. This is a practical restricted format check, not full international/RFC email support or mailbox verification.
- Canadian/US phone format: 10 digits, optionally preceded by country code 1 or +1. Spaces, dots, dashes, and correctly positioned area-code parentheses are accepted. Area and exchange codes begin with 2-9. It does not verify assignment, ownership, reachability, or every numbering-plan rule. International non-+1 numbers and extensions are not supported.
- Typed dates are committed before saving. The date must be today or later, with year at most 9999. This is a demo business rule.
- Salary uses plain digits with an optional one- or two-digit decimal part, from 0 to 9999999999.99. Currency symbols, commas, negative values, and exponent notation are rejected.
- A relatives explanation is required only for Yes. Choosing No clears and disables it.
- No means No for work authorization; it is saved as a boolean, not treated as a missing answer.

## Repeated rows
Earlier entries remain untouched. Each intentional valid submission still creates a new application. Clearing the form and blocking concurrent clicks reduce accidental repeat submissions in this application window; they are not database-wide duplicate detection or an exactly-once guarantee. No unique constraint is added and no rows are deleted.

## Demo test checklist
1. Submit an empty form: warning, no insert.
2. Fill required fields but use bad-email: warning, inputs kept.
3. Use 12345 as phone: warning, inputs kept.
4. Use name@example.com and 416-555-0123: formats accepted.
5. Test yesterday as available date: warning.
6. Test negative salary or salary with 3 decimal places: warning.
7. Select relatives Yes without explanation: warning.
8. Submit a complete valid example: saving state, success popup, blank form afterward.
9. Confirm the new row in Workbench using SELECT * FROM wk10_lab.applications ORDER BY id DESC LIMIT 20;
10. Stop MySQL and try a valid submission: error popup, inputs kept. Restart MySQL afterward.

Prepared by source review; compilation, JavaFX GUI testing, and live MySQL testing must be performed locally. Existing database.sql and database rows are unchanged.
