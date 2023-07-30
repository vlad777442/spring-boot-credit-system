package com.neoflex.conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Loan application dto")
public class LoanApplicationRequestDTO {
    @NotNull(message = "Loan amount cannot be null")
    @DecimalMin(value = "10000", message = "Loan amount must be greater than or equal to 10000")
    private BigDecimal amount;

    @NotNull(message = "Loan term cannot be null")
    @Min(value = 6, message = "Loan term must be greater than or equal to 6")
    private Integer term;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "First name must contain only Latin letters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "Last name must contain only Latin letters")
    private String lastName;

    @Size(min = 2, max = 30, message = "Middle name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "Middle name must contain only Latin letters")
    private String middleName;

    @NotBlank(message = "Email address cannot be blank")
    @Pattern(regexp = "[\\w\\.]{2,50}@[\\w\\.]{2,20}", message = "Invalid email address format")
    private String email;

    @NotNull(message = "Birthdate cannot be null")
    @Past(message = "Birthdate must be a date in the past")
    private LocalDate birthdate;

    @NotBlank(message = "Passport series cannot be blank")
    @Pattern(regexp = "\\d{4}", message = "Passport series must be a 4-digit number")
    private String passportSeries;

    @NotBlank(message = "Passport number cannot be blank")
    @Pattern(regexp = "\\d{6}", message = "Passport number must be a 6-digit number")
    private String passportNumber;
}
