package application.service;

import application.dao.ApplicationDao;
import application.model.EmploymentApplication;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

public final class ApplicationService {
    private final ApplicationDao dao;

    public ApplicationService(ApplicationDao dao) {
        this.dao = Objects.requireNonNull(dao);
    }

    public void validate(EmploymentApplication a) {
        Objects.requireNonNull(a, "Application is required.");
        if (a.fullName() == null || a.fullName().isBlank()
                || a.availableDate() == null || a.gender() == null
                || a.gender().isBlank() || a.signature() == null
                || a.signature().isBlank()) {
            throw new IllegalArgumentException("Enter your name, date, signature and gender.");
        }
        if (a.salary() == null || a.salary().signum() < 0
                || a.salary().compareTo(new BigDecimal("9999999999.99")) > 0) {
            throw new IllegalArgumentException("Salary must be between 0 and 9999999999.99.");
        }
        if (a.relatives() && (a.explanation() == null || a.explanation().isBlank())) {
            throw new IllegalArgumentException("Please enter your relative's name and relationship.");
        }
    }

    public void save(EmploymentApplication application) throws SQLException {
        validate(application);
        dao.save(application);
    }
}
