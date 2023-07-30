package com.neoflex.conveyor.dto;

import com.neoflex.conveyor.dto.enumType.EmploymentStatusType;
import com.neoflex.conveyor.dto.enumType.PositionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Employment dto")
public class EmploymentDTO {
    private EmploymentStatusType employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private PositionType position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
