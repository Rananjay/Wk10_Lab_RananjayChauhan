package application.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmploymentApplication(
        String fullName, String address, String phone, String email,
        String education, String gender, LocalDate availableDate,
        String position, BigDecimal salary, boolean authorized,
        boolean relatives, String explanation, String signature) {
}
