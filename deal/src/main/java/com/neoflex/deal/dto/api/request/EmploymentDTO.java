package com.neoflex.deal.dto.api.request;

import com.neoflex.deal.dto.enums.PositionType;
import com.neoflex.deal.model.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Builder
@Jacksonized
@Schema(description = "Employment dto")
public class EmploymentDTO {
    @Schema(description = "Employment status type", example = "EMPLOYED")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Employer's INN", example = "1234567890")
    private String employerINN;

    @Schema(description = "Salary amount", example = "50000")
    private BigDecimal salary;

    @Schema(description = "Position type", example = "WORKER")
    private PositionType position;

    @Schema(description = "Total work experience in months", example = "24")
    private Integer workExperienceTotal;

    @Schema(description = "Current work experience in months", example = "12")
    private Integer workExperienceCurrent;
}
