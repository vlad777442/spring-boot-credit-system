package com.neoflex.conveyor.dto.api.request;

import com.neoflex.conveyor.dto.enums.EmploymentStatusType;
import com.neoflex.conveyor.dto.enums.PositionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Employment dto")
public class EmploymentDTO {
    @Schema(name = "Employment status type", example = "EMPLOYED")
    private EmploymentStatusType employmentStatus;

    @Schema(name = "Employer's INN", example = "1234567890")
    private String employerINN;

    @Schema(name = "Salary amount", example = "50000")
    private BigDecimal salary;

    @Schema(name = "Position type", example = "MIDDLE")
    private PositionType position;

    @Schema(name = "Total work experience in months", example = "24")
    private Integer workExperienceTotal;

    @Schema(name = "Current work experience in months", example = "12")
    private Integer workExperienceCurrent;
}
