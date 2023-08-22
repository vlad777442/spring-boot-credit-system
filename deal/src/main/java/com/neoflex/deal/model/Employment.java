package com.neoflex.deal.model;

import com.neoflex.deal.model.enums.EmploymentPosition;
import com.neoflex.deal.model.enums.EmploymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Employment information")
public class Employment implements Serializable {
    @Schema(description = "Employment ID")
    private Long employmentId;

    @Schema(description = "Employment status")
    private EmploymentStatus status;

    @Schema(description = "Employer INN")
    private String employerInn;

    @Schema(description = "Salary")
    private BigDecimal salary;

    @Schema(description = "Employment position")
    private EmploymentPosition position;

    @Schema(description = "Total work experience")
    private Integer workExperienceTotal;

    @Schema(description = "Current work experience")
    private Integer workExperienceCurrent;
}
