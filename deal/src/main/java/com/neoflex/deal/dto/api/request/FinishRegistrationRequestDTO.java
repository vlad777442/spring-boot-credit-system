package com.neoflex.deal.dto.api.request;

import com.neoflex.deal.dto.enums.GenderType;
import com.neoflex.deal.dto.enums.MaritalStatusType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Registration dto")
public class FinishRegistrationRequestDTO {
    private GenderType gender;
    private MaritalStatusType maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBrach;
    private EmploymentDTO employment;
    private String account;
}
