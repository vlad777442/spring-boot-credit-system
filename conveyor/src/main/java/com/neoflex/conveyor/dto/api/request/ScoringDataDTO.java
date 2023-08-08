package com.neoflex.conveyor.dto.api.request;

import com.neoflex.conveyor.dto.enums.GenderType;
import com.neoflex.conveyor.dto.enums.MaritalStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Scoring data dto")
public class ScoringDataDTO {
    @Schema(description = "Loan amount", example = "200000")
    @NotNull(message = "Loan amount cannot be null")
    @DecimalMin(value = "10000", message = "Loan amount must be greater than or equal to 10000")
    private BigDecimal amount;

    @Schema(description = "Loan term (in months)", example = "24")
    @NotNull(message = "Loan term cannot be null")
    @Min(value = 6, message = "Loan term must be greater than or equal to 6")
    private Integer term;

    @Schema(description = "First name", example = "John")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "First name must contain only Latin letters")
    private String firstName;

    @Schema(description = "Last name", example = "Doe")
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "Last name must contain only Latin letters")
    private String lastName;

    @Schema(description = "Middle name", example = "William")
    @Size(min = 2, max = 30, message = "Middle name must be between 2 and 30 characters")
    @Pattern(regexp = "[A-Za-z]+", message = "Middle name must contain only Latin letters")
    private String middleName;

    @Schema(description = "Gender", example = "MALE")
    @NotNull(message = "Gender cannot be null")
    private GenderType gender;

    @Schema(description = "Birthdate", example = "1990-01-01")
    @NotNull(message = "Birthdate cannot be null")
    @Past(message = "Birthdate must be a date in the past")
    private LocalDate birthdate;

    @Schema(description = "Passport series", example = "1234")
    @NotBlank(message = "Passport series cannot be blank")
    @Pattern(regexp = "\\d{4}", message = "Passport series must be a 4-digit number")
    private String passportSeries;

    @Schema(description = "Passport number", example = "567890")
    @NotBlank(message = "Passport number cannot be blank")
    @Pattern(regexp = "\\d{6}", message = "Passport number must be a 6-digit number")
    private String passportNumber;

    @Schema(description = "Passport issue date", example = "2012-05-15")
    @NotNull(message = "Passport issue date cannot be null")
    @Past(message = "Passport issue date must be a date in the past")
    private LocalDate passportIssueDate;

    @Schema(description = "Passport issue branch", example = "Moscow")
    @NotBlank(message = "Passport issue branch cannot be blank")
    private String passportIssueBranch;

    @Schema(description = "Marital status", example = "SINGLE")
    @NotNull(message = "Marital status cannot be null")
    private MaritalStatusType maritalStatus;

    @Schema(description = "Number of dependents", example = "2")
    @Min(value = 0, message = "Dependent amount cannot be negative")
    private Integer dependentAmount;

    @Schema(description = "Employment details")
    @NotNull(message = "Employment details cannot be null")
    private EmploymentDTO employment;

    @Schema(description = "Bank account", example = "1234567890")
    @NotBlank(message = "Bank account cannot be blank")
    private String account;

    @Schema(description = "Insurance status", example = "true")
    @NotNull(message = "Insurance status cannot be null")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Salary client status", example = "false")
    @NotNull(message = "Salary client status cannot be null")
    private Boolean isSalaryClient;
}
