package com.neoflex.conveyor.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentDTO {
    private EmploymentStatusType employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Enum position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
}
