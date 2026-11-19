package application.dao;

import application.config.Database;
import application.model.EmploymentApplication;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class JdbcApplicationDao implements ApplicationDao {
    private static final String INSERT =
            "INSERT INTO applications (full_name, address, phone, email, education, "
            + "gender, available_date, position, salary, authorized, relatives, explanation, signature) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public void save(EmploymentApplication a) throws SQLException {
        try (Connection connection = Database.openConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT)) {
            statement.setString(1, a.fullName());
            statement.setString(2, a.address());
            statement.setString(3, a.phone());
            statement.setString(4, a.email());
            statement.setString(5, a.education());
            statement.setString(6, a.gender());
            statement.setDate(7, Date.valueOf(a.availableDate()));
            statement.setString(8, a.position());
            statement.setBigDecimal(9, a.salary());
            statement.setBoolean(10, a.authorized());
            statement.setBoolean(11, a.relatives());
            statement.setString(12, a.relatives() ? a.explanation() : "");
            statement.setString(13, a.signature());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("The application was not inserted.");
            }
        }
    }
}
