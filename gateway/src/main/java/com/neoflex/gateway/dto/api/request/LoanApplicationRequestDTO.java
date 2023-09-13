package com.neoflex.gateway.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@Jacksonized
@Schema(description = "Loan application dto")
public class LoanApplicationRequestDTO {
    @Schema(description = "Loan amount", example = "500000")
    @NotNull(message = "Loan amount cannot be null")
    @DecimalMin(value = "10000", message = "Loan amount must be greater than or equal to 10000")
    private BigDecimal amount;

    @Schema(description = "Loan term (in months)", example = "12")
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

    @Schema(description = "Email address", example = "john.doe@example.com")
    @NotBlank(message = "Email address cannot be blank")
    @Email
    private String email;

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
}
