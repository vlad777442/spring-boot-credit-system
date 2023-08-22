package com.neoflex.deal.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Passport information")
public class Passport implements Serializable {
    @Schema(description = "Passport series")
    private String series;

    @Schema(description = "Passport number")
    private String number;

    @Schema(description = "Passport issue branch")
    private String issueBranch;

    @Schema(description = "Passport issue date")
    private LocalDate issueDate;
}
