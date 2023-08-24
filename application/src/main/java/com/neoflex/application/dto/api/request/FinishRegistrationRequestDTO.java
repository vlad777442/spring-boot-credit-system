package com.neoflex.application.dto.api.request;


import com.neoflex.application.dto.enums.GenderType;
import com.neoflex.application.dto.enums.MaritalStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Builder
@Jacksonized
@Schema(description = "Registration dto")
public class FinishRegistrationRequestDTO {
    @Schema(description = "Gender type", example = "MALE")
    private GenderType gender;

    @Schema(description = "Marital status type", example = "MARRIED")
    private MaritalStatusType maritalStatus;

    @Schema(description = "Number of dependents", example = "2")
    @Min(value = 0, message = "Dependent amount must be non-negative")
    private Integer dependentAmount;

    @Schema(description = "Passport issue date", example = "2020-08-21")
    @PastOrPresent(message = "Passport issue date must be in the past or present")
    private LocalDate passportIssueDate;

    @Schema(description = "Passport issue branch", example = "City ABC")
    @NotBlank(message = "Passport issue branch must not be blank")
    private String passportIssueBrach;

    @Schema(description = "Employment information")
    private EmploymentDTO employment;

    @Schema(description = "Account number", example = "123456789")
    @NotBlank(message = "Account number must not be blank")
    private String account;
}
