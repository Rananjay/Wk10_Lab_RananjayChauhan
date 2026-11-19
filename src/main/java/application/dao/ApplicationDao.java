package application.dao;

import application.model.EmploymentApplication;
import java.sql.SQLException;

public interface ApplicationDao {
    void save(EmploymentApplication application) throws SQLException;
}
